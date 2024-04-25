// LER Notation Abstraction

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class LER {

	public interface Loop {
		public void print();
	}

	// List of loops with this LER statement -> L
	private LinkedList<Loop> loops;

	// Set of for loop iter vars relevant to this notation.
	private LinkedHashSet<String> iterVars;

	// Expression -> E
	private String rawE;
	private LinkedList<Operand> operands;
	private LinkedList<String> ops;

	// Result operand LE -> R
	private String result;

	// Holds the potential multiple LER statements after optimization.
	private List<LER> optimized;

	// CTOR
	public LER() {
		this.loops = new LinkedList<Loop>();
		this.operands = new LinkedList<Operand>();
		this.ops = new LinkedList<String>();
		this.iterVars = new LinkedHashSet<String>();
		this.optimized = new LinkedList<LER>();
	}

	// SETTERS ...
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

	public void setResult(String R) {
		result = R;
	}

	public void setE(String E) {
		rawE = E;
	}

	// OUTPUT Methods
	
	// LER String representation.
	@Override
	public String toString() {
		String output = "";
		for (Loop L : loops) {
			output += L.toString();
		}	
		output += rawE + "=" + result;
		return output;
	}


	// Output pseudo code for loops.
	public void outputPseudo(boolean abstracted) {
		int indent = 0;
		for (Loop L : loops) {
			System.out.print(" ".repeat(2 * indent++));
			L.print();
		}
		int i;
		System.out.print(" ".repeat(2 * indent++));
		System.out.printf("%s = ", result);
		for (i = 0; i < operands.size(); ++i) {
			Operand O = operands.get(i);
			System.out.printf("%s ", abstracted ? O.getAbstracted() : O.getRaw());
			if (i < ops.size()) {
				System.out.printf("%s ", ops.get(i));
			}
		}
		System.out.println();
	}

	// Output LER Noation
	public void outputLER() {
		if (optimized.isEmpty()) {
			System.out.println(this.toString());
		} else {
			for (LER L : optimized) {
				System.out.println(L.toString());
			}
		}
	}

	private void cat4Removal() {
		// Iterate backwards from inner to outer loops.
		ArrayList<Operand> toBeMoved;
		ArrayList<Operand> stayingPut = new ArrayList<Operand>();
		int tempCount = 0;

		for (int i = loops.size() - 1; i >= 0; --i) {
			toBeMoved = new ArrayList<Operand>();
			Loop L = loops.get(i);
			if (L instanceof ForLoop FL) {
				for (Operand O : operands) {
					// We need to check here that we havent already looked at an operand.
					if (!O.getRelLoops().contains(FL.getIter()) && !stayingPut.contains(O))
						toBeMoved.add(O);
					else
						stayingPut.add(O);

				}
			}

			// Check we have a variable to remove here.
			if (!toBeMoved.isEmpty()) {
				String tempVar = String.format("temp%d", tempCount++);
				operands.removeAll(toBeMoved);
			}
		}
	}

	// OPTIMIZATION METHODS
	public void optimize() {
		abstractOperands();
		cat4Removal();
	}

	// Performs operand abstraction, computing relLoops(x) for each operand.
	private void abstractOperands() {
		operands.forEach(O -> {
			if (!iterVars.isEmpty())
				O.selfAbstract(iterVars);
		});
	}
}
