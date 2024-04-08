# Makefile for LER Optimizer
COMPILER := javac
LER_DIR := LER
SOURCE := $(wildcard $(LER_DIR)/Glory*.java)
SOURCE += $(LER_DIR)/DirectiveListener.java
PYTHON := python3

compile: $(SOURCE)
	$(COMPILER) $(SOURCE)

setup:
	export CLASSPATH=".:/usr/local/lib/antlr-4.13.1-complete.jar:$CLASSPATH"
	alias antlr4='java -Xmx500M -cp "/usr/local/lib/antlr-4.13.1-complete.jar:$CLASSPATH" org.antlr.v4.Tool'
	alias grun='java -Xmx500M -cp "/usr/local/lib/antlr-4.13.1-complete.jar:$CLASSPATH" org.antlr.v4.gui.TestRig'

clean:
	rm $(LER_DIR)/*.class

test: compile
	$(PYTHON) runAllTestCases.py
