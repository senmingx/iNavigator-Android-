package com.senming.placessearch.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.senming.placessearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlacePhotosAdapter extends BaseAdapter {

    private List<String> photoUrls;
    private Context context;

    public PlacePhotosAdapter(List<String> photoUrls, Context context) {
        this.photoUrls = photoUrls;
        this.context = context;
    }

    @Override
    public int getCount() {
        return photoUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return photoUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_photos, parent, false);
        ImageView photoView = (ImageView) convertView.findViewById(R.id.place_photo);
        Picasso.get().load(photoUrls.get(position)).into(photoView);
        return convertView;
    }
}
