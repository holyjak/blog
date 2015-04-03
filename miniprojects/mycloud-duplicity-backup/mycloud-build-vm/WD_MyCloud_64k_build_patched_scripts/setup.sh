#!/bin/bash
# setup.sh <bootstrap_tar> <debian_output_dir>
#
# This sets up a Debian bootstrap chroot directory given a bootstrap_tar file.
# It will automatically install the proper qemu static emulator as required.
#
if [ $# -ne 2 ]; then
	echo "Usage: setup.sh <bootstrap_tar> <debian_output_dir>"
	exit 1
fi

DEBIAN_INTEL_HOST=0
DIR_EXISTS=0
bootstrap_tar=$1
debian_output_dir=$2

[ -d "$debian_output_dir" ] && DIR_EXISTS=1

mkdir -p "$debian_output_dir"
debian_output_dir="`(cd \"$2\" && pwd)`"
canonical_link_dir="`readlink -f $debian_output_dir`"
[ ! -z $canonical_link_dir ] && debian_output_dir=$canonical_link_dir

echo "debian_output_dir=$debian_output_dir"

arch=`dpkg --print-architecture`
[ "$arch" == "amd64" -o "$arch" == "i386" ] && DEBIAN_INTEL_HOST=1

## check for proper qemu support if on Intel machine
if [ $DEBIAN_INTEL_HOST -eq 1 ]; then
    dpkg -l | grep -q binfmt-support
	binfmt_installed=$?
    dpkg -l | grep -q qemu-user-static
	qemu_installed=$?
    [ "$binfmt_installed" != "0" -o "$qemu_installed" != "0" ] && echo "binfmt-support and/or qemu-user-static packages are not installed, required for qemu emulation" && exit 1
fi

[ -d "qemu" ] && qemu_dir="qemu"
[ -d "../qemu" ] && qemu_dir="../qemu"

if [ $DIR_EXISTS -eq 1 ]; then 
   sudo umount -f $debian_output_dir/proc
   sudo umount -f $debian_output_dir/dev/pts
   sudo umount -f $debian_output_dir/dev
else
	# Create new bootstrap directory and setup the bootstrap
	sudo tar xf $bootstrap_tar -C $debian_output_dir

	if [ $DEBIAN_INTEL_HOST -eq 1 ]; then
		file $debian_output_dir/bin/bash | grep -q "ARM"
		if [ $? -eq 0 ]; then
			echo "Found ARM architecture, installing qemu-arm-static"
                        if [ -f /usr/bin/qemu-arm-static ]; then
                        	QEMU_ARM_STATIC=/usr/bin/qemu-arm-static
                        else
				QEMU_ARM_STATIC=$qemu_dir/$arch/qemu-arm-static
                        fi
			cp -av $QEMU_ARM_STATIC $debian_output_dir/usr/bin
		fi
	fi

	# Copy in host machines resolve.conf
	cp /etc/resolv.conf $debian_output_dir/etc/resolv.conf
fi

if [ $DEBIAN_INTEL_HOST -ne 1 ]; then
    # create bind mounts to allow access to drive volumes from chroot
	mount | awk '{if( match($3,"/mnt") && !match($1,"/mnt")) print $3}' > /tmp/mnt_list
	while read mnt_pt
	do
		bind_mnt_dir=${debian_output_dir}${mnt_pt}
		mount | grep -q $bind_mnt_dir
		if [ $? -ne 0 ]; then
		    echo "Creating bind mount at $bind_mnt_dir"
			mkdir -p $bind_mnt_dir
			mount --bind ${mnt_pt} $bind_mnt_dir
		fi
	done < /tmp/mnt_list
fi

sudo chroot $debian_output_dir /bin/bash -x <<'EOF'
mount -t proc none /proc
mount -t devtmpfs none /dev
mount -t devpts none /dev/pts
groupadd -g 1000 share
usermod -u 501 -g 1000 nobody
exit
EOF
