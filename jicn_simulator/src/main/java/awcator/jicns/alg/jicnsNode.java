package awcator.jicns.alg;

public abstract class jicnsNode {
    public int powerConsumption = 0;
    /**
     * powerConsumption= Number of operations Node performed so far (including IO operation+Data processing Operation)
     * PowerConsumption completely depends on IO operations/CPU cycle
     */

    public int hits = 0;
    /**
     * Number of times data existed in cache.
     * DummyImplementation: if(data.exisitIN(cache)) then hits++
     */

    public int misses = 0; //Number of times data wsnt exsisted in cache
    /**
     * Number of times data were not existed in cache.
     * DummyImplementation: if(data.doesnotexisitIN(cache)) then misses++
     */

    public int LocalMemorySize = 100;
    /**
     * Number of items node can keep in its memory
     * In Reality: This variable represents NOdes's memory size (HDD) to store its server contents
     */

    public int cacheMemorySize = 10;
    /**
     * Number of cahcable items node can keep in its memory
     * A rule to keep data or to remove data
     * In Reality: This variable represents NOdes's memory size (HDD) to store its server contents
     */

    String localMemory[] = new String[LocalMemorySize];
    /**
     * This varible contains NodeServer's localMemory contents
     * In Reality: This represent Nodes HardDisk
     */

    String cacheMemory[] = new String[cacheMemorySize];

    /***
     * This varible contains NodeServer's InMemory cache contents
     * In Reality: This will the superfast access memory type which is RAM.
     */

    public abstract void onIncomingReqData();

    /***
     * A node implementable fucntion
     * It symbolizes what to be done when you recive incomming data request
     * Some Implementable ideas:
     *      I dont own this data, i'll forward to others
     *      I own this Data, Should I have to make any change to Cahce? By thinking of improtance in future
     */

    public abstract void cacheLookUp();

    /***
     * A node implementable function
     * It symbolizes what to be done to lookUp data in cache
     * Some Implementable ideas:
     *      How to lookup in cache? Should I have to lookup linearly or byIndexOf or recentFirst or Search from old data?
     *      Make sure to increses operationcount by modifiing powerConsumption varaible. To get more accury of the algorithm which node was implemted with
     */


    public abstract void shouldICacheOrNot();

    /***
     * A node implementable function
     * It symbolizes what to be done to do with the incomming data after llokedup
     * SomeIdeas to be implemented:
     *      Temprateure data are critical. It may change anytime. its better not to cache. MayBe I can cahce for few minutes.
     *      Since data alredy exsist in cache? should I still keep it in cache for future or should I remove it?
     *      Since data dsnt exsist in cache, is it worth to cache this data?
     *      if decided yes. Whoam Should i remove from the cache if cache is full?
     *      if cache is not full, what order should i put it? timeOrder? frequency? populatirity? OldFirst? recently used First? BigSize data first?
     */

    public abstract void onAddedToCache();
    /***
     * A node implementable function
     * It symbolizes what to do by the server when new data is added into the cache?
     * Implemt the Effect of memory/powerconsumption.
     * Which data from cahce should I remvoe to add keep this data?
     * Should I have to forward the same data to others even If i have the same data?
     */

    public abstract void onRemovedFromCache();
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

    public abstract void onReqOutGoingData();
    /**
     * A node implementable function
     * It symbolizes what to do by the server when new data is eggressed
     * Some Implementable ideas:
     *      Since my previous nodes dont have acess to this data, should i tell my immidite lower nodes to cahce it if he has good cache size left
     *      Decide which direction to pass info. return the answer? or forward request?
     */

    /**
     * What to do when data is recving from the node who knows the data
     */
    public abstract void onRespIncomingData();
    public abstract void onRespOutGoingData();

    public void onCacheHit() {
        hits++;
    }
    /**
     * What to do when cacheHitHappens?
     */

    public void onCacheMiss() {
        misses++;
    }
    /**
     * What to do when Cache MissHappens?
     */
}