// LER Notation Abstraction
import java.util.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class LER {

	public interface Loop {
		public void print();
		Set<String> getRelLoops(); 
	}

	// List of loops with this LER statement -> L
	private LinkedList<Loop> loops;
	private Set<String> relLoops;
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
		this.relLoops = new LinkedHashSet<String>();
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
			}
		});
		
	}

	//Performs minimu union alg
	// LER class

	public void setRelLoopsForForLoops() {
		for (Operand operand : operands) {
			Set<String> relLoops = operand.selfAbstract(iterVars); // Assuming selfAbstract returns the relevant loops
			for (Loop loop : loops) {
				if (loop instanceof ForLoop) {
					ForLoop forLoop = (ForLoop) loop;
					if (relLoops.contains(forLoop.getIter())) {
						forLoop.setRelLoops(relLoops); // Set the relLoops for this ForLoop
					}
				}
			}
		}
	}
	
	
	
	public LinkedList getLoops() {
		return this.loops;
	}
	
	private boolean isSubset(Set<String> set1, Set<String> set2) {
		return set2.containsAll(set1);
	}
	
	public Set<ForLoop> createInitialNodes(LinkedList<Loop> S) {
		Set<ForLoop> initialNodes = new HashSet<>();
		for (Loop node : S) {
			
			if (node instanceof ForLoop) {
				
				initialNodes.add((ForLoop) node);
			}
		}
		return initialNodes;
	}
	
	
    public ForLoop findMinimumCostLoop(Set<ForLoop> worklist) {
        ForLoop minLoop = null;
        double minCost = Double.MAX_VALUE;
        for (ForLoop loop : worklist) {
            if (((ForLoop) loop).getCost() < minCost) {
                minLoop = loop;
                minCost = ((ForLoop) loop).getCost();
            }
        }
		
        return minLoop;
    }

	public void calculateCost() {
		// Iterate over each reduction loop
		for (Loop loop : loops) {
			if (loop instanceof ForLoop) {
				ForLoop forLoop = (ForLoop) loop;
				double minCost = Double.MAX_VALUE;
				
				// Iterate over each other reduction loop to calculate cost
				for (Loop otherLoop : loops) {
					if (loop != otherLoop && otherLoop instanceof ForLoop) {
						ForLoop otherForLoop = (ForLoop) otherLoop;
						Set<String> relLoops = forLoop.getRelLoops();
						Set<String> otherRelLoops = otherForLoop.getRelLoops();
						System.out.printf("relLoops %s", relLoops);
						System.out.printf("otherRelLoops %s", otherRelLoops);
						if (isSubset(relLoops, otherRelLoops) && !otherRelLoops.containsAll(relLoops)) {
							// Compute cost based on the subset condition
							double cost = computeCost(forLoop, otherForLoop);
							minCost = Math.min(minCost, cost);
						}
					}
				}
				
				// Set the minimum cost for the loop
				forLoop.setCost(minCost);
			}
		}
	}
	
	public double computeCost(ForLoop loop1, ForLoop loop2) {
		// Check if relLoops of loop1 is a subset of relLoops of loop2
		System.out.printf("computer cost 1");
		if (isSubset(loop1.getRelLoops(), loop2.getRelLoops())) {
			System.out.printf("computer cost 2");
			// Compute cost based on some logic
			// For example, you could multiply the ranges of index values of loops in relLoops
			double cost = loop1.getIndexRange() * loop2.getIndexRange();
			return cost;
		} else {
			// If relLoops of loop1 is not a subset of relLoops of loop2, return a high cost
			return Double.MAX_VALUE;
		}
	}
	
	public void minUnion() {
		// Calculate costs for all loops
		calculateCost();
		
		// Create forest F to record optimized order to compute the reductions
		Forest F = new Forest();
		
		// Create worklist and add initial nodes
		Set<ForLoop> worklist = new HashSet<>(createInitialNodes(loops));
	
		// While worklist is not empty
		while (!worklist.isEmpty()) {
			// Find loop with minimum cost in worklist
			ForLoop thisLoop = findMinimumCostLoop(worklist);
			System.out.printf("findMinimumCostLoop %s", thisLoop.getIter());
			// Remove thisLoop from worklist and add it to F
			worklist.remove(thisLoop);
			F.addNode(thisLoop);
			
			// Update cost of other loops in worklist
			for (ForLoop l : worklist) {
				System.out.printf("work list");
				if (isSubset(thisLoop.getRelLoops(), l.getRelLoops())) {
					// Update cost of l based on the minimum union algorithm
					l.setCost(l.getCost() / thisLoop.getIndexRange());
				}
			}
			
			// Confirm parent relation with children
			for (ForLoop c : thisLoop.getChildren()) {
				System.out.printf("parent loop");
				if (c.getParent() != null) {
					c.getParent().removeChild(c);
				} else {
					c.setParent(thisLoop);
				}
			}
		}
		
		// Output forest F
		F.output();
	}
	
	 
	
}
