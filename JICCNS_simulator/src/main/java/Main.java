import awcator.jiccns.meta;
import awcator.jiccns.ui.frame;

public class Main {
    public static final String version = "JICNCS_202209";
    /**
     * public static int[][] a = {{-1, 3, 2, -1, -1, -1}, //0
     * {-1, -1, -1, -1, 2, -1}, //1
     * {-1, -1, -1, -1, -1, 1}, //2
     * {4, -1, 5, -1, -1, -1}, //3
     * {1, -1, 1, -1, -1, 8},//4
     * {-1, -1, -1, 1, -1, -1} //5
     * };
     * A simple graph i used to test the environemt
     */
    public static int ms = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Loading " + version);
        meta.blueprint_map = meta.loadBluePrint();
        frame app = new frame();
    }
}
/**
 * // TODO: 12/10/22
 * * FIFO CRP node is missing
 * <p>
 * // TODO: 11/5/22  Features
 * Save Architecture and reload option ---PRIORITY LOWEST
 * LRU NODE  PRIORITY Medium
 * Harddisk cache read write speed -MEDIUM
 * MaxEgressAwareness Node PRIORITY LOW
 * LinkedList cache/queue cache/hashmap cache PRIORITY LOW
 * FX GUI for graphs generate PRIORITY LOW
 * ML based apprch Node PRIORITY HIGH
 * Summary generator on throughput/hitsration and etc PRIORITY LOW
 * Compartor with other type of nodes PRIORITY LOW
 * <p>
 * __________________________________________________________________________
 * <p>
 * // TODO: 11/5/22  KNOWN BUGS TO BE FIX
 * A     B
 * \   /
 * c    Implimentation BUG: if data sent by A and B reaches at C at same time what should be done? curreny two broadcast msgs from c to node D (Inefficent)
 * |
 * D
 * <p>
 * Notedown average time requred to process the query and grph it
 * Total network packets moved
 * <p>
 * // TODO: 11/6/22 Algorithm validation
 * run 2 times with unkown data query
 * query from all node with random query
 */

