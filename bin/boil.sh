#! /bin/bash

cd ..
echo 'Deleting class files...'
find . -type f -iname \*.class -delete
echo 'Compiling...'
javac --module-path ./jfxmac/lib --add-modules javafx.controls Kettlelog.java
echo 'Opening application....'
java --module-path ./jfxmac/lib --add-modules javafx.controls Kettlelog