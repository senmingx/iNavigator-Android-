package com.senming.placessearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.senming.placessearch.Adapter.PlaceResultAdapter;
import com.senming.placessearch.DataObjects.PlaceResult;
import com.senming.placessearch.fragment.SearchForm;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlacesListActivity extends AppCompatActivity {

    public static final String RESULTS = "results";
    public static final String PLACE_NAME = "name";
    public static final String PLACE_ADDR = "address";
    public static final String PLACE_ICON = "icon";
    public static final String PLACE_LAT = "lat";
    public static final String PLACE_LNG = "lng";
    public static final String PLACE_ID = "place_id";

    private List<PlaceResult> places;
    List<PlaceResult> placesOnPage;
    private PlaceResultAdapter pAdapter;
    private Context context;
    private ListView list_places;
    private TextView noResultsView;
    private Button previous;
    private Button next;

    private int pageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);
        Log.e("Create", "Yes");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        list_places = (ListView) findViewById(R.id.place_item_list);
        noResultsView = (TextView) findViewById(R.id.no_place_results);

        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        places = new ArrayList<>();

        if (getIntent() != null) {
            String jsonResults = getIntent().getStringExtra(SearchForm.RESULTS);
            parseJsonResults(jsonResults);
            int last = places.size() < 20 * (pageIndex + 1) ? places.size() : 20 * (pageIndex + 1);
            placesOnPage = places.subList(20 * pageIndex, last);
            setUpResultsOnView();

            if (places.size() > 20) {
                next.setEnabled(true);
            }

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageIndex++;

                    Log.e("page", Integer.toString(pageIndex));
                    int last = places.size() < 20 * (pageIndex + 1) ? places.size() : 20 * (pageIndex + 1);
                    placesOnPage = places.subList(20 * pageIndex, last);
                    setUpResultsOnView();
                    if (places.size() <= 20 * (pageIndex + 1)) {
                        next.setEnabled(false);
                    } else {
                        next.setEnabled(true);
                    }

                    if (pageIndex < 1) {
                        previous.setEnabled(false);
                    } else {
                        previous.setEnabled(true);
                    }
                }
            });

            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageIndex--;
                    Log.e("page", Integer.toString(pageIndex));

                    int last = places.size() < 20 * (pageIndex + 1) ? places.size() : 20 * (pageIndex + 1);
                    placesOnPage = places.subList(20 * pageIndex, last);
                    setUpResultsOnView();
                    if (places.size() < 20 * (pageIndex + 1)) {
                        next.setEnabled(false);
                    } else {
                        next.setEnabled(true);
                    }

                    if (pageIndex < 1) {
                        previous.setEnabled(false);
                    } else {
                        previous.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        setUpResultsOnView();
        Log.e("Restart", "Yes");
    }

    public void onStop() {
        super.onStop();
        Log.e("Stop", "Yes");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Destroy", "Yes");
    }

    /*
        Parse results json object
     */
    private void parseJsonResults(String json) {
        try {
            JSONObject results = new JSONObject(json);
            JSONArray placesList = results.getJSONArray(RESULTS);
            Log.e("length", Integer.toString(placesList.length()));
            for (int i = 0; i < placesList.length(); i++) {
                JSONObject placeJson = (JSONObject) placesList.get(i);
                PlaceResult place = new PlaceResult();

                place.setName(placeJson.getString(PLACE_NAME));
                place.setAddress(placeJson.getString(PLACE_ADDR));
                place.setIconUrl(placeJson.getString(PLACE_ICON));
                place.setLat(placeJson.getString(PLACE_LAT));
                place.setLng(placeJson.getString(PLACE_LNG));
                place.setPlaceId(placeJson.getString(PLACE_ID));

                places.add(place);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        Show results on screen.
     */
    private void setUpResultsOnView() {
        context = PlacesListActivity.this;
        if (places.size() > 0) {
            pAdapter = new PlaceResultAdapter(placesOnPage, context);
            list_places.setAdapter(pAdapter);
            list_places.setVisibility(View.VISIBLE);
            noResultsView.setVisibility(View.GONE);
        } else {
            list_places.setVisibility(View.GONE);
            noResultsView.setVisibility(View.VISIBLE);
        }
    }
}
