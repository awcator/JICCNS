package awcator.jiccns.alg;

import java.util.Arrays;

/**
 * Node Summary:
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 *
 * CacheStrategy: Nope
 */

public class SimpleNode extends jicnsNodeImpl {
    /**
     * This varible contains NodeServer's localMemory contents
     * In Reality: This represent Nodes HardDisk
     */
    String[][] localMemory;
    /***
     * This varible contains NodeServer's InMemory cache contents
     * In Reality: This will the superfast access memory type which is RAM.
     */
    String[][] cacheMemory;
    int id = 0;
    private int localMemory_seekPointer = 0;
    private int localcache_seekPointer = 0;

    public SimpleNode(int nodeid, int egressSize) {
        id = nodeid;
        egress = new int[egressSize][2];
    }

    @Override
    public void onIncomingReqData() {
        //System.out.println("Reached Node" + getNodeID());
    }

    @Override
    public void cacheLookUp() {

    }

    @Override
    public void shouldICacheOrNot() {

    }

    @Override
    public void onAddedToCache() {

    }

    @Override
    public void onRemovedFromCache() {

    }

    @Override
    public void onReqOutGoingData() {

    }

    @Override
    public void onRespIncomingData() {

    }

    @Override
    public void onRespOutGoingData() {

    }

    @Override
    public void onCacheHit() {
        hits = hits + 1;
        System.out.println("fcuk youcxcxzcx");
    }

    @Override
    public void onCacheMiss() {
        misses = misses + 1;
    }

    @Override
    public void addToPayloadMemory(String key, String value) {
        localMemory[localMemory_seekPointer][0] = key;
        localMemory[localMemory_seekPointer][1] = value;
        localMemory_seekPointer++;
        changePowerConsumptionBy(1);
    }

    @Override
    public void allocatePayloadMemorySize() {
        localMemory = new String[getMaxLocalPayloadSize()][2];
    }

    @Override
    public int getMaxLocalPayloadSize() {
        return LocalPayloadSize;
    }

    @Override
    public void changePowerConsumptionBy(float changeBy) {
        powerConsumption += changeBy;
    }

    @Override
    public String[][] getPayloadContents() {
        return (localMemory == null) ? null : Arrays.copyOfRange(localMemory, 0, localMemory_seekPointer);
    }

    @Override
    public void addToCacheMemory(String key, String value) {
        System.out.println("Addin to cahce " + key + " " + value + "  " + getMaxLocaCacheSize());
        cacheMemory[localcache_seekPointer][0] = key;
        cacheMemory[localcache_seekPointer][1] = value;
        localcache_seekPointer++;
        changePowerConsumptionBy(1);
    }

    @Override
    public void allocateCacheMemorySize() {
        cacheMemory = new String[getMaxLocaCacheSize()][2];
    }

    @Override
    public int getMaxLocaCacheSize() {
        return cacheMemorySize;
    }

    @Override
    public String[][] getCacheContents() {
        return (cacheMemory == null) ? null : Arrays.copyOfRange(cacheMemory, 0, localcache_seekPointer);
    }

    @Override
    public boolean isMyNeibhour(int nodeNumber) {
        for (int i = 0; i < egress.length; i++) {
            if (egress[i][0] == nodeNumber) return true;
        }
        return false;
    }

    @Override
    public int getMsToReachNode(int nodeNumber) {
        for (int i = 0; i < egress.length; i++) {
            if (egress[i][0] == nodeNumber) return egress[i][1]; // refer egress datastructre for more info , how values  are stored
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
}
