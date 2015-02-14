/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package searchLogic;

import java.util.HashMap;
import java.util.PriorityQueue;


public class SearchEngine {
    public static PriorityQueue<Integer> findDocuments(String[] query, HashMap<String, PriorityQueue<Integer>> index) {
        PriorityQueue<Integer> resultList = new PriorityQueue<>();
        for (String s : query) {
            
            
        }
        return resultList;
    }

    public static PriorityQueue<Integer> findDocuments(String query, HashMap<String, PriorityQueue<Integer>> index) {
        PriorityQueue<Integer> results = new PriorityQueue<>();
        for (String s : index.keySet()) {
            if (s.equals(query))
                results.addAll(index.get(s));
        }
        return results;
    }
    

    protected static String[] tokenize(String query) {
        return null;
    }

    protected static String[] filterStopWords(String[] tokens) {
        return null;
    }
    
    protected static String[] stemmerize(String[] words) {
        return null;
    }        
}
