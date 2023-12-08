package com.usi.hikemap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.usi.hikemap.R;
import com.usi.hikemap.model.Route;
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
            // Set the values
            ((TextView)itemView.findViewById(R.id.timestamp_text)).setText(route.getIdRoute());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(route);
                    //Toast.makeText(context, "Redirect to specific route", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }

}




