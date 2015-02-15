/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package searchLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import org.tartarus.snowball.SnowballStemmer;


public class SearchEngine {
    private static final String CHARSET = "UTF-8";
    private static final String STOP_FILE_ENG = "./resourses/englishStopList";
    private static final String STOP_FILE_RUS = "./resourses/russianStopList";
    
    
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
    

    protected static ArrayList<String> tokenize(String query) {
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
    
    public  static ArrayList<String> stopAndStemmerize(ArrayList<String> words) throws Exception {
      String lang = "english";
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
      return result;
    } 
    
    public static HashSet<String> getStopList (String lang) {
        
        Charset charset = Charset.forName(CHARSET);
        try {
            BufferedReader reader;
            switch (lang) {
                case "russian":
                    reader = Files.newBufferedReader(Paths.get(STOP_FILE_RUS), charset);
                    break;
                case "english":
                default:
                    reader = Files.newBufferedReader(Paths.get(STOP_FILE_ENG), charset);
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
