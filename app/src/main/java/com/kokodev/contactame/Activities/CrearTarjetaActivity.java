package com.kokodev.contactame.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class CrearTarjetaActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton imageButton;
    EditText etCargo,etPagina,etDescripcion,etDireccion,etLocalidad,etOrganizacion,etTelefono;
    Switch aSwitch;
    Button btnCrearTarjeta,btnCancelarTarjeta;
    private String usuarioID;
    Uri imagenUri = null;


    StorageReference storageReference;

    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceUsuario;


    ProgressDialog progressDialog;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_tarjeta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCrearTarjeta);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crear Tarjeta de Presentación");
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tarjetas");
        databaseReferenceUsuario = FirebaseDatabase.getInstance().getReference().child("usuarios");

        iniciarUI();

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
                imageButton.setImageURI(imagenUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibImagenTarjeta:

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);

                break;
            case R.id.btnCrearTarjeta:

                guardarTarjeta();

                break;
            case R.id.btnCancelarTarjeta:

                finish();

                break;
        }
    }


    private void guardarTarjeta() {

        progressDialog.setMessage("Guardando Tarjeta...");


        final String cargo = etCargo.getText().toString().trim();
        final String pagina = etPagina.getText().toString().trim();
        final String descripcion = etDescripcion.getText().toString().trim();
        final String direccion = etDireccion.getText().toString().trim();
        final String localidad = etLocalidad.getText().toString().trim();
        final String organizacion = etOrganizacion.getText().toString().trim();
        final String telefono = etTelefono.getText().toString().trim();

        final Boolean publico = aSwitch.isChecked();


        if (!TextUtils.isEmpty(cargo) && imagenUri != null){

            progressDialog.show();

            StorageReference ruta = storageReference.child("Imagenes_Tarjetas").child(usuarioID);
            final StorageReference ruta2 = storageReference.child("QR_Tarjetas").child(usuarioID);

            ruta.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri imagenURL = taskSnapshot.getDownloadUrl();


                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(usuarioID, BarcodeFormat.QR_CODE,200,200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        StorageMetadata storageMetadata = new StorageMetadata.Builder()
                                .setContentType("image/jpeg")
                                .build();


                        UploadTask uploadTask = ruta2.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                FirebaseUser usuario= FirebaseAuth.getInstance().getCurrentUser();
                                Tarjeta tarjeta = new Tarjeta(usuarioID,cargo,pagina,descripcion,direccion,localidad,organizacion,telefono,imagenURL.toString(),downloadUrl.toString(),publico);

                                databaseReference.child(usuarioID).setValue(tarjeta);
                            }
                        });


                    }catch (WriterException e){
                        e.printStackTrace();
                    }



                    progressDialog.dismiss();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });

        }else
        {
            Toast.makeText(getApplicationContext(),"Debe llenar todos los campos",Toast.LENGTH_LONG).show();
        }

    }


    private void iniciarUI() {
        usuarioID = getIntent().getExtras().getString("usuarioID");
        imageButton = (ImageButton) findViewById(R.id.ibImagenTarjeta);
        etCargo = (EditText) findViewById(R.id.etCargo);
        etPagina = (EditText) findViewById(R.id.etPaginaWeb);
        etDescripcion = (EditText) findViewById(R.id.etDescripcion);
        etDireccion = (EditText) findViewById(R.id.etDireccion);
        etLocalidad = (EditText) findViewById(R.id.etLocalidad);
        etOrganizacion = (EditText) findViewById(R.id.etOrganizacion);
        etTelefono = (EditText) findViewById(R.id.etTelefono);

        aSwitch = (Switch) findViewById(R.id.swPublico);
        btnCrearTarjeta = (Button) findViewById(R.id.btnCrearTarjeta);
        btnCancelarTarjeta = (Button) findViewById(R.id.btnCancelarTarjeta);

        progressDialog = new ProgressDialog(this);

        imageButton.setOnClickListener(this);
        btnCrearTarjeta.setOnClickListener(this);
        btnCancelarTarjeta.setOnClickListener(this);



    }


}
