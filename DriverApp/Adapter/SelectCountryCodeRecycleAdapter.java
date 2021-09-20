package com.aumento.onlinecabdriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aumento.onlinecabdriver.LoginActivity;
import com.aumento.onlinecabdriver.ModelClass.SelectCountryCodeModel;
import com.aumento.onlinecabdriver.R;

import java.util.List;


public class SelectCountryCodeRecycleAdapter extends RecyclerView.Adapter<SelectCountryCodeRecycleAdapter.MyViewHolder> {

    Context context;
    private List<SelectCountryCodeModel> OfferList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView country_name, country_code;
        LinearLayout selectedCountry;


        public MyViewHolder(View view) {
            super(view);

            country_code = (TextView) view.findViewById(R.id.country_code);
            selectedCountry = view.findViewById(R.id.selected_country);
        }
    }

    public SelectCountryCodeRecycleAdapter(Context context, List<SelectCountryCodeModel> offerList) {
        this.OfferList = offerList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_coutnry_code_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final SelectCountryCodeModel lists = OfferList.get(position);
        holder.country_code.setText(lists.getCountry_code());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof LoginActivity){
                    ((LoginActivity)context).selectedCountry(lists.getCountry_code());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return OfferList.size();
    }

}


