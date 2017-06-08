package com.kokodev.contactame.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class EditarTarjetaActivity extends AppCompatActivity implements View.OnClickListener{

    private Tarjeta tarjeta;
    private ImageView ibImgTarjeta;
    private EditText etCar,etWeb,etDesc,etDir,etLoc,etOrg,etTel;
    private Switch swPub;
    private Button btnEditarTarjeta,btnCancelarEditTarjeta;

    Uri imagenUri = null;

    private ProgressDialog progressDialog;

    private String usuarioID;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tarjeta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditarTarjeta);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edición de Tarjeta");
        tarjeta = (Tarjeta)getIntent().getExtras().getSerializable("tarjeta");

        ibImgTarjeta = (ImageView) findViewById(R.id.ibImgTarjeta);
        etCar = (EditText) findViewById(R.id.etCar);
        etWeb = (EditText) findViewById(R.id.etWeb);
        etDesc = (EditText) findViewById(R.id.etDesc);
        etDir = (EditText) findViewById(R.id.etDir);
        etLoc = (EditText) findViewById(R.id.etLoc);
        etOrg = (EditText) findViewById(R.id.etOrg);
        etTel = (EditText) findViewById(R.id.etTel);
        swPub = (Switch) findViewById(R.id.swPub);

        setImageTarjeta(getApplicationContext(),tarjeta.getImagenTarjeta());

        ibImgTarjeta.setOnClickListener(this);
        btnEditarTarjeta = (Button) findViewById(R.id.btnEditarTarjeta);
        btnCancelarEditTarjeta = (Button) findViewById(R.id.btnCancelarEditTarjeta);
        btnEditarTarjeta.setOnClickListener(this);
        btnCancelarEditTarjeta.setOnClickListener(this);

        etCar.setText(tarjeta.getCargo());
        etWeb.setText(tarjeta.getPagina());
        etDesc.setText(tarjeta.getDescripcion());
        etDir.setText(tarjeta.getDireccion());
        etLoc.setText(tarjeta.getLocalidad());
        etOrg.setText(tarjeta.getOrganizacion());
        etTel.setText(tarjeta.getTelefono());
        if (tarjeta.getPublico()){
            swPub.setChecked(true);
        }else{
            swPub.setChecked(false);
        }

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tarjetas");
        databaseReferenceUsuario = FirebaseDatabase.getInstance().getReference().child("usuarios");
        usuarioID = tarjeta.getId_tarjeta();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibImgTarjeta:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
                break;
            case R.id.btnEditarTarjeta:
                editarTarjeta();
                break;
            case R.id.btnCancelarEditTarjeta:
                finish();
                break;
        }
    }

    private void editarTarjeta() {


        final String cargo = etCar.getText().toString().trim();
        final String pagina = etWeb.getText().toString().trim();
        final String descripcion = etDesc.getText().toString().trim();
        final String direccion = etDir.getText().toString().trim();
        final String localidad = etLoc.getText().toString().trim();
        final String organizacion = etOrg.getText().toString().trim();
        final String telefono = etTel.getText().toString().trim();

        final Boolean publico = swPub.isChecked();


        if (!TextUtils.isEmpty(cargo)){
            progressDialog.setMessage(getString(R.string.actualizando_tarjeta));
            progressDialog.show();
            if (imagenUri != null){

                StorageReference ruta = storageReference.child("Imagenes_Tarjetas").child(usuarioID);

                ruta.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String imagenURL = taskSnapshot.getDownloadUrl().toString();

                        databaseReference.child(usuarioID).child("cargo").setValue(cargo);
                        databaseReference.child(usuarioID).child("pagina").setValue(pagina);
                        databaseReference.child(usuarioID).child("descripcion").setValue(descripcion);
                        databaseReference.child(usuarioID).child("direccion").setValue(direccion);
                        databaseReference.child(usuarioID).child("localidad").setValue(localidad);
                        databaseReference.child(usuarioID).child("organizacion").setValue(organizacion);
                        databaseReference.child(usuarioID).child("telefono").setValue(telefono);
                        databaseReference.child(usuarioID).child("imagenTarjeta").setValue(imagenURL);
                        databaseReference.child(usuarioID).child("publico").setValue(publico);
                        progressDialog.dismiss();
                        finish();
                    }
                });
            }else{
                databaseReference.child(usuarioID).child("cargo").setValue(cargo);
                databaseReference.child(usuarioID).child("pagina").setValue(pagina);
                databaseReference.child(usuarioID).child("descripcion").setValue(descripcion);
                databaseReference.child(usuarioID).child("direccion").setValue(direccion);
                databaseReference.child(usuarioID).child("localidad").setValue(localidad);
                databaseReference.child(usuarioID).child("organizacion").setValue(organizacion);
                databaseReference.child(usuarioID).child("telefono").setValue(telefono);
                databaseReference.child(usuarioID).child("publico").setValue(publico);
                progressDialog.dismiss();
                finish();
            }

            Toast.makeText(getApplicationContext(), R.string.tarjeta_editada,Toast.LENGTH_LONG).show();
        }else
        {
            Toast.makeText(getApplicationContext(), R.string.debe_lenar_todos_los_campos,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imagenUri = data.getData();
            CropImage.activity(imagenUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(2,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imagenUri = result.getUri();
                ibImgTarjeta.setImageURI(imagenUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void setImageTarjeta(final Context ctx, final String image){
        Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ibImgTarjeta, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(image).into(ibImgTarjeta);
            }
        });
    }

}
