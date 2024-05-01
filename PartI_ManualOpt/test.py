#!/usr/bin/python3
import os
from subprocess import run, PIPE
import matplotlib.pyplot as plt

COMPILER = "gcc"

data = {}

if __name__ == "__main__":
    print("Testing Unoptimized ... -O0")
    for test_dir in os.listdir("."):
        if os.path.isdir(test_dir):
            print(f"Testing {test_dir}...")
            os.chdir(test_dir)
            os.system(f"{COMPILER} -O0 {test_dir}.cpp -o {test_dir}.out")
            os.system(f"{COMPILER} -O0 {test_dir}_opt.cpp -o {test_dir}_opt.out")

            with open(f"{test_dir}.result", "w") as result_file:
                command = [f"./{test_dir}.out"]
                result = run(command, stdout=result_file, stderr=PIPE)
                command = [f"./{test_dir}_opt.out"]
                result = run(command, stdout=result_file, stderr=PIPE)

            with open(f"{test_dir}.result", "r") as result_file:
                times = [
                    float(time.rstrip()[:-1].split()[-1])
                    for time in result_file.readlines()
                    if time.startswith("time")
                ]
                data[test_dir] = times[0] / times[1];

            os.remove(f"{test_dir}.out")
            os.remove(f"{test_dir}_opt.out")
            os.remove(f"{test_dir}.result")

            os.chdir("..")

    plt.xlabel("Program")
    plt.ylabel("Speedup")
    plt.bar(list(data.keys()), list(data.values()), color="orange")
    plt.title("Program speedups after LER Optimization with -O0")
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig("speedup_O0.png")

    data = {}

    print("Testing with Optimization Flags ... -O3 -msse")
    for test_dir in os.listdir("."):
        if os.path.isdir(test_dir):
            print(f"Testing {test_dir}...")
            os.chdir(test_dir)
            os.system(f"{COMPILER} -O3 -msse {test_dir}.cpp -o {test_dir}.out")
            os.system(f"{COMPILER} -O3 -msse {test_dir}_opt.cpp -o {test_dir}_opt.out")

            with open(f"{test_dir}.result", "w") as result_file:
                command = [f"./{test_dir}.out"]
                result = run(command, stdout=result_file, stderr=PIPE)
                command = [f"./{test_dir}_opt.out"]
                result = run(command, stdout=result_file, stderr=PIPE)

            with open(f"{test_dir}.result", "r") as result_file:
                times = [
                    float(time.rstrip()[:-1].split()[-1])
                    for time in result_file.readlines()
                    if time.startswith("time")
                ]
                data[test_dir] = times[0] / times[1];

            os.remove(f"{test_dir}.out")
            os.remove(f"{test_dir}_opt.out")
            os.remove(f"{test_dir}.result")

            os.chdir("..")

    plt.xlabel("Program")
    plt.ylabel("Speedup")
    plt.bar(list(data.keys()), list(data.values()), color="orange")
    plt.title("Program speedups after LER Optimization -O3")
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig("speedup_O3.png")

    
