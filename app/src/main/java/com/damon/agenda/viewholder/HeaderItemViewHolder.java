package com.damon.agenda.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.agenda.R;
import com.google.android.material.textview.MaterialTextView;

public class HeaderItemViewHolder  extends RecyclerView.ViewHolder {

    public MaterialTextView titulo;
    public HeaderItemViewHolder(@NonNull View itemView) {
        super(itemView);

        titulo = itemView.findViewById(R.id.header_title_item);
    }
}
