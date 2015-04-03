Build duplicity for WD MyCloud v4
=================================

v4 of MyCloud Linux switched to page size 64k, which made packages from
public repositories unusable and it is necessary to rebuild them from 
sources. This project contains a script and VM to build those packages.

It is 99% based on http://community.wd.com/t5/WD-My-Cloud/GUIDE-Building-packages-for-the-new-firmware-someone-tried-it/m-p/770653#M18650 and the update at http://community.wd.com/t5/WD-My-Cloud/GUIDE-Building-packages-for-the-new-firmware-someone-tried-it/m-p/841385#M27799

Prerequisities
--------------

Vagrant and VirtualBox.

Usage
-----

 0. Increase the memory of the VM (currently 250MB) if you have enough, using VirtualBox
 1. Run `vagrant up`
 2. Run `vagrant ssh` to enter the VM
 3. Setup the build environment: run `sudo /vagrant/install.sh` in the VM; check for errors
 4. Build the packages: run `sudo nohup /vagrant/build-duplicity.sh &` (it will take lot of time and we don't want to lose the build process when ssh disconnects; watch via `sudo tail -f nohup.out`)
 5. Copy the built wheezy and jessie packages to MyCloud; important: *exclude -dev* packages

Note: Building takes a long time.

Alternative approaches
----------------------

 * http://jodal.no/2015/03/08/building-arm-debs-with-pbuilder/
 * https://blog.night-shade.org.uk/2013/12/building-a-pure-debian-armhf-rootfs/
