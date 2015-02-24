

import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import main.Document;
import static main.Main.createDocs;
import static main.Main.createIndex;
import org.json.JSONException;
import org.json.JSONObject;
import static searchLogic.SearchEngine.findDocuments;

public class RequestHandler extends HttpServlet
{
    private static final HashMap<String, Document> docs = createDocs();        
    private static final HashMap<String, PriorityQueue<Integer>> index = createIndex(docs);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
//        req.setCharacterEncoding("UTF-8");
        String query = req.getParameter("query");
//        URLEncoder.encode(query, "UTF-8");
        String logic = req.getParameter("logic").toLowerCase();
        
        String textCount = req.getParameter("count");
        int count = (textCount.equals("")) ? 10 : Integer.parseInt(textCount);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
//        out.println("<meta charset=\"UTF-8\">");
        ArrayList<String> resultsList = findDocuments(query, index, logic, docs);
//        Map jsonMap = new LinkedHashMap();
        System.out.println("resList" + resultsList);
        
        
//        JSONObject output = new JSONObject();
//        try {
            String result = "{";
            for (String i : resultsList) {
                if (count == 0) break;
                System.out.println("i"+i);
//                output.append(i,docs.get(i).getText());
//                result += "\"id\":";
                result += "\""+ i+ "\":\"" + docs.get(i).getText() + "\",";
                count--;
            }
            result += "}";
            System.out.println(result);
            out.print(result);
            //            JSONObject output = new JSONObject(result);
//            System.out.println(output.toString());
//                output.write(out); //.writeJSONString(jsonMap, out);
            //            out.print(output);
            //            out.flush();

    //        JSONObject json=new JSONObject();
    //        ArrayList<Integer> resultsList = findDocuments(query, index, logic, docs);
    //        out.println("<html>");
    //        out.println("<body>");
    //        out.println("U asked 4 \"" + query + "\".<br>");
    //        out.println("With max number of results \"" + count + "\".<br>");
    //        if (resultsList.isEmpty()) {
    //            out.println("Unfortunately, no documents were found.<br>");
    //        }
    //        else {
    //            for (Integer i : resultsList) {
    //                if (count == 0) break;
    //                out.println(docs.get(i));
    //                out.println("<br>");
    //                count--;
    //            }
    //        }
    //        out.println("</body>");
    //        out.println("</html>");
//            } catch (JSONException ex) {
//                Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
//            }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
//        int id = Integer.parseInt(req.getParameter("id"));
//        String text = req.getParameter("text");
//        
//        JSONObject object = new JSONObject();
//        object.append(id, text);
         
        try {
//            input = new JSONObject(req.getParameter("mydata")); // this parses the json
//            Iterator it = jObj.keys(); //gets all the keys
//            
//            StringBuilder sb = new StringBuilder();
//            String s;
//            while ((s = req.getReader().readLine()) != null) {
//                sb.append(s);
//            }
//            JSONObject input = new JSONObject(sb); 
//            System.out.println(req.getContentType());
            JSONObject input = new JSONObject(req.getParameter("jsonn"));
        Iterator<?> keys = input.keys();
        PrintWriter out = resp.getWriter();
        while( keys.hasNext() ){
            String id = (String)keys.next();
            String text = (String) input.get(id); // instanceof JSONObject ){

            
        
//            Student student = (Student) gson.fromJson(sb.toString(), Student.class);
 
//            while(it.hasNext())
//            {
//                String key = it.next(); // get key
//                Object o = jObj.get(key); // get value
//                session.putValue(key, o); // store in session
//            } 
        


            
            out.println("<html>");
            out.println("<meta charset=\"UTF-8\">");
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

        }
            out.println("<a href=\"index.html\">Go back</a>"); 
            out.println("</body>");
            out.println("</html>");        
        } catch (JSONException ex) {
            Logger.getLogger(RequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }
    
    private static void addDocs(String id, String text) throws Exception  {
        Document doc = new Document(id, text);
        docs.put(id, doc);
        addDocToIndex(doc);
//        docs.put(2, new Document(2, "Another quoted text will b here"));
    }
    
    public static void addDocToIndex(Document doc) {
        String id = doc.getId();
        ArrayList<String> terms;
        terms = doc.getWords();
        HashMap<String, Integer> tfds = new HashMap<>();
        for (String term : terms) {
            if (tfds.containsKey(term))
                tfds.put(term, tfds.get(term) + 1);
            else
                tfds.put(term, 1);
        }
//        System.out.println("tfds" + tfds.toString() );
        for (HashMap.Entry pair : tfds.entrySet()) {
            String term = (String) pair.getKey();
            int tfd = (int) pair.getValue();
            doc.setRelevancy(term, computeRank(term, tfd));
            if (index.containsKey(term)) {
                // recompute rank for every doc with this term. ugly, redo!
                for (Integer d : index.get(term)) {
                    docs.get(d.toString()).setRelevancy(term, computeRank(term, computeTFD(term, docs.get(d.toString()).getWords())));
                }
                index.get(term).add(Integer.parseInt(id));
            }
            else {
                PriorityQueue<Integer> q = new PriorityQueue<>();
                q.add(Integer.parseInt(id));
                index.put(term, q);
            }                
        }
        docs.toString();
        index.toString();
} 
    
    public static double computeRank (String term, int tfd)  {
        int D = docs.size();
        if (D == 0) D = 1;        
        if (index.containsKey(term)) {
            int dft = index.get(term).size();
            if (dft == 0) dft = 1;
            return tfd * Math.log(D / dft);
        }
        else 
            return tfd * Math.log(D);
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