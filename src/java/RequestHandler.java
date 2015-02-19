

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import main.Document;
import main.Main.*;
import static main.Main.createDocs;
import static main.Main.createIndex;
import static searchLogic.SearchEngine.findDocuments;
import static searchLogic.SearchEngine.stopAndStemmerize;
import static searchLogic.SearchEngine.tokenize;

public class RequestHandler extends HttpServlet
{
    private static final HashMap<Integer, Document> docs = createDocs();        
    private static final HashMap<String, PriorityQueue<Integer>> index = createIndex(docs);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        String query = req.getParameter("query");
        
        String logic = req.getParameter("logic").toLowerCase();
        
        String textCount = req.getParameter("count");
        int count = (textCount.equals("")) ? 10 : Integer.parseInt(textCount);
        
        PrintWriter out = resp.getWriter();
        
        
        ArrayList<Integer> resultsList = findDocuments(query, index, logic, docs);
        out.println("<html>");
        out.println("<body>");
        out.println("U asked 4 \"" + query + "\".<br>");
        out.println("With max number of results \"" + count + "\".<br>");
        if (resultsList.isEmpty()) {
            out.println("Unfortunately, no documents were found.<br>");
        }
        else {
            for (Integer i : resultsList) {
                if (count == 0) break;
                out.println(docs.get(i));
                out.println("<br>");
                count--;
            }
        }
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        int id = Integer.parseInt(req.getParameter("id"));
        String text = req.getParameter("text");
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<body>");        
        if (docs.containsKey(id)) {
            out.println("You tried to add document with existing id " + id + ". Please, use another id.<br>");
        }
        else {
            try {
                addDocs(id, text);
                out.println("You added document \"" + text + "\" with id " + id + "<br>");
            } catch (Exception ex) {
                Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        out.println("<a href=\"index.html\">Go back</a>"); 
        out.println("</body>");
        out.println("</html>");
            
    }
    
    private static void addDocs(int id, String text) throws Exception  {
        Document doc = new Document(id, text);
        docs.put(id, doc);
        addDocToIndex(doc);
//        docs.put(2, new Document(2, "Another quoted text will b here"));
    }
    
    public static void addDocToIndex(Document doc) {
        int id = doc.getId();
        ArrayList<String> terms;
        terms = doc.getWords();
        HashMap<String, Integer> tfds = new HashMap<>();
        for (String term : terms) {
            if (tfds.containsKey(term))
                tfds.put(term, tfds.get(term) + 1);
            else
                tfds.put(term, 1);
        }
        for (HashMap.Entry pair : tfds.entrySet()) {
            String term = (String) pair.getKey();
            int tfd = (int) pair.getValue();
            doc.setRelevancy(term, computeRank(term, tfd));
            if (index.containsKey(term)) {
                // recompute rank for every doc with this term. ugly, redo!
                for (Integer d : index.get(term)) {
                    docs.get(d).setRelevancy(term, computeRank(term, computeTFD(term, docs.get(d).getWords())));
                }
                index.get(term).add(id);
            }
            else {
                PriorityQueue<Integer> q = new PriorityQueue<>();
                q.add(id);
                index.put(term, q);
            }                
        }
} 
    
    public static double computeRank (String term, int tfd)  {
//        System.out.println(index..size());
        double idf;
        int D = docs.size();
        if (D == 0) D = 1;        
        if (index.containsKey(term)) {
            int dft = index.get(term).size();
            if (dft == 0) dft = 1;
            idf = tfd * Math.log(D / dft);
            return idf;
        }
        else 
            idf = tfd * Math.log(D);
        return idf;
    }
    
//    public static int computeDFT (String term)  {
//        int dft = 0;
//        for (HashMap.Entry pair : index.entrySet()) {
//                String indexTerm = (String) pair.getKey();
//                int tfd = (int) pair.getValue(); 
//        }
//        return tfd;
//    }   
    
    public static int computeTFD (String term, ArrayList<String> words)  {
        int tfd = 0;
        for (String w : words) {
            if (term.equals(w))
                tfd++;
        }          
        return tfd;
    }
    

}