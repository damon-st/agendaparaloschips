package com.damon.agenda.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.agenda.ItemClickListener;
import com.damon.agenda.R;


public class ChipViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtprdouctName , txtProductDescription,txtProductPrice,txtProductState,txtFecha,txtcodigo;
    public ImageView imageView,mas_opciones,colorEstado;
    public ItemClickListener listener;
    public RelativeLayout categoriaRelative;

    public CardView cardDisign ;
    public ProgressBar progressBar;

    public ChipViewHolder(@NonNull View itemView) {
        super(itemView);


        imageView = itemView.findViewById(R.id.produc_seller_image);
        txtprdouctName = itemView.findViewById(R.id.product_seller_Name);
        txtProductDescription = itemView.findViewById(R.id.product_seller_Description);
        txtProductPrice = itemView.findViewById(R.id.product_seller_Price);
        txtProductState = itemView.findViewById(R.id.product_seller_state);
        txtFecha = itemView.findViewById(R.id.product_fechas);
        txtcodigo = itemView.findViewById(R.id.product_codigo);
        mas_opciones = itemView.findViewById(R.id.mas_opciones);
        colorEstado = itemView.findViewById(R.id.colorEstado);
        categoriaRelative = itemView.findViewById(R.id.categoriaRelative);
        progressBar = itemView.findViewById(R.id.progresbar);
        cardDisign = itemView.findViewById(R.id.cardView);


    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v)
    {
        listener.onClick(v,getAdapterPosition(),false);
    }

}
