// LER Notation Abstraction

import java.util.LinkedList;

public class LER {
	public interface Loop {
		public void print();
	}

	// List of loops with this LER statement.
	private LinkedList<Loop> loops;

	public LER() {
		this.loops = new LinkedList<Loop>();
	}

	public void addLoop(Loop L) {
		loops.add(L);
	}

	public void output() {
		int indent = 0;
		for (Loop L : loops) {
			System.out.print("\t".repeat(indent));
			L.print();
			indent++;
		}
	}
}
