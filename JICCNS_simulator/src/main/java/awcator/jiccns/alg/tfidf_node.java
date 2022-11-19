package awcator.jiccns.alg;

import java.util.Arrays;

/**
 * Node Summary:
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 * <p>
 * CacheStrategy: Nope
 */

public class tfidf_node extends jicnsNodeImpl {
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

    public tfidf_node(int nodeid, int egressSize) {
        id = nodeid;
        egress = new int[egressSize][2];
    }

    @Override
    public void onIncomingReqData() {
        //System.out.println("Packet Reached Node" + getNodeID());
    }

    @Override
    public String cacheLookUp(String queryKey) {
        for (int i = 0; i < localcache_seekPointer && i < cacheMemorySize; i++) {
            if (cacheMemory[i][0].equalsIgnoreCase(queryKey)) {
                onCacheHit();
                changePowerConsumptionBy(i + 1);
                return cacheMemory[i][1];
            }
        }
        onCacheMiss();
        changePowerConsumptionBy(localcache_seekPointer);
        return null;
    }

    @Override
    public String hddLookUp(String query_key) {
        for (int i = 0; i < localMemory_seekPointer && i < getMaxLocalPayloadSize(); i++) {
            if (localMemory[i][0].equalsIgnoreCase(query_key)) {
                onHDDHit();
                changePowerConsumptionBy(i + 1);
                return localMemory[i][1];
            }
        }
        changePowerConsumptionBy(localMemory_seekPointer);
        onHDDMiss();
        return null;
    }

    @Override
    public void shouldICacheOrNot() {

    }

    @Override
    public void onAddedToCache() {
        changePowerConsumptionBy(1);
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
        cache_hits = cache_hits + 1;
    }

    @Override
    public void onHDDHit() {
        hdd_hits++;
    }

    @Override
    public void onCacheMiss() {
        cache_misses = cache_misses + 1;
    }

    @Override
    public void onHDDMiss() {
        hdd_misses++;
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
    public boolean addToCacheMemory(String key, String value) {
        System.out.println("Addin to cahce " + key + " " + value + "  " + getMaxLocaCacheSize());
        try {
            cacheMemory[localcache_seekPointer][0] = key;
            cacheMemory[localcache_seekPointer][1] = value;
            localcache_seekPointer++;
            onAddedToCache();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
            if (egress[i][0] == nodeNumber)
                return egress[i][1]; // refer egress datastructre for more info , how values  are stored
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
    public String nodeType() {
        return "SimpleNode";
    }
}
