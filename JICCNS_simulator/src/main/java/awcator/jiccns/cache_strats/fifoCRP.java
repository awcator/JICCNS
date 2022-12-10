package awcator.jiccns.cache_strats;

import java.util.Arrays;

/**
 * Node Summary: First In First Out Replacemnt Policy Node
 * <p>
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 * Replacemnt Type: oldest first
 * <p>
 * CacheStrategy: FIFO/Oldest First
 * Extra Memoty: Nope
 */

public class fifoCRP extends jicnsCacheImpl {
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

    public fifoCRP(int nodeid) {
        id = nodeid;
    }

    @Override
    public void changeCachePowerConsumptionBy(float units) {
        CACHE_POWER_CONSUMPTION += units;
    }

    @Override
    public String cacheLookUp(String queryKey, boolean immunity_power_consumption) {
        //System.out.println("NODE"+getNodeID()+" will lookup "+queryKey);
        if (immunity_power_consumption == false) CACHE_LOOKUP_COUNT++;
        for (int i = 0; i < Math.min(localcache_seekPointer, getMaxLocalCacheSize()) && i < CACHE_MEMORY_SIZE; i++) {
            if (cacheMemory[i][0].equalsIgnoreCase(queryKey)) {
                if (immunity_power_consumption == false) {
                    onCacheHit();
                    changeCachePowerConsumptionBy(i + 1);
                }
                return cacheMemory[i][1];
            }
        }

        if (immunity_power_consumption == false) {
            changeCachePowerConsumptionBy(Math.min(localcache_seekPointer, getMaxLocalCacheSize()));
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
                    changeCachePowerConsumptionBy(i + 1);
                }
                return localMemory[i][1];
            }
        }
        if (immunity_power_consumption == false) {
            changeCachePowerConsumptionBy(localMemory_seekPointer);
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
        return haveICachedBefore == null;
    }

    @Override
    public void onAddedToCache(String key, String value) {
        System.out.println("Cache was Added to cacheMeomry for the key and values : " + key + " : " + value);
        changeCachePowerConsumptionBy(1);
        CACHE_ENQUE_COUNT++;
    }

    @Override
    public void onRemovedFromCache(String key, String value) {
        System.err.println(getCacheType() + " Node" + getNodeID() + "Cache was removed from cacheMeomry for the key and values : " + key + " : " + value);
        changeCachePowerConsumptionBy(1);
        CACHE_DEQUE_COUNT++;
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
            changeCachePowerConsumptionBy(1);
            return true;
        }
        changeCachePowerConsumptionBy(1);
        System.err.println(getCacheContents() + " Node" + getNodeID() + " FAILED_ADD_HDD: HDD Memory exceeded");
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
    public String[][] getPayloadContents() {
        return (localMemory == null) ? null : Arrays.copyOfRange(localMemory, 0, localMemory_seekPointer);
    }

    @Override
    public boolean addToCacheMemory(String key, String value, boolean softload) {
        System.out.println(getCacheType() + " Node" + getNodeID() + " Addin to cahce " + key + " " + value + "  " + getMaxLocalCacheSize() + "   " + localcache_seekPointer);
        try {
            if (localcache_seekPointer < getMaxLocalCacheSize()) {
                cacheMemory[localcache_seekPointer][0] = key;
                cacheMemory[localcache_seekPointer][1] = value;
                localcache_seekPointer++;
                onAddedToCache(key, value);
            } else {
                //Random Replacment Strategy
                int randInt = localcache_seekPointer % getMaxLocalCacheSize();
                //System.out.println("Node"+getNodeID()+" is ready to replace cache content from "+cacheMemory[randInt][0]+" with "+key);
                String cache_key_removed = cacheMemory[randInt][0]; //Cache key that is being removed
                String cache_value_removed = cacheMemory[randInt][1]; //Cache value that is being removed
                cacheMemory[randInt][0] = key;
                cacheMemory[randInt][1] = value;
                onRemovedFromCache(cache_key_removed, cache_value_removed);
                onAddedToCache(key, value);
                localcache_seekPointer++;
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
        System.out.println(localcache_seekPointer + "   " + getMaxLocalCacheSize());
        return (cacheMemory == null) ? null : Arrays.copyOfRange(cacheMemory, 0, Math.min(localcache_seekPointer, getMaxLocalCacheSize()));
    }


    @Override
    public int getNodeID() {
        return id;
    }

    @Override
    public String getCacheType() {
        return "fifoCRP";
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
}
