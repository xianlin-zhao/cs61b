package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.List;

public class Solver {
    private WorldState iState;
    private int ans;
    private List<WorldState> seq;

    public Solver(WorldState initial) {
        iState = initial;
        ans = 0;
        seq = new ArrayList<WorldState>();
        SearchNode initNode = new SearchNode(initial, 0, null);
        MinPQ<SearchNode> pq = new MinPQ<>();
        pq.insert(initNode);
        while (!pq.isEmpty()) {
            SearchNode now = pq.delMin();
            if (now.getState().isGoal()) {
                ans = now.getMove();
                Stack<WorldState> stk = new Stack<>();
                while (now != null) {
                    stk.push(now.getState());
                    now = now.getPrevNode();
                }
                while (!stk.isEmpty()) {
                    seq.add(stk.peek());
                    stk.pop();
                }
                break;
            } else {
                for (WorldState st : now.getState().neighbors()) {
                    if (now.getPrevNode() != null && st.equals(now.getPrevNode().getState())) {
                        continue;
                    }
                    SearchNode tmp = new SearchNode(st, now.getMove() + 1, now);
                    pq.insert(tmp);
                }
            }
        }
    }

    public int moves() {
        return ans;
    }

    public Iterable<WorldState> solution() {
        return seq;
    }
}
