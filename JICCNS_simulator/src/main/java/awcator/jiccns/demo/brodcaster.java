package awcator.jiccns.demo;

import java.util.Comparator;
import java.util.PriorityQueue;

public class brodcaster {
    /**
     * This test program is itended to demonste the broadcasting messages
     */

    public static void main(String[] args)throws Exception {
        int[][] a = {
                //{0,1, 2, 3, 4, 5}
                {-1, 2, 3, 4, 5, 7}, //0   -1 impies there is no path for node 0 from node 0. a[0][5]=7 ==> it takes 7 ms to reach node 5 from node 0
                {4, -1, 5, 6, 2, -1}, //1
                {3, 8, -1, -1, -1, 1}, //2
                {4, -1, 5, -1, -1, -1}, //3
                {1, 2, 1, -1, -1, 8},//4
                {1, 3, 5, 1, -1, -1} //5
        };

        path rootparent = new path("node4", 0, null, 4); //start from node4 with inital timeout of 4ms
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

        int previous_ms=temppath.ms;
        boolean expectNewMs=false;
        boolean check_previous_ms=true;
        while (!pq.isEmpty()) {
            //To see how fast the queues grows/ or the edges grows n(n-1) edges can be drawn using n vertex
            //System.out.println("SiZE "+pq.size());

            temppath = pq.poll();
            if(check_previous_ms ){
                if(temppath.ms==previous_ms){
                    //dontsleep
                }
                else {
                    //Thread.sleep(2000);
                    System.out.println();
                    previous_ms= temppath.ms;
                }
            }
            //prints time digaram
            System.out.println(temppath.ms + " " + temppath);
            int foucusedNode = temppath.focusedNode;
            if (foucusedNode == 0) {
                System.out.print(" -----END---\n");
                continue;
            } else {
                for (int i = 0; i < a.length; i++) {
                    if (i != foucusedNode && a[foucusedNode][i] != -1 && !temppath.pa.contains("node"+i)) {
                        path newpath = new path(temppath.pa + "-->node" + i, temppath.ms + a[foucusedNode][i], temppath, i);
                        pq.add(newpath);
                    }
                }
            }
            if(check_previous_ms){
                previous_ms= temppath.ms;
            }
        }
    }

    static class path {
        public String pa;
        public path parent;
        public int ms;
        public int focusedNode = 0;

        public path(String p, int mss, path par, int fc) {
            pa = p;
            ms = mss;
            parent = par;
            focusedNode = fc;
        }

        @Override
        public String toString() {
            return pa;
        }
    }
}
