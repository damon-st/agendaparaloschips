package com.damon.agenda.prueba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.agenda.viewholder.ChipViewHolder;
import com.damon.agenda.model.Chips;
import com.damon.agenda.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<ChipViewHolder> {

    List<Chips>  chipsList;
    Context context;

    public MyAdapter(Context context){
        this.chipsList  = new ArrayList<>();
        this.context = context;
    }

    public  void addAll(List<Chips> newUser){
        int initSize = chipsList.size();
        chipsList.addAll(newUser);
        notifyItemRangeChanged(initSize,newUser.size());
    }

    public  void  removeLastItem(){
        chipsList.remove(chipsList.size()-1);
    }

    public  String getLasItemId(){
        return  chipsList.get(chipsList.size()-1).getPid();
    }


    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.nuevodisenodechip,parent,false);

        return new ChipViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChipViewHolder holder, int position) {
        Chips chips  = chipsList.get(position);
        holder.txtprdouctName.setText(chips.getCategory());
        Picasso.get().load(chips.getImage()).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(R.mipmap.ic_launcher).into(holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chipsList.size();
    }
}
