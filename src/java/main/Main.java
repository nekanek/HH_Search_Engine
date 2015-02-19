package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import searchLogic.SearchEngine;
import static searchLogic.SearchEngine.findDocuments;
import static searchLogic.SearchEngine.stopAndStemmerize;
import static searchLogic.SearchEngine.tokenize;

public class Main {
    
    

    public static void main(String[] args) throws Exception {
        
        HashMap<Integer, Document> docs = createDocs();
        
        HashMap<String, PriorityQueue<Integer>> index = createIndex(docs);
        
//        String[] query = getSearchTokens(args);
//        ArrayList<String> words = new ArrayList<>();
//        words.add("cakes");
//        words.add("device0");
//        words.add("loving");
//        words.add("cyberpunkish");
//        words.add("at");
//        words.add("been");
//        words.add("beenishing");
//        ArrayList<String> wordsR = (ArrayList<String>) searchLogic.SearchEngine.stopAndStemmerize(words);
//        System.out.println(wordsR.toString());
//        String query="some";
//
//        System.out.println(SearchEngine.getStopList("english").toString());
//        System.out.println("size "+SearchEngine.getStopList("english").size());
        
        String query="some";
        System.out.println("Query: " + query);
        ArrayList<Integer> resultsList = findDocuments(query, index, "and", null);
        for (Integer i : resultsList) {
            System.out.println(docs.get(i));
        }
        
    }
    
    public static HashMap<Integer, Document> createDocs() {
        HashMap<Integer, Document> docs = new HashMap<>();
//        docs.put(1, new Document(1, "Some quoted text will b here"));
//        docs.put(2, new Document(2, "Another quoted text will b here"));
        return docs;
    }  
    
    public static HashMap<String, PriorityQueue<Integer>> createIndex(HashMap<Integer, Document> docs) {
//        HashMap<String, PriorityQueue<Document>> index = new HashMap<>();
        HashMap<String, PriorityQueue<Integer>> index = new HashMap<>();
        
        String text;
        Document doc;
        ArrayList<String> terms;
        int id;
        Iterator it = docs.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            doc = (Document) pair.getValue();
            text = doc.getText();
            try {
                terms = stopAndStemmerize(tokenize(text));
                id = (int) pair.getKey();
                for (String term : terms) {
                    if (index.containsKey(term))
                        index.get(term).add(id);
                    else {
                        PriorityQueue<Integer> q = new PriorityQueue<>();
                        q.add(id);
                        index.put(term, q);
                    }
                }                
            } 
            catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);} 
            finally {
                it.remove(); }
        }
        return index;
    }    
    

        
// implement comparator by id in Document
//        PriorityQueue<Document> some = new PriorityQueue<>();
//        PriorityQueue<Integer> some = new PriorityQueue<>();                
//        some.add(1);
//        PriorityQueue<Integer> another = new PriorityQueue<>();                
//        another.add(2);
//        
//        index.put("some", some);     
//        index.put("another", another);
//        return index;
//    }
    

    
    private static String[] getSearchTokens(String[] query) {
        return query;
    }




    
}
