package com.damon.agenda.prueba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.damon.agenda.model.Chips;
import com.damon.agenda.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PruebaActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    final  int ITEM_LOAD_COUNT = 11;
    int total_item =0, last_visible_item;
    MyAdapter adapter;
    boolean isLoading= false, isMaxData=false;

    String  last_node ="", last_key ="";

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        recyclerView = findViewById(R.id.recycler_view);

        progressBar = findViewById(R.id.progress);


        getLastKeyFromFirebase();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);


        getUsers();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                total_item = layoutManager.getItemCount();
                last_visible_item = layoutManager.findLastVisibleItemPosition();

                if(!isLoading && total_item <=(last_visible_item + ITEM_LOAD_COUNT) ){
                    getUsers();
                    isLoading=true;
                }
            }
        });


        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMaxData = false;
                last_node = adapter.getLasItemId();
                adapter.removeLastItem();
                adapter.notifyDataSetChanged();
                getLastKeyFromFirebase();
                getUsers();
            }
        });
    }

    private void getUsers() {

        if(!isMaxData){
            Query query;
            if (TextUtils.isEmpty(last_node)){
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Products")
                        .orderByChild("pid")
                        .limitToFirst(ITEM_LOAD_COUNT);
            }else {
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Products")
                        .orderByChild("pid")
                        .startAt(last_node)
                        .limitToFirst(ITEM_LOAD_COUNT);
            }
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()){
                        List<Chips> newWallpa = new ArrayList<>();
                        for (DataSnapshot userSnapsho: dataSnapshot.getChildren()){
                            newWallpa.add(userSnapsho.getValue(Chips.class));
                        }

                        last_node = newWallpa.get(newWallpa.size() -1).getPid();
                        if(!last_node.equals(last_key)){
                            newWallpa.remove(newWallpa.size() -1);
                        }else {
                            last_node = "end"; // fix error infinite load final item
                        }
                        adapter.addAll(newWallpa);
                        isLoading = false;
                    }else {
                        isLoading = false;
                        isMaxData = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isLoading = false;
                }
            });
        }
    }

    private void getLastKeyFromFirebase() {

        //query get last key
        Query getLasKey = FirebaseDatabase.getInstance().getReference()
                .child("Products")
                .orderByChild("pid")
                .limitToFirst(1);

        getLasKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lasKey : dataSnapshot.getChildren()){
                    last_key = lasKey.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PruebaActivity.this, "Cannot get last key", Toast.LENGTH_SHORT).show();
            }
        });
    }
}