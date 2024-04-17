
// For loop Abstraction
import java.lang.IllegalArgumentException;

public class ForLoop implements LER.Loop {
	private String iter;
	private String lb;
	private String ub;
	private int type;
	private int step = 1;

	public ForLoop(String iter, String lb, String ub, int type) throws IllegalArgumentException {
		this.iter = iter;
		this.lb = lb;
		this.ub = ub;
		if (GloryUtil.isForType(type)) {
			this.type = type;
		} else {
			throw new IllegalArgumentException(String.format("Cannot instantiate for loop with type: %s!",
					GloryUtil.getGloryTokenString(type)));
		}
	}

	public void print() {
		System.out.printf("%s FOR LOOP (%s) %s -> %s\n", GloryUtil.getGloryTokenString(type), iter, lb, ub);
	}

	// Getter for iter
	public String getIter() {
		return iter;
	}

	// Setter for iter
	public void setIter(String iter) {
		this.iter = iter;
	}

	// Getter for lb
	public String getLb() {
		return lb;
	}

	// Setter for lb
	public void setLb(String lb) {
		this.lb = lb;
	}

	// Getter for ub
	public String getUb() {
		return ub;
	}

	// Setter for ub
	public void setUb(String ub) {
		this.ub = ub;
	}

	// Getter for type
	public int getType() {
		return type;
	}

	// Setter for type
	public void setType(int type) {
		this.type = type;
	}

	// Getter for step
	public int getStep() {
		return step;
	}

	// Setter for step
	public void setStep(int step) {
		this.step = step;
	}

}
