package com.senming.placessearch.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.senming.placessearch.Adapter.PlacePhotosAdapter;
import com.senming.placessearch.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class PlacePhotos extends Fragment {

    private static final String PHOTO_URL_PREFIX = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference=";
    private static final String PHOTO_API_KEY = "&key=AIzaSyCSneaqCUT7AWUVy_hoZycBE4QE8YvIgnQ";

    private Context context;
    private PlacePhotosAdapter pAdapter;
    private ListView listPhotos;
    private TextView noPhotosView;
    private List<String> urls;

    @SuppressLint("ValidFragment")
    public PlacePhotos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_place_photos, container, false);

        context = getContext();
        urls = new ArrayList<>();
        listPhotos = (ListView) view.findViewById(R.id.place_photos);
        noPhotosView = (TextView) view.findViewById(R.id.no_photos);

        if (getActivity().getIntent() != null) {
            String jsonResponse = getActivity().getIntent().getStringExtra("place_details");
            parseJsonResults(jsonResponse);

            if (urls.size() > 0) {
                pAdapter = new PlacePhotosAdapter(urls, context);
                listPhotos.setAdapter(pAdapter);
                listPhotos.setVisibility(View.VISIBLE);
                noPhotosView.setVisibility(View.GONE);
            } else {
                listPhotos.setVisibility(View.GONE);
                noPhotosView.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private void parseJsonResults(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONObject result = response.getJSONObject("result");
            JSONArray photos = result.getJSONArray("photos");
            for (int i = 0; i < photos.length(); i++) {
                JSONObject photo = photos.getJSONObject(i);
                String photoReference = photo.getString("photo_reference");
                String url = PHOTO_URL_PREFIX + photoReference + PHOTO_API_KEY;
                urls.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
