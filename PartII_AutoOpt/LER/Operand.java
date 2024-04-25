// Operand class

import java.util.LinkedHashSet;
import java.util.Set;

public class Operand {

	private Set<String> relLoops;
	// Raw operand string from AST
	private String raw;

	// Variable name, separated from access.
	private String varName;

	public Operand(String raw) {
		relLoops = new LinkedHashSet<String>();
		this.raw = raw;
	}

	public String getRaw() {
		return raw;
	}

	@Override
	public String toString() {
		return raw;
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

	public void selfAbstract(Set<String> itervars) {
		// Check if an actual index var brackets or parentheses.
		int open, close;
		if (raw.contains("[") && raw.contains("]")) {
			open = raw.indexOf('[');
			close = raw.lastIndexOf(']');

		} else if (raw.contains("(") && raw.contains(")")) {
			open = raw.indexOf('(');
			if (open == 0)
				return;
			close = raw.lastIndexOf(')');
		} else {
			varName = raw;
			return;
		}

		String access = raw.substring(open, close + 1);

		varName = raw.substring(0, open);

		for (String iterVar : itervars) {
			if (access.contains(iterVar))
				relLoops.add(iterVar);
		}
	}

	public Set<String> getRelLoops() {
		return relLoops;
	}

}
