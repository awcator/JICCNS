package awcator.jiccns.device_strats;

import awcator.jiccns.cache_strats.jicnsCacheImpl;
import awcator.jiccns.cache_strats.tfidfCRP;
import awcator.jiccns.ui.NodeUI;
import awcator.jiccns.ui.path;

public class tfidf_asnnode extends asnnode {
    String CANON_NAME = "TF_IDF_ASN_NODE";

    public tfidf_asnnode(int nodeid, int egressSize, jicnsCacheImpl strtegy) {
        super(nodeid, egressSize, strtegy);
    }

    @Override
    public void onIncomingReqData(String data, NodeUI[] list, path path_so_far) {
        if (getCacheStrategy().getCacheType().equalsIgnoreCase("tfidfCRP")) {
            tfidfCRP crp = (tfidfCRP) getCacheStrategy();
            crp.requestHistory += data + " ";
        }
        super.onIncomingReqData(data, list, path_so_far);
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

    @Override
    public void onBeginSession(String... data) {
        if (getCacheStrategy().getCacheType().equalsIgnoreCase("tfidfCRP")) {
            tfidfCRP crp = (tfidfCRP) getCacheStrategy();
            crp.requestHistory += data + " ";
        }
        super.onBeginSession(data);
    }
}
