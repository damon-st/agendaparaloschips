package com.damon.agenda.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.damon.agenda.R;
import com.damon.agenda.adapters.CategoryAdapter;
import com.damon.agenda.model.CategoryModel;
import com.damon.agenda.model.Chips;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    private ImageView addChip;

    private ArrayList<CategoryModel> categoryModelsList;

    private ViewPager2 pagerCategory;

    private CategoryAdapter categoryAdapter;

    private Handler sliderHandler = new Handler();

    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();

        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference reference = firebaseDatabase.getReference().child("Products");

        addChip = findViewById(R.id.addChip);

        categoryModelsList = new ArrayList<>();
        pagerCategory = findViewById(R.id.pager_main);
        pagerCategory.setClipToPadding(false);
        pagerCategory.setClipChildren(false);
        pagerCategory.setOffscreenPageLimit(3);
        pagerCategory.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float radio = 1-Math.abs(position);
                page.setScaleY(0.85f+radio*0.15f);
            }
        });

        pagerCategory.setPageTransformer(compositePageTransformer);

        categoryModelsList.add(new CategoryModel("Movistar",R.drawable.iconomovistar));
        categoryModelsList.add(new CategoryModel("Claro",R.drawable.iconoclaro));
        categoryModelsList.add(new CategoryModel("Tuenti",R.drawable.iconotuenti));
        categoryModelsList.add(new CategoryModel("Cnt",R.drawable.iconocnt));

        categoryAdapter = new CategoryAdapter(categoryModelsList,this,pagerCategory);

        pagerCategory.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        pagerCategory.setAdapter(categoryAdapter);


        pagerCategory.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunable);
                sliderHandler.postDelayed(sliderRunable,2000);//slide duration 3 seconds
            }
        });

        addChip.setOnClickListener(v -> AddChip());
//        long date  = new Date().getTime();
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                        Chips chips = snapshot.getValue(Chips.class);
//                        HashMap<String,Object> hashMap = new HashMap<>();
//                        hashMap.put("formatDate",date);
//                        reference.child(chips.getPid()).updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError databaseError) {
//                Toast.makeText(MainActivity.this, "Errir "  + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void AddChip(){
        Intent intent = new Intent(MainActivity.this, AddChip.class);
        startActivity(intent);
    }

    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
          if (pagerCategory.getCurrentItem()<categoryModelsList.size()-1){
              pagerCategory.setCurrentItem(pagerCategory.getCurrentItem()+1);
          }else {
              pagerCategory.setCurrentItem(0);
          }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunable,2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteCache(this);
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
