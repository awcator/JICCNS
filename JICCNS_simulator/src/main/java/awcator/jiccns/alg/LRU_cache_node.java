package awcator.jiccns.alg;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Node Summary:
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 * <p>
 * CacheStrategy: LRU
 * Cache StorageType: Queue
 */

public class LRU_cache_node extends jicnsNodeImpl {
    /**
     * This varible contains NodeServer's localMemory contents
     * In Reality: This represent Nodes HardDisk
     */
    String[][] localMemory;
    /***
     * This varible contains NodeServer's InMemory cache contents
     * In Reality: This will the superfast access memory type which is RAM.
     */
    Queue<queueImplemtion> cacheMemory;
    int id = 0;
    private int localMemory_seekPointer = 0;
    private int localcache_seekPointer = 0;

    public LRU_cache_node(int nodeid, int egressSize) {
        id = nodeid;
        egress = new int[egressSize][2];
    }

    @Override
    public void onIncomingReqData() {
        //System.out.println("Packet Reached Node" + getNodeID());
    }

    @Override
    public String cacheLookUp(String queryKey) {
        int i = 0;
        for (queueImplemtion keyValue : cacheMemory) {
            if (keyValue.key.equalsIgnoreCase(queryKey)) {
                onCacheHit();
                changePowerConsumptionBy(i + 1);
            }
            i++;
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
        //System.out.println("node"+ getNodeID()+" : Cache Hit");
    }

    @Override
    public void onHDDHit() {
        System.out.println("HDD HIT");
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
    public boolean addToPayloadMemory(String key, String value) {
        if (localcache_seekPointer < getMaxLocalPayloadSize()) {
            localMemory[localMemory_seekPointer][0] = key;
            localMemory[localMemory_seekPointer][1] = value;
            localMemory_seekPointer++;
            changePowerConsumptionBy(1);
            return true;
        }
        changePowerConsumptionBy(1);
        return false;
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
        try {
            System.out.println("Addin to cahce " + key + " " + value + "  " + getMaxLocaCacheSize());
            cacheMemory.add(new queueImplemtion(key, value));
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
        cacheMemory = new LinkedList<>();
    }

    @Override
    public int getMaxLocaCacheSize() {
        return cacheMemorySize;
    }

    @Override
    public String[][] getCacheContents() {
        String x[][] = new String[cacheMemory.size()][2];
        int i = 0;
        for (queueImplemtion kv : cacheMemory) {
            x[i][0] = kv.key;
            x[i][1] = kv.value;
            i++;
        }
        return x;
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
        return "LRU_cache_node";
    }

    class queueImplemtion {
        String key;
        String value;

        public queueImplemtion(String k, String v) {
            key = k;
            value = v;
        }

        @Override
        public String toString() {
            return key + ":" + value;
        }
    }
}
