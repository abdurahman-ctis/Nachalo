package com.atakishiyev.playstation__brat_;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private HashMap<String, PSClub.revData> reviews;
    private Iterator<Map.Entry<String, PSClub.revData>> it;
    private LayoutInflater mInflater;

    ReviewAdapter(Context context, HashMap<String, PSClub.revData> reviews) {
        this.mInflater = LayoutInflater.from(context);
        this.reviews = reviews;
        it = this.reviews.entrySet().iterator();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.review_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map.Entry<String, PSClub.revData> pair = it.next();
        holder.uname.setText(pair.getKey());
        holder.review.setText(pair.getValue().reviewContent);
        holder.starText.setText(pair.getValue().rate.toString());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return reviews.size();
    }


    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView uname, review, starText;
        ImageView stars;

        ViewHolder(View itemView) {
            super(itemView);
            starText = itemView.findViewById(R.id.starsText);
            uname = itemView.findViewById(R.id.unameText);
            stars = itemView.findViewById(R.id.imageView);
            review = itemView.findViewById(R.id.reviewText);
        }
    }
}
