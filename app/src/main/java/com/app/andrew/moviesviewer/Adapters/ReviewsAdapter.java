package com.app.andrew.moviesviewer.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.andrew.moviesviewer.DataHolder.Review;
import com.app.andrew.moviesviewer.R;

import java.util.ArrayList;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewHolder> {
    private ArrayList<Review> reviews;

    public ReviewsAdapter(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, null); // todo check if this is parent
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.author.setText(review.getAuthor());
        holder.description.setText(review.getComment());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        public TextView author, description;

        public ReviewHolder(View itemView) {
            super(itemView);
            author = (TextView)itemView.findViewById(R.id.reviewer_name);
            description = (TextView)itemView.findViewById(R.id.reviewer_review);
        }
    }
}

