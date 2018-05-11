package com.senming.placessearch.DataObjects;

import java.util.Comparator;

public class ReviewOrderHighestRatingComparator implements Comparator<Review> {

    @Override
    public int compare(Review o1, Review o2) {
        return o2.getRating() - o1.getRating();
    }
}
