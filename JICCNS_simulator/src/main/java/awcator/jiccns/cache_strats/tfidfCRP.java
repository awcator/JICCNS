package awcator.jiccns.cache_strats;

import java.util.*;

/**
 * Node Summary: Tf-IDF based Cache replacment Policy Node
 * <p>
 * Payload Storage type: Custom priority queue based on popularity
 * Payload add type: worstcase O(n) bestcase o(1) direct addition
 * Replacemnt Type: Popularity based cache repalcemnt
 * <p>
 * CacheStrategy: CosineSimilarity with older data it was vistied with
 * Extra Memory: YES
 */

public class tfidfCRP extends jicnsCacheImpl {
    /**
     * This varible contains NodeServer's localMemory contents
     * In Reality: This represent Nodes HardDisk
     */
    String[][] localMemory;
    /***
     * This varible contains NodeServer's InMemory cache contents
     * In Reality: This will the superfast access memory type which is RAM.
     */
    PriorityQueue<tfdif_array> cacheMemory;
    int id = 0;
    private int localMemory_seekPointer = 0;
    private int localcache_seekPointer = 0;

    /**
     * TF-IDF specific to store the history of requests recived by the node. Using this we will calulate the
     * popularity of the data for the future using tf-idf
     */
    private String requestHistory = "";

    public tfidfCRP(int nodeid) {
        id = nodeid;
    }

    @Override
    public String cacheLookUp(String queryKey, boolean immunity_power_consumption) {
        //System.out.println("NODE"+getNodeID()+" will lookup "+queryKey);
        if (immunity_power_consumption == false) CACHE_LOOKUP_COUNT++;
        int i = 0;
        // TODO: 11/26/22 Poll way itteration to get correct counts
        for (tfdif_array tf : cacheMemory) {
            i++;
            if (tf.key.equalsIgnoreCase(queryKey)) {
                if (immunity_power_consumption == false) {
                    onCacheHit();
                    changeCachePowerConsumptionBy(i + 1);
                }
                return tf.value;
            }
        }

        if (immunity_power_consumption == false) {
            changeCachePowerConsumptionBy(localcache_seekPointer);
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
        System.err.println(getCacheType() + " Node" + getNodeID() + " FAILED_ADD_HDD: HDD Memory exceeded");
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
        try {
            System.out.println(getCacheType() + " Node" + getNodeID() + " Addin to cahce " + key + " " + value + "  " + getMaxLocalCacheSize() + "   " + localcache_seekPointer);
            if (softload == true) requestHistory += key + " ";
            List<String> queryHistory = Arrays.asList(requestHistory.trim().split(" "));
            List<List<String>> documents = new ArrayList<>();
            TFIDF_Calulator calculator = new TFIDF_Calulator();
            List<String> doc = Arrays.asList(key.trim().split(" "));
            List<String> empty_doc = Arrays.asList("".split(" "));
            documents.add(empty_doc);
            documents.add(queryHistory);
            for (tfdif_array tf : cacheMemory) {
                documents.add(Arrays.asList(tf.key.trim().split(" ")));
            }
            Set<String> myset = new HashSet<>(); // A set thtat contains every possible words
            for (List<String> docky : documents) {
                for (String word : docky) {
                    myset.add(word);
                }
            }
            System.out.println(documents + "  ********** " + doc + " **********  " + queryHistory);
            double cos = calculator.cosineSimlarityTFIDF(documents, doc, queryHistory, myset);
            System.out.println("---->" + doc + "  " + cos);

            cacheMemory.add(new tfdif_array(key, value, cos));
            onAddedToCache(key, value);
            //Now reclulate all tf-idf due to new  entry
            PriorityQueue<tfdif_array> cacheMemory2 = new PriorityQueue<>((a, b) -> Double.compare(a.cos_sim, b.cos_sim));
            System.out.println("------NEW TIDF cache table----");
            while (!cacheMemory.isEmpty()) {
                tfdif_array temp_tf = cacheMemory.poll();
                List<String> temp_doc = Arrays.asList(temp_tf.key.trim().split(" "));
                double temp_cos = calculator.cosineSimlarityTFIDF(documents, temp_doc, queryHistory, myset);
                System.out.println(temp_doc + "   " + temp_cos);
                temp_tf.cos_sim = temp_cos;
                cacheMemory2.add(temp_tf);
            }
            System.out.println("_______________");
            cacheMemory = cacheMemory2;
            // now trimout size to maxSize
            while (cacheMemory.size() > getMaxLocalCacheSize()) {
                tfdif_array removedCacheData = cacheMemory.poll();
                onRemovedFromCache(removedCacheData.key, removedCacheData.value);
            }
            System.gc();
            localcache_seekPointer = cacheMemory.size();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void allocateCacheMemorySize() {
        cacheMemory = new PriorityQueue<>((a, b) -> Double.compare(a.cos_sim, b.cos_sim));
    }

    @Override
    public int getMaxLocalCacheSize() {
        return CACHE_MEMORY_SIZE;
    }

    @Override
    public String[][] getCacheContents() {
        String[][] x = new String[cacheMemory.size()][2];
        int i = 0;
        for (tfdif_array tf : cacheMemory) {
            x[i][0] = tf.key;
            x[i][1] = tf.value;
            i++;
        }
        System.gc();
        return x;
    }

    @Override
    public int getNodeID() {
        return id;
    }

    @Override
    public String getCacheType() {
        return "TF-IDF_CRP";
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

    class TFIDF_Calulator {
        public double tf(List<String> doc, String term) {
            double result = 0;
            for (String word : doc) {
                if (term.equalsIgnoreCase(word))
                    result++;
            }
            //System.out.println(result+" "+doc.size());

            return result / doc.size();
        }

        public double cosineSimlarityTFIDF(List<List<String>> docs, List<String> docA, List<String> docB, Set<String> s) {
            //System.out.println(docs+ "  "+docA+"   "+docB+"   "+s);
            double numerator = 0;
            double denominator = 0;
            double tfidf_A = 0;
            double tfidf_B = 0;
            double totaltfidf_A = 0;
            double totaltfidf_B = 0;

            for (String word : s) {

                tfidf_A = tfIdf(docA, docs, word);
                tfidf_B = tfIdf(docB, docs, word);

                numerator += (tfidf_A * tfidf_B);
                totaltfidf_A += Math.pow(tfidf_A, 2);
                totaltfidf_B += Math.pow(tfidf_B, 2);

                //System.out.println(word+ " "+tfidf_A+"  "+tfidf_B+"   "+numerator+"   "+totaltfidf_A+"   "+totaltfidf_B);
            }
            return numerator / (Math.sqrt(totaltfidf_A) * Math.sqrt(totaltfidf_B));
        }

        /**
         * @param docs list of list of strings represents the dataset
         * @param term String represents a term
         * @return the inverse term frequency of term in documents
         */
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

            //System.out.println(docs.size()+" "+n);

            //return Math.log(docs.size() / n);
            return Math.log10((double) docs.size() / n);
        }

        /**
         * @param doc  a text document
         * @param docs all documents
         * @param term term
         * @return the TF-IDF of term
         */
        public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
            //System.out.println(tf(doc, term)+" "+idf(docs, term));
            //System.exit(0);
            return tf(doc, term) * idf(docs, term);
        }
    }

    class tfdif_array {
        String key;
        String value;
        double cos_sim;

        public tfdif_array(String k, String v, double c) {
            key = k;
            value = v;
            cos_sim = c;
        }

        @Override
        public String toString() {
            return key + ":" + value;
        }
    }
}
