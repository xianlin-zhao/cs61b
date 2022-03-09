package hw4.puzzle;

public class SearchNode implements Comparable<SearchNode> {
    private WorldState state;
    private int move;
    private SearchNode prevNode;
    public int priority;

    public SearchNode(WorldState ws, int m, SearchNode node) {
        state = ws;
        move = m;
        prevNode = node;
        priority = move + ws.estimatedDistanceToGoal();
    }

    public int compareTo(SearchNode s) {
        return this.priority - s.priority;
    }

    public WorldState getState() {
        return state;
    }

    public int getMove() {
        return move;
    }

    public SearchNode getPrevNode() {
        return prevNode;
    }
}
