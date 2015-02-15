package searchLogic;

import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.isNull;
import static searchLogic.SearchEngine.*;


public class Query {
    private final ArrayList<String> queryWords;
    private final String logic;
    private final Integer count;
    private final Integer DEFAULT_COUNT = 10;
    private final String DEFAULT_LOGIC = "or";

    public Query(String query, String logic, Integer count) throws Exception {
        this.queryWords = stopAndStemmerize(tokenize(query));
        if (isNull(logic)) this.logic = DEFAULT_LOGIC;
        else this.logic = logic;
        if (isNull(count)) this.count = DEFAULT_COUNT;
        else this.count = count;
        
    }

    
}
