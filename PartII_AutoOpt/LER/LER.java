// LER Notation Abstraction
import java.util.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;

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
	private LinkedList<Operand> operands;
	private LinkedList<String> ops;

	// Result operand LE -> R
	private String result;

	// CTOR
	public LER() {
		this.loops = new LinkedList<Loop>();
		this.operands = new LinkedList<Operand>();
		this.ops = new LinkedList<String>();
		this.iterVars = new LinkedHashSet<String>();
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

	// OUTPUT
	public void output(boolean abstracted) {
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

	// OPTIMIZATION METHODS
	public void optimize() {
		abstractOperands();
	}

	// Performs operand abstraction, computing relLoops(x) for each operand.
	private void abstractOperands() {
		operands.forEach(O -> {
			if (!iterVars.isEmpty()){
				O.selfAbstract(iterVars);
				relLoops = O.getRelLoops();
				System.out.printf("abstractOperands %s",relLoops);
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
