package com.itg8.healthapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.itg8.healthapp.model.AlarmModel;

import java.util.List;

public class AlarmAdapter  extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {

    private List<AlarmModel> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public AlarmAdapter(List<AlarmModel> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_alarm, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AlarmModel movie = moviesList.get(position);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
