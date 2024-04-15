#!/usr/bin/python3
import os

COMPILER = "gcc"

data = {}

if __name__ == "__main__":
    for test_dir in os.listdir("."):
        if os.path.isdir(test_dir) and test_dir != "priv2":
            os.chdir(test_dir)
            print(f"Compiling {test_dir}...")
            os.system(f"{COMPILER} -O3 -msse {test_dir}.cpp -o {test_dir}.out")
            print(f"Compiling {test_dir}_opt...")
            os.system(f"{COMPILER} -O3 -msse {test_dir}_opt.cpp -o {test_dir}_opt.out")

            print(f"Executing {test_dir}...")
            os.system(f"./{test_dir}.out")
            print(f"Executing {test_dir}_opt...")
            os.system(f"./{test_dir}_opt.out")

            os.remove(f"{test_dir}.out")
            os.remove(f"{test_dir}_opt.out")

            os.chdir("..")
