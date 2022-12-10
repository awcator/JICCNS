package awcator.jiccns.cache_strats;

import java.util.Arrays;

/**
 * Node Summary:
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 * Replacemnt Type: No replacments. OneTime Hardcoded cache and memory
 * <p>
 * CacheStrategy: Nope
 * Extra Memoty: Nope
 */

public class noncacheable extends jicnsCacheImpl {
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

    public noncacheable(int nodeid) {
        id = nodeid;
    }

    @Override
    public String cacheLookUp(String queryKey, boolean immunity_power_consumption) {
        if (immunity_power_consumption == false) CACHE_LOOKUP_COUNT++;
        //System.out.println("NODE"+getNodeID()+" will lookup "+queryKey);
        for (int i = 0; i < localcache_seekPointer && i < CACHE_MEMORY_SIZE; i++) {
            if (cacheMemory[i][0].equalsIgnoreCase(queryKey)) {
                if (immunity_power_consumption == false) {
                    changeCachePowerConsumptionBy(i + 1);
                    onCacheHit();
                }
                return cacheMemory[i][1];
            }
        }
        if (immunity_power_consumption == false) {
            onCacheMiss();
            changeCachePowerConsumptionBy(localcache_seekPointer);
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
        //Always dont cacheIT
        //FORCE no cache
        return false;
    }

    @Override
    public void onAddedToCache(String key, String value) {
        changeCachePowerConsumptionBy(1);
        CACHE_ENQUE_COUNT++;
    }

    @Override
    public void onRemovedFromCache(String key, String value) {
        CACHE_DEQUE_COUNT++;
        changeCachePowerConsumptionBy(1);
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
        System.err.println(getCacheType() + " Node" + getNodeID() + " FAILED_ADD_HDD: HDD Memory exceeded");
        changeCachePowerConsumptionBy(1);
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
    public void changeCachePowerConsumptionBy(float changeBy) {
        CACHE_POWER_CONSUMPTION += changeBy;
    }

    @Override
    public String[][] getPayloadContents() {
        return (localMemory == null) ? null : Arrays.copyOfRange(localMemory, 0, localMemory_seekPointer);
    }

    @Override
    public boolean addToCacheMemory(String key, String value, boolean softload) {
        System.out.println("Addin to cahce " + key + " " + value + "  " + getMaxLocalCacheSize());
        try {
            if (localcache_seekPointer < getMaxLocalCacheSize()) {
                cacheMemory[localcache_seekPointer][0] = key;
                cacheMemory[localcache_seekPointer][1] = value;
                localcache_seekPointer++;
                onAddedToCache(key, value);
                return true;
            } else {
                changeCachePowerConsumptionBy(1);
                System.err.println("ADD_FAIL_CACHE: Cache OverFlow at Node" + getNodeID() + "  for data " + key);
                // TODO: 11/26/22 Cache Overflow metrics? may be in future
            }
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
    public int getNodeID() {
        return id;
    }

    @Override
    public String getCacheType() {
        return "NonCache";
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
