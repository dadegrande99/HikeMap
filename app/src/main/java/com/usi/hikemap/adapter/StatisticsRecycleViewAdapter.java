package com.usi.hikemap.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.usi.hikemap.R;
import com.usi.hikemap.model.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatisticsRecycleViewAdapter extends RecyclerView.Adapter<StatisticsRecycleViewAdapter.StatisticsViewHolder> {

    private static final String TAG = "ProfileRecycleViewAdapter";
    private static final int INITIAL_DISPLAY_COUNT = 3; // Adjust this based on your initial display requirements

    public interface OnItemClickListener {
        void onItemClick(Route route);
    }

    private List<Route> displayedRoutes; // Initially displayed items
    private List<Route> allRoutes;       // All items
    private OnItemClickListener onItemClickListener;
    private Context context;

    public StatisticsRecycleViewAdapter(Context context, List<Route> routes, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.allRoutes = routes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_layout, parent, false);
        return new StatisticsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        holder.bind(allRoutes.get(position));
        Route route = allRoutes.get(position);
    }

    @Override
    public int getItemCount() {
        if (allRoutes != null) {
            return allRoutes.size();
        }
        return 0;
    }

    public class StatisticsViewHolder extends RecyclerView.ViewHolder {

        public StatisticsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Route route) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
            LocalDateTime dateTime = LocalDateTime.parse(route.getTimestamp(), formatter);
            String data = dateTime.getDayOfMonth() + ":" + dateTime.getMonthValue() + ":" + dateTime.getYear() + " : " + dateTime.getHour() + ":" + dateTime.getMinute();

            ((TextView) itemView.findViewById(R.id.timestamp_text)).setText(route.getIdRoute());
            ((TextView) itemView.findViewById(R.id.date_text)).setText(data);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(route);
                }
            });
        }
    }


}
