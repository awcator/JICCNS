import awcator.jiccns.meta;
import awcator.jiccns.ui.frame;

public class Main {
    public static final String version = "JICNCS_202209";
    /**
     * public static int[][] a = {{-1, 3, 2, -1, -1, -1}, //0
     *             {-1, -1, -1, -1, 2, -1}, //1
     *             {-1, -1, -1, -1, -1, 1}, //2
     *             {4, -1, 5, -1, -1, -1}, //3
     *             {1, -1, 1, -1, -1, 8},//4
     *             {-1, -1, -1, 1, -1, -1} //5
     *     };
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
 * // TODO: 11/5/22  Features
 * Save Architecture and reload option
 * LRU NODE
 * MaxEgressAwareness Node
 * LinkedList cache/queue cache/hashmap cache
 * FX GUI for graphs generate
 * ML based apprchNode
 * Summary generator on throughput/hitsration and etc
 * Compartor with other type of nodes
 *
 * __________________________________________________________________________
 *
 *  // TODO: 11/5/22  KNOWN BUGS TO BE FIX
 *
 *  A     B
 *   \   /
 *     c    Implimentation BUG: if data sent by A and B reaches at C at same time what should be done? curreny two broadcast msgs from c to node D (Inefficent)
 *     |
 *     D
 *
 *  Currently brodcast is happening from source node 3 to destiantion node 5 (HARDCODED) need to be fixed
 */
