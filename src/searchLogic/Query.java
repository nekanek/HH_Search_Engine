package searchLogic;

import static java.util.Objects.isNull;
import static searchLogic.SearchEngine.*;


public class Query {
    private final String[] queryWords;
    private final String logic;
    private final Integer count;
    private final Integer DEFAULT_COUNT = 10;
    private final String DEFAULT_LOGIC = "or";

    public Query(String query, String logic, Integer count) {
        this.queryWords = stemmerize(filterStopWords(tokenize(query)));
        if (isNull(logic)) this.logic = DEFAULT_LOGIC;
        else this.logic = logic;
        if (isNull(count)) this.count = DEFAULT_COUNT;
        else this.count = count;
        
    }

    
}
