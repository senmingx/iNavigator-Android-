package com.senming.placessearch.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.senming.placessearch.Adapter.CustomAutoCompleteAdapter;
import com.senming.placessearch.PlacesListActivity;
import com.senming.placessearch.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchForm extends Fragment {

    public static final String RESULTS = "results";
    public static final String TAG = "CurrentLocNearByPlaces";
    private static final int LOC_REQ_CODE = 1;
    private static final String URL_PREFIX = "https://cs571-hw8-200018.appspot.com/";

    private Button search;
    private Button clear;
    private EditText keyword;
    private EditText distance;
    private Spinner category;
    private RadioGroup from;
    private AutoCompleteTextView location;
    private RadioButton currLoc;
    private RadioButton otherLoc;
    private TextView errorKeyword;
    private TextView errorLocation;

    private View view;
    private List<String> autoPlaces = new ArrayList<>();
    private boolean getLoc = false;
    private double curr_lat;
    private double curr_lng;

    public SearchForm() {
        // Required empty public constructor
    }

    public static SearchForm newInstance() {
        SearchForm fragment = new SearchForm();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search_form, container, false);

        search = (Button) view.findViewById(R.id.search);
        clear = (Button) view.findViewById(R.id.clear);
        from = (RadioGroup) view.findViewById(R.id.from);
        keyword = (EditText) view.findViewById(R.id.keyword);
        distance = (EditText) view.findViewById(R.id.distance);
        category = (Spinner) view.findViewById(R.id.category);
        location = (AutoCompleteTextView) view.findViewById(R.id.location);
        currLoc = (RadioButton) view.findViewById(R.id.curr_loc);
        otherLoc = (RadioButton) view.findViewById(R.id.other_loc);
        errorKeyword = (TextView) view.findViewById(R.id.error_keyword);
        errorLocation = (TextView) view.findViewById(R.id.error_location);

        // Request access location permission and get lat lng
        requestCurrentLocation();

        // Default disable location text input, only enable when select other_loc
        location.setEnabled(false);
        from.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (otherLoc.isChecked()) {
                    Log.e("a", "other_loc");
                    location.setEnabled(true);
                } else if (currLoc.isChecked()){
                    Log.e("a", "curr_loc");
                    location.setEnabled(false);
                } else {
                    Log.e("a", "none");
                    location.setEnabled(false);
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keywordText = keyword.getText().toString();
                String locationText  = location.getText().toString();

                if (passValidate(keywordText, locationText)) {
                    Log.e("b", "pass");
                    // Show ProgressDialog
                    ProgressDialog dialog = new ProgressDialog(view.getContext());
                    dialog.setMessage("Fetching results");
                    dialog.setCancelable(false);
                    dialog.show();
                    // Make request
                    makeRequest(dialog);
                } else {
                    Log.e("b", "fail");
                    CharSequence text = "Please fill all fields with errors";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(getActivity(), text, duration).show();
                }
            }

            private void makeRequest(final ProgressDialog dialog) {
                StringBuilder url = new StringBuilder();
                url.append(URL_PREFIX);
                String urlKeyword = keyword.getText().toString();
                String urlCategory = category.getSelectedItem().toString().toLowerCase().
                        replaceAll(" ", "_");
                String urlDistance = distance.getText().toString();
                String urlLat = "34.0266";
                String urlLng = "-118.2831";
                if (getLoc) {
                    urlLat = Double.toString(curr_lat);
                    urlLng = Double.toString(curr_lng);
                }
                String urlFrom = currLoc.isChecked() ? "here" : "other";
                String urlLocation = location.getText().toString();
                url.append("search?").append("keyword=").append(urlKeyword).append("&category=")
                        .append(urlCategory).append("&distance=").append(urlDistance)
                        .append("&lat=").append(urlLat).append("&lng=").append(urlLng)
                        .append("&from=").append(urlFrom).append("&location=").append(urlLocation);
                String urlStr = url.toString();
                Log.e(TAG, urlStr);

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(view.getContext());
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, urlStr,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e(TAG,"Response is: "+ response);
                                // Cancel ProgressDialog
                                dialog.dismiss();

                                // Activate places list activity
                                Intent intent = new Intent();
                                intent.setClass(view.getContext(), PlacesListActivity.class);
                                intent.putExtra(RESULTS, response);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "That didn't work!");
                        Toast.makeText(getActivity(),"Fail to load data from server, please try again",
                                Toast.LENGTH_SHORT).show();
                        // Cancel ProgressDialog
                        dialog.dismiss();
                    }
                });
                stringRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 10000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {

                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }

            private boolean passValidate(String keyword, String location) {
                // First hide error messages
                errorKeyword.setVisibility(View.GONE);
                errorLocation.setVisibility(View.GONE);

                // Do validations and show error msg if has error
                boolean result = true;
                if (keyword.isEmpty() || keyword.trim().isEmpty()) {
                    errorKeyword.setVisibility(View.VISIBLE);
                    result = false;
                }
                if (otherLoc.isChecked() && (location.isEmpty() || location.trim().isEmpty())) {
                    errorLocation.setVisibility(View.VISIBLE);
                    result = false;
                }
                if (!currLoc.isChecked() && !otherLoc.isChecked()) {
                    result = false;
                }
                return result;
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        // Set autocomplete
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(getContext());
        location.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    private void clearFields() {
        location.setEnabled(false);
        keyword.setText("");
        location.setText("");
        distance.setText("");
        from.clearCheck();
        errorKeyword.setVisibility(View.GONE);
        errorLocation.setVisibility(View.GONE);
        category.setSelection(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        clearFields();
    }

    private void requestCurrentLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOC_REQ_CODE);
        }
        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        curr_lat = location.getLatitude();
                        curr_lng = location.getLongitude();
                        getLoc = true;
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
    }

}
