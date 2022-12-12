package com.example.practice;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ItemWeatherRecyclerView extends RecyclerView.Adapter<ItemWeatherRecyclerView.ViewHolder> {

    private ArrayList<ItemWeather> weatherList;

    public ItemWeatherRecyclerView(ArrayList<ItemWeather> weatherList) {
        this.weatherList = weatherList;
    }

    @Override
    public ItemWeatherRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemWeather weather = weatherList.get(position);

        holder.temperature.setText("Temperature: " + weather.getTemp().toString() + " °С");
        holder.time.setText("Time: " + weather.getTime());

    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView temperature, time;

        public ViewHolder(View itemView) {

            super(itemView);
            itemView.setOnClickListener(this);
            temperature = itemView.findViewById(R.id.temperature);
            time = itemView.findViewById(R.id.time);
        }

        @Override
        public void onClick(View v) {
            Log.i("DEBUG", "Item RecyclerView Cliqué");
        }
    }
}