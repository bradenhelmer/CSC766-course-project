// LER Notation Abstraction

import java.util.*;

public class LER {

	public interface Loop {
		public void print();

		public void printCStmt();

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
		this.relLoops = new LinkedHashSet<String>();
	}

	// SETTERS ...
	public void addLoop(Loop L) {
		if (L instanceof ForLoop FL) {
			iterVars.add(FL.getIter());
		}
		loops.add(L);
	}

	private List<Loop> getForLoopsFromRelLoops(Set<String> RL) {
		return loops.stream().filter(loop -> (loop instanceof ForLoop FL) && RL.contains(FL.getIter())).toList();
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

	public Operand getResult() {
		return result;
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

	public void setResult(Operand R) {
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
				System.out.printf("%s ", O.getNextOp());
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

	private void __outputCCodeInternal(LER ler) {
		int indent = 0;
		for (Loop L : ler.getLoops()) {
			System.out.print(" ".repeat(2 * indent++));
			L.printCStmt();
		}
		System.out.print(" ".repeat(2 * indent++));
		System.out.printf("%s =", ler.getResult().getRaw());
		for (Operand O : ler.getOperands()) {
			System.out.printf(" %s%s", O.getRaw(), O.getNextOp().isEmpty() ? ";" : " " + O.getNextOp());
		}
		indent--;
		while (indent > 0) {
			System.out.printf("\n%s}", " ".repeat(2 * --indent));
		}
		System.out.println();

	}

	// Output C code for Part III
	public void outputCCode() {
		if (optimized.isEmpty()) {
			__outputCCodeInternal(this);
		} else {
			for (LER L : optimized) {
				__outputCCodeInternal(L);
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
			} else {
				for (Operand O : operands) {
					if (O.isIndexed()) {
						toBeMoved.add(O);
					}
				}
			}

			// Algorithm:
			// 1. For each rel loop in an operand that is getting replaced
			// - Create a new LER object with that loop(s) and operand(s) pointing to a new
			// variable.
			while (!toBeMoved.isEmpty()) {
				ArrayList<Operand> operandsWithMatchingRelLoops = new ArrayList<Operand>();
				Set<String> currRelLoops = new LinkedHashSet<String>();
				Iterator<Operand> it = toBeMoved.iterator();
				while (it.hasNext()) {
					Operand O = it.next();
					if (operandsWithMatchingRelLoops.isEmpty()) {
						operandsWithMatchingRelLoops.add(O);
						currRelLoops = O.getRelLoops();
						it.remove();
					} else {
						@SuppressWarnings("unchecked")
						ArrayList<Operand> omrCpy = (ArrayList<Operand>) operandsWithMatchingRelLoops.clone();
						for (Operand OMR : omrCpy) {
							if (O.getRelLoops().equals(OMR.getRelLoops())) {
								operandsWithMatchingRelLoops.add(O);
								it.remove();
							} else {
								continue;
							}
						}
					}
				}

				// Get actual loops for new LER
				List<Loop> loopList = getForLoopsFromRelLoops(currRelLoops);

				// Construct new LER for removed loops.
				LER newLer = new LER();
				loopList.forEach(loop -> newLer.addLoop(loop));
				Operand newResult;
				if (result.isIndexed()) {
					Operand rClone = (Operand) result.clone();
					rClone.replaceVarName(String.format("TEMP%d", tempNum++));
					newResult = rClone;
				} else {
					newResult = new Operand(String.format("TEMP%d", tempNum++));
				}

				newResult.setPrevOp(operandsWithMatchingRelLoops.get(0).getPrevOp());
				newResult.setNextOp(
						operandsWithMatchingRelLoops.get(operandsWithMatchingRelLoops.size() - 1).getNextOp());
				List<Operand> clones = new ArrayList<Operand>();
				for (Operand O : operandsWithMatchingRelLoops) {
					clones.add((Operand) O.clone());
				}
				clones.get(0).setPrevOp("");
				clones.get(clones.size() - 1).setNextOp("");
				newLer.addOperands(clones);
				newLer.setResult(newResult);

				// Do operand switch
				int last_index = 0;
				for (Operand O : operandsWithMatchingRelLoops) {
					if (operands.contains(O)) {
						last_index = operands.indexOf(O);
						operands.remove(O);
					}
				}

				// Clean up unnecessary loops in self
				for (Loop l : loops) {
					if (l instanceof ForLoop FL) {
						boolean stay = false;
						for (Operand O : operands) {
							if (O.isIndexed() && O.getRelLoops().contains(FL.getIter()) || stayingPut.contains(O)) {
								stay = true;
							}
						}
						if (!stay) {
							loops.remove(l);
						}
					}
				}

				operands.add(last_index, newResult);
				optimized.add(newLer);
				optimized.add(this);

			}

		}

	}

	// OPTIMIZATION METHODS
	public void optimize() {
		abstractOperands();
		cat3Removal();
		if (optimized.isEmpty()) {
			cat4Removal();
		}
	}

	// Performs operand abstraction, computing relLoops(x) for each operand.
	private void abstractOperands() {
		operands.forEach(O -> {
			if (!iterVars.isEmpty()) {
				O.selfAbstract(iterVars);
			}
		});

	}

	// Category 3 Redudancy Removal Algorithm Pseudo Code
	// -------------------------------------------------- 
	// 1. Find Operands that can be removed from current loop nest -> toBeMoved (List<Operand>)
	// 		1. Other operands that cannot be moved -> stayingPut (List<Operand>)
	// 
	// 2. For each operand in toBeMoved...
	// 		1. Find their associated loops (relLoops) and clone them into -> newLoops (List<ForLoop>)
	//
	//      2. Utilize min-union code to derive best combinations of loops.
	//		^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	//		Not sure if this should go here or somewhere below but this is my best guess.
	//
	// 3. Create a new LER object that for the removed loops -> newLER (LER)
	// 		1. Derive the new result/temp operand for newLER from the relLoops of each operand in toBeMoved -> newResult (Operand)	
	// 			- This will most likely need some manipulation methods in the Operand class for creating new indexes.
	//
	// 		2. For each loop in newLoops, add loop into newLER.
	//
	// 		3. newLER.setResult(newResult)
	// 		
	// 		4. Add all operands from toBeMoved into the newLER
	// 	
	// 	4. Modify the existing LER object to replace the moved operands with the newLER result (temp varaible)
	// 		1. Assuming the operand(s) removed are in order...
	//
	// 			1. The first operand in toBeMoved: newResult.setPrevOp(toBeMoved.get(0).getPrevOp())
	// 											   newResult.setNextOp(toBeMoved.get(toBeMoved.size() - 1).getNextOp())
	// 				- This allows the operands to be in order when the final result gets printed.
	//
	// 		2. Remove operands in original LER (this) and replace with result of newLER (newReslt), need to keep track of where the last operand
	// 		   was removed so that it can be inserted correctly.
	//
	//      3. Now that we have the operands in place, ensure that the original LER (this) has the correct loops, removing any that
	//         are unnecessary.
	//  
	//  5. Add newLER into optimized (this.optimized.add(newLER))
	//  
	//  6. Add this into optimized (this.optimized.add(this))
	//
	

	private void cat3Removal(){}
	// 	setRelLoopsForForLoops();
	// 	
	// 	ArrayList<Operand> toBeMoved;
	// 	ArrayList<Operand> stayingPut ;
	// 	int tempNum = 0;
	// 	//ArrayList<Operand>[] toBeMoved = new ArrayList[loops.size()];
	// 	// for (int i = 0; i < loops.size(); i++) {
	// 	// 	toBeMoved[i] = new ArrayList<Operand>();
	// 	// }
	// 	for (int i = 0; i< loops.size(); i++) {
	// 		toBeMoved = new ArrayList<Operand>();
	// 		stayingPut = new ArrayList<Operand>();
	// 		Loop L = loops.get(i);
	// 		if (L instanceof ForLoop FL) {
	// 			for (Operand O : operands) {
	// 				if (!O.getRelLoops().isEmpty()) {
	// 					// We need to check here that we havent already looked at an operand.
	// 					if (!O.getRelLoops().contains(FL.getIter()) )//&& !stayingPut.contains(O)
	// 						toBeMoved.add(O);
	// 					else if(O.getRelLoops().contains(FL.getIter()))
	// 						stayingPut.add(O);
	// 				}
	// 			}
	// 			FL.setCost(toBeMoved);
	// 			FL.setOperand(stayingPut);
	// 			System.out.println("tobemoved"+toBeMoved+ "!!" + stayingPut);
	// 		} else {
	// 			for (Operand O : operands) {
	// 				if (O.isIndexed()) {
	// 					toBeMoved.add(O);
	// 				}
	// 			}
	// 		}			
	// 		
	// 		// }Σi∫1,M∫Σj∫1,M∫Γk∫1,N∫Σl∫1,N∫x[i,l]*y[l,j]*s[j,k]=r[i,k]//Σi∫1,N∫Σj∫1,N∫Σk∫1,N∫Σt∫1,N∫a[i,j]*b[i,k]=x
	// 	ForLoop thisLoop = findMinimumCostLoop();
	// 	//System.out.println("cost"+thisLoop.getCost());
	// 	// if(thisLoop.getOperand().isEmpty()){
	//
	// 	// }
	// 	//ArrayList<Operand> operandsWithMatchingRelLoops = new ArrayList<Operand>();
	// 	Set<String> currRelLoops = new LinkedHashSet<String>();
	//
	// 	ArrayList<Operand> candidateOp = thisLoop.getToBeMoved();
	// 	
	// 	if (candidateOp != null) {
	// 		for (int j = 0; j < candidateOp.size(); j++) {
	// 			currRelLoops.addAll(candidateOp.get(j).getRelLoops());
	// 		}
	// 	}
	// 			
	// 		// System.out.println("currRelLoops" + currRelLoops);
	// 		// // Get actual loops for new LER
	// 		List<Loop> loopList = getForLoopsFromRelLoops(currRelLoops);
	// 		
	// 		System.out.println("loppList" + loopList);
	// 		// Construct new LER for removed loops.
	// 		LER newLer = new LER();
	// 		loopList.forEach(loop -> newLer.addLoop(loop));
	// 		Operand newResult;
	// 		if (result.isIndexed()) {
	// 			System.out.println("injaaa");
	// 			Operand rClone = (Operand) result.clone();
	// 			rClone.replaceVarName(String.format("TEMP%d", tempNum++));
	// 			newResult = rClone;
	// 			System.out.println("injaaa" + newResult);
	// 		} else {
	// 			System.out.println("injaaa222");
	// 			newResult = new Operand(String.format("TEMP%d", tempNum++));
	// 		}
	// 		
	// 		newResult.setPrevOp(thisLoop.getToBeMoved().get(0).getPrevOp());
	// 		newResult.setNextOp(
	// 			thisLoop.getToBeMoved().get(thisLoop.getToBeMoved().size() - 1).getNextOp());
	// 		List<Operand> clones = new ArrayList<Operand>();
	// 		for (Operand O : thisLoop.getToBeMoved()) {
	// 			clones.add((Operand) O.clone());
	// 		}
	// 		clones.get(0).setPrevOp("");
	// 		clones.get(clones.size() - 1).setNextOp("");
	// 		newLer.addOperands(clones);
	// 		newLer.setResult(newResult);
	//
	// 		// Do operand switch
	// 		int last_index = 0;
	// 		for (Operand O : thisLoop.getOperand()) {
	// 			if (operands.contains(O)) {
	// 				System.out.println("injaaa3");
	// 				last_index = operands.indexOf(O);
	// 				operands.remove(O);
	// 			}
	// 		}
	//
	// 		// Clean up unnecessary loops in self
	// 		for (Loop l : loops) {
	// 			if (l instanceof ForLoop FL) {
	// 				boolean stay = false;
	// 				for (Operand O : operands) {
	// 					if (O.isIndexed() && O.getRelLoops().contains(FL.getIter())) {
	// 						stay = true;
	// 					}
	// 				}
	// 				if (!stay) {
	// 					loops.remove(l);
	// 				}
	// 			}
	// 		}
	//
	// 		operands.add(last_index, newResult);
	// 		optimized.add(newLer);
	// 		optimized.add(this);
	//
	// 	//minUnion() ;
	// }

	private void setRelLoopsForForLoops() {
		
			//Set<String> relLoops = operand.selfAbstract(iterVars);
		for (Loop loop : loops) {
			if (loop instanceof ForLoop) {
					ForLoop forLoop = (ForLoop) loop;
				for (Operand operand : operands) {
						//System.out.println("operator " + forLoop.getIter());
						if(operand.getRelLoops().contains(forLoop.getIter())){
							forLoop.setRelLoops(operand.getRelLoops());
						}
				}
				System.out.println("loop relLoops: " + forLoop.getRelLoops());
			}
			
		}
	}

	public LinkedList<Loop> getLoops() {
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
				//if (forLoop.getType() == 23 || forLoop.getType() == 24) {// check to be reduction loop
				initialNodes.add((ForLoop) forLoop);
				//}
			}
		}
		return initialNodes;
	}

	public ForLoop findMinimumCostLoop() {
		int min = Integer.MAX_VALUE;
		ForLoop minLoop = null;
	
		for (Loop loop : loops) {
			if (loop instanceof ForLoop) {
				ForLoop currentLoop = (ForLoop) loop;
				int cost = currentLoop.getCost();
				if (cost < min) {
					min = cost;
					minLoop = currentLoop;
				}
			}
		}
	
		return minLoop;
	}
	

	public void calculateCost(Set<ForLoop> worklist) {

		// for (ForLoop loop : worklist) {

		// 	ForLoop forLoop = (ForLoop) loop;
		// 	forLoop.setCost(computeCost(forLoop));
		// }
	}

	public int computeCost(ForLoop loop) {
		int cost = 1;
		for (String relLoop : relLoops) {
			cost = cost * 2;
		}
		cost = cost * loop.getIndexRange();
		return cost;
	}

	private boolean isIndependent(ForLoop loop, LinkedList<Loop> worklist) {
		for (Loop otherLoop : worklist) {
			if(otherLoop instanceof ForLoop FL){
				if (loop != FL && !Collections.disjoint(loop.getRelLoops(), FL.getRelLoops())) {
				return false;
				}
			}
		}

		return true;
	}

	public void minUnion() {
		Set<ForLoop> worklist = new HashSet<>(createInitialNodes(loops));

		calculateCost(worklist);

		Forest F = new Forest();
		List<ForLoop> loopsToRemove = new ArrayList<>(); // Store loops to remove

		// for (ForLoop thisLoop : worklist) {
		// 	if (thisLoop.getRelLoops().isEmpty() || isIndependent(thisLoop, worklist)) {
		// 		F.addNode(thisLoop);
		// 		loopsToRemove.add(thisLoop); // Add to remove list
		// 	}
		// }
		// System.out.println(loopsToRemove.size());
		// worklist.removeAll(loopsToRemove);

		// while (!worklist.isEmpty()) {
		// 	ForLoop thisLoop = findMinimumCostLoop(worklist);

		// 	worklist.remove(thisLoop);
		// 	F.addNode(thisLoop);
		// 	for (ForLoop l : worklist) {
		// 		if (isSubset(thisLoop.getRelLoops(), l.getRelLoops())) {
																		


		// 			l.addChild(thisLoop);
		// 			//l.setCost(l.getCost() / thisLoop.getIndexRange());
		// 		}
		// 	}

		// 	for (ForLoop c : thisLoop.getChildren()) {
		// 		if (c.getParent() != null) {
		// 			c.getParent().removeChild(c);
		// 		} else {
		// 			c.setParent(thisLoop);
		// 		}
		// 	}
		// }

		// // Output forest F
		// F.output();
	}

}
