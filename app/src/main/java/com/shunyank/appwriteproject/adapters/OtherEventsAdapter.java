package com.shunyank.appwriteproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shunyank.appwriteproject.R;
import com.shunyank.appwriteproject.activities.ui.dashboard.AirQualityFragment;
import com.shunyank.appwriteproject.models.MyEventModel;

import java.util.ArrayList;

public class OtherEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MyEventModel> data;
    public interface ClickHandler{
        void onClick(MyEventModel myEventModel);
    }
    public void setData(ArrayList<MyEventModel> data) {
        this.data = data;
    }

    ClickHandler clickHandler;

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new MyEventViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.other_events_item_layout,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof addEventButtonViewHolder){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickHandler.onClick(null);
                }
            });
        }else {
            MyEventModel myEventModel = data.get(position);
            ((MyEventViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickHandler.onClick(myEventModel);
                }
            });
            Glide.with(holder.itemView.getContext()).load(myEventModel.poster_url).into( ((MyEventViewHolder)holder).poster);
            ((MyEventViewHolder)holder).status.setText(myEventModel.status);
            ((MyEventViewHolder)holder).eventname.setText(myEventModel.name);
            ((MyEventViewHolder)holder).address_txt.setText(
                    AirQualityFragment.getAddressFromLatLng(holder.itemView.getContext(),
                           Double.parseDouble( myEventModel.lat)
                            ,
                           Double.parseDouble( myEventModel.geoLong))
            );



        }
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position)==null){
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        if(data==null){
            return 0;
        }
        return data.size();
    }

    public class addEventButtonViewHolder extends RecyclerView.ViewHolder{

        public addEventButtonViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class MyEventViewHolder extends RecyclerView.ViewHolder{

        public ImageView poster;
        public TextView eventname;
        public TextView status,address_txt;
        public MyEventViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.event_poster);
            eventname = itemView.findViewById(R.id.event_name);
            status = itemView.findViewById(R.id.status);
            address_txt = itemView.findViewById(R.id.address_txt);
        }
    }

}
