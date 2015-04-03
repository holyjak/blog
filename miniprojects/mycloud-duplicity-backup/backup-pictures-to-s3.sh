#!/bin/bash

# Extra arguments to pass to duplicity for whatever reason
EXTRA_OPTS=""

# WD Live Duplicity Back Up To Amazon S3 (and Glacier)
#
# Requires python-boto, duplicity, util-linux, and trickle to be installed
# Install using: apt-get install python-boto duplicity util-linux trickle
#
# See this blog post for more info:
# http://www.x2q.net/blog/2013/02/24/howto-backup-wd-mybook-live-to-amazon-s3-and-glacier/

function die {
   echo "EXITING:" $1
   exit 1
}
## Arguments
if [ $# -ne 1 ]; then
   die "Required 1 argument: pictures|phone"
fi
SRC_ARG=$1

case "$SRC_ARG" in
'pictures')
  SOURCE="/DataVolume/shares/Public/Shared Pictures/"
  PREFIX=shared_pictures-
  ;;
'phone')
  SOURCE="/DataVolume/shares/Public/bob/"
  PREFIX=bob-
  ;;
*)
  die "Unknown backup source $SRC_ARG; known: bob, pictures."
  ;;
esac

# Exclusive locking using flock. Ensures that only instance is active at the
# time. This is useful, when your backup might run for several days.
# flock man page: http://linux.die.net/man/2/flock
# See https://tobrunet.ch/2013/01/follow-up-bash-script-locking-with-flock/
set -e
backup_id="$(basename $0)_${SRC_ARG}"
pidfile="/var/run/${backup_id}"
exec 200>$pidfile
flock -n 200 || die "Already running, exiting"
pid=$$
echo $pid 1>&200

echo "Going to backup $SOURCE as $PREFIX"

# Path variables
export PATH="/opt/bin:/opt/sbin:/bin:/sbin:/usr/bin:/usr/sbin:/usr/local/sbin"
export HOME="/root"

# Export some ENV variables so you don't have to type anything
export AWS_ACCESS_KEY_ID="<your aws access key ID>"
export AWS_SECRET_ACCESS_KEY="<your aws secret key>"

# GPG (currently unused)
#export PASSPHRASE=<your GPG passphrase>
#export GPG_KEY=<your key id>

# Bandwidth limiting
export MAXUPLOAD=2048 # kbyte per second

# The source of your backup
CACHE=/DataVolume/Temp/.cache/duplicity
TEMP=/DataVolume/Temp

# The destination
# Note that the bucket need not exist but does need to be unique amongst all
# Amazon S3 users. So, choose wisely.
DEST=s3://s3-eu-west-1.amazonaws.com/my-backup-bucket

# Tip: Exclude folders with `--exclude-if-present filename`
# or pass in names to ignore via stdin via `--exclude-filelist-stdin`
echo "Going to run backup ..."
nohup trickle -u ${MAXUPLOAD} duplicity \
--tempdir=${TEMP} \
--no-encryption \
--verbosity 5 \
--s3-use-rrs \
--volsize 128 \
--archive-dir ${CACHE} \
--asynchronous-upload \
--file-prefix=$PREFIX \
--file-prefix-archive data- \
--s3-use-new-style \
--name ${SRC_ARG} \
${EXTRA_OPTS} "${SOURCE}" "${DEST}"

#--full-if-older-than 180D \
#--s3-unencrypted-connection \ # usually provider better upload speeds

# Reset the ENV variables. Don't need them sitting around
export AWS_ACCESS_KEY_ID=
export AWS_SECRET_ACCESS_KEY=
