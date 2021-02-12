package com.damon.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.damon.agenda.model.Chips;
import com.damon.agenda.ui.VerImagenActivity;
import com.damon.agenda.viewholder.ChipViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CategoriaChipsActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager, layoutManagerDos;
    private String categoryID;

    private DatabaseReference unverifiedProductsRef,Sellerreference,users;
    private ImageButton boton_buscar;
    private EditText texto_buscar;
    private FirebaseStorage storage;

    private RecyclerView recyclerDos;

    private ProgressBar progressBarDos;

    Boolean isScrolling = false;

    int currentItems, totalItems, scrollOutitems;

    LinearLayoutManager layoutManagerTres;

    FirebaseRecyclerAdapter<Chips, ChipViewHolder> adapter;
    FirebaseRecyclerOptions<Chips> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_chips);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        categoryID = getIntent().getStringExtra("category");
        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        layoutManagerTres = new LinearLayoutManager(this);


        storage = FirebaseStorage.getInstance();

        progressBarDos = findViewById(R.id.progresbar2);

        recyclerView = findViewById(R.id.reciclerMain);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerTres);

        recyclerDos = findViewById(R.id.recyclerDos);
        recyclerDos.setHasFixedSize(true);
        layoutManagerDos = new LinearLayoutManager(this);
        recyclerDos.setLayoutManager(layoutManagerDos);


        boton_buscar = findViewById(R.id.boton_buscar);
        texto_buscar = findViewById(R.id.texto_buscar);


        CargarChips();


        texto_buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (after>0){

                    if (recyclerDos.getVisibility()!=View.VISIBLE){
                        recyclerDos.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                }else {
                    if (recyclerDos.getVisibility()!=View.GONE){
                        recyclerDos.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                Buscar(search);
            }
        });

        boton_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = texto_buscar.getText().toString().toLowerCase();

                if (TextUtils.isEmpty(search)){
                    Toast.makeText(CategoriaChipsActivity.this, "Escribe El numero Porfavor", Toast.LENGTH_SHORT).show();
                }else {
                    Buscar(search);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private  void CargarChips(){
        options = new FirebaseRecyclerOptions.Builder<Chips>()
                .setQuery(unverifiedProductsRef.orderByChild("category").equalTo(categoryID)
                        ,Chips.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Chips, ChipViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChipViewHolder holder, int position, @NonNull final Chips model) {



                holder.txtprdouctName.setText(model.getCategory());
                holder.txtProductDescription.setText("Fecha de Registro= :"+model.getDate());
                holder.txtProductPrice.setText("Numero = " + model.getNumero() );
                holder.txtProductState.setText("Estado : "+model.getProductState());
                holder.txtFecha.setText("Fecha de Venta: "+model.getFecha());
                holder.txtcodigo.setText("Codigo de Chip: "+model.getCodigo());

                Picasso.get().load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.imageView);
                    }
                });

                String status = model.getProductState();

                if (status.equals("Vendido")){
                    holder.colorEstado.setImageResource(R.drawable.color_verde);
                    holder.txtProductState.setBackgroundResource(R.drawable.color_verde);

                }else {
                    holder.colorEstado.setImageResource(R.drawable.nuevocolor);
                    holder.txtProductState.setBackgroundResource(R.drawable.nuevocolor);
                }

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(), VerImagenActivity.class);
                        intent.putExtra("url",model.getImage());
                        holder.itemView.getContext().startActivity(intent);
                    }
                });


                holder.mas_opciones.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                final  String productID =model.getPid();
                                final  String fecha = model.getDate();
                                final  String imagen = model.getImage();
                                switch (menuItem.getItemId()) {
                                    case R.id.maracar_vendido:
                                        ChangeProductVenta(productID);
                                        ChangeProductState(productID,"Vendido");
                                        return true;
                                    case R.id.maracar_novendido:
                                        ChangeProductVenta(productID);
                                        ChangeProductState(productID,"No Vendido");
                                        return true;
                                    case  R.id.eliminar_chip:
                                        ELiminarChip(productID,imagen);
                                        return  true;


                                    default:
                                        return false;
                                }
                            }
                        });
                        popupMenu.inflate(R.menu.opciones);
                        popupMenu.show();
                    }
                });





            }

            @NonNull
            @Override
            public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nuevodisenodechip, parent, false);
                ChipViewHolder holder = new ChipViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = layoutManagerTres.getChildCount();
                totalItems = layoutManagerTres.getItemCount();
                scrollOutitems = layoutManagerTres.findFirstVisibleItemPosition();

                if (isScrolling &&(currentItems + scrollOutitems == totalItems)){
                    //data fectch
                    isScrolling = false;
                    //fetchData(adapter);
                }
            }
        });
    }

    private void fetchData(final FirebaseRecyclerAdapter<Chips, ChipViewHolder> adapter) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

            }
        },5000);
    }


    private void ELiminarChip(final String productID, String imagen) {
        final StorageReference reference = storage.getReferenceFromUrl(imagen);
        unverifiedProductsRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CategoriaChipsActivity.this, "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else {
                                Toast.makeText(CategoriaChipsActivity.this, "Error al eliminar la imagen", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(CategoriaChipsActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void ChangeProductVenta(final String productID) {


        AlertDialog.Builder builder = new AlertDialog.Builder(CategoriaChipsActivity.this);
        builder.setTitle("Fecha De Venta");

        final EditText fechaAqui = new EditText(CategoriaChipsActivity.this);
        fechaAqui.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date()));
        builder.setView(fechaAqui);

        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (!fechaAqui.getText().toString().equals("")) {
                    unverifiedProductsRef.child(productID).child("fecha")
                            .setValue(fechaAqui.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CategoriaChipsActivity.this, "Acualizado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();


    }

    private void ChangeProductState(String productID,String venta) {

        unverifiedProductsRef.child(productID).child("productState")
                .setValue(venta)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CategoriaChipsActivity.this, "El chip asido Marcado Como Vendido", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    private void Buscar(String  search){


        options = new FirebaseRecyclerOptions.Builder<Chips>()
                .setQuery(unverifiedProductsRef.orderByChild("numero").startAt(search).endAt(search+"\uf8ff")
                        ,Chips.class)
                .build();


           adapter  = new FirebaseRecyclerAdapter<Chips, ChipViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChipViewHolder holder, int position, @NonNull final Chips model) {


                String  category = model.getCategory();

                if (!category.equals(categoryID)) holder.categoriaRelative.removeAllViews();

                holder.txtprdouctName.setText(model.getCategory());
                holder.txtProductDescription.setText("Fecha de Registro= :"+model.getDate());
                holder.txtProductPrice.setText("Numero = " + model.getNumero() );
                holder.txtProductState.setText("Estado : "+model.getProductState());
                holder.txtFecha.setText("Fecha de Venta: "+model.getFecha());
                holder.txtcodigo.setText("Codigo de Chip: "+model.getCodigo());

                Picasso.get().load(model.getImage()).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.imageView);
                    }
                });

                String status = model.getProductState();

                if (status.equals("Vendido")){
                    holder.colorEstado.setImageResource(R.drawable.color_verde);
                    holder.txtProductState.setBackgroundResource(R.drawable.color_verde);

                }else {
                    holder.colorEstado.setImageResource(R.drawable.nuevocolor);
                    holder.txtProductState.setBackgroundResource(R.drawable.nuevocolor);
                }

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(holder.itemView.getContext(),VerImagenActivity.class);
                        intent.putExtra("url",model.getImage());
                        holder.itemView.getContext().startActivity(intent);
                    }
                });


                holder.mas_opciones.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                final  String productID =model.getPid();
                                final  String fecha = model.getDate();
                                final  String imagen = model.getImage();
                                switch (menuItem.getItemId()) {
                                    case R.id.maracar_vendido:
                                        ChangeProductVenta(productID);
                                        ChangeProductState(productID,"Vendido");
                                        return true;
                                    case R.id.maracar_novendido:
                                        ChangeProductVenta(productID);
                                        ChangeProductState(productID,"No Vendido");

                                    case  R.id.eliminar_chip:
                                        ELiminarChip(productID, imagen);
                                        return  true;

                                    default:
                                        return false;
                                }
                            }
                        });
                        popupMenu.inflate(R.menu.opciones);
                        popupMenu.show();
                    }
                });


            }

            @NonNull
            @Override
            public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nuevodisenodechip, parent, false);
                ChipViewHolder holder = new ChipViewHolder(view);
                return holder;
            }
        };
        recyclerDos.setAdapter(adapter);
        adapter.startListening();
    }

}
