package com.ebay;

import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Created by ajkale on 9/18/15.
 */
public class LTRValueSourceParser extends ValueSourceParser {

    RankerFactory rFact = new RankerFactory();
    Ranker ranker;

    public void init(NamedList args) {
        SolrParams params = SolrParams.toSolrParams(args);
        // handle configuration parameters
        // passed through solrconfig.xml
        ranker = rFact.loadRankerFromFile("mymodel.txt");
    }

    @Override
    public ValueSource parse(FunctionQParser fqp) throws SyntaxError {

        ValueSource sourceQuery = fqp.parseValueSource();
        ValueSource feature1 = fqp.parseValueSource();
        ValueSource feature2 = fqp.parseValueSource();
        ValueSource feature3 = fqp.parseValueSource();

        return new LTRValueSource(fqp,
                ranker,
                sourceQuery,
                feature1,
                feature2,
                feature3
                );
    }
}
