package com.damon.agenda.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.damon.agenda.R;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class VerImagenActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageUrl;
    private PhotoView photoViewAttacher;// libreria que se necesita para hacer zoom las imagenes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_imagen);


        imageView = findViewById(R.id.image_viewer);
        imageUrl = getIntent().getStringExtra("url");

        try {
            photoViewAttacher = findViewById(R.id.image_viewer);
        }catch (Exception e){
            e.printStackTrace();
        }

        Picasso.get().load(imageUrl).into(imageView);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
