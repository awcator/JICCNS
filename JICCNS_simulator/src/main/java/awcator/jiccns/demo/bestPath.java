package awcator.jiccns.demo;

import java.util.HashSet;

public class bestPath {
    public static int graph[][] = {
            // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11
            {-1, 5, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1}, //0
            {-1, -1, -1, 1, 1, 1, -1, -1, -1, -1, -1, -1}, //1
            {-1, -1, -1, -1, -1, -1, 1, 1, -1, -1, -1, -1}, //2
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, //3
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, -1}, //4
            {-1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1}, //5
            {-1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1}, //6
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, //7
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, //8
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2}, //9
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8}, //10
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}, //11
    };

    public static void main(String args[]) throws Exception {
        HashSet<Integer> answerNode = new HashSet<>();
        //answerNode.add(6);
        //answerNode.add(8);*/
        answerNode.add(11);
        bestPath m = new bestPath();
        System.out.println("From\ttimesofar\tprev");
        System.out.println(m.shortDistanceWithInAS(0, 0, answerNode, -1, ""));
    }

    public pathNode shortDistanceWithInAS(int from, int time_so_far, HashSet<Integer> answerNode, int previous, String chain) throws Exception {
        pathNode dummy_pathNode = new pathNode("", Integer.MAX_VALUE);
        int MIN = Integer.MAX_VALUE;
        chain += "-->" + Integer.toString(from);
        System.out.println(from + "\t" + time_so_far + "\t" + previous);
        if (answerNode.contains(from)) {
            System.out.println("Answer at " + time_so_far);
            return new pathNode(chain, time_so_far);
        } else {
            for (int i = 0; i < graph.length; i++) {
                if (graph[from][i] != -1) {
                    int dummyTime = time_so_far;
                    dummyTime += graph[from][i];
                    pathNode pk = shortDistanceWithInAS(i, dummyTime, answerNode, from, chain);
                    int k = pk.MS;
                    if (k < MIN) dummy_pathNode = pk;
                    if (k < MIN) MIN = k;
                }
            }
            return dummy_pathNode;
        }
    }

    class pathNode {
        int MS = 0;
        String Path = "";

        public pathNode(String x, int y) {
            MS = y;
            Path = x;
        }

        @Override
        public String toString() {
            return Path;
        }
    }
}
