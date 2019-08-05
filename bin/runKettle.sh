#! /bin/bash

echo 'Deleting Class files'
cd ..
find . -type f -iname \*.class -delete
javac --module-path ./jfx/lib --add-modules javafx.controls Kettlelog.java
java --module-path ./jfx/lib --add-modules javafx.controls Kettlelog