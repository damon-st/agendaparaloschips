package com.damon.agenda.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.agenda.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import id.zelory.compressor.Compressor;

public class AddChip extends AppCompatActivity {

    private ImageView selecionarFoto, iconoChip;
    private EditText ingresarNumero,ingresarcodigo;
    private RadioGroup GrupoPrincipal;
    private RadioButton movistarRadio, claroRadio, CntRadio, TuentiRadio;


    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef, sellerRef;

    private ProgressDialog loaginBar;
    private Uri imageUri;
    private String myUrl = "";
    private String checker = "";

    private String  numeroCelular, codigochip;
    private int seleccion;
    private String seleccionCheck,saveCurrentDate,saveCurrentTime,productRandomKey, downloadImageUrl;
    private Button añadir;

    private Bitmap bitmap;



    private Dialog dialogProgress;
    private TextView textoDialogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chip);

        if (ActivityCompat.checkSelfPermission(AddChip.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }

        getSupportActionBar().setTitle("Crear Chip");

        dialogProgress = new Dialog(this);

        selecionarFoto = findViewById(R.id.fotochip);
        iconoChip = findViewById(R.id.icono_chips);
        ingresarNumero = findViewById(R.id.texto_numero_chip);
        movistarRadio = findViewById(R.id.radioButton_movistar);
        claroRadio = findViewById(R.id.radioButton_claro);
        CntRadio = findViewById(R.id.radioButton_cnt);
        TuentiRadio = findViewById(R.id.radioButton_tuenti);
        loaginBar = new ProgressDialog(this);
        GrupoPrincipal = findViewById(R.id.GrupoChips);
        añadir = findViewById(R.id.botonChip);
        ingresarcodigo = findViewById(R.id.texto_codigo_chip);

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ValidateProductData();
            }
        });


        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Users");

        selecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        GrupoPrincipal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton_movistar){
                    seleccionCheck = "Movistar";
                    iconoChip.setImageResource(R.drawable.iconomovistar);
                }else if (checkedId == R.id.radioButton_claro){
                    seleccionCheck = "Claro";
                    iconoChip.setImageResource(R.drawable.iconoclaro);
                }else if (checkedId == R.id.radioButton_cnt){
                    seleccionCheck = "Cnt";
                    iconoChip.setImageResource(R.drawable.iconocnt);
                }else if (checkedId == R.id.radioButton_tuenti){
                    seleccionCheck = "Tuenti";
                    iconoChip.setImageResource(R.drawable.iconotuenti);
                }
            }
        });



    }
    private void OpenGallery() {
        checker = "clicked";
        //Comience a recortar la actividad para la imagen adquirida previamente guardada en el dispositivo
        CropImage.activity(imageUri)
              //  .setAspectRatio(1,1)
                .start(AddChip.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();//aqui recueramos
            selecionarFoto.setImageURI(imageUri);//aqui asignamos

        }else {
            Toast.makeText(this, "Error, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddChip.this,AddChip.class));
            finish();
        }

    }

    private void ValidateProductData() {

        numeroCelular = ingresarNumero.getText().toString();
        codigochip =  ingresarcodigo.getText().toString();

        if (imageUri == null){
            Toast.makeText(this, "La imagen del chip es obligatorio", Toast.LENGTH_SHORT).show();
        }else if (seleccionCheck ==null){
            Toast.makeText(this, "Porfavor Selecciona la Categoria ", Toast.LENGTH_SHORT).show();
        } else if (numeroCelular.isEmpty()) {
            Toast.makeText(this, "Porfavor Ingresa El numero celular", Toast.LENGTH_SHORT).show();
        } else if (codigochip.isEmpty()){
            Toast.makeText(this, "Porfavor Ingresa El codigo del Chip", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }


    private void ShowProgress(){
        dialogProgress.setContentView(R.layout.dialogo_progress);
        textoDialogo = dialogProgress.findViewById(R.id.texto_progresso);

        dialogProgress.setCancelable(false);
        dialogProgress.setCanceledOnTouchOutside(false);
        dialogProgress.show();
    }


    private void StoreProductInformation() {

//        loaginBar.setTitle("Añadiendo Chip");
//        loaginBar.setMessage("Porfavor Espera,añadiendo el Chip...");
//        loaginBar.setCanceledOnTouchOutside(false);
//        loaginBar.show();

        ShowProgress();

        if (textoDialogo != null) textoDialogo.setText("Preparando para subir la imagen ");

        Calendar calendar =  Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");//mes año
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");//hora segundos
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate.replace('.', ':').replace(',', ' ') + saveCurrentTime.replace('.', ':').replace(',', ' ');

        //combinamos con la hoora
        final StorageReference filePath = ProductImagesRef.child(imageUri.getLastPathSegment()+productRandomKey+".jpg");


        // primero creamos un archivo con el path de la imagen
        File tumb_filePath = new File(imageUri.getPath());

        //segundo creamos  una compression con la libreria que importamos damos su alto y minuto y calidad
        try {

            bitmap = new Compressor(this)
                    .setMaxWidth(400)
                    .setMaxHeight(400)
                    .setQuality(90)
                    .compressToBitmap(tumb_filePath);

        }catch (IOException e){
            e.printStackTrace();
        }

        // despues creamos un array de bytes para que se suba a Firebase Storage

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
        final  byte[] thumb_bye = byteArrayOutputStream.toByteArray();





        final UploadTask uploadTask = filePath.putBytes(thumb_bye);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AddChip.this, "Error:"+message, Toast.LENGTH_SHORT).show();
               // loaginBar.dismiss();
                dialogProgress.cancel();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Toast.makeText(AddChip.this, "Imagen Guardada con Existo", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();

                           // Toast.makeText(AddChip.this, "Chip imagen guardado ", Toast.LENGTH_SHORT).show();

                            if (textoDialogo != null) textoDialogo.setText("Imagen guardada correctamente");

                            SaveProductInfoDatabase();

                        }
                    }
                });
            }
        });

    }
    float  ventaFecha;
    private void SaveProductInfoDatabase() {


        if (textoDialogo != null) textoDialogo.setText("Preparando para subir los datos");


        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        long fechLong = date.getTime();
        try {
            ventaFecha = date.getTime();
//            System.out.println("venta " + ventaFecha);
////            System.out.println(ventaFecha);
            String output = dateFormat.format(ventaFecha);
            ventaFecha = Long.parseLong(output.replace("-",""));
//            System.out.println(output); // 2013-12-04
//            fecha_tv.setText(output);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        productRandomKey = ProductsRef.push().getKey();


        String fecha = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                .format(new Date());
        HashMap<String ,Object> productMap  = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",fecha);
        productMap.put("time",saveCurrentTime);
        productMap.put("numero",numeroCelular.toLowerCase());
        productMap.put("image",downloadImageUrl);
        productMap.put("category",seleccionCheck);
        productMap.put("codigo",codigochip);
        productMap.put("search",numeroCelular.toLowerCase());
        productMap.put("numerofecha",ventaFecha);
        productMap.put("formatDate",fechLong);

//        //nuevo creado para los vendedores
//        productMap.put("name",sName);
//        productMap.put("address",sAddress);
//        productMap.put("phone",sPhone);
//        productMap.put("email",sEmail);
//        productMap.put("uid",sID);
        //  productMap.put("productState","No Aprovado"); como nose como aser el buscador cuando esta ese estado lo apruebo todo
        productMap.put("productState","No Vendido");
        productMap.put("fecha","");

        String clearCaracter = productRandomKey.replace('.', ':').replace(',', ' ');


        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

//                            Intent intent = new Intent(AddChip.this, MainActivity.class);
//                            startActivity(intent);


                            //loaginBar.dismiss();
                           // Toast.makeText(AddChip.this, "El chip esta añadido Correctamente", Toast.LENGTH_SHORT).show();
                          //  finish();
                            if (textoDialogo != null) textoDialogo.setText("Datos subidos correctamente ala base de datos ");
                            dialogProgress.cancel();
                            ClearDatas();
                        }
                        else {
                            if (textoDialogo != null) textoDialogo.setText("Error al subir los datos ");
                            // loaginBar.dismiss();
                            String message =task.getException().toString();
                            Toast.makeText(AddChip.this, "Error"+message, Toast.LENGTH_SHORT).show();
                            dialogProgress.cancel();
                            finish();
                        }
                    }
                });
    }

    private void ClearDatas() {
         imageUri = null;
         ingresarNumero.setText("");
         ingresarcodigo.setText("");
         GrupoPrincipal.clearCheck();
         selecionarFoto.setImageResource(R.drawable.iconofotoparachip);
         iconoChip.setImageResource(R.drawable.color_blanco);
    }
}
