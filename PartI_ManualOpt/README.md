## Part I Manual Optimization

This directory contains all the files related to part I of the course project, manual optimization. Each subdirectory contains three files
1. The original test file e.g bgd.cpp
2. A text file containing the optimized LER notation for the original program e.g bgd.ler
3. The optimized C code e.g bgd_opt.cpp

### Testing
For building and testing the programs:
```bash
sudo apt install pip
pip3 install matplotlib
python3 test.py
```
This will invoke a python script to compile all the code and execute all original and optimized programs and create two plots: speedup_O0.png and speedup_O3.png.

### Comparison Report
The speedups observed between the original C programs and the optimized ones derived from the optimized LER notation were very substantial. When compiling with -O0 flags (no optimzation) the speed up for most prorgams was very profound with some programs have speedups in the thousands. However, when compliling with "-O3 -msse" optimization flags, the speed ups were even higher. For example, we observed for ccsd_multisize a speedup of just below 1000x with -O0 flags and over 4500x with optimiziation flags. This is probably due to a large SIMD optimization for the largely nested loops. 

All of GCC's loop optimizations are generally included with the -O3 flag, however, it is still at the compiler's discretion as to which ones will be used based on the program at hand. It may be useful to experiment by trying different combinations of loop optimizations explicitly enabled and disabled. This would be useful in optimizing for specific prorgams and use cases. Additionally, newer compiler technologies like LLVM's [polly](https://github.com/llvm/llvm-project/tree/main/polly) optimization framework or MLIR's [affine dialect](https://mlir.llvm.org/docs/Dialects/Affine/) could provide even further analysis/optimization opportunities than that of traditional compiler technique.
