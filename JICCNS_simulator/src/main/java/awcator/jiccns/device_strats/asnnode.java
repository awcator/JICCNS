package awcator.jiccns.device_strats;

import awcator.jiccns.cache_strats.jicnsCacheImpl;
import awcator.jiccns.nodehelpers.ASN_short_distanceFinder;
import awcator.jiccns.ui.NodeUI;
import awcator.jiccns.ui.path;

import java.util.HashSet;

public class asnnode extends jicnsDeviceImpl {
    int id = -1;
    String device_desc = "asn";
    HashSet<Integer> blockNodeList;

    public asnnode(int nodeid, int egressSize, jicnsCacheImpl strtegy) {
        id = nodeid;
        EGRESS = new int[egressSize][2];
        setCacheStrategy(strtegy);
    }

    @Override
    public String getDeviceDescription() {
        return device_desc;
    }

    @Override
    public void setDeviceDescription(String str) {
        device_desc = str;
    }

    @Override
    public jicnsCacheImpl getCacheStrategy() {
        return DEVICE_CACHE_STRATEGY;
    }

    @Override
    public void setCacheStrategy(jicnsCacheImpl my_cache_strategy) {
        DEVICE_CACHE_STRATEGY = my_cache_strategy;
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
    public void onReqOutGoingData(String... data) {
        REQUEST_FORWARDED_COUNT++;
        REQUEST_COUNT++;
        System.out.println("NODE" + getNodeID() + " Is forwarding requests to its neibhour node node" + data[0] + " with curent path " + data[1]);
    }

    @Override
    public void onRespIncomingData(NodeUI[] list, path current_path, String... data) {
        REQUEST_COUNT++;
        System.out.println("NODE" + getNodeID() + " recived as response KEY" + data[0]);
        if (getCacheStrategy().shouldICacheOrNot(data[0], data[1], list, current_path)) {
            getCacheStrategy().addToCacheMemory(data[0], data[1], false);
        }
        //data recived by the node as response to quyert
    }

    @Override
    public void onRespOutGoingData() {
        REQUEST_COUNT++;
        REQUEST_ANSWER_FORWARDED_COUNT++;
        System.out.println("Node" + getNodeID() + " Forwarding answer to Query its original requester in a path");
    }

    @Override
    public void changeDevicePowerConsumptionBy(float changeBy) {
        DEVICE_POWER_CONSUMPTION += changeBy;
    }


    @Override
    public boolean isMyNeibhour(int nodeNumber) {
        for (int i = 0; i < EGRESS.length; i++) {
            if (EGRESS[i][0] == nodeNumber) return true;
        }
        return false;
    }

    @Override
    public int getMsToReachNode(int nodeNumber, NodeUI[] list) {
        for (int i = 0; i < EGRESS.length; i++) {
            if (EGRESS[i][0] == nodeNumber) {
                //return EGRESS[i][1]; // refer egress datastructre for more info , how values  are stored
                //Eculdian Distance
                return (int) Math.sqrt(Math.pow(list[nodeNumber].getX() - list[getNodeID()].getX(), 2) + Math.pow(list[nodeNumber].getY() - list[getNodeID()].getY(), 2));
                //Manhattan Distance
                //return (int) Math.abs(list[nodeNumber].getX() - list[getNodeID()].getX()) + Math.abs(list[nodeNumber].getY() - list[getNodeID()].getY());
            }
        }
        return -1;
    }

    @Override
    public boolean allowCycles() {
        return false;
    }

    @Override
    public int getNodeID() {
        return id;
    }

    @Override
    public String getDeviceType() {
        return DEVICE_TYPE;
    }

    @Override
    public void setDeviceType(String str) {
        DEVICE_TYPE = str;
    }

    @Override
    public void onBeginSession(String... data) {
        System.out.println(getDeviceType() + " Node" + getNodeID() + " started requesing " + data[0]);
    }

    @Override
    public int getNumberOfRequestsHandled() {
        return REQUEST_COUNT;
    }

    @Override
    public int getNumberOfRequestsAnsweredBYME() {
        return REQUEST_ANSWERED_BY_ME_COUNT;
    }

    @Override
    public int getNumberOfRequestsForwarded() {
        return REQUEST_FORWARDED_COUNT;
    }

    @Override
    public boolean shouldIPassThroughthisNode(int node, String extra_pathInfo) {
        return true;
    }

    @Override
    public String getCanonicalName() {
        return getDeviceType();
    }

    @Override
    public boolean setCanonicalName(String str) {
        return false;
    }

    @Override
    public int getNumberOfRequestesAnswereForwardedCount() {
        return REQUEST_ANSWER_FORWARDED_COUNT;
    }

    @Override
    public void onRequestAnsweredByMe() {
        REQUEST_ANSWERED_BY_ME_COUNT++;
    }

    public ASN_short_distanceFinder getASN_short_distanceFinderInstance() {
        return new ASN_short_distanceFinder();
    }
}
