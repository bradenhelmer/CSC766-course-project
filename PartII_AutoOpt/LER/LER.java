// LER Notation Abstraction

import java.util.LinkedList;

public class LER {

	public interface Loop {
		public void print();
	}

	// List of loops with this LER statement -> L
	private LinkedList<Loop> loops;

	// Expression -> E
	private LinkedList<String> operands;
	private LinkedList<String> ops;

	// Result operand LE -> R
	private String result;

	public LER() {
		this.loops = new LinkedList<Loop>();
		this.operands = new LinkedList<String>();
		this.ops = new LinkedList<String>();
	}

	public void addLoop(Loop L) {
		loops.add(L);
	}

	public void addOp(String OP) {
		ops.add(OP);
	}

	public void addOperand(String O) {
		operands.add(O);
	}

	public void output() {
		int indent = 0;
		for (Loop L : loops) {
			System.out.print(" ".repeat(2 * indent++));
			L.print();
		}
		int i;
		System.out.print(" ".repeat(2 * indent++));
		for (i = 0; i < operands.size(); ++i) {
			System.out.printf("%s ", operands.get(i));
			if (i < ops.size()) {
				System.out.printf("%s ", ops.get(i));
			}
		}
		System.out.printf("= %s\n", result);
	}

	public void setResult(String R) {
		result = R;
	}
}
