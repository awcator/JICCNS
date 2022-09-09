package awcator.jicns.alg;

public class SimpleNode extends jicnsNodeImpl {
    /**
     * Number of times data existed in cache.
     * DummyImplementation: if(data.exisitIN(cache)) then hits++
     */
    public static int hits = 0;
    /**
     * powerConsumption= Number of operations Node performed so far (including IO operation+Data processing Operation)
     * PowerConsumption completely depends on IO operations/CPU cycle
     */
    public int powerConsumption = 0;
    /**
     * Number of times data were not existed in cache.
     * DummyImplementation: if(data.doesnotexisitIN(cache)) then misses++
     */
    public int misses = 0;

    /**
     * Number of items node can keep in its memory
     * In Reality: This variable represents NOdes's memory size (HDD) to store its server contents
     */
    public int LocalMemorySize = 100;

    /**
     * Number of cahcable items node can keep in its memory
     * A rule to keep data or to remove data
     * In Reality: This variable represents NOdes's memory size (HDD) to store its server contents
     */
    public int cacheMemorySize = 10;

    /**
     * This varible contains NodeServer's localMemory contents
     * In Reality: This represent Nodes HardDisk
     */
    String[] localMemory = new String[LocalMemorySize];

    /***
     * This varible contains NodeServer's InMemory cache contents
     * In Reality: This will the superfast access memory type which is RAM.
     */
    String[] cacheMemory = new String[cacheMemorySize];
    int id = 0;

    public SimpleNode(int nodeid, int egressSize) {
        id = nodeid;
        egress = new int[egressSize][2];
    }

    @Override
    public void onIncomingReqData() {

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

    public void loadNodeFromBluePrint() {

    }
}
