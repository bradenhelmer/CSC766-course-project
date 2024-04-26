# Makefile for LER Optimizer
COMPILER := javac
LER_DIR := LER
SOURCE := $(wildcard $(LER_DIR)/*.java)
PYTHON := python3

compile: $(SOURCE)
	$(COMPILER) -g $(SOURCE)

clean:
	rm $(LER_DIR)/*.class testResult/*

test: compile
	java --class-path="./LER:$$CLASSPATH" Glory testcases/case2.txt
