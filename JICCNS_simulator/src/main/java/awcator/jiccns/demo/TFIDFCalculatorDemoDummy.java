package awcator.jiccns.demo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TFIDFCalculatorDemoDummy {
    public static void main(String[] args) {
        /*
        List<String> doc1 = Arrays.asList("the best italian restaurant enjoy the best pasta".split(" "));
        List<String> doc2 = Arrays.asList("american restaurant enjoy the best hamburger".split(" "));
        List<String> doc3 = Arrays.asList("korean restaurant enjoy the best bibi".split(" "));
        List<String> doc4 = Arrays.asList("the best the best american restaurant".split(" "));
        List<List<String>> documents = Arrays.asList(doc1, doc2, doc3, doc4);
        TFIDFCalculatorDemoDummy calculator = new TFIDFCalculatorDemoDummy();
        */
        List<String> empty_doc = Arrays.asList("".split(" "));
        List<String> doc1 = Arrays.asList("largest scrapper in the world".split(" "));
        List<String> doc2 = Arrays.asList("smallest, country".split(" "));
        List<String> queryHistory = Arrays.asList("apple".split(" "));
        List<List<String>> documents = Arrays.asList(empty_doc, doc1, queryHistory);
        TFIDFCalculatorDemoDummy calculator = new TFIDFCalculatorDemoDummy();
        //double tfidf = calculator.tfIdf(doc1, documents, "enjoy");
        //System.out.print(tfidf);
        Set<String> myset = new HashSet<>();
        for (List<String> doc : documents) {
            for (String word : doc) {
                myset.add(word);
            }
        }
        System.out.println(calculator.cosineSimlarityTFIDF(documents, doc1, queryHistory, myset));
        System.out.println("----cosine simlairy WRT searchTerm-----");
        for (List<String> doc : documents) {
            System.out.println(doc);
            System.out.println(calculator.cosineSimlarityTFIDF(documents, doc, queryHistory, myset));
        }
    }

    /**
     * @param doc  list of strings
     * @param term String represents a term
     * @return term frequency of term in document
     */
    public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        //System.out.println(result+" "+doc.size());

        return result / doc.size();
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
