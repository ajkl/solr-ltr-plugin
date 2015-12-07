package org.test;

public class RankingFeatures {
    double feature1 = 0;
    double feature2 = 0;
    int feature3 = 1;

    RankingFeatures(double feature1,
            double feature2,
            int feature3) {
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.feature3 = feature3;
    }

    String toLibSVMFormat() {
        StringBuilder sb = new StringBuilder();
        return sb.append("1 qid:1 ")
                .append("1:" + feature1)
                .append(" 2:" + feature2)
                .append(" 3:" + feature3)
                .toString();
    }
}