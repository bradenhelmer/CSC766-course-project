## Part I Manual Optimization

This directory contains all the files related to part I of the course project, manual optimization. Each subdirectory contains three files
1. The original test file e.g bgd.cpp
2. A text file containing the optimized LER notation for the original program e.g bgd.ler
3. The optimized C code e.g bgd_opt.cpp

### Testing
For building and testing the programs:
```
make test
```
This will compile all the code and invoke a python script that will execute all original and optimized programs and plot them on a graph compare.png.
