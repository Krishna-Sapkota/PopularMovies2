package com.project.krishna.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.krishna.popularmovies.datamodel.MoviesAdapter;
import com.project.krishna.popularmovies.datamodel.Review;

/**
 * Created by Krishna on 12/17/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private Review[] reviews;
    Context mContext;

    public ReviewAdapter(Context context,Review[] reviews){
        mContext=context;
        this.reviews=reviews;
    }
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context c=parent.getContext();
        int layout_poster=R.layout.reviews_items;
        LayoutInflater layoutInflater=LayoutInflater.from(c);
        View root=layoutInflater.inflate(layout_poster,parent,false);

        return new ReviewViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String author=reviews[position].getAuthor();
        String reviewText=reviews[position].getReviewText();
        holder.authorTV.setText(author);
        holder.reviewTV.setText(reviewText);


    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView authorTV;
        TextView reviewTV;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            authorTV=itemView.findViewById(R.id.review_author);
            reviewTV=itemView.findViewById(R.id.review_text);
        }
    }
}
