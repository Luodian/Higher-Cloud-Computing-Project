#!/usr/bin/env bash
sudo apt-get install sysbench
# num-thread使用上述内核个数
sysbench --test=cpu --cpu-max-prime=20000 --num-threads=4 run