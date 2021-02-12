package com.damon.agenda.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.damon.agenda.CategoriaChipsActivity;
import com.damon.agenda.R;
import com.damon.agenda.model.CategoryModel;
import com.damon.agenda.ui.SeeChips;
import com.damon.agenda.viewholder.CategoryViewHolder;

import java.util.ArrayDeque;
import java.util.ArrayList;


public  class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private ArrayList<CategoryModel> categoryModelsList;
    private Activity activity;
    private ViewPager2 pager2;

    public CategoryAdapter(ArrayList<CategoryModel> categoryModelsList, Activity activity,ViewPager2 pager2){
        this.categoryModelsList = categoryModelsList;
        this.activity = activity;
        this.pager2 = pager2;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_category_chip,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel categoryModel = categoryModelsList.get(position);
        holder.categoryImage.setImageResource(categoryModel.getImagen());
        holder.tituloCategory.setText(categoryModel.getTitulo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SeeChips.class);
                intent.putExtra("category",categoryModel.getTitulo());
                activity.startActivity(intent);
            }
        });

        if (position == categoryModelsList.size()-2){
            pager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return categoryModelsList.size();
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            categoryModelsList.addAll(categoryModelsList);
            notifyDataSetChanged();
        }
    };
}
