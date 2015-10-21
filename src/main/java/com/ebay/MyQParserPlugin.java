package com.ebay;

import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import static org.apache.solr.search.QParser.getParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.SyntaxError;

public class MyQParserPlugin extends QParserPlugin {

    RankerFactory rFact = new RankerFactory();
    Ranker ranker;

    public void init(NamedList args) {
        SolrParams params = SolrParams.toSolrParams(args);
        // handle configuration parameters
        // passed through solrconfig.xml
        ranker = rFact.loadRankerFromFile("mymodel.txt");
    }

    public QParser createParser(String qstr,
                                SolrParams localParams, SolrParams params, SolrQueryRequest req) {

        return new MyParser(qstr, localParams, params, req, ranker);
    }

    private static class MyParser extends QParser {

        private Query innerQuery;
        Ranker customRanker;

        public MyParser(String qstr, SolrParams localParams,
                        SolrParams params, SolrQueryRequest req, Ranker ranker) {
            super(qstr, localParams, params, req);
            try {
                customRanker = ranker;
                QParser parser = getParser(qstr, "lucene", getReq());
                System.out.println("qstr : " + qstr);
                this.innerQuery = parser.parse();
            } catch (SyntaxError ex) {
                throw new RuntimeException("error parsing query", ex);
            }
        }

        public Query parse() throws SyntaxError {
            System.out.println("InnerQuery : " + innerQuery);
            return new MyQuery(innerQuery, customRanker);
        }
    }
}