package com.senming.placessearch.DataObjects;

import java.util.Comparator;

public class ReviewOrderLowestRatingComparator implements Comparator<Review> {
    @Override
    public int compare(Review o1, Review o2) {
        return o1.getRating() - o2.getRating();
    }
}
