package com.senming.placessearch.DataObjects;

import java.util.Comparator;

public class ReviewOrderMostRecentComparator implements Comparator<Review> {
    @Override
    public int compare(Review o1, Review o2) {
        return o2.getTime().compareTo(o1.getTime());
    }
}
