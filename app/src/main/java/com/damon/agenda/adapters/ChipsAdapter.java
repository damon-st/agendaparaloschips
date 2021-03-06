package com.damon.agenda.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.agenda.R;
import com.damon.agenda.ui.VerImagenActivity;
import com.damon.agenda.model.Chips;
import com.damon.agenda.viewholder.ChipViewHolder;
import com.damon.agenda.viewholder.HeaderItemViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ChipsAdapter  extends RecyclerView.Adapter<ChipViewHolder> implements StickyRecyclerHeadersAdapter<HeaderItemViewHolder> {

    private Activity activity;

    private List<Chips> chipsList = new ArrayList<>();
    private List<Chips> searchChipList = new ArrayList<>();

    private DatabaseReference unverifiedProductsRef;
    private FirebaseStorage storage;
    private Timer timer;
    private Calendar calendar;

    public ChipsAdapter(Activity activity, List<Chips> chipsList,DatabaseReference unverifiedProductsRef,FirebaseStorage storage) {
        this.activity = activity;
        this.chipsList = chipsList;
        this.unverifiedProductsRef = unverifiedProductsRef;
        this.storage = storage;
        searchChipList = chipsList;
    }

    @NonNull
    @Override
    public ChipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.nuevodisenodechip,parent,false);
        calendar = Calendar.getInstance();
        return new ChipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipViewHolder holder, int position) {

        Chips model = chipsList.get(position);

//        System.out.println("FECHA " + model.getConverDate());

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
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,holder.imageView, ViewCompat.getTransitionName(holder.imageView));
                activity.startActivity(intent,compat.toBundle());
            }
        });


        holder.mas_opciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(activity, v);
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
                            case R.id.cambiar_fecha_registro:
                                changeProductRegisterDay(position,model);
                                return true;

                            case  R.id.eliminar_chip:
                                AlertDialog.Builder confirm = new AlertDialog.Builder(activity);
                                confirm.setTitle("Eliminar Chip");
                                confirm.setMessage("Estas seguro de que quieres eliminar "+
                                        "una vez eliminado no se podra recuperar jamas?");
                                confirm.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ELiminarChip(productID,imagen);
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
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

        holder.txtProductPrice.setOnClickListener(v -> CopiarPortapapeles(model.getNumero()));

    }

    private void changeProductRegisterDay(int position, Chips model) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar1  = Calendar.getInstance();
                calendar1.set(year,month,dayOfMonth);
                long fecha = calendar1.getTime().getTime();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("formatDate",fecha);
                unverifiedProductsRef.child(model.getPid()).updateChildren(hashMap);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }

    private void CopiarPortapapeles(String data){
        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData texto = ClipData.newPlainText("",data) ;
        clipboardManager.setPrimaryClip(texto);
        Toast.makeText(activity, "Se a copiado al portatapeles", Toast.LENGTH_SHORT).show();
    }

    @Override
    public long getHeaderId(int position) {
        Chips chips = chipsList.get(position);
//        System.out.println("FECHA " + chips.getConverDate());
        return chips.getConverDate().getMonth();
    }

    @Override
    public HeaderItemViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(activity).inflate(R.layout.header_item,parent,false);
        return new HeaderItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderItemViewHolder holder, int i) {
        Chips chips = chipsList.get(i);
        holder.titulo.setText(getMonth(chips.getConverDate().getMonth()) + " " + getYear(chips.getConverDate()));
    }

    private int getYear(Date date){
        calendar.setTime(date);
       return calendar.get(Calendar.YEAR);
    }

    private String getMonth(int position){
        String[] moth = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto",
                        "Septiembre","Octubre","Noviembre","Diciembre"};

        return moth[position];
    }

    @Override
    public int getItemCount() {
        return chipsList.size();
    }

    private void ChangeProductVenta(final String productID) {


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Fecha De Venta");

        final EditText fechaAqui = new EditText(activity);
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
                                        Toast.makeText(activity, "Acualizado", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(activity, "El chip asido Marcado Como Vendido", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ELiminarChip(final String productID, String imagen) {
        final StorageReference reference = storage.getReferenceFromUrl(imagen);
        unverifiedProductsRef.child(productID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(activity, "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                    reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else {
                                Toast.makeText(activity, "Error al eliminar la imagen", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(activity, "Error..", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void SearchChip(String buscar){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (buscar.trim().isEmpty()){
                    chipsList = searchChipList;
                }else {
                    ArrayList<Chips> temp = new ArrayList<>();
                    for (Chips chips : searchChipList){
                        if (chips.getNumero().trim().toLowerCase().contains(buscar.trim().toLowerCase())
                           || chips.getCodigo().trim().toLowerCase().contains(buscar.trim().toLowerCase())){
                            temp.add(chips);
                        }
                    }

                    chipsList = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },500);
    }

    public void CancelTimer(){
        if (timer !=null){
            timer.cancel();
        }
    }

    public void FilterSearch(String filter){
        ArrayList<Chips> temp = new ArrayList<>();
        for (Chips chips : searchChipList){
            if (chips.getProductState().toLowerCase().equals(filter.toLowerCase())){
                temp.add(chips);
            }
        }

        chipsList = temp;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public  void SearchForOneDay(String  day){
        ArrayList<Chips> temp = new ArrayList<>();
        for (Chips chips : searchChipList){
            if (chips.getDate().trim().toLowerCase().contains(day.trim().toLowerCase())){
                temp.add(chips);
            }
        }

        chipsList = temp;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
