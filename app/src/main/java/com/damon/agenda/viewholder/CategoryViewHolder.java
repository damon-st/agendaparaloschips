package com.damon.agenda.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.agenda.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public ImageView categoryImage;
    public TextView tituloCategory;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        categoryImage = itemView.findViewById(R.id.imagen_categoria);
        tituloCategory = itemView.findViewById(R.id.texto_categoria);
    }
}
