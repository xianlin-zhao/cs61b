import javax.swing.text.StyledEditorKit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    private static Long source;
    private static Long destination;
    private static GraphDB graph;

    public static class SearchNode implements Comparable<SearchNode> {
        public Long id;
        public SearchNode before;
        public double distance;
        public double priority;

        public SearchNode (Long id, SearchNode before, double fromS) {
            this.id = id;
            this.before = before;
            this.distance = fromS;
            this.priority = fromS + graph.distance(id, destination);
        }
        @Override
        public int compareTo(SearchNode o) {
            if (this.priority < o.priority) {
                return -1;
            }
            if (this.priority > o.priority) {
                return 1;
            }
            return 0;
        }
    }

    private static double distanceToDest(Long id) {
        GraphDB.Node v = graph.vertices.get(id);
        GraphDB.Node dest = graph.vertices.get(destination);
        return GraphDB.distance(v.getLon(), v.getLat(), dest.getLon(), dest.getLat());
    }

    private static double distance(GraphDB g, Long id1, Long id2) {
        GraphDB.Node v1 = g.vertices.get(id1);
        GraphDB.Node v2 = g.vertices.get(id2);
        return GraphDB.distance(v1.getLon(), v1.getLat(), v2.getLon(), v2.getLat());
    }

    private static boolean isGoal(SearchNode v) {
        return distanceToDest(v.id) == 0;
    }
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        graph = g;
        source = graph.closest(stlon, stlat);
        destination = graph.closest(destlon, destlat);
        Map<Long, Boolean> marked = new HashMap<>();

        PriorityQueue<SearchNode> pq = new PriorityQueue<>();
        pq.offer(new SearchNode(source, null, 0));
        while (!pq.isEmpty() && !isGoal(pq.peek())) {
            SearchNode now = pq.poll();
            marked.put(now.id, true);
            for (Long adj : graph.adjacent(now.id)) {
                if (!marked.containsKey(adj) || marked.get(adj) == false) {
                    pq.offer(new SearchNode(adj, now, now.distance + distance(graph, adj, now.id)));
                }
            }
        }

        SearchNode track = pq.peek();
        List<Long> ans = new ArrayList<>();
        while (track != null) {
            ans.add(track.id);
            track = track.before;
        }
        Collections.reverse(ans);
        return ans;
    }

    private static ArrayList<GraphDB.Edge> routeToEdge(GraphDB g, List<Long> route) {
        ArrayList<GraphDB.Edge> ans = new ArrayList<>();
        for (int i = 1; i < route.size(); i++) {
            Long nowNode = route.get(i - 1);
            Long aftNode = route.get(i);
            for (GraphDB.Edge e : g.adjEdge.get(nowNode)) {
                Long v = e.getV();
                Long w = e.getW();
                if (v.equals(aftNode) || w.equals(aftNode)) {
                    ans.add(e);
                    break;
                }
            }
        }
        return ans;
    }

    private static int calcDirection(double pre, double now) {
        double relative = now - pre;
        double absRelative = Math.abs(relative);
        if (absRelative > 180) {
            absRelative = 360 - absRelative;
            relative *= -1;
        }
        if (absRelative <= 15) {
            return NavigationDirection.STRAIGHT;
        }
        if (absRelative <= 30) {
            return relative < 0 ? NavigationDirection.SLIGHT_LEFT : NavigationDirection.SLIGHT_RIGHT;
        }
        if (absRelative <= 100) {
            return relative < 0 ? NavigationDirection.LEFT : NavigationDirection.RIGHT;
        }
        return relative < 0 ? NavigationDirection.SHARP_LEFT : NavigationDirection.SHARP_RIGHT;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> ans = new ArrayList<>();
        int direction = NavigationDirection.START;
        String name = null;
        double distance = 0;

        ArrayList<GraphDB.Edge> edges = routeToEdge(g, route);
        if (edges.size() == 1) {
            name = edges.get(0).getName();
            distance = edges.get(0).getWeight();
            ans.add(new NavigationDirection(direction, name, distance));
            return ans;
        }
        for (int i = 1; i < edges.size(); i++) {
            GraphDB.Edge preEdge = edges.get(i - 1);
            GraphDB.Edge aftEdge = edges.get(i);
            Long preNode = route.get(i - 1);
            Long nowNode = route.get(i);
            Long aftNode = route.get(i + 1);

            String preName = preEdge.getName();
            String aftName = aftEdge.getName();

            distance += preEdge.getWeight();
            if (!preName.equals(aftName)) {
                double preBearing = g.bearing(preNode, nowNode);
                double aftBearing = g.bearing(nowNode, aftNode);
                ans.add(new NavigationDirection(direction, preName, distance));
                distance = 0;
                direction = calcDirection(preBearing, aftBearing);
            }
            if (i == edges.size() - 1) {
                distance += aftEdge.getWeight();
                ans.add(new NavigationDirection(direction, aftName, distance));
            }
        }
        return ans;
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public NavigationDirection(int dir, String name, double dis) {
            direction = dir;
            way = name;
            distance = dis;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
