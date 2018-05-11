package com.senming.placessearch.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.senming.placessearch.Adapter.PlaceReviewAdapter;
import com.senming.placessearch.DataObjects.Review;
import com.senming.placessearch.DataObjects.ReviewOrderDefaultComparator;
import com.senming.placessearch.DataObjects.ReviewOrderHighestRatingComparator;
import com.senming.placessearch.DataObjects.ReviewOrderLeastRecentComparator;
import com.senming.placessearch.DataObjects.ReviewOrderLowestRatingComparator;
import com.senming.placessearch.DataObjects.ReviewOrderMostRecentComparator;
import com.senming.placessearch.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceReviews extends Fragment {

    private static final String YELP_PREFIX = "https://cs571-hw8-200018.appspot.com/getYelpReviews?";

    private List<Review> googleReviews;
    private List<Review> yelpReviews;

    private Context context;
    private PlaceReviewAdapter rAdapter;
    private ListView listReviews;
    private TextView noReviewsView;

    private Spinner provider;
    private Spinner order;

    private String placeName;
    private String placeAddress;
    private String placeCity;
    private String placeState;
    private String placeCountry;

    public PlaceReviews() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_place_reviews, container, false);
        listReviews = (ListView) view.findViewById(R.id.reviews);
        noReviewsView = (TextView) view.findViewById(R.id.no_reviews);
        context = getContext();

        provider = view.findViewById(R.id.review_provider);
        order = view.findViewById(R.id.review_order);

        googleReviews = new ArrayList<>();
        yelpReviews = new ArrayList<>();

        if (getActivity().getIntent() != null) {
            String response = getActivity().getIntent().getStringExtra("place_details");
            Log.e("ResponseReview", response);
            parseJsonData(response);
            getYelpReviews();

            if (googleReviews.size() > 0) {
                rAdapter = new PlaceReviewAdapter(googleReviews, context);
                listReviews.setAdapter(rAdapter);
                noReviewsView.setVisibility(View.GONE);
                listReviews.setVisibility(View.VISIBLE);
            } else {
                noReviewsView.setVisibility(View.VISIBLE);
                listReviews.setVisibility(View.GONE);
            }
        }

        // Set provider
        provider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeProviderAndOrder();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set order
        order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeProviderAndOrder();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void changeProviderAndOrder() {
        String reviewProvider = provider.getSelectedItem().toString();

        // Setup provider and order
        if (reviewProvider.equals("Google reviews")) {
            setProviderAndSort(googleReviews);
        } else {
            setProviderAndSort(yelpReviews);
        }
    }

    private void setProviderAndSort(List<Review> reviews) {
        if (reviews.size() > 0) {
            sortReviews(reviews);
            rAdapter = new PlaceReviewAdapter(reviews, context);
            listReviews.setAdapter(rAdapter);
            noReviewsView.setVisibility(View.GONE);
            listReviews.setVisibility(View.VISIBLE);
        } else {
            noReviewsView.setVisibility(View.VISIBLE);
            listReviews.setVisibility(View.GONE);
        }
    }

    private void sortReviews(List<Review> reviews) {
        String reviewOrder = order.getSelectedItem().toString();
        switch (reviewOrder) {
            case "Default order":
                Collections.sort(reviews, new ReviewOrderDefaultComparator());
                break;
            case "Highest rating":
                Collections.sort(reviews, new ReviewOrderHighestRatingComparator());
                break;
            case "Lowest rating":
                Collections.sort(reviews, new ReviewOrderLowestRatingComparator());
                break;
            case "Most recent":
                Collections.sort(reviews, new ReviewOrderMostRecentComparator());
                break;
            case "Least recent":
                Collections.sort(reviews, new ReviewOrderLeastRecentComparator());
                break;
            default:
                break;
        }
    }

    private void parseJsonData(String response) {
        boolean hasReviews = false;
        placeName = "";
        placeAddress = "";
        placeCity = "";
        placeState = "";
        placeCountry = "";

        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject result = jsonResponse.getJSONObject("result");

            // Get components of yelp url
            placeName = result.getString("name");
            placeAddress = result.getString("formatted_address").split(",")[0];
            JSONArray addrComponents = result.getJSONArray("address_components");
            for (int i = 0; i < addrComponents.length(); i++) {
                JSONObject component = (JSONObject) addrComponents.get(i);
                JSONArray compArray = component.getJSONArray("types");
                for (int j = 0; j < compArray.length(); j++) {
                    String type = compArray.getString(j);
                    switch (type) {
                        case "locality":
                            placeCity = component.getString("short_name");
                            break;
                        case "administrative_area_level_1":
                            placeState = component.getString("short_name");
                            break;
                        case "country":
                            placeCountry = component.getString("short_name");
                            break;
                        default:
                            break;
                    }
                }
            }

            // Parse google reviews
            JSONArray googleRevieswArray = result.getJSONArray("reviews");
            for (int i = 0; i < googleRevieswArray.length(); i++) {
                JSONObject item = (JSONObject) googleRevieswArray.getJSONObject(i);
                String authorName = item.has("author_name") ?
                        item.getString("author_name") : "";
                String authorUrl = item.has("author_url") ?
                        item.getString("author_url") : "";
                String authorPhotoUrl = item.has("profile_photo_url") ?
                        item.getString("profile_photo_url") : "";
                int rating = item.has("rating") ?
                        Integer.valueOf(item.getString("rating")) : 0;
                String text = item.has("text") ?
                        item.getString("text") : "";
                long timestamp = item.has("time") ?
                        Long.valueOf(item.getString("time")) : 0;
                Timestamp ts = new Timestamp(timestamp * 1000);
                String time = ts.toString().substring(0, 19);
                Log.e("Google Review Time", time);

                Review googleReviewItem = new Review(authorName, authorUrl, authorPhotoUrl, rating,
                        time, text, i);
                googleReviews.add(googleReviewItem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getYelpReviews() {
        String yelpUrl = YELP_PREFIX + "name=" + placeName + "&addr=" + placeAddress + "&city="
                + placeCity + "&state=" + placeState + "&country=" + placeCountry;
        Log.e("yelp", yelpUrl);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://www.google.com";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, yelpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseJsonYelp(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void parseJsonYelp(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("results")) {
                JSONArray yelpReviewArr = jsonResponse.getJSONArray("results");
                for (int i = 0; i < yelpReviewArr.length(); i++) {
                    JSONObject item = yelpReviewArr.getJSONObject(i);
                    String authorUrl = item.has("url") ?
                            item.getString("url") : "";
                    String text = item.has("text") ?
                            item.getString("text") : "";
                    int rating = item.has("rating") ?
                            Integer.valueOf(item.getString("rating")) : 0;
                    String time = item.has("time_created") ?
                            item.getString("time_created") : "";

                    // User name and photo
                    JSONObject user = item.getJSONObject("user");
                    String authorPhotoUrl = user.has("image_url") ?
                            user.getString("image_url") : "";
                    String authorName = user.has("name") ?
                            user.getString("name") : "";

                    Review yelpReviewItem = new Review(authorName, authorUrl, authorPhotoUrl,
                            rating, time, text, i);
                    yelpReviews.add(yelpReviewItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
