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
			}
		});
		
	}

	//Performs minimu union alg
	// LER class

	public void setRelLoopsForForLoops() {
		for (Operand operand : operands) {
			Set<String> relLoops = operand.selfAbstract(iterVars); 
			for (Loop loop : loops) {
				if (loop instanceof ForLoop) {
					ForLoop forLoop = (ForLoop) loop;
					if (relLoops.contains(forLoop.getIter())) {
						forLoop.setRelLoops(relLoops); 
					}
				}
			}
		}
	}
	
		
	public LinkedList getLoops() {
		return this.loops;
	}
	
	private boolean isSubset(Set<String> set1, Set<String> set2) {
		boolean t = set2.containsAll(set1);
		return set2.containsAll(set1);
	}
	
	public Set<ForLoop> createInitialNodes(LinkedList<Loop> S) {
		Set<ForLoop> initialNodes = new HashSet<>();
		for (Loop node : S) {
			
			if (node instanceof ForLoop) {
				ForLoop forLoop = (ForLoop) node;
				if(forLoop.getType()== 23 || forLoop.getType() == 24){//check to be reduction loop
					initialNodes.add((ForLoop) forLoop);
				}
			}
		}
		return initialNodes;
	}
	
	
    public ForLoop findMinimumCostLoop(Set<ForLoop> worklist) {
		int min = Integer.MAX_VALUE;
		ForLoop minLoop = null;

		for (ForLoop loop : worklist) {
			minLoop = (ForLoop) loop;
			min = Math.min(min, minLoop.getCost());
			
		}	
		return minLoop;
    }

	public void calculateCost(Set<ForLoop> worklist) {
		
		for (ForLoop loop : worklist) {
			
			
			ForLoop forLoop = (ForLoop) loop;
			forLoop.setCost(computeCost(forLoop)); 
		}
	}
	
	public int computeCost(ForLoop loop) {
		int cost = 1;
		for (String relLoop : relLoops) {
			cost = cost *2;
		}
		cost = cost * loop.getIndexRange() ;
		return cost;
	}
	
	private boolean isIndependent(ForLoop loop, Set<ForLoop> worklist) {
		for (ForLoop otherLoop : worklist) {
			if (loop != otherLoop && !Collections.disjoint(loop.getRelLoops(), otherLoop.getRelLoops())) {
				return false;
			}
		}
		
		return true;
	}

	public void minUnion() {
		Set<ForLoop> worklist = new HashSet<>(createInitialNodes(loops));
	
		calculateCost(worklist);
	
		Forest F = new Forest();
		List<ForLoop> loopsToRemove = new ArrayList<>(); // Store loops to remove
	
		for (ForLoop thisLoop : worklist) {
			if (thisLoop.getRelLoops().isEmpty() || isIndependent(thisLoop, worklist)) {
				F.addNode(thisLoop);
				loopsToRemove.add(thisLoop); // Add to remove list
			}
		}
	
		worklist.removeAll(loopsToRemove);
	
		while (!worklist.isEmpty()) {
			ForLoop thisLoop = findMinimumCostLoop(worklist);
	
			worklist.remove(thisLoop);
			F.addNode(thisLoop);
			for (ForLoop l : worklist) {
				if (isSubset(thisLoop.getRelLoops(), l.getRelLoops()) ) {//&& !thisLoop.getRelLoops().contains(l.getIter())

					l.addChild(thisLoop);
					l.setCost(l.getCost() / thisLoop.getIndexRange());
				}
			}
			
			for (ForLoop c : thisLoop.getChildren()) {
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
