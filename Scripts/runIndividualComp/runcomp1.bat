@echo off
title comp1

javac -sourcepath ../../Components/comp1 ../../Components/comp1/*.java
start "comp1" /D"../../Components/comp1" java Createcomp1
