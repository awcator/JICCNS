package awcator.jiccns.demo;

import java.util.*;

public class pq_basedon_tfidf {
    public static void main(String args[]) {
        class tfdif_array {
            String data;
            double cos_sim;

            public tfdif_array(String D, double c) {
                data = D;
                cos_sim = c;
            }

            @Override
            public String toString() {
                return data+" "+cos_sim;
            }
        }

        PriorityQueue<tfdif_array> pq = new PriorityQueue<>((a,b) -> Double.compare( a.cos_sim, b.cos_sim));
        pq.add(new tfdif_array("Cello",0.7));
        pq.add(new tfdif_array("jjello",7));
        pq.add(new tfdif_array("Hello",0.1));
        pq.add(new tfdif_array("Iello",0.000011));
        pq.add(new tfdif_array("xello",0.000017));
        pq.add(new tfdif_array("Bello",1));
        System.out.println(pq);
        System.out.println("-------------");
        for(tfdif_array tf:pq){
            System.out.println(tf);
        }
        System.out.println("-------------");
        Iterator<tfdif_array> iter = pq.iterator();
        while (iter.hasNext()){
            System.out.println(iter.next());
        }
        System.out.println("-------------");
        while (!pq.isEmpty())
            System.out.println(pq.poll());
    }
}
