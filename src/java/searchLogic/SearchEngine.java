/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package searchLogic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Document;
import org.tartarus.snowball.SnowballStemmer;


public class SearchEngine {
    private static final String CHARSET = "UTF-8";
    private static final String STOP_FILE_ENG = "/docroot/resourses/englishStopList";
    private static final String STOP_FILE_RUS = "/docroot/resourses/russianStopList";
//    private static final String DEFAULT_LOGIC = "and";
    
    private static class Pair {
        int id;
        double relevancy;

        public Pair(int id, double relevancy) {
            this.id = id;
            this.relevancy = relevancy;
        }
        @Override
        public String toString() {
            return "(id:" + id + ", relevancy:" + relevancy +")"; //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    private static class relevancyComparator implements Comparator<Pair> {
        @Override
        public int compare(Pair a, Pair b) {
        return Double.compare(a.relevancy, b.relevancy); 
        }
    }
    
    public static ArrayList<String> findDocuments(String query, HashMap<String, PriorityQueue<Integer>> index, String logic, HashMap<String, Document> docs) {
//        if (!logic.equals("or")) logic = DEFAULT_LOGIC;
//        Charset.forName("UTF-16").encode(query);
        ArrayList<Integer> results= new ArrayList<>();
        try {
            ArrayList<String> qWords = stopAndStemmerize(tokenize(query));
            results = (logic.equals("or")) ? orLogicSearch (qWords,index, docs) : andLogicSearch (qWords, index, docs);
//            return results; 
        } catch (Exception ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<String> stringResults = new ArrayList<>();
        for (Integer result : results) {
            stringResults.add(result.toString());
        }
        
        return stringResults;    
    }

    // so ugly, rewrite !!!!
    public static ArrayList<Integer> andLogicSearch (ArrayList<String> qWords, HashMap<String, PriorityQueue<Integer>> index, HashMap<String, Document> docs) {
        ArrayList<PriorityQueue<Integer>> terms = new ArrayList<>();
        for (String qWord : qWords) {
            if (index.containsKey(qWord)) {
                terms.add(index.get(qWord));
            }
        }
        ArrayList<Integer> results = new ArrayList<>();
        if (terms.isEmpty()) return results;
        
        

        PriorityQueue<Integer> docResults = terms.get(0);
        if (terms.size() != 1) { 
        
            PriorityQueue<Integer> term2;
            PriorityQueue<Integer> tempResults = new PriorityQueue<>();
            for (int i = 1; i < terms.size(); i++) {
                term2 = terms.get(i);
        //start of unique AND logic part - refactorr!!!!                    
                while (!docResults.isEmpty() && !term2.isEmpty()) {
                    if (docResults.peek().equals(term2.peek())) {
                        tempResults.add(docResults.remove());
                        term2.remove();
                    }
                    else if (docResults.peek() < (term2.peek())) {
                        docResults.remove();
                    }
                    else 
                        term2.remove();
                }
                docResults = tempResults;
                tempResults = new PriorityQueue<>();
            }
        }
        //end of unique AND logic part
        
        ArrayList<Pair> allDocsRelevancy = new ArrayList<>();

        double docRelevancy;
        for (Integer id : docResults) {
            String s = id.toString();
            docRelevancy = 0;
            HashMap<String, Double> relevancy = docs.get(s).getRelevancy();
            for (String qWord : qWords) {
                if (relevancy.containsKey(qWord))
                    docRelevancy += relevancy.get(qWord);
            }
            allDocsRelevancy.add(new Pair(id, docRelevancy));
        }
        Collections.sort(allDocsRelevancy, new relevancyComparator());
        System.out.println(allDocsRelevancy.toString());
        for (Pair p : allDocsRelevancy) {
            results.add(p.id);
        }
        return results;
                
    }
    
    public static ArrayList<Integer> orLogicSearch (ArrayList<String> qWords, HashMap<String, PriorityQueue<Integer>> index, HashMap<String, Document> docs) {
        ArrayList<PriorityQueue<Integer>> terms = new ArrayList<>();
        for (String qWord : qWords) {
            if (index.containsKey(qWord)) {
                terms.add(index.get(qWord));
            }
        }
        ArrayList<Integer> results = new ArrayList<>();
        if (terms.isEmpty()) return results;
        
        System.out.println("index " + index);
        System.out.println("docs " + docs);
        System.out.println("qWords " + qWords);
        
        PriorityQueue<Integer> docResults = terms.get(0);
        if (terms.size() != 1) { 
            PriorityQueue<Integer> term2;
            for (int i = 1; i < terms.size(); i++) {
                term2 = terms.get(i);
            // start of unique OR logic part - refactorr!!!!
            // optimize!!!                
                while (!term2.isEmpty()) {
                    if (docResults.contains(term2.peek())) 
                        term2.remove();
                    else  
                        docResults.add(term2.remove());
                }
            }
        }
        //end of unique OR logic part
        
        System.out.println("docResalts " + docResults.toString());
        
        ArrayList<Pair> allDocsRelevancy = new ArrayList<>();
        double docRelevancy;
        for (Integer id : docResults) {
            String s = id.toString();
            docRelevancy = 0;
            HashMap<String, Double> relevancy = docs.get(s).getRelevancy();
            for (String qWord : qWords) {
                if (relevancy.containsKey(qWord))
                    docRelevancy += relevancy.get(qWord);
            }
            allDocsRelevancy.add(new Pair(id, docRelevancy));
        }
        Collections.sort(allDocsRelevancy, new relevancyComparator());
        for (Pair p : allDocsRelevancy) {
            results.add(p.id);
        }
        return results;
                
    }    
//    public static PriorityQueue<Integer> findDocuments(String query, HashMap<String, PriorityQueue<Integer>> index) {
//        PriorityQueue<Integer> results = new PriorityQueue<>();
//        
//        for (String s : index.keySet()) {
//            if (s.equals(query))
//                results.addAll(index.get(s));
//        }
//        return results;
//    }    

    public  static ArrayList<String> tokenize(String query) {
        String[] splits = query.split("\\p{javaWhitespace}");
        ArrayList<String> tokens = new ArrayList<>();
        tokens.addAll(Arrays.asList(splits));
        return tokens;
    }

//    protected static ArrayList<String> filterStopWords(ArrayList<String> tokens) {
//        
//        
//        return null;
//    }
//            
//    public  static ArrayList<String> stemmerize(ArrayList<String> words) throws Exception {
//      String lang = "english";
//      Class stemClass=Class.forName("org.tartarus.snowball.ext." + lang + "Stemmer");
//      SnowballStemmer stemmer=(SnowballStemmer)stemClass.newInstance();
//      for (int i=0; i < words.size(); i++) {
//        stemmer.setCurrent(words.get(i));
//        stemmer.stem();
//        words.set(i,stemmer.getCurrent());
//      }
//      return words;
//    } 
    
    // do smth about encoding
    public  static ArrayList<String> stopAndStemmerize(ArrayList<String> words) throws Exception {
        String lang = "english";
        for (String word : words) {
//            String s = "яя";
//            System.out.println(s.matches("[\\p{IsCyrillic}]+"));
            if(word.matches("[\\p{IsCyrillic}]+")) {lang = "russian"; break; }
            if(word.matches("[a-zA-Z]")) {lang = "english"; break; }
        }
      
        HashSet<String> stopList = getStopList(lang);
        ArrayList<String> result = new ArrayList<>();
        Class stemClass=Class.forName("org.tartarus.snowball.ext." + lang + "Stemmer");
        SnowballStemmer stemmer=(SnowballStemmer)stemClass.newInstance();
        String word;
        for (int i=0; i < words.size(); i++) {
            word = words.get(i);
            if (stopList.contains(word)) continue;
            stemmer.setCurrent(word);
            stemmer.stem();
            result.add(stemmer.getCurrent());
        }       
//        System.out.println(result.toString());
        return result;
    } 
    
    public static HashSet<String> getStopList (String lang) {
        
        Charset charset = Charset.forName(CHARSET);
        try {
            BufferedReader reader;
            switch (lang) {
                case "russian":
                    reader = Files.newBufferedReader(Paths.get(System.getProperty("com.sun.aas.instanceRoot") + STOP_FILE_RUS), charset);
                    break;
                case "english":
                default:
//                    File f = new File();
                    reader = Files.newBufferedReader(Paths.get(System.getProperty("com.sun.aas.instanceRoot") + STOP_FILE_ENG));
//                    reader = Files.newBufferedReader((Thread.currentThread().getContextClassLoader().getResource("emailActivationTemplate.txt")));
//                    reader = Files.newBufferedReader(Paths.get(STOP_FILE_ENG), charset);
            }
            String line;
            String word;
            HashSet<String> stopList;
            stopList = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split("\\p{javaWhitespace}");
                for (int i = 0; i < splits.length; i++) {
                    word = splits[i];
                    stopList.add(word);
                }
            }
            return stopList;
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return new HashSet<>();
    }
    
}
