package com.sdmp.proj4;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private static final int PADDING = 10;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 150;
    private Context mContext;          // This will have to be passed to the ImageView
    private List<Integer> mThumbIds;   // Adapter must store AdapterView's items

    public ImageAdapter(Context c, List<Integer> ids) {
        mContext = c;
        this.mThumbIds = ids;
    }
    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mThumbIds.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;

        // if convertView's not recycled, initialize some attributes
        if (imageView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HEIGHT));
            imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setAdjustViewBounds(true);
            imageView.setTag(imageView);
        }

        //ImageView.setImageResource(mThumbIds[position]);
        imageView.setImageResource(mThumbIds.get(position));
        return imageView;
    }
}
