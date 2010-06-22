#!/bin/bash
for i in e ne n nw w sw s se
do
    for j in 0000 0001 0002 0003 0004 0005 0006 0007 0008 0009 0010 0011
    do
        convert "$1/walking\ $i"*.bmp +append ~/tmp/walking_$i.png
    end
done

