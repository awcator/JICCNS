package awcator.jiccns.device_strats;

import awcator.jiccns.cache_strats.jicnsCacheImpl;

public class gpnode extends jicnsDeviceImpl {
    int id = 0;
    String device_desc="awcatornode";
    public gpnode(int nodeid, int egressSize, jicnsCacheImpl strtegy) {
        id = nodeid;
        EGRESS = new int[egressSize][2];
        setCacheStrategy(strtegy);
    }

    @Override
    public void setDeviceDescription(String str) {
        device_desc=str;
    }

    @Override
    public String getDeviceDescription() {
        return device_desc;
    }

    @Override
    public String getNodeDomain() {
        return nodedomain;
    }

    @Override
    public void setNodeDomain(String domain) {
        nodedomain = domain;
    }

    @Override
    public jicnsCacheImpl getCacheStrategy() {
        return nodechacestrtegy;
    }

    @Override
    public void setCacheStrategy(jicnsCacheImpl my_cache_strategy) {
        nodechacestrtegy = my_cache_strategy;
    }

    @Override
    public void onIncomingReqData(String data) {
        //System.out.println("Packet Reached Node" + getNodeID());
        //System.out.println("Recived query_answer from other nodes" + data);
        REQUEST_COUNT++;
    }

    @Override
    public void onReqOutGoingData(String... data) {
        REQUEST_FORWARDED_COUNT++;
        REQUEST_COUNT++;
        System.out.println("NODE" + getNodeID() + " Is forwarding requests to its neibhour node node" + data[0] + " with curent path " + data[1]);
    }

    @Override
    public void onRespIncomingData(String... data) {
        REQUEST_COUNT++;
        System.out.println("NODE" + getNodeID() + " recived as response KEY" + data[0]);
        if (getCacheStrategy().shouldICacheOrNot(data[0], data[1])) {
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
    public int getMsToReachNode(int nodeNumber) {
        for (int i = 0; i < EGRESS.length; i++) {
            if (EGRESS[i][0] == nodeNumber)
                return EGRESS[i][1]; // refer egress datastructre for more info , how values  are stored
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
        return "Consumer";
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
    public int getNumberOfRequestesAnswereForwardedCount() {
        return REQUEST_ANSWER_FORWARDED_COUNT;
    }

    @Override
    public void onRequestAnsweredByMe() {
        REQUEST_ANSWERED_BY_ME_COUNT++;
    }
}
