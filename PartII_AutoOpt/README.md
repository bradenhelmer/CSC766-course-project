## Part II Auto Optimization

This directory contains all the source code for the LER optimizing compiler.  
### Build Instructions
Ensure you have antlr installed and in CLASSPATH as instructed in this [README](https://github.com/bradenhelmer/CSC766-course-project/blob/main/PartII_AutoOpt/README-2stu.txt).
```
export CLASSPATH="/usr/local/lib/antlr-4.13.1-complete.jar:$CLASSPATH"
make
```
### Testing Instructions
To run all tests:
```
mkdir testResult
python3 runAllTestCases.py
```
This will place all the result files in the `testResult` directory.

### Current Results
The output result for each test case is the original LER notation as input, followed by the optimized LER notation, and then the generated C code for the optimized notation. Category 4 redundancy removals are working great, with the optimized LER compiler correctly optimizing cases 2, 7, and 9. 

<strong>NOTE:</strong> THE C CODE GENERATION IS PART OF PARTIII_AutoOpGen! We decided to leave it in as part of PartII for simplicity.

### Bugs and Limitations
...
