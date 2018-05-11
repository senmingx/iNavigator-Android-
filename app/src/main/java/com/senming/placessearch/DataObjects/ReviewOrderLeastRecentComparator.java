package com.senming.placessearch.DataObjects;

import java.util.Comparator;

public class ReviewOrderLeastRecentComparator implements Comparator<Review> {
    @Override
    public int compare(Review o1, Review o2) {
        return o1.getTime().compareTo(o2.getTime());
    }
}
