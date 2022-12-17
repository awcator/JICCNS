package awcator.jiccns.device_strats;

import awcator.jiccns.cache_strats.jicnsCacheImpl;
import awcator.jiccns.exceptions.notAsnException;
import awcator.jiccns.ui.NodeUI;
import awcator.jiccns.ui.path;

public abstract class jicnsDeviceImpl {
    public jicnsCacheImpl DEVICE_CACHE_STRATEGY;
    /**
     * Number of requests made on the node
     */
    public int REQUEST_COUNT = 0;
    /**
     * Number of requests answered on the node
     */
    public int REQUEST_ANSWERED_BY_ME_COUNT = 0;
    /**
     * Number of requests forwrded from the node
     */
    public int REQUEST_FORWARDED_COUNT = 0;
    /**
     * Number of requests forwrded from the node
     */
    public int REQUEST_ANSWER_FORWARDED_COUNT = 0;
    /**
     * powerConsumption= Number of operations Node performed so far (including IO operation+Data processing Operation)
     * PowerConsumption completely depends on IO operations/CPU cycle
     */
    public float DEVICE_POWER_CONSUMPTION = 0;
    /**
     * Egress rules:
     * egress[4][0]=5 implies, there exist a 4th route from curennt node to node5
     * egress[4][1]=2 implies,  the 4th route has a latency of 2 ms to the destination node
     */
    public int[][] EGRESS;
    String DEVICE_TYPE;

    abstract public String getDeviceDescription();

    abstract public void setDeviceDescription(String str);

    abstract public jicnsCacheImpl getCacheStrategy();

    abstract public void setCacheStrategy(jicnsCacheImpl my_cache_strategy);

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of times node forwarded answer from its parent to childs
     */
    abstract public int getNumberOfRequestesAnswereForwardedCount();

    /**
     * for graffana metrics
     *
     * @return it should do what to do when request is answered by the node
     */
    abstract public void onRequestAnsweredByMe();

    /**
     * Number of items node can keep in its memory
     * In Reality: This variable represents NOdes's memory size (HDD) to store its server contents
     * Override if requrred to set appropirate size
     */

    /***
     * A node implementable fucntion
     * It symbolizes what to be done when you recive incomming data request
     * Some Implementable ideas:
     *      I dont own this data, i'll forward to others
     *      I own this Data, Should I have to make any change to Cahce? By thinking of improtance in future
     * param: data: It is the dataquery other nodes requested this node
     */
    public abstract void onIncomingReqData(String data, NodeUI[] list, path path_so_far) throws notAsnException;

    /**
     * A node implementable function
     * It symbolizes what to do by the server when new data is eggressed
     * Some Implementable ideas:
     * Since my previous nodes dont have acess to this data, should i tell my immidite lower nodes to cahce it if he has good cache size left
     * Decide which direction to pass info. return the answer? or forward request? any other strategy?
     * Decide if to broadcast next paretnts? or send one by one? or any other strategy?
     * whom to forward? shall I decide based on latency? shall I decide on Hops? or battery consumption? or more strategy?
     * <p>
     * data[0]: reciving nodeID
     * data[1]: currentPath so far from the sourceNode
     */

    public abstract void onReqOutGoingData(String... data);

    /**
     * What to do when data is recving from the node who knows the data
     * <p>
     * data[0]=key
     * data[1]=value
     */
    public abstract void onRespIncomingData(String... data);

    public abstract void onRespOutGoingData();

    abstract public void changeDevicePowerConsumptionBy(float changeBy);

    /**
     * It checks if node B is neibhour of current node
     *
     * @param nodeNumber This is the ID of node B
     * @return true if Node B is immidiate neibhour of A/curernt Node. else false
     */
    abstract public boolean isMyNeibhour(int nodeNumber);

    /**
     * It returns latency required to reach node B from current node if path exsist
     *
     * @param nodeNumber This is the ID of node B
     * @return int in ms if Node B is immidiate neibhour of A/curernt Node. else -1 indicating no latency info because path dsnt exsit
     */
    abstract public int getMsToReachNode(int nodeNumber, NodeUI[] list);

    /**
     * if you want your route to pass through a node that was alredy visited return true; else false
     * suppose you are broadcasting from node A to z it passes through A->B->C now Assume C node has egress to node B. this is called cycle here.
     *
     * @return true
     */
    abstract public boolean allowCycles();

    /**
     * This will return the Node ID of the particular node
     *
     * @return id
     */
    abstract public int getNodeID();

    /**
     * It will return what kind of node it is
     *
     * @return must return somehing , not null
     */
    abstract public String getDeviceType();

    abstract public void setDeviceType(String str);

    /**
     * use this function to bootup your nodes. This function will be called when node begins first broadcast intitaies
     */
    abstract public void onBeginSession(String... data);

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of requests the node handled so far (incomming_req+outgoing req)
     */
    abstract public int getNumberOfRequestsHandled();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of requests the node sucesfully answered by itself without forwarding
     */
    abstract public int getNumberOfRequestsAnsweredBYME();

    /**
     * for graffana metrics
     *
     * @return it should return an int showing number of requests the node forwarded to others
     */
    abstract public int getNumberOfRequestsForwarded();

    abstract public boolean shouldIPassThroughthisNode(int node, String extra_pathInfo);

    /**
     * Get DeviceName
     *
     * @return
     */
    abstract public String getCanonicalName();

    abstract public boolean setCanonicalName(String str);
}
