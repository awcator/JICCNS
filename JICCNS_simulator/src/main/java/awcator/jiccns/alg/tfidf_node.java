package awcator.jiccns.alg;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Node Summary:
 * Payload Storage type: Arrays[][]
 * Payload add type: Linear additon to array
 * <p>
 * CacheStrategy: Nope
 */

public class tfidf_node extends jicnsNodeImpl {
    static Set<String> bagofwords_dataset = new HashSet<>();
    /**
     * TF_IDF specific: To store history of requests in that porticular node
     */
    String historyOfRequests;
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

    /**
     * Converts string into spaced String
     *
     * @param str eg: hello_world-car
     * @return will return 'hello world car'
     */
    private String makeSpacedData(String str) {
        return str.trim().replaceAll("_", " ").replaceAll("-", " ").trim();
    }

    @Override
    public void onIncomingReqData(String data) {
        //System.out.println("Packet Reached Node" + getNodeID());
        historyOfRequests += makeSpacedData(data) + " ";
    }

    @Override
    public String cacheLookUp(String queryKey, boolean immunity_power_consumption) {
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
    public String hddLookUp(String query_key, boolean immunity_power_consumption) {
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
    public boolean shouldICacheOrNot(String key, String value) {
        return false;
    }

    @Override
    public void onAddedToCache(String key, String value) {
        changePowerConsumptionBy(1);
    }

    @Override
    public void onRemovedFromCache(String key, String value) {

    }

    @Override
    public void onReqOutGoingData(String... data) {

    }

    @Override
    public void onRespIncomingData(String... data) {

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
        System.out.println("Addin to cahce " + key + " " + value + "  " + getMaxLocalCacheSize());
        try {
            cacheMemory[localcache_seekPointer][0] = key;
            cacheMemory[localcache_seekPointer][1] = value;
            localcache_seekPointer++;
            onAddedToCache(key, value);
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
        return "tfidf_node";
    }

    class TFIDF_Calulator {
        public double tf(List<String> doc, String term) {
            double result = 0;
            for (String word : doc) {
                if (term.equalsIgnoreCase(word))
                    result++;
            }
            return result / doc.size();
        }

        public double idf(List<List<String>> docs, String term) {
            double n = 0;
            for (List<String> doc : docs) {
                for (String word : doc) {
                    if (term.equalsIgnoreCase(word)) {
                        n++;
                        break;
                    }
                }
            }
            return Math.log10((double) docs.size() / (double) n);
        }

        public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
            return tf(doc, term) * idf(docs, term);
        }

        public double cosineSimlarityTFIDF(List<List<String>> docs, List<String> docA, List<String> docB, Set<String> bagofwords) {
            double numerator = 0;
            double denominator = 0;
            double tfidf_A = 0;
            double tfidf_B = 0;
            double totaltfidf_A = 0;
            double totaltfidf_B = 0;
            for (String word : bagofwords) {
                tfidf_A = tfIdf(docA, docs, word);
                tfidf_B = tfIdf(docB, docs, word);
                numerator += (tfidf_A * tfidf_B);
                totaltfidf_A += Math.pow(tfidf_A, 2);
                totaltfidf_B += Math.pow(tfidf_B, 2);
            }
            return numerator / (Math.sqrt(totaltfidf_A) * Math.sqrt(totaltfidf_B));
        }
    }
}
