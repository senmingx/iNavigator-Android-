package com.senming.placessearch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.senming.placessearch.DataObjects.Review;
import com.senming.placessearch.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceReviewAdapter extends BaseAdapter {

    private List<Review> rData;
    private Context context;

    public PlaceReviewAdapter(List<Review> rData, Context context) {
        this.rData = rData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return rData.size();
    }

    @Override
    public Object getItem(int position) {
        return rData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.list_reviews, parent, false);
        ImageView authorPhoto = (ImageView) convertView.findViewById(R.id.review_author_photo);
        TextView authorName = (TextView) convertView.findViewById(R.id.review_author_name);
        RatingBar rating = (RatingBar) convertView.findViewById(R.id.review_rating);
        TextView time = (TextView) convertView.findViewById(R.id.review_time);
        TextView text = (TextView) convertView.findViewById(R.id.review_text);

        Picasso.get().load(rData.get(position).getProfilePhotoUrl()).into(authorPhoto);
        authorName.setText(rData.get(position).getAuthorName());
        rating.setRating(rData.get(position).getRating());
        time.setText(rData.get(position).getTime());
        text.setText(rData.get(position).getText());

        final String url = rData.get(position).getAuthorUrl();

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
