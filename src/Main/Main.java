package Main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import static searchLogic.SearchEngine.findDocuments;

public class Main {
    
    

    public static void main(String[] args) {
        
        HashMap<Integer, Document> docs = createDocs();
        
        HashMap<String, PriorityQueue<Integer>> index = createIndex(docs);
        
//        String[] query = getSearchTokens(args);
        String query="some";

        
        System.out.println("Query: " + query);
        PriorityQueue<Integer> resultsList = findDocuments(query, index);
        for (Integer i : resultsList) {
            System.out.println(docs.get(i));
        }
        
    }
    
    private static HashMap<Integer, Document> createDocs() {
        HashMap<Integer, Document> docs = new HashMap<>();
        docs.put(1, new Document(1, "Some quoted text will b here"));
        docs.put(2, new Document(2, "Another quoted text will b here"));
        return docs;
    }  
    
    private static HashMap<String, PriorityQueue<Integer>> createIndex(HashMap<Integer, Document> docs) {
//        HashMap<String, PriorityQueue<Document>> index = new HashMap<>();
        HashMap<String, PriorityQueue<Integer>> index = new HashMap<>();
        
        // implement tokenizer, stemmer
            
        
// implement comparator by id in Document
//        PriorityQueue<Document> some = new PriorityQueue<>();
        PriorityQueue<Integer> some = new PriorityQueue<>();                
        some.add(1);
        PriorityQueue<Integer> another = new PriorityQueue<>();                
        another.add(2);
        
        index.put("some", some);     
        index.put("another", another);
        return index;
    }
    
    private static String[] getSearchTokens(String[] query) {
        return query;
    }




    
}
