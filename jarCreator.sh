#!/bin/sh
jar cfm Implementor.jar Manifest.mf -C out/production/Java-Advanced/ ru/ifmo/ctddev/zenkova/implementor/Implementor.class -C artifacts/ info/kgeorgiy/java/advanced/implementor/Impler.class -C artifacts info/kgeorgiy/java/advanced/implementor/ImplerException.class -C artifacts/ info/kgeorgiy/java/advanced/implementor/JarImpler.class
