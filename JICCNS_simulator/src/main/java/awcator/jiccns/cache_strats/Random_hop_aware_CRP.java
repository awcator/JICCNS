package awcator.jiccns.cache_strats;

import awcator.jiccns.ui.NodeUI;
import awcator.jiccns.ui.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Node Summary: Random Cache Replacemnt Policy Node
 * <p>
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 * Replacemnt Type: Random
 * <p>
 * CacheStrategy: Random CRP, but it checks which nodes is best suited for cahcing insted of caching in all
 * based on closest to the source but it has the max egress length in the path
 * Extra Memoty: yes to store HopCount and Path
 */

public class Random_hop_aware_CRP extends RandomCRP {

    public Random_hop_aware_CRP(int nodeid) {
        super(nodeid);
    }

    @Override
    public boolean shouldICacheOrNot(String key, String value, NodeUI[] list, path current_path) {
        System.out.println(current_path.pa);
        System.out.println(current_path.backtrack);
        /*String s1=current_path.backtrack;
        String s2=current_path.pa;*/
        String s1 = current_path.backtrack;
        String s2 = current_path.pa;
        List<String> l1 = Arrays.asList(s1.replace("node", "").split("-->"));
        List<String> l2 = Arrays.asList(s2.replace("node", "").split("-->"));
        Collections.reverse(l2);
        List<String> newList = new ArrayList<String>(l1);
        newList.addAll(l2);
        int MAX_EGRESS = Integer.MIN_VALUE;
        int pos = -1;
        // Caluate the node that has most outgoings and closer to the source, except the  node that gave answer, so substract 1 from totalSize
        for (int i = 1; i < newList.size() - 1; i++) {
            String str = newList.get(i);
            int length = list[Integer.parseInt(str)].jicnsNode.EGRESS.length;
            if (length > MAX_EGRESS) {
                MAX_EGRESS = length;
                pos = Integer.parseInt(str);
            }
        }
        //if current node has most egress and closer to the source then we have to cache it
        if (pos == getNodeID()) {
            return super.shouldICacheOrNot(key, value, list, current_path);
        }
        return false;
    }

    @Override
    public String getCacheType() {
        return "Random_hop_aware_CRP";
    }
}
