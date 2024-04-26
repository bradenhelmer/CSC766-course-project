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
	private LinkedList<Operand> operands;
	private LinkedList<String> ops;

	// Result operand LE -> R
	private String rawE;
	private Operand result;

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

	public String getLastOp() {
		return ops.get(ops.size() - 1);
	}

	public Operand getLastOperand() {
		return operands.get(operands.size() - 1);
	}

	public LinkedList<Operand> getOperands() {
		return operands;
	}

	public void addOperand(String O) {
		operands.add(new Operand(O));
	}

	public void addOperands(List<Operand> operands) {
		this.operands.addAll(operands);
	}

	public void addOperand(Operand O) {
		operands.add(O);
	}

	public void setResult(String R) {
		result = new Operand(R);
		if (!iterVars.isEmpty())
			result.selfAbstract(iterVars);
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

		for (int i = 0; i < operands.size(); ++i) {
			Operand O = operands.get(i);
			output += O.toString() + O.getNextOp();
		}

		output += "=" + result;
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
		int tempNum = 0;

		for (int i = loops.size() - 1; i >= 0; --i) {
			toBeMoved = new ArrayList<Operand>();
			Loop L = loops.get(i);
			if (L instanceof ForLoop FL) {
				for (Operand O : operands) {
					if (!O.getRelLoops().isEmpty()) {
						// We need to check here that we havent already looked at an operand.
						if (!O.getRelLoops().contains(FL.getIter()) && !stayingPut.contains(O))
							toBeMoved.add(O);
						else
							stayingPut.add(O);
					}
				}
			}

			// Check we have a variable to remove here.
			if (!toBeMoved.isEmpty()) {
				String newR = String.format("TEMP%d", tempNum++);
				LER newLER = new LER();

				for (Loop l : loops) {
					if (l instanceof ForLoop FL) {
						for (Operand O : toBeMoved) {
							if (O.getRelLoops().contains(FL.getIter())) {

								// Create new result variable as an operand.

								newLER.addLoop(FL);
								newLER.addOperands(operands);
								newLER.setResult(result.getRaw());
								result.replaceVarName(newR);
								System.out.println(result);
							}
						}
					}
				}
				operands.removeAll(toBeMoved);

				optimized.add(this);
				optimized.add(newLER);
				newLER.getOperands().removeAll(stayingPut);
				newLER.addOperand(result);

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
