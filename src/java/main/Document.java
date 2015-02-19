/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.util.ArrayList;
import java.util.HashMap;
import static searchLogic.SearchEngine.stopAndStemmerize;
import static searchLogic.SearchEngine.tokenize;


public class Document {
    private final int id;
    private final String text;
    private final ArrayList<String> words;
    private final HashMap<String, Double> relevancy = new HashMap<>();
    
    public Document(int i, String text) throws Exception{
        this.id = i;
        this.text = text;
        this.words = stopAndStemmerize(tokenize(text));
    }

    @Override
    public String toString() {
        return "Document{" + "id=" + id + ", text=" + text + '}';
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    public HashMap<String, Double> getRelevancy() {
        return new HashMap<>(relevancy);
    }

    public void setRelevancy(String term, double rank) {
        this.relevancy.put(term, rank);
    }

    public ArrayList<String> getWords() {
        return words;
    }
    
    
    
}
