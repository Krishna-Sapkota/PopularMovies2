package com.project.krishna.popularmovies.datamodel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.project.krishna.popularmovies.R;

import java.util.List;

/**
 * Created by Krishna on 11/13/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.PosterViewHolder> {
    private List<Movies> moviesList;
    private Context context;
    final private MovieThumbnailClickListener mOnClickListener;

    public interface MovieThumbnailClickListener{
         void onThumnailClick(int clickedIndex);
    }


    public MoviesAdapter(Context c,List<Movies> movies,MovieThumbnailClickListener listener){
        mOnClickListener=listener;
        moviesList=movies;
        context=c;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context c=parent.getContext();
        int layout_poster=R.layout.movies_list;
        LayoutInflater layoutInflater=LayoutInflater.from(c);
        View root=layoutInflater.inflate(layout_poster,parent,false);

        return new PosterViewHolder(root);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        String posterURL=moviesList.get(position).getPosterURL();
        Glide.with(context)
                .load(posterURL)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    public  class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView poster;
        public PosterViewHolder(View itemView) {

            super(itemView);
            poster=itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
         int clickedPosition=getAdapterPosition();
         mOnClickListener.onThumnailClick(clickedPosition);
        }
    }
}
