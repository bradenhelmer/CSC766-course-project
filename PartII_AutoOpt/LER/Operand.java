// Operand class

import java.util.LinkedHashSet;
import java.util.Set;

public class Operand implements Cloneable {

	private Set<String> relLoops;
	// Raw operand string from AST
	private String raw;

	private String prevOp;
	private String nextOp;

	private boolean isIndexed;

	public void setPrevOp(String prev) {
		prevOp = prev;
	}

	public void setNextOp(String next) {
		nextOp = next;
	}

	public String getPrevOp() {
		return prevOp;
	}

	public String getNextOp() {
		return nextOp;
	}

	// Variable name, separated from access.
	private String varName;

	public Operand(String raw) {
		relLoops = new LinkedHashSet<String>();
		nextOp = "";
		prevOp = "";
		this.raw = raw;
	}

	public String getRaw() {
		return raw;
	}

	@Override
	public String toString() {
		if (isIndexed) {
			if (raw.contains("(")) {
				return varName + raw.substring(raw.indexOf('('));
			}
			if (raw.contains("[")) {
				return varName + raw.substring(raw.indexOf('['));
			}
		}
		return raw;
	}

	@Override
	public Operand clone() {
		try {
			return (Operand) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	public String getAbstracted() {
		if (relLoops.isEmpty()) {
			return raw;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(varName + "{ ");
		for (String RL : relLoops) {
			sb.append(RL + ", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("}");
		return sb.toString();
	}

	public Set<String> selfAbstract(Set<String> itervars) {

		// Check if an actual index var brackets or parentheses.
		int open, close;
		if (raw.contains("[") && raw.contains("]")) {
			open = raw.indexOf('[');
			close = raw.lastIndexOf(']');

			isIndexed = true;

		} else if (raw.contains("(") && raw.contains(")")) {
			open = raw.indexOf('(');
			if (open == 0)
				return relLoops;
			close = raw.lastIndexOf(')');

			isIndexed = true;

		} else {
			// If neither brackets nor parentheses are found, return empty set
			return relLoops;
		}

		String access = raw.substring(open, close + 1);
		String varName = raw.substring(0, open);

		// Add loop variables to relLoops if they are found in the access part
		this.varName = varName;

		// Add loop variables to relLoops if they are found in the access part
		for (String iterVar : itervars) {
			if (access.contains(iterVar)) {
				if (access.contains(iterVar)) {
					relLoops.add(iterVar);
				}
			}

		}
		return relLoops;
	}

	public Set<String> getRelLoops() {
		return relLoops;
	}

	public void replaceVarName(String newName) {
		varName = newName;
	}

	public boolean isIndexed() {
		return isIndexed;
	}

	// Setter for relLoops
	public void setRelLoops(Set<String> relLoops) {
		this.relLoops = relLoops;

	}

}
