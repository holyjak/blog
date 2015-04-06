## Build all the packages needed for duplicity and realted SW
set -e

# 1. Wheezy (stable) packages
for PKG in util-linux trickle python-boto python-crypto python-pyasn1 libgmp10 python-support python-pycparser python-paramiko python-ndg-httpsclient python-cryptography python-jwt python-openssl libffi6 libpython2.7-stdlib python-ply python-six python-cffi libpython2.7-minimal python-ecdsa python-lockfile libpython-stdlib python-requests python-oauthlib python-pkg-resources python-chardet librsync1 python-urllib3 libsmartcols1 libmount1 libblkid1 util-linux python2.7 python-minimal python2.7-minimal pythoni python-gnupginterface python3 python3-minimal python3-chardet2; do
  ./scripts/build-package-wheezy.sh $PKG
done

# 2. Jessie (test) packages
# (Only duplicity 0.6.24 supports separate prefix for data files)
for PKG in duplicity ; do
  ./scripts/build-package-jessie.sh $PKG
done

# 3. Done
cp /wdmc-build/64k-jessie/build/root/*.deb /wdmc-build/64k-wheezy/build/root/*.deb /vagrant/

echo "!!! All the .deb files are now in /vagrant/ (i.e. the directory with the Vagrantfile)"
echo "Tip1: Do not install the -dev packages; you don't need them and they are likely to cause problems."
echo "Tip2: You likely do not actually need to install the Python 3 packages; skip them if they cause any problems."
echo "ENJOY"
