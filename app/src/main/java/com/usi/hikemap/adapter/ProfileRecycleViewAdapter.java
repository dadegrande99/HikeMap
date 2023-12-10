package com.usi.hikemap.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.usi.hikemap.R;
import com.usi.hikemap.model.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProfileRecycleViewAdapter extends RecyclerView.Adapter<ProfileRecycleViewAdapter.ProfileViewHolder> {

    public static String TAG = "ProfileRecycleViewAdapter";

    public interface OnItemClickListener{
        void onItemClick(Route route);
    }

    private List<Route> routes;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public ProfileRecycleViewAdapter(Context context, List<Route> routes, OnItemClickListener onItemClickListener){
        this.context = context;
        this.routes = routes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_layout, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.bind(routes.get(position));
        Route route = routes.get(position);
    }

    @Override
    public int getItemCount() {
        if(routes != null){
            return routes.size();
        }
        return 0;
    }

//-----------------------

    public class ProfileViewHolder extends RecyclerView.ViewHolder{

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Route route){

            Log.d(TAG, route.getTimestamp());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
            LocalDateTime dateTime = LocalDateTime.parse(route.getTimestamp(), formatter);
            String data = dateTime.getDayOfMonth() + ":" + dateTime.getMonthValue() + ":" + dateTime.getYear() + " : " + dateTime.getHour() + ":" + dateTime.getMinute();

            ((TextView)itemView.findViewById(R.id.date_text)).setText(data);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(route);
                }
            });

        }
    }

}




