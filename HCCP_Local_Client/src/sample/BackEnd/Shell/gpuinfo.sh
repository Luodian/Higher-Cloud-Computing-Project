#!/usr/bin/env bash
lspci | grep -i --color 'vga\|3d\|2d'
#cat src/sample/BackEnd/Shell/gpuinfo.txt