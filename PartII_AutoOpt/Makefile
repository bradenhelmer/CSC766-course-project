# Makefile for LER Optimizer
COMPILER := javac
LER_DIR := LER
SOURCE := $(wildcard $(LER_DIR)/Glory*.java)
SOURCE += $(LER_DIR)/DirectiveListener.java
PYTHON := python3

compile: $(SOURCE)
	$(COMPILER) $(SOURCE)

clean:
	rm $(LER_DIR)/*.class testResult/*

test: compile
	$(PYTHON) runAllTestCases.py
