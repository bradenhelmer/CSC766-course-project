
// For loop Abstraction
import java.lang.IllegalArgumentException;
import java.util.*;

public class ForLoop implements LER.Loop {
	private String iter;
	private String lb;
	private String ub;
	private int type;
	private int step = 1;
	private String ID;
    private double cost;
    private Set<String> relLoops;
    private Set<ForLoop> children;
    private ForLoop parent;
    private int indexRange;

	public ForLoop(String iter, String lb, String ub, int type) throws IllegalArgumentException {
		this.iter = iter;
		this.lb = lb;
		this.ub = ub;
		this.ID = ID;
        this.cost = 0;
        this.relLoops = new HashSet<>();
        this.children = new HashSet<>();
        this.parent = null;
        this.indexRange = 1;
		if (GloryUtil.isForType(type)) {
			this.type = type;
		} else {
			throw new IllegalArgumentException(String.format("Cannot instantiate for loop with type: %s!",
					GloryUtil.getGloryTokenString(type)));
		}
	}

	@Override
	public String toString() {
		return String.format("%s∫%s,%s∫", GloryUtil.getLoopSymbol(type), lb, ub);
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
    
    
    public double getCost() {
        return cost;
    }
    
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    public Set<String> getRelLoops() {
		//System.out.printf("getFor %s",this.relLoops);
        return this.relLoops;
    }
    
    public Set<ForLoop> getChildren() {
        return children;
    }
    
    public void addChild(ForLoop child) {
        children.add(child);
    }
    
    public void removeChild(ForLoop child) {
        children.remove(child);
    }
    
    public ForLoop getParent() {
        return parent;
    }
    
    public void setParent(ForLoop parent) {
        this.parent = parent;
    }
    
    public int getIndexRange() {
        int lowerBound = Integer.parseInt(lb);
		int upperBound = Integer.parseInt(ub);
		return upperBound - lowerBound + 1;
    }
    
    public void setIndexRange(int indexRange) {
        this.indexRange = indexRange;
    }
    
	public void setRelLoops(Set<String> relLoops) {
		//System.out.printf("SetFor %s",relLoops);
        this.relLoops = relLoops;
    }

}
