// LER Notation Abstraction

import java.util.LinkedHashSet;
import java.util.LinkedList;

public class LER {

	public interface Loop {
		public void print();
	}

	// List of loops with this LER statement -> L
	private LinkedList<Loop> loops;

	// Set of for loop iter vars relevant to this notation.
	private LinkedHashSet<String> iterVars;

	// Expression -> E
	private LinkedList<Operand> operands;
	private LinkedList<String> ops;

	// Result operand LE -> R
	private String result;

	public LER() {
		this.loops = new LinkedList<Loop>();
		this.operands = new LinkedList<Operand>();
		this.ops = new LinkedList<String>();
		this.iterVars = new LinkedHashSet<String>();
	}

	public void addLoop(Loop L) {
		if (L instanceof ForLoop FL) {
			iterVars.add(FL.getIter());
		}
		loops.add(L);
	}

	public void addOp(String OP) {
		ops.add(OP);
	}

	public void addOperand(String O) {
		operands.add(new Operand(O));
	}

	public void output(boolean abstracted) {
		int indent = 0;
		for (Loop L : loops) {
			System.out.print(" ".repeat(2 * indent++));
			L.print();
		}
		int i;
		System.out.print(" ".repeat(2 * indent++));
		for (i = 0; i < operands.size(); ++i) {
			Operand O = operands.get(i);
			System.out.printf("%s ", abstracted ? O.getAbstracted() : O.getRaw());
			if (i < ops.size()) {
				System.out.printf("%s ", ops.get(i));
			}
		}
		System.out.printf("= %s\n", result);
	}

	public void setResult(String R) {
		result = R;
	}

	// Performs operand abstraction, computing relLoops(x) for each operand.
	public void abstractOperands() {
		operands.forEach(O -> {
			if (!iterVars.isEmpty())
				O.selfAbstract(iterVars);
		});
	}
}
