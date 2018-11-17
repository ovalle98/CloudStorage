package com.example.ovall.practicaiv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PICK_IMAGE_REQUEST = 234;

    private ImageView imageView;
    private EditText editText;
    private Button btnSubir, btnAbrir;
    private Uri filePath;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageRef= FirebaseStorage.getInstance().getReference();

        imageView = (ImageView) findViewById(R.id.Imagen);
        editText = (EditText) findViewById(R.id.etNombre);
        btnAbrir = (Button) findViewById(R.id.btnAbrir);
        btnSubir = (Button) findViewById(R.id.btnSubir);

        btnAbrir.setOnClickListener(this);
        btnSubir.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnAbrir){
            showFileChooser();
        }
        if(v.getId() == R.id.btnSubir){
            uploadFile();
        }
    }

    private void showFileChooser(){
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"AbirImagen"),PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data != null && data.getData() !=null){
            //Seleccionamos la imagen
            filePath=data.getData();
            //Convertir un imagenView a bitmap
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFile(){
        if(filePath!= null) {

            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("Cargando0....");

            progressDialog.show();


            //StorageReference riversRef = mStorageRef.child("images/profile.jpg");
            StorageReference riversRef = mStorageRef.child("images/"+editText.getText().toString()+".jpg");
            //Introducimos el URI
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Archivo subido", Toast.LENGTH_LONG).show();
                        }
                    })
                    //POR SI OCURRE UN ERROR
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress= (100* taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int)progress)+ "% Subiendo...");
                        }
                    })
            ;
        }else{
            //Mostrar error
        }
    }


}
