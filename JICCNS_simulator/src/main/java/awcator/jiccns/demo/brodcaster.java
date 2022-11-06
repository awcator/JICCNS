package awcator.jiccns.demo;

import java.util.Comparator;
import java.util.PriorityQueue;

public class brodcaster {
    /**
     * This test program is itended to demonste the broadcasting messages
     */

    public static void main(String[] args) throws Exception {
        /**
         int[][] a = {
         //{0,1, 2, 3, 4, 5}
         {-1, 2, 3, 4, 5, 7}, //0   -1 impies there is no path for node 0 from node 0. a[0][5]=7 ==> it takes 7 ms to reach node 5 from node 0
         {4, -1, 5, 6, 2, -1}, //1
         {3, 8, -1, -1, -1, 1}, //2
         {4, -1, 5, -1, -1, -1}, //3
         {1, 2, 1, -1, -1, 8},//4
         {1, 3, 5, 1, -1, -1} //5
         };
         */
        int[][] a = {
                //{0,1, 2, 3, 4, 5}
                {-1, 5, 1, -1}, //0   -1 impies there is no path for node 0 from node 0. a[0][5]=7 ==> it takes 7 ms to reach node 5 from node 0
                {-1, -1, -1, 1}, //1
                {-1, 7, -1, 3}, //2
                {1, -1, -1, -1} //3
        };
        int endNode = 0;
        int sourceNode = 3;
        path rootparent = new path("node" + sourceNode, 0, null, sourceNode, endNode, null, true); //start from node0 with inital timeout of 0ms
        class path_sorter implements Comparator<path> {
            @Override
            public int compare(path s1, path s2) {
                if (s1.ms > s2.ms) return 1;
                else if (s1.ms < s2.ms) return -1;
                return 0;
            }
        }
        PriorityQueue<path> pq = new PriorityQueue<path>(new path_sorter());
        path temppath = rootparent;
        pq.add(temppath);

        int previous_ms = temppath.ms;
        boolean expectNewMs = false;
        boolean check_previous_ms = true;
        while (!pq.isEmpty()) {
            //To see how fast the queues grows/ or the edges grows n(n-1) edges can be drawn using n vertex
            //System.out.println("SiZE "+pq.size());
            //System.out.println(pq);
            temppath = pq.poll();
            if (check_previous_ms) {
                if (temppath.ms == previous_ms) {
                    //dontsleep
                } else {
                    //Thread.sleep(2000);
                    previous_ms = temppath.ms;
                }
            }
            //prints time digaram
            System.out.print("\n" + temppath.ms + " " + temppath);
            int foucusedNode = temppath.focusedNode;
            if (foucusedNode == temppath.destinationNode) {
                if (temppath.forward == true) {
                    path newpath = new path("node" + temppath.focusedNode, temppath.ms, null, temppath.focusedNode, sourceNode, temppath.pa, false);
                    pq.add(newpath);
                    System.out.print("[FEND]");
                    continue;
                } else {
                    System.out.print("[BEND]");
                    continue;
                }
            } else {
                if (temppath.forward == true) {
                    //Broadcast to neibhour nodes
                    for (int i = 0; i < a.length; i++) {
                        if (i != foucusedNode && a[foucusedNode][i] != -1 && !temppath.pa.contains("node" + i)) {
                            path newpath = new path(temppath.pa + "-->node" + i, temppath.ms + a[foucusedNode][i], temppath, i, temppath.parent == null ? endNode : temppath.parent.destinationNode, null, true);
                            pq.add(newpath);
                        }
                    }
                } else {
                    //use backtrak return back
                    String PATH = temppath.backtrack;
                    String newPATHwithoutLastNode = PATH.substring(0, PATH.lastIndexOf("-->") == -1 ? 0 : PATH.lastIndexOf("-->"));
                    String last_node_onPATH = newPATHwithoutLastNode.substring(newPATHwithoutLastNode.lastIndexOf("-->") == -1 ? 0 : newPATHwithoutLastNode.lastIndexOf("-->") + 3);
                    int nodeIDfromNode = Integer.parseInt(last_node_onPATH.replace("node", ""));
                    path newpath = new path(temppath.pa + "-->node" + nodeIDfromNode, temppath.ms + a[nodeIDfromNode][foucusedNode], temppath, nodeIDfromNode, sourceNode, newPATHwithoutLastNode, false);
                    pq.add(newpath);
                }
            }
            if (check_previous_ms) {
                previous_ms = temppath.ms;
            }
        }
    }

    static class path {
        public String pa;
        public path parent;
        public int ms;
        public int focusedNode = 0;

        public int destinationNode = -1;
        boolean forward = true;
        String backtrack = "";

        public path(String p, int mss, path par, int fc, int des, String tracePath, boolean isForward) {
            pa = p;
            ms = mss;
            parent = par;
            focusedNode = fc;
            destinationNode = des;
            backtrack = tracePath;
            forward = isForward;
        }

        @Override
        public String toString() {
            return pa;
        }
    }
}
