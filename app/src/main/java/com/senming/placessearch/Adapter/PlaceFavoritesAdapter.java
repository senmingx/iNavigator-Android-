package com.senming.placessearch.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.senming.placessearch.DataObjects.PlaceResult;
import com.senming.placessearch.PlaceDetailsActivity;
import com.senming.placessearch.R;
import com.senming.placessearch.Util.SPUtilForPlace;
import com.senming.placessearch.fragment.Favorites;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.senming.placessearch.fragment.SearchForm.TAG;

public class PlaceFavoritesAdapter extends BaseAdapter {
    private static final String GOOGLE_PLACE_DETAILS_API_PREFIX =
            "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private static final String GOOGLE_PLACE_DETAILS_KEY = "AIzaSyCeWOCNwwjtAFFOjXl1DQkl2ZBLbsEdn6E";


    private List<PlaceResult> data;
    private Context context;
    private Intent intent;
    private LocalBroadcastManager localBroadcastManager;

    public PlaceFavoritesAdapter(List<PlaceResult> data, Context context) {
        this.data = data;
        this.context = context;
        this.intent = new Intent();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_places, parent,
                false);
        ImageView icon = (ImageView) convertView.findViewById(R.id.place_icon);
        LinearLayout placeText = (LinearLayout) convertView.findViewById(R.id.place_text);
        TextView name = (TextView) convertView.findViewById(R.id.place_name);
        TextView addr = (TextView) convertView.findViewById(R.id.place_addr);
        ImageButton favor = (ImageButton) convertView.findViewById(R.id.place_favor);
        ImageButton unfavor = (ImageButton) convertView.findViewById(R.id.place_unfavor);
        localBroadcastManager = LocalBroadcastManager.getInstance(parent.getContext());

        favor.setVisibility(View.GONE);
        unfavor.setVisibility(View.VISIBLE);

        name.setText(data.get(position).getName());
        addr.setText(data.get(position).getAddress());
        Picasso.get().load(data.get(position).getIconUrl()).into(icon);

        // Set click listener to item linearlayout, when click, jump to place details activity
        placeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show progress dialog
                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setMessage("Fetching results");
                dialog.setCancelable(false);
                dialog.show();

                intent.setClass(context, PlaceDetailsActivity.class);
                String placeId = data.get(position).getPlaceId();

                // Get place details from google RESTful API
                String place_details_url = GOOGLE_PLACE_DETAILS_API_PREFIX + placeId + "&key="
                        + GOOGLE_PLACE_DETAILS_KEY;
                RequestQueue queue = Volley.newRequestQueue(context);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, place_details_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
//                                Log.e(TAG, response);
                                intent.putExtra("place_name", data.get(position).getName());
                                intent.putExtra("place_id", data.get(position).getPlaceId());
                                intent.putExtra("place_lat", data.get(position).getLat());
                                intent.putExtra("place_lng", data.get(position).getLng());
                                intent.putExtra("place_details", response);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("place_object", data.get(position));
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,"That didn't work!");
                        dialog.dismiss();
                    }
                });
                queue.add(stringRequest);

            }
        });

        // Set click listener to unfavor button
        final View finalConvertView1 = convertView;
        unfavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("unfavor", Integer.toString(position));

                // Remove from SharedPreference
                try {
                    SPUtilForPlace.removeFavoritePlace(context, data.get(position).getPlaceId());

                    // Test if remove successfully.
                    PlaceResult pl = SPUtilForPlace.getFavoritePlace(context, data.get(position).getPlaceId());
                    if (pl != null) {
                        Log.e("SP", pl.getName());
                    } else {
                        Log.e("SP", "Remove successfully");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String text = data.get(position).getName();

                // Remover from list
                data.remove(position);
                notifyDataSetChanged();

                text += " was removed from favorites";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();

                if (data.size() == 0) {
                    final Intent intent = new Intent(Favorites.LOCAL_BROADCAST);
                    intent.putExtra("empty_favorites", true);
                    localBroadcastManager.sendBroadcast(intent);
                }
            }
        });


        return convertView;
    }
}
