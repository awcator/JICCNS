package awcator.jiccns.device_strats;

import awcator.jiccns.cache_strats.jicnsCacheImpl;
import awcator.jiccns.ui.NodeUI;
import awcator.jiccns.ui.path;

import java.util.HashSet;

public class tfidf_asnnode extends asnnode {
    String CANON_NAME = "TF_IDF_ASN_NODE";

    public tfidf_asnnode(int nodeid, int egressSize, jicnsCacheImpl strtegy) {
        super(nodeid, egressSize, strtegy);
    }

    @Override
    public void onIncomingReqData(String data, NodeUI[] list, path path_so_far) {
        REQUEST_COUNT++;
        blockNodeList = null;
        System.gc();
        blockNodeList = new HashSet<>();
        blockNodeList.add(getNodeID());
        /**
         * Always self block yourself, so that packet wont rerotue by you. incomingPackier-->ASNA node--(whom should i forward?)--Asn B,ASN C,ASN D, execpt ASN-A. becuse i'm ASnA
         */

        // TODO: 12/17/22 ASN communications  to decide best path
        //System.out.println("Packet Reached Node" + getNodeID());
        //System.out.println("Recived query_answer from other nodes" + data);
    }

    @Override
    public String getCanonicalName() {
        return CANON_NAME;
    }

    @Override
    public boolean setCanonicalName(String str) {
        CANON_NAME = str;
        return true;
    }
}
