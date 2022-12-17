package awcator.jiccns.cache_strats;

import awcator.jiccns.ui.NodeUI;
import awcator.jiccns.ui.path;

public abstract class jicnsCacheImpl {
    /**
     * Number of times data existed in cache.
     * DummyImplementation: if(data.exisitIN(cache)) then hits++
     */
    public int CACHE_HITS_COUNT = 0;
    /**
     * Number of times data existed in HDD (internal storage/payload).
     * DummyImplementation: if(data.exisitIN(HDD)) then hits++
     */
    public int HDD_HITS_COUNT = 0;
    /**
     * Number of times data were not existed in cache.
     * DummyImplementation: if(data.doesnotexisitIN(cache)) then misses++
     */
    public int CACHE_MISS_COUNT = 0;
    /**
     * Number of times data were not existed in HDD. NOTE LOOKUP first happens in Cache then HDD
     * DummyImplementation: if(data.doesnotexisitIN(cache)) then misses++
     */
    public int HDD_MISS_COUNT = 0;
    /**
     * Number of times data node lookedup its cachetable
     */
    public int CACHE_LOOKUP_COUNT = 0;
    /**
     * Number of times data node lookedup its hddtable
     */
    public int HDD_LOOKUP_COUNT = 0;


    public int LOCAL_PAYLOAD_SIZE = 100;

    /**
     * Number of cahcable items node can keep in its memory
     * A rule to keep data or to remove data
     * In Reality: This variable represents NOdes's memory size (HDD) to store its server contents
     */
    public int CACHE_MEMORY_SIZE = 10;

    /**
     * Number of times node Cached the data
     */
    public int CACHE_ENQUE_COUNT = 0;

    /**
     * Number of times node removed Cache data
     */
    public int CACHE_DEQUE_COUNT = 0;

    public int CACHE_POWER_CONSUMPTION = 0;

    abstract public void changeCachePowerConsumptionBy(float units);

    /***
     * A node implementable function
     * It symbolizes what to be done to lookUp data in cache
     * Some Implementable ideas:
     *      How to lookup in cache? Should I have to lookup linearly or byIndexOf or recentFirst or Search from old data?
     *      Make sure to increses operationcount by modifiing powerConsumption varaible. To get more accury of the algorithm which node was implemted with
     *
     * Pram:
     * query_key:  A string key that is used to lookup inits cacheMemory
     * immune_to_powerconsumption: a boolean type to increse powerconsumption or not.
     *     SomeTimes you may need to query your lookuptable for processing data, you may dont want to effect its internal powerconumption param
     */
    abstract public String cacheLookUp(String query_key, boolean immunity_power_consumption);

    /***
     * A node implementable function
     * It symbolizes what to be done to lookUp data in its internal Memory
     *
     * Pram:
     * query_key:  A string key that is used to lookup inits HDD
     * immune_to_powerconsumption: a boolean type to increse powerconsumption or not.
     *     SomeTimes you may need to query your lookuptable for processing data, you may dont want to effect its internal powerconumption param
     */
    abstract public String hddLookUp(String query_key, boolean immunity_power_consumption);

    /***
     * A node implementable function
     * It symbolizes what to be done to do with the incomming data after llokedup
     * SomeIdeas to be implemented:
     *      Temprateure data are critical. It may change anytime. its better not to cache. MayBe I can cahce for few minutes.
     *      Since data alredy exsist in cache? should I still keep it in cache for future or should I remove it?
     *      Since data dsnt exsist in cache, is it worth to cache this data?
     *      if decided yes. Whoam Should i remove from the cache if cache is full?
     *      if cache is not full, what order should i put it? timeOrder? frequency? populatirity? OldFirst? recently used First? BigSize data first?
     * @return
     */
    public abstract boolean shouldICacheOrNot(String key, String value, NodeUI[] list, path current_path);

    /***
     * A node implementable function
     * It symbolizes what to do by the server when new data is added into the cache?
     * Implemt the Effect of memory/powerconsumption.
     * Which data from cahce should I remvoe to add keep this data?
     * Should I have to forward the same data to others even If i have the same data?
     */
    public abstract void onAddedToCache(String key, String value);

    /***
     * A node implementable function
     * It symbolizes what to do by the server when new data is removed into the cache?
     * Some Implementable ideas:
     * Implement the Effect of memory/powerconsumption for processing to remove operation.
     * Which data from cahce should I remvoe to add keep this data?
     * Should I have to forward the same data to others even If i have the same data?
     * Or should I have to ask others to stop caching this, since I alredy cached it?
     * Or since I know this is least impornt data. should i tell others to stop cachec this data?
     */
    public abstract void onRemovedFromCache(String key, String value);


    abstract public void onCacheHit();

    abstract public void onHDDHit();

    /**
     * What to do when cacheHitHappens?
     */

    abstract public void onCacheMiss();

    abstract public void onHDDMiss();

    /**
     * What to do when Cache MissHappens?
     */
    abstract public boolean addToPayloadMemory(String key, String value);

    abstract public void allocatePayloadMemorySize();

    abstract public int getMaxLocalPayloadSize();

    abstract public String[][] getPayloadContents();

    abstract public boolean addToCacheMemory(String key, String value, boolean softload);

    abstract public void allocateCacheMemorySize();

    abstract public int getMaxLocalCacheSize();

    abstract public String[][] getCacheContents();

    abstract public int getNumberOfCacheHits();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of cacheMiss
     */
    abstract public int getNumberOfCacheMiss();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of cacheLookups
     */
    abstract public int getNumberOfTimesCachelookups();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of HDDHits
     */
    abstract public int getNumberOfHDDHits();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of HDD Miss
     */
    abstract public int getNumberOfHDDMiss();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of HDDLookups
     */
    abstract public int getNumberOfTimesHDDlookups();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of times node cached the data
     */
    abstract public int getNumberOfCacheEnque();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of times node dequed the cached data
     */
    abstract public int getNumberOfCacheDeque();

    abstract public String getCacheType();

    abstract public int getNodeID();
}