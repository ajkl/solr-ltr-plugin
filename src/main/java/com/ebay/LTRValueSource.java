package com.ebay;

import ciir.umass.edu.learning.DenseDataPoint;
import ciir.umass.edu.learning.Ranker;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.DoubleDocValues;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.solr.common.SolrException;
import org.apache.solr.search.FunctionQParser;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by ajkale on 9/18/15.
 */
public class LTRValueSource extends ValueSource {


    protected  final ValueSource sourceQuery;
    protected final ValueSource feature1;
    protected final ValueSource feature2;
    protected final ValueSource feature3;
    protected final Ranker ranker;
    protected final FunctionQParser fqp;

    public LTRValueSource(FunctionQParser fqp,
                        Ranker ranker,
                        ValueSource sourceQuery,
                        ValueSource feature1,
                        ValueSource feature2,
                        ValueSource feature3) {
        /*if (imp == null || ranker == null) {
            throw new SolrException(
                    SolrException.ErrorCode.BAD_REQUEST, "input missing from LTR function"
            );
        }*/
        this.fqp = fqp;
        this.ranker = ranker;
        this.sourceQuery = sourceQuery;
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.feature3 = feature3;
    }

    @Override
    public FunctionValues getValues(Map context, AtomicReaderContext atomicReaderContext) throws IOException {

        final FunctionValues sourceQueryVal = sourceQuery.getValues(context, atomicReaderContext);
        final FunctionValues feature1Val = feature1.getValues(context, atomicReaderContext);
        final FunctionValues feature2Val = feature2.getValues(context, atomicReaderContext);
        final FunctionValues feature3Val = feature3.getValues(context, atomicReaderContext);

        return new DoubleDocValues(this) {

            @Override
            public double doubleVal(int doc) {

                String query = sourceQueryVal.strVal(doc);

                RankingFeatures rankingFeatures = new RankingFeatures(
                        feature1Val.doubleVal(doc),
                        feature2Val.doubleVal(doc),
                        feature3Val.intVal(doc)
                        );

                System.out.println("Document features : " + rankingFeatures.toLibSVMFormat());
                DenseDataPoint dp = new DenseDataPoint(rankingFeatures.toLibSVMFormat());
                final double customScore = ranker.eval(dp);

                System.out.println("Score : " + customScore);
                return customScore;
            }

            @Override
            public String toString(int doc) {
                return ranker.getFeatures().toString();
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return sourceQuery.hashCode();
    }

    @Override
    public String description() {
        return "Learning to Rank" ;
    }
}
