# Makefile for LER Optimizer
COMPILER := javac
LER_DIR := LER
SOURCE := $(wildcard $(LER_DIR)/Glory*.java)
SOURCE += $(LER_DIR)/DirectiveListener.java
SOURCE += $(LER_DIR)/LER*.java
SOURCE += $(LER_DIR)/*Loop.java
PYTHON := python3

compile: $(SOURCE)
	$(COMPILER) -g $(SOURCE)

clean:
	rm $(LER_DIR)/*.class testResult/*

test: compile
	$(PYTHON) runAllTestCases.py
