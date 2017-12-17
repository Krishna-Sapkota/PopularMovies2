package com.project.krishna.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Krishna on 12/17/17.
 */

public class TrailerAdapter extends BaseAdapter{

    private Context mContext;
    private String[]  name;
    private int[] icon;

    public TrailerAdapter(Context context, String[] trailerName, int[] imageIds) {
        mContext = context;
        name = trailerName;
        icon = imageIds;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return name.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }


    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root;
        root = inflater.inflate(R.layout.trailer_list, parent, false);
        TextView nameTV;
        ImageView playIcon;
        playIcon = (ImageView) root.findViewById(R.id.iv_play_icon);
        nameTV = (TextView) root.findViewById(R.id.tv_trailer_name);
        nameTV.setText(name[position]);
        playIcon.setImageResource(icon[position]);

        return (root);
    }
}

