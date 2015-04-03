Backup of WD MyCloud to S3/Glacier with duplicity
=================================================

How to back up your precious files stored on the WD My Cloud NAS
into S3 with the slow but low-cost storage class "Glacier".

How does the backup work: duplicity does its job and uploads files
to S3. The large data archives are recognized by S3 Lifecycle rules that
we set up based on their prefix and moved to the Glacier storage class
soon after upload. (It takes hours to restore something from Glacier but
its cost is orders of magnitude lower than that of S3 itself). We leave
metadata files in S3 so that duplicity can read them.

90% of this is based on http://www.x2q.net/2013/02/24/howto-backup-wd-mybook-live-to-amazon-s3-and-glacier/ and the WD build guide (http://community.wd.com/t5/WD-My-Cloud/GUIDE-Building-packages-for-the-new-firmware-someone-tried-it/m-p/770653#M18650 and the update at http://community.wd.com/t5/WD-My-Cloud/GUIDE-Building-packages-for-the-new-firmware-someone-tried-it/m-p/841385#M27799). Kudos to the authors!

You will need to:

 1. Build duplicity and its dependencies (since WD Debian v04
    switched to page size of 64kB, all pre-built binaries are unusable)
 2. Configure S3 to move the data files to Glacier after 0 days
 3. Create your backup script - see `backup-pictures-to-s3.sh`
 4. Schedule to run incremental backups regularly via Cron
 5. Preferably test restore manually

## 1. Build duplicity and its dependencies

See `./mycloud-build-vm/README.md`
This is based on duplicity 0.6.24 (available in the Jessie release of Debian);
the older one in Wheezy does not support the crucial option `--file-prefix-archive`.

## 2. Configure S3

Create a backup bucket - either call it `my-backup-bucket` or update the
backup script with your bucket name. (Duplicity can sometimes create it but
especially if you want it in an European zone, it might be easier to create
it manually).

Set rules to move the large data files to Glacier (they will remain visible in the bucket 
but their Storage Class will become Glacier soon after upload; they will not be visible
directly in Glacier). Given the example backup script and the two prefixes it uses, you
want to configure add Lifecycle rules for both:

 * Rule Name: Archive to Glacier
 * Apply the Rule to: A prefix - either bob-data- or shared\_pictures-data- 
 * Action on Objects: Archive Only
 * Archive to the Glacier Storage Clas 0 days after the object's creation date.

Tip: Create a dedicated user for backups via AWS IAM, having access only to the backup bucket;
this is the Policy you would want to create (modify the bucket name as appropriate):

    {
      "Version": "2012-10-17",
      "Statement": [
        {
          "Effect": "Allow",
          "Action": "s3:*",
          "Resource": ["arn:aws:s3:::my-backup-bucket", "arn:aws:s3:::my-backup-bucket/*"]
        }
      ]
    }

## 3. Create your backup script

Modify the attached `backup-pictures-to-s3.sh`:

 * Set your AWS ID and secret
 * Modify the supported `SRC_ARG`, `SOURCE`, and `PREFIX` values

Notice that the script sets a prefix for all the files (data archive, manifest, ...) to
distinguish backups of different directories and also adds another prefix (`data-`) to
the archive files so that we can move just these to Glacier.

## 4. Schedule to run incremental backups regularly via Cron

For example to backup pictures every Tuesday and phone pictures every Wednesday at 20:00:

    0 20 * * 2 /root/backup-pictures-to-s3.sh pictures
    0 20 * * 3 /root/backup-pictures-to-s3.sh phone

## 5. Preferably test restore manually

See `./restore.example`. You likely also want to try these:
`duplicity list-current-files [options] target_url`,
`duplicity verify [options] source_url target_dir`,
`duplicity collection-status [options] target_url` to verify the backup is alright.

## Caveats

You likely want to run a full backup some time and clean up old (incremental) backups.
This has to be done manually.
