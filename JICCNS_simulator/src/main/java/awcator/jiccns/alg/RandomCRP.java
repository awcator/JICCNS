package awcator.jiccns.alg;

import java.util.Arrays;
import java.util.Random;

/**
 * Node Summary: Random Cache Replacemnt Policy Node
 * <p>
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 * Replacemnt Type: Random
 * <p>
 * CacheStrategy: Random CRP
 * Extra Memoty: Nope
 */

public class RandomCRP extends jicnsNodeImpl {
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

    public RandomCRP(int nodeid, int egressSize) {
        id = nodeid;
        EGRESS = new int[egressSize][2];
    }

    @Override
    public void onIncomingReqData(String data) {
        REQUEST_COUNT++;
        //System.out.println("Packet Reached Node" + getNodeID());
        //System.out.println("Recived query_answer from other nodes" + data);
    }

    @Override
    public String cacheLookUp(String queryKey, boolean immunity_power_consumption) {
        if (immunity_power_consumption == false) CACHE_LOOKUP_COUNT++;
        //System.out.println("NODE"+getNodeID()+" will lookup "+queryKey);
        for (int i = 0; i < localcache_seekPointer && i < CACHE_MEMORY_SIZE; i++) {
            if (cacheMemory[i][0].equalsIgnoreCase(queryKey)) {
                if (immunity_power_consumption == false) {
                    onCacheHit();
                    changePowerConsumptionBy(i + 1);
                }
                return cacheMemory[i][1];
            }
        }

        if (immunity_power_consumption == false) {
            changePowerConsumptionBy(localcache_seekPointer);
            onCacheMiss();
        }
        return null;
    }

    @Override
    public String hddLookUp(String query_key, boolean immunity_power_consumption) {
        if (immunity_power_consumption == false) HDD_LOOKUP_COUNT++;
        for (int i = 0; i < localMemory_seekPointer && i < getMaxLocalPayloadSize(); i++) {
            if (localMemory[i][0].equalsIgnoreCase(query_key)) {
                if (immunity_power_consumption == false) {
                    onHDDHit();
                    changePowerConsumptionBy(i + 1);
                }
                return localMemory[i][1];
            }
        }
        if (immunity_power_consumption == false) {
            changePowerConsumptionBy(localMemory_seekPointer);
            onHDDMiss();
        }
        return null;
    }

    @Override
    public boolean shouldICacheOrNot(String key, String value) {
        String haveICachedBefore = cacheLookUp(key, true);
        if (haveICachedBefore == null) {
            haveICachedBefore = hddLookUp(key, true);
        }
        if (haveICachedBefore == null) {
            return true;
        }
        return false;
    }

    @Override
    public void onAddedToCache(String key, String value) {
        System.out.println("Cache was Added to cacheMeomry for the key and values : " + key + " : " + value);
        changePowerConsumptionBy(1);
        CACHE_ENQUE_COUNT++;
    }

    @Override
    public void onRemovedFromCache(String key, String value) {
        System.err.println(nodeType() + " Node" + getNodeID() + "Cache was removed from cacheMeomry for the key and values : " + key + " : " + value);
        changePowerConsumptionBy(1);
        CACHE_DEQUE_COUNT++;
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
        System.out.println("NODE" + getNodeID() + " recived as response KEY. Will try to cache if required" + data[0]);
        //data recived by the node as response to quyert
        if (shouldICacheOrNot(data[0], data[1])) {
            addToCacheMemory(data[0], data[1], false);
        }
    }

    @Override
    public void onRespOutGoingData() {
        REQUEST_COUNT++;
        REQUEST_ANSWER_FORWARDED_COUNT++;
        System.out.println("Node" + getNodeID() + " Forwarding answer to Query its original requester in a path");
    }

    @Override
    public void onCacheHit() {
        CACHE_HITS_COUNT++;
        //System.out.println("node"+ getNodeID()+" : Cache Hit");
    }

    @Override
    public void onHDDHit() {
        //System.out.println("HDD HIT");
        HDD_HITS_COUNT++;
    }

    @Override
    public void onCacheMiss() {
        CACHE_MISS_COUNT++;
    }

    @Override
    public void onHDDMiss() {
        HDD_MISS_COUNT++;
    }

    @Override
    public boolean addToPayloadMemory(String key, String value) {
        if (localMemory_seekPointer < getMaxLocalPayloadSize()) {
            localMemory[localMemory_seekPointer][0] = key;
            localMemory[localMemory_seekPointer][1] = value;
            localMemory_seekPointer++;
            changePowerConsumptionBy(1);
            return true;
        }
        changePowerConsumptionBy(1);
        System.err.println(nodeType() + " Node" + getNodeID() + " FAILED_ADD_HDD: HDD Memory exceeded");
        return false;
    }

    @Override
    public void allocatePayloadMemorySize() {
        localMemory = new String[getMaxLocalPayloadSize()][2];
    }

    @Override
    public int getMaxLocalPayloadSize() {
        return LOCAL_PAYLOAD_SIZE;
    }

    @Override
    public void changePowerConsumptionBy(float changeBy) {
        POWER_CONSUMPTION += changeBy;
    }

    @Override
    public String[][] getPayloadContents() {
        return (localMemory == null) ? null : Arrays.copyOfRange(localMemory, 0, localMemory_seekPointer);
    }

    @Override
    public boolean addToCacheMemory(String key, String value, boolean softload) {
        System.out.println(nodeType() + " Node" + getNodeID() + " Addin to cahce " + key + " " + value + "  " + getMaxLocalCacheSize() + "   " + localcache_seekPointer);
        try {
            if (localcache_seekPointer < getMaxLocalCacheSize()) {
                cacheMemory[localcache_seekPointer][0] = key;
                cacheMemory[localcache_seekPointer][1] = value;
                localcache_seekPointer++;
                onAddedToCache(key, value);
            } else {
                //Random Replacment Strategy
                Random r = new Random();
                int randInt = r.nextInt(getMaxLocalCacheSize());
                //System.out.println("Node"+getNodeID()+" is ready to replace cache content from "+cacheMemory[randInt][0]+" with "+key);
                String cache_key_removed = cacheMemory[randInt][0]; //Cache key that is being removed
                String cache_value_removed = cacheMemory[randInt][1]; //Cache value that is being removed
                cacheMemory[randInt][0] = key;
                cacheMemory[randInt][1] = value;
                onRemovedFromCache(cache_key_removed, cache_value_removed);
                onAddedToCache(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void allocateCacheMemorySize() {
        cacheMemory = new String[getMaxLocalCacheSize()][2];
    }

    @Override
    public int getMaxLocalCacheSize() {
        return CACHE_MEMORY_SIZE;
    }

    @Override
    public String[][] getCacheContents() {
        return (cacheMemory == null) ? null : Arrays.copyOfRange(cacheMemory, 0, localcache_seekPointer);
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
    public String nodeType() {
        return "RandomCRP";
    }

    @Override
    public void onBeginSession(String... data) {
        System.out.println(nodeType() + " Node" + getNodeID() + " started requesing " + data[0]);
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
    public int getNumberOfCacheHits() {
        return CACHE_HITS_COUNT;
    }

    @Override
    public int getNumberOfCacheMiss() {
        return CACHE_MISS_COUNT;
    }

    @Override
    public int getNumberOfTimesCachelookups() {
        return CACHE_LOOKUP_COUNT;
    }

    @Override
    public int getNumberOfHDDHits() {
        return HDD_HITS_COUNT;
    }

    @Override
    public int getNumberOfHDDMiss() {
        return HDD_MISS_COUNT;
    }

    @Override
    public int getNumberOfTimesHDDlookups() {
        return HDD_LOOKUP_COUNT;
    }

    @Override
    public int getNumberOfCacheEnque() {
        return CACHE_ENQUE_COUNT;
    }

    @Override
    public int getNumberOfCacheDeque() {
        return CACHE_DEQUE_COUNT;
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
