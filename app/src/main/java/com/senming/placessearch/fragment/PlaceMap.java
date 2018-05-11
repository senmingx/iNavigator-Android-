package com.senming.placessearch.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.senming.placessearch.Adapter.CustomAutoCompleteAdapter;
import com.senming.placessearch.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceMap extends Fragment implements OnMapReadyCallback {

    private static final String BACKEND_URL_PREFIX = "https://cs571-hw8-200018.appspot.com/getLatLng?location=";
    private static final String GOOGLE_DIRECTION_PREFIX = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_DIRECTION_KEY = "AIzaSyDJzq9OnQT-5p0OAhvqACSVVOl0xT8GH3g";

    private double targetLat;
    private double targetLng;

    private GoogleMap mMap;
    private Marker originMarker;
    private PolylineOptions rectOptions;
    private Polyline routePolyline;

    private AutoCompleteTextView originView;
    private Spinner modeView;

    private String polyline;


    public PlaceMap() {
        // Required empty public constructor
    }

    public static PlaceMap newInstance() {
        PlaceMap placeMap = new PlaceMap();
        return placeMap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_place_map, container, false);
        originView = view.findViewById(R.id.map_origin);
        modeView = view.findViewById(R.id.travel_mode);

        if (getActivity().getIntent() != null) {
            targetLat = Double.valueOf(getActivity().getIntent().getStringExtra("place_lat"));
            targetLng = Double.valueOf(getActivity().getIntent().getStringExtra("place_lng"));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set autocomplete
        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(getContext());
        originView.setAdapter(adapter);
        originView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getDirections();
            }
        });

        return view;
    }

    private void getDirections() {
//        String origin = originView.getText().toString();
        final String origin = originView.getText().toString();
        final String mode = modeView.getSelectedItem().toString().toLowerCase();
        String urlGetLatLng = BACKEND_URL_PREFIX + origin;

        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest reqGetLatLng = new StringRequest(Request.Method.GET, urlGetLatLng,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            String status = jsonObj.getString("status");
                            if (status.equals("OK")) {
                                double originLat = Double.parseDouble(jsonObj.getString("lat"));
                                double originLng = Double.parseDouble(jsonObj.getString("lng"));

                                if (originMarker instanceof Marker) {
                                    originMarker.remove();
                                }
                                originMarker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(originLat, originLng))
                                        .title("Origin_Marker"));

                                String urlGetRoutes = GOOGLE_DIRECTION_PREFIX + "origin=" + originLat
                                        + "," + originLng + "&destination=" + targetLat + ","
                                        + targetLng + "&mode=" + mode + "&key=" + GOOGLE_DIRECTION_KEY;
//                                Log.e("location", urlGetRoutes);
                                getRoutes(urlGetRoutes, queue);
                            } else {
                                Toast.makeText(getContext(), "Fail to get position of origin location",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(reqGetLatLng);
    }

    private void getRoutes(String urlGetRoutes, RequestQueue queue) {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlGetRoutes,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        parseJson(response);

                        if (routePolyline != null) {
                            routePolyline.remove();
                        }
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        List<LatLng> routes = PolyUtil.decode(polyline);
                        rectOptions = new PolylineOptions();
                        rectOptions.color(Color.BLUE);
                        for (LatLng point : routes) {
                            rectOptions.add(point);
                            builder.include(point);
                        }
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                        routePolyline = mMap.addPolyline(rectOptions);
                        mMap.animateCamera(cu);
                    }

                    private void parseJson(String response) {
                        try {
                            polyline = "";
                            JSONObject jsonRes = new JSONObject(response);
                            if (jsonRes.getJSONArray("routes") != null) {
                                JSONArray routesArray = jsonRes.getJSONArray("routes");
                                if (routesArray.length() == 0) {
                                    Toast.makeText(getContext(), "Fail to get position of From", Toast.LENGTH_SHORT);
                                } else {
                                    JSONObject route = (JSONObject)routesArray.get(0);
                                    polyline = route.getJSONObject("overview_polyline").getString("points");
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Fail to get position of From", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Fail to get position of From", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(targetLat, targetLng))
                .title("Marker"));
        LatLng target = new LatLng(targetLat, targetLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15));

        modeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDirections();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
