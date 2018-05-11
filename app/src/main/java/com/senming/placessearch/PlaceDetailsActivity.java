package com.senming.placessearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.senming.placessearch.Adapter.TopFragmentPagerAdapterForDetails;
import com.senming.placessearch.DataObjects.PlaceResult;
import com.senming.placessearch.Util.SPUtilForPlace;

import org.json.JSONObject;

public class PlaceDetailsActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private String placeName = "";
    private String placeAddress = "";
    private String placeWebsite = "";

    private GoogleApiClient mGoogleApiClient;

    private PlaceResult place;

    private MenuItem favorMenu;
    private MenuItem unfavorMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setElevation(0);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        TopFragmentPagerAdapterForDetails mAdapter = new TopFragmentPagerAdapterForDetails(
                this.getSupportFragmentManager(), this, mGoogleApiClient);

        mTabLayout = (TabLayout) findViewById(R.id.top_tabs_for_details);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_for_details);

        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,
                R.drawable.layout_divider_vertical));
        linearLayout.setDividerPadding(30);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();

        if (getIntent() != null) {
            placeName = getIntent().getStringExtra("place_name");
            Bundle bundle = getIntent().getExtras();
            place = (PlaceResult) bundle.getSerializable("place_object");
            setTitle(placeName);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        favorMenu = menu.findItem(R.id.action_favor);
        unfavorMenu = menu.findItem(R.id.action_unfavor);
        if (SPUtilForPlace.contains(this, place.getPlaceId())) {
            favorMenu.setVisible(false);
            unfavorMenu.setVisible(true);
        } else {
            favorMenu.setVisible(true);
            unfavorMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_twitter:
                tweet();
                return true;
            case R.id.action_favor:
                favor();
                return true;
            case R.id.action_unfavor:
                unfavor();
                return true;
            default:
                finish();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setupTabIcons() {
        mTabLayout.getTabAt(0).setCustomView(R.layout.tab_top_place_info);
        mTabLayout.getTabAt(1).setCustomView(R.layout.tab_top_place_photos);
        mTabLayout.getTabAt(2).setCustomView(R.layout.tab_top_place_map);
        mTabLayout.getTabAt(3).setCustomView(R.layout.tab_top_place_reviews);
    }

    private void favor() {
        try {
            SPUtilForPlace.saveFavoritePlace(this, place.getPlaceId(), place);
            favorMenu.setVisible(false);
            unfavorMenu.setVisible(true);
            Toast.makeText(this, placeName + "was added to favorites", Toast.LENGTH_SHORT)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unfavor() {
        SPUtilForPlace.removeFavoritePlace(this, place.getPlaceId());
        favorMenu.setVisible(true);
        unfavorMenu.setVisible(false);
        Toast.makeText(this, placeName + "was removed from favorites", Toast.LENGTH_SHORT)
            .show();
    }

    private void tweet() {
        if (getIntent() != null) {
            String response = getIntent().getStringExtra("place_details");
            parseJsonResult(response);

            String twitterUrl = "https://twitter.com/intent/tweet?" + "text=" + "Check out "
                    + placeName + " located at " + placeAddress + ". Website: "
                    + "&url=" + placeWebsite + "&hashtags=TravelAndEntertainmentSearch";

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(twitterUrl));
            startActivity(intent);
        }
    }

    private void parseJsonResult(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            JSONObject result = response.getJSONObject("result");

            if (result.getString("formatted_address") != null) {
                placeAddress = result.getString("formatted_address");
            }

            if (result.getString("website") != null) {
                placeWebsite = result.getString("website");
            } else if (result.getString("url") != null) {
                placeWebsite = result.getString("url");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
