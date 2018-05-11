package com.senming.placessearch.DataObjects;

import java.util.Comparator;

public class ReviewOrderDefaultComparator implements Comparator<Review> {
    @Override
    public int compare(Review o1, Review o2) {
        return o1.getOriginalIndex() - o2.getOriginalIndex();
    }
}
