package com.ebay;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ciir.umass.edu.learning.DataPoint;
import ciir.umass.edu.learning.DenseDataPoint;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.CustomScoreProvider;
import org.apache.lucene.queries.CustomScoreQuery;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import ciir.umass.edu.learning.tree.LambdaMART;

public class MyQuery extends CustomScoreQuery {

    //static RankerFactory rFact = new RankerFactory();
    //static Ranker ranker = rFact.loadRankerFromFile("models/mymodel.txt");

    Query _query;
    Ranker _ranker;

    public MyQuery(Query subQuery, Ranker ranker) {
        super(subQuery);
        System.out.println("Query : " + subQuery);
        _query = subQuery;
        _ranker = ranker;
    }

    @Override
    protected CustomScoreProvider getCustomScoreProvider(
            AtomicReaderContext context) throws IOException {
        return new MyScoreProvider(context);
    }

    class MyScoreProvider extends CustomScoreProvider {

//        RankerFactory rFact = new RankerFactory();
//        Ranker ranker = rFact.loadRankerFromFile("mymodel.txt");

        public MyScoreProvider(AtomicReaderContext context) {
            super(context);
        }

        @Override
        public float customScore(int doc, float subQueryScore,
                                 float valSrcScore) throws IOException {
            return customScore(doc, subQueryScore, new float[]{valSrcScore});
        }

        // Rescores by counting the number of terms in the field
        @Override
        public float customScore(int doc, float subQueryScore, float valSrcScores[]) throws IOException {

            long start = System.currentTimeMillis();
            IndexReader r = context.reader();

            Document d = context.reader().document(doc);

            //System.out.println(System.getProperty("user.dir"));

            DenseDataPoint dp = new DenseDataPoint("1 qid:4227542 1:212.0662416738023 2:420 3:303 4:8 5:0 6:3 7:0 8:0 9:13 10:9 11:-0.4444444444444444 12:-4 13:4 14:2 15:2.5 16:4.0 17:1");
            double customScore = _ranker.eval(dp) + Double.parseDouble(d.get("imp").trim());
            //System.out.println(dp.toString());
            //System.out.println("Score :: " + customScore);
            //for(IndexableField f : d.getFields())
            //    System.out.println(f.binaryValue().utf8ToString());

//            Set<Term> terms = new HashSet<Term>();
//            _query.extractTerms( terms );

//            for (Term t : terms)
//                System.out.println(t.text());

            //System.out.println(d.get("source_query"));
            //System.out.println(d.get("imp"));
//            float imp = 0.0f;
//            try
//            {
//                imp = Float.parseFloat(d.get("imp").trim());
//                //System.out.println("float imp = " + imp);
//            }
//            catch (NumberFormatException nfe)
//            {
//                System.out.println("NumberFormatException: " + nfe.getMessage());
//            }
            //System.out.println("return imp = " + imp);
            //System.out.println("start : " + start + "end : " + System.currentTimeMillis());
            return (float) customScore;
        }

//            // Method is called for every
//            // matching document of the subQuery
//
//            Document d = context.reader().document(doc);
//            // plugin external score calculation based on the fields...
//            List fields = d.getFields();
//            // and return the custom score
//            float score = 1.0f;
//            return score;

    }
}