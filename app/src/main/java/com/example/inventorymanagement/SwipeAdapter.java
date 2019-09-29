package com.example.inventorymanagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SwipeAdapter extends PagerAdapter {
    private int[] imageResource;
    private Context context;
    private LayoutInflater layoutInflater;

    public SwipeAdapter(Context context, int[] imgs)
    {
        this.context=context;
        this.imageResource=imgs;
    }

    @Override
    public int getCount() {
        return imageResource.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view==(LinearLayout)o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView =layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.image);
        imageView.setImageResource(imageResource[position]);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
