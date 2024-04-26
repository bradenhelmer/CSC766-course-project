// LER Notation Abstraction
import java.util.*;
import java.util.*;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;

public class LER {

	public interface Loop {
		public void print();
		Set<String> getRelLoops(); 
		Set<String> getRelLoops(); 
	}

	// List of loops with this LER statement -> L
	private LinkedList<Loop> loops;
	private Set<String> relLoops;
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
			if (!iterVars.isEmpty()){
				O.selfAbstract(iterVars);
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
