import java.util.*;

public class Forest {
    
    private Set<LER.Loop> nodes;
    
    public Forest() {
        nodes = new HashSet<>();
    }
    
    public void addNode(LER.Loop node) {
        nodes.add(node);
    }
    
    public void output() {
        for (LER.Loop node : nodes) {
            System.out.printf("Forest:");
            System.out.println(((ForLoop) node).getIter());
        }
    }
}
