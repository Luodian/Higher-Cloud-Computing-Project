#!/usr/bin/env bash
# num-thread使用上述内核个数
sysbench --test=cpu --cpu-max-prime=20000 --num-threads=4 run
# cat src/main/java/sample/BackEnd/Shell/Assesment.txt