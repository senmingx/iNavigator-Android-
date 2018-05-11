package com.senming.placessearch.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.android.gms.common.api.GoogleApiClient;
import com.senming.placessearch.fragment.PlaceInfo;
import com.senming.placessearch.fragment.PlaceMap;
import com.senming.placessearch.fragment.PlacePhotos;
import com.senming.placessearch.fragment.PlaceReviews;

public class TopFragmentPagerAdapterForDetails extends FragmentPagerAdapter {

    final int PAGE_COUNT = 4;
    private String tabTitles[] = {"INFO", "PHOTOS", "MAP", "REVIEWS"};

    private Context context;
    private GoogleApiClient mGoogleApiClient;

    public TopFragmentPagerAdapterForDetails(FragmentManager fm, Context context, GoogleApiClient mGoogleApiClient) {
        super(fm);
        this.context = context;
        this.mGoogleApiClient = mGoogleApiClient;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PlaceInfo();
            case 1:
                return new PlacePhotos();
            case 2:
                return new PlaceMap();
            case 3:
                return new PlaceReviews();
            default:
                return new PlaceInfo();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
