#!/bin/bash
# See http://community.wd.com/t5/WD-My-Cloud/GUIDE-Building-packages-for-the-new-firmware-someone-tried-it/m-p/770653#M18650
# ... and the update on http://community.wd.com/t5/WD-My-Cloud/GUIDE-Building-packages-for-the-new-firmware-someone-tried-it/m-p/841385#M27799
## About Jessie (Ubuntu testing):
# NOTE: Another important thing to note is that to build jessie packages it might be required to build a new patched version of binutils, becase the one provided by WD has become old: if a package you want to build depends on a newer version of binutils, it might download the newer unpatched version of binutils and hence produce an invalid package (i.e.: a package that won't run properly on the My Cloud).
# Follow the instructions in ./binutils/README-binutils-64k-pagesize.txt to know how to build a newer patched version (the procedure is very much similar to the one used to build standard packages).

function die {
    echo -e "\e[31m!!!! ERROR: $1\e[0m"
    exit 1
}

# qemu-user-static, binfmt-support are needed to build newer binutils, needed for jessie
echo "deb http://ftp.debian.org/debian wheezy-backports main contrib non-free" >> /etc/apt/sources.list
apt-get update
apt-get -t wheezy-backports --assume-yes install qemu-user-static
apt-get --assume-yes install binfmt-support

# Prepare the build env
mkdir /wdmc-build
cd /wdmc-build

# Download My Cloud GPL source
wget --progress=dot:giga http://download.wdc.com/gpl/gpl-source-wd_my_cloud-04.01.03-421.zip || die "Failed to download MyCloud source, update the link; see http://support.wdc.com/product/download.asp?groupid=904&sid=233"
unzip gpl-source-*.zip packages/build_tools/debian/*

mkdir 64k-wheezy
cp -R packages/build_tools/debian/* ./64k-wheezy
echo '#!/bin/bash' > 64k-wheezy/build.sh
echo './build-armhf-package.sh --pagesize=64k $1 wheezy' >>64k-wheezy/build.sh
chmod a+x ./64k-wheezy/build.sh

mkdir 64k-jessie
cp -R packages/build_tools/debian/* ./64k-jessie
echo '#!/bin/bash' >>64k-jessie/build.sh
echo './build-armhf-package.sh --pagesize=64k $1 jessie' >>64k-jessie/build.sh
chmod a+x ./64k-jessie/build.sh

rm -rf packages/

echo ">>> INFO Installing patched setup, build scripts"
cd /wdmc-build/64k-wheezy
mv setup.sh setup.sh.backup
mv build-armhf-package.sh build-armhf-package.sh.backup
cp /vagrant/WD_MyCloud_64k_build_patched_scripts/*.sh .

cd /wdmc-build/64k-jessie
mv setup.sh setup.sh.backup
mv build-armhf-package.sh build-armhf-package.sh.backup
cp /vagrant/WD_MyCloud_64k_build_patched_scripts/*.sh .


## prepare an emulated ARM system and replace the qemu-arm-static binary provided by the bootstrap with the recent one we've installed in our actual build system
# 64k wheezy setup
cd /wdmc-build/64k-wheezy
echo ">>> INFO Going to set up $(pwd)"
./setup.sh bootstrap/wheezy-bootstrap_*_armhf.tar.gz build
# Note: Ignore any errors produced by setup.sh: that script is really buggy and many things it tries to do seem to be useless, unless we apply the mentioned qemu fix.
mv build/usr/bin/qemu-arm-static build/usr/bin/qemu-arm-static_orig
cp /usr/bin/qemu-arm-static build/usr/bin/qemu-arm-static

# 64k jessie setup
cd /wdmc-build/64k-jessie
echo ">>> INFO Going to set up $(pwd)"
./setup.sh bootstrap/jessie-bootstrap_*_armhf.tar.gz build
cp /usr/bin/qemu-arm-static build/usr/bin/qemu-arm-static

echo ">>> INFO Updating sources.list in the build environments"
cat > /wdmc-build/64k-wheezy/build/etc/apt/sources.list <<- END
# install-added-sources:
deb http://ftp.debian.org/debian wheezy main contrib non-free
deb-src http://ftp.debian.org/debian wheezy main contrib non-free
deb http://security.debian.org/ wheezy/updates main contrib non-free
deb-src http://security.debian.org/ wheezy/updates main contrib non-free
deb http://ftp.debian.org/debian wheezy-updates main contrib non-free
deb-src http://ftp.debian.org/debian wheezy-updates main contrib non-free
# Only comment out backports if you really need them; and perhaps additional setup is needed
#deb http://ftp.debian.org/debian wheezy-backports main contrib non-free
#deb-src http://ftp.debian.org/debian wheezy-backports main contrib non-free
END

cat > /wdmc-build/64k-jessie/build/etc/apt/sources.list <<- END
# install-added-sources:
deb http://ftp.debian.org/debian jessie main contrib non-free
deb-src http://ftp.debian.org/debian jessie main contrib non-free
deb http://security.debian.org/ jessie/updates main contrib non-free
deb-src http://security.debian.org/ jessie/updates main contrib non-free
deb http://ftp.debian.org/debian jessie-updates main contrib non-free
deb-src http://ftp.debian.org/debian jessie-updates main contrib non-free
END

echo "Note: Wheezy has g++ 4.6 but 4.7 is available, see the source article for instructions to get it"
echo ">>> INFO Building binutils for jessie"
patch /wdmc-build/64k-jessie/binutils/build-64k-binutils.sh < /vagrant/scripts/build-64k-binutils.patch2ignore_applied_patches
echo "!!! This is going to contain about patches anre require your input (twice 'n'):"
cd /wdmc-build/64k-jessie && binutils/build-64k-binutils.sh
# While patching file ld/emulparams/armelf_linux.sh it asks:
# Reversed (or previously applied) patch detected!  Assume -R? [n] 
# Apply anyway? [n]
