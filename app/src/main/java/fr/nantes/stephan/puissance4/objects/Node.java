package fr.nantes.stephan.puissance4.objects;

import java.util.ArrayList;

/**
 * Created by Ugho on 24/03/2014.
 */
public class Node {

    private int col;
    private boolean root = false;
    private int estimation = 0;
    private int depth = 0;
    private Node parent;
    private ArrayList<Node> myNodes = new ArrayList<>();

    public Node() {
        //empty constructor
    }

    public Node(int col, int deph, Node parent) {
        this.col = col;
        this.depth = deph;
        this.parent = parent;
    }

    public int getEstimation() {
        return estimation;
    }

    public void setEstimation(int estimation) {
        this.estimation = estimation;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public ArrayList<Node> getMyNodes() {
        return myNodes;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }
}
