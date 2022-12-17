package awcator.jiccns.cache_strats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

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

public class tfidf_with_live_timeoutCRP extends tfidfCRP {
    HashMap<String, Long> timeouttable;
    int MAX_STORE_FOR = 1 * 60;//1 minutes
    boolean DELET_OUTSIDE = true;

    public tfidf_with_live_timeoutCRP(int nodeid) {
        super(nodeid);
        timeouttable = new HashMap<>();
    }

    @Override
    public void onAddedToCache(String key, String value) {
        super.onAddedToCache(key, value);
        timeouttable.put(key, System.currentTimeMillis());
    }

    @Override
    public void onRemovedFromCache(String key, String value) {
        super.onRemovedFromCache(key, value);
        if (DELET_OUTSIDE)
            timeouttable.remove(key);
    }

    @Override
    public String cacheLookUp(String queryKey, boolean immunity_power_consumption) {
        DELET_OUTSIDE = false;
        String answer = super.cacheLookUp(queryKey, immunity_power_consumption);
        if (!immunity_power_consumption) {
            System.out.println("----------------------------------------####################################################################################");
            PriorityQueue<tfdif_array> cacheMemory2 = new PriorityQueue<>((a, b) -> Double.compare(a.cos_sim, b.cos_sim));
            for (tfdif_array aray : cacheMemory) {
                cacheMemory2.add(aray);
            }
            for (Iterator<Map.Entry<String, Long>> it = timeouttable.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Long> entry = it.next();
                int k = (int) (System.currentTimeMillis() - entry.getValue()) / 1000;
                if (k > MAX_STORE_FOR) {
                    for (tfdif_array tf : cacheMemory2) {
                        if (tf.key.equalsIgnoreCase(queryKey)) {
                            System.out.println("Timed out deleting the cache");
                            cacheMemory.remove(tf);
                            onRemovedFromCache(tf.key, tf.value);
                            it.remove();
                        }
                    }
                    localcache_seekPointer = cacheMemory.size();
                }
            }
            System.out.println("-----Timeout table------");
            System.out.println(timeouttable);
            System.out.println("-----</Timeout table>------");
            cacheMemory2 = null;
            System.gc();
            DELET_OUTSIDE = true;
        }
        return answer;
    }

    @Override
    public String getCacheType() {
        return "tfidf_with_live_timeoutCRP";
    }
}
