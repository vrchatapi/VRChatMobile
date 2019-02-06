#!/bin/sh
cordova build --release android
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore keystore.jks release-unsigned.apk Upload
rm release-signed.apk
zipalign -v 4 release-unsigned.apk release-signed.apk 