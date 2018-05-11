package com.senming.placessearch.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.senming.placessearch.Adapter.PlaceFavoritesAdapter;
import com.senming.placessearch.DataObjects.PlaceResult;
import com.senming.placessearch.R;
import com.senming.placessearch.Util.SPUtilForPlace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favorites extends Fragment {

    public static final String LOCAL_BROADCAST = "com.senming.placessearch.LOCAL_BROADCAST";

    private List<PlaceResult> places;
    List<PlaceResult> placesOnPage;
    private PlaceFavoritesAdapter pAdapter;
    private Context context;
    private ListView list_places;
    private TextView noResultsView;

    private IntentFilter intentFilter;
    private BroadcastReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;

    public Favorites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("favorite", "onCreateView");
        View view =  inflater.inflate(R.layout.fragment_favorites, container, false);

        context = getContext();
        list_places = (ListView) view.findViewById(R.id.place_favorites_list);
        noResultsView = (TextView) view.findViewById(R.id.no_place_favorites);

        // Deal with broadcast, used to detect empty list
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        intentFilter = new IntentFilter();
        localReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!action.equals(LOCAL_BROADCAST)) {
                    return;
                }

                boolean favoritesIsEmpty = intent.getBooleanExtra("empty_favorites", false);
                if (favoritesIsEmpty) {
                    list_places.setVisibility(View.GONE);
                    noResultsView.setVisibility(View.VISIBLE);
                }
            }
        };
        intentFilter.addAction(LOCAL_BROADCAST);
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpResultsOnView();
        Log.e("favorite", "onResume");
    }

    private void setUpResultsOnView() {
        places = new ArrayList<>();

        Map<String, ?> map = SPUtilForPlace.getAllFavoritePlacesMap(context);
        for (String placeId : map.keySet()) {
            PlaceResult place = (PlaceResult) SPUtilForPlace.getFavoritePlace(context, placeId);
            places.add(place);
        }

        if (places.size() > 0) {
            pAdapter = new PlaceFavoritesAdapter(places, context);
            list_places.setAdapter(pAdapter);
            list_places.setVisibility(View.VISIBLE);
            noResultsView.setVisibility(View.GONE);
        } else {
            list_places.setVisibility(View.GONE);
            noResultsView.setVisibility(View.VISIBLE);
        }
    }

}
