package com.aumento.onlinecabdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aumento.onlinecabdriver.ModelClass.TripHistoryModelClass;
import com.aumento.onlinecabdriver.R;
import com.aumento.onlinecabdriver.Utils.GlobalPreference;

import java.util.List;

public class TripHistoryRecycleAdapter extends RecyclerView.Adapter<TripHistoryRecycleAdapter.MyViewHolder> {

    private final GlobalPreference globalPreference;
    Context context;
    private List<TripHistoryModelClass> tripHistoryList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView routeTV, dateTimeTV, driverNameTV, priceTV;

        public MyViewHolder(View view) {
            super(view);
            routeTV = (TextView) view.findViewById(R.id.routeTextView);
            dateTimeTV = (TextView) view.findViewById(R.id.dateTimeTextView);
            driverNameTV = (TextView) view.findViewById(R.id.driverNameTextView);
            priceTV = (TextView) view.findViewById(R.id.priceTextView);
        }
    }

    public TripHistoryRecycleAdapter(Context context, List<TripHistoryModelClass> tripHistoryList) {
        this.tripHistoryList = tripHistoryList;
        this.context = context;
        globalPreference = new GlobalPreference(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_trip_history_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final TripHistoryModelClass lists = tripHistoryList.get(position);
        holder.routeTV.setText(""+lists.getStart_loc()+" to "+lists.getEnd_loc());
        holder.dateTimeTV.setText(lists.getDate()+" "+lists.getStime());
        holder.driverNameTV.setText(lists.getUser());
        holder.priceTV.setText("₹"+lists.getAmount());

    }


    @Override
    public int getItemCount() {
        return tripHistoryList.size();
    }

}


