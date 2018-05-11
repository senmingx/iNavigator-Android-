package com.senming.placessearch.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.senming.placessearch.R;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceInfo extends Fragment  {

    private TextView address;
    private TextView phone;
    private TextView priceLevel;
    private RatingBar rating;
    private TextView googlePage;
    private TextView website;

    private String placeAddress = "";
    private String placePhone = "";
    private int placePriceLevel = -1;
    private double placeRating = -1;
    private String placeGooglePage = "";
    private String placeWebsite = "";

    public PlaceInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_info, container, false);

        address = (TextView) view.findViewById(R.id.place_info_addr);
        phone = (TextView) view.findViewById(R.id.place_info_phone);
        priceLevel = (TextView) view.findViewById(R.id.place_info_price);
        rating = (RatingBar) view.findViewById(R.id.place_info_rating);
        googlePage = (TextView) view.findViewById(R.id.place_info_googlepage);
        website = (TextView) view.findViewById(R.id.place_info_website);

        if (getActivity().getIntent() != null) {
            String jsonResult = getActivity().getIntent().getStringExtra("place_details");
            parseJsonResults(jsonResult);
            setDataToView(view);
        }

        return view;
    }

    private void setDataToView(View view) {
        if (placeAddress.length() > 0) {
            address.setText(placeAddress);
        } else {
            view.findViewById(R.id.place_info_addr_row).setVisibility(View.GONE);
        }

        if (placePhone.length() > 0) {
            phone.setText(placePhone);
        } else {
            view.findViewById(R.id.place_info_phone_row).setVisibility(View.GONE);
        }

        if (placePriceLevel >= 0) {
            String price = "";
            for (int i = 0; i < placePriceLevel; i++) {
                price += "$";
            }
            priceLevel.setText(price);
        } else {
            view.findViewById(R.id.place_info_price_row).setVisibility(View.GONE);
        }

        if (placeRating >= 0) {
            rating.setRating((float) placeRating);
            Log.e("rating", Double.toString(placeRating));
        } else {
            view.findViewById(R.id.place_info_rating_row).setVisibility(View.GONE);
        }

        if (placeGooglePage.length() > 0) {
            googlePage.setText(placeGooglePage);
        } else {
            view.findViewById(R.id.place_info_googlepage_row).setVisibility(View.GONE);
        }

        if (placeWebsite.length() > 0) {
            website.setText(placeWebsite);
        } else {
            view.findViewById(R.id.place_info_website_row).setVisibility(View.GONE);
        }
    }

    /*
        Parse results json object
     */
    private void parseJsonResults(String json) {
        try {
            JSONObject response = new JSONObject(json);
            JSONObject result = response.getJSONObject("result");

            if (result.getString("formatted_address") != null) {
                placeAddress = result.getString("formatted_address");
            }
            if (result.getString("international_phone_number") != null) {
                placePhone = result.getString("international_phone_number");
            }
            if (result.getString("price_level") != null) {
                placePriceLevel = Integer.parseInt(result.getString("price_level"));
            }
            if (result.getString("rating") != null) {
                placeRating = Double.parseDouble(result.getString("rating"));
            }
            if (result.getString("url") != null) {
                placeGooglePage = result.getString("url");
            }
            if (result.getString("website") != null) {
                placeWebsite = result.getString("website");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
