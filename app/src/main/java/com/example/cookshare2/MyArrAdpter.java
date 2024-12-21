package com.example.cookshare2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyArrAdpter extends ArrayAdapter<Recipe> {


    public MyArrAdpter(@NonNull Context context, int resource, @NonNull List<Recipe> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.myror, parent, false);
        TextView name = convertView.findViewById(R.id.tvName);
        TextView difficulty = convertView.findViewById(R.id.tvDifficulty);
        ImageView image = convertView.findViewById(R.id.imageView2);

        name.setText(getItem(position).name);
        difficulty.setText(("Difficulty  ") + String.valueOf(getItem(position).difficulty));
        Picasso.get().load(getItem(position).pic).into(image);


        return convertView;
    }
}
