package com.damon.agenda.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.agenda.R;
import com.damon.agenda.adapters.ChipsAdapter;
import com.damon.agenda.model.Chips;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.rey.material.widget.ProgressView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SeeChips extends AppCompatActivity {


    private RecyclerView recyclerView;
    private String category;
    private DatabaseReference refCategory,refDeleteOrUpdate;
    private FirebaseStorage storage;

    private List<Chips> chipsList  = new ArrayList<>();

    private ChipsAdapter adapter;

    private Dialog dialog;
    private Button btn_noVendido,btn_vendido,btn_search_one_day;
    private ProgressView progressBar;

    private ImageButton btn_scroll;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_chips);

        dialog = new Dialog(this);

        progressBar = findViewById(R.id.progress_linear);

        btn_scroll = findViewById(R.id.btn_scroll);

        recyclerView = findViewById(R.id.rcy_see_chips);

        category = getIntent().getStringExtra("category");

        getSupportActionBar().setTitle("Chip " + category);

        refCategory = FirebaseDatabase.getInstance().getReference().child("Products");

        storage = FirebaseStorage.getInstance();
        refDeleteOrUpdate = FirebaseDatabase.getInstance().getReference().child("Products");


        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(SeeChips.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ChipsAdapter(this,chipsList,refDeleteOrUpdate,storage);
        recyclerView.setAdapter(adapter);

        InitializeDataChips();

        InicializeDialogFilter();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                if ((pastVisibleItem+visibleItemCount) >= totalItemCount){
                    btn_scroll.setVisibility(View.GONE);
                }else {
                    btn_scroll.setVisibility(View.VISIBLE);
                }
            }
        });

        btn_scroll.setOnClickListener(v -> {
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        });

        InitializeCalendary();
    }

    private void InitializeDataChips() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                refCategory.orderByChild("category").equalTo(category).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Chips chips = snapshot.getValue(Chips.class);
//                                System.out.println("Fecha " + chips.getConverDate());
                                chipsList.add(chips);
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                chipsList.sort((c1,c2) -> c1.getConverDate().compareTo(c2.getConverDate()));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                            adapter.notifyDataSetChanged();
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SeeChips.this, "Error en la base de datos \n "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    private void InicializeDialogFilter() {
        dialog.setContentView(R.layout.dialog_filter);

        btn_vendido = dialog.findViewById(R.id.btn_filter_vendido);
        btn_noVendido = dialog.findViewById(R.id.btn_filter_novendido);
        btn_search_one_day = dialog.findViewById(R.id.search_one_day);

        btn_vendido.setOnClickListener(v -> FilterSearch("Vendido"));
        btn_noVendido.setOnClickListener(v -> FilterSearch("No Vendido"));
        btn_search_one_day.setOnClickListener(v -> starTime.show());

        dialog.create();
    }
    DatePickerDialog starTime;
    private void InitializeCalendary(){
        Calendar newCalendar = Calendar.getInstance();
         starTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year,month,dayOfMonth);
                String fecha = new SimpleDateFormat(" dd MMMM yyyy ")
                        .format(newDate.getTime());
                adapter.SearchForOneDay(fecha);
                dialog.dismiss();
            }
        }, newCalendar.get(Calendar.YEAR),newCalendar.get(Calendar.MONTH),newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void FilterSearch(String  search){
        adapter.FilterSearch(search);
        dialog.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_chip,menu);
        MenuItem menuItem =menu.findItem(R.id.search_chip);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setQueryHint("Buscar por numero o codigo");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.CancelTimer();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                adapter.SearchChip(newText);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.filtrar:
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}