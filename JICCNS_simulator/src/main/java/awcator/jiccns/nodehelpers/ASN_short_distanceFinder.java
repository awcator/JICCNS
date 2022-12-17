package awcator.jiccns.nodehelpers;

import awcator.jiccns.exceptions.notAsnException;
import awcator.jiccns.ui.NodeUI;

import java.util.Arrays;

public class ASN_short_distanceFinder {
    boolean shorterstDistanceFound = false;
    int shorterstDistance = Integer.MAX_VALUE;

    /**
     * @param from        node ID
     * @param time_so_far timeMS
     * @param key         key_to be searched
     * @param previous    previous/current node
     * @param chain       Chain used to get the soltuion
     * @param nodes       nodes[] to input the graph
     * @return
     */
    private shortestPathFromASN getShortDistanceWithInAS(int from, int time_so_far, String key, int previous, String chain, NodeUI[] nodes) {
        /**
         * Dont loop between ASN and nodes if they have cycle paths
         */
        shortestPathFromASN dummy_pathNode = new shortestPathFromASN("", Integer.MAX_VALUE);
        //if (chain.contains("-->" + from)) return dummy_pathNode;
        if (Arrays.asList(chain.split("-->")).contains(Integer.toString(from))) return dummy_pathNode;
        int MIN = Integer.MAX_VALUE;
        chain += "-->" + from;
        System.out.println(from + "\t" + time_so_far + "\t" + previous);
        String Value = nodes[from].jicnsNode.getCacheStrategy().cacheLookUp(key, true);
        if (Value == null) {
            Value = nodes[from].jicnsNode.getCacheStrategy().hddLookUp(key, true);
        }
        if (Value != null) {
            System.out.println("Packet can be answerable by node" + from + ", Chain SoFar" + chain + "TimeSoFar" + time_so_far + " value=" + Value + "\nCheckign if more short path available or not");
            shorterstDistanceFound = true;
            shorterstDistance = Math.min(shorterstDistance, time_so_far);
            return new shortestPathFromASN(chain, time_so_far);
        } else {
            for (int count = 0; count < nodes[from].jicnsNode.EGRESS.length; count++) {
                int temp = nodes[from].jicnsNode.EGRESS[count][0];
                NodeUI asn_neibhour_ui = nodes[temp];
                int dummyTime = time_so_far;

                dummyTime += nodes[from].jicnsNode.getMsToReachNode(asn_neibhour_ui.jicnsNode.getNodeID(), nodes);
                if (dummyTime > shorterstDistance && shorterstDistanceFound) {
                    System.out.println("Skipping this path..As a better path with a known solution alredy known by ASN; PATH=" + chain);
                    return dummy_pathNode;//max time
                }
                shortestPathFromASN pk = getShortDistanceWithInAS(temp, dummyTime, key, from, chain, nodes);
                int k = pk.MS;
                if (k < MIN) dummy_pathNode = pk;
                if (k < MIN) {
                    MIN = k;
                    shorterstDistance = MIN;
                }
            }
            return dummy_pathNode;
        }
    }

    public shortestPathFromASN getShortPath(int from, int time_so_far, String key, int previous, String chain, NodeUI[] nodes) throws notAsnException {
        shorterstDistanceFound = false;
        shorterstDistance = Integer.MAX_VALUE;
        if (nodes[from].jicnsNode.getDeviceType().equalsIgnoreCase("ASN")) {
            return getShortDistanceWithInAS(from, time_so_far, key, previous, chain, nodes);
        } else {
            throw new notAsnException();
        }
    }

}

