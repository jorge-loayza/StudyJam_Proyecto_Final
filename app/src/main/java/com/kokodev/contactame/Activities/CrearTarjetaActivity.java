package com.kokodev.contactame.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;

public class CrearTarjetaActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton imageButton;
    EditText etCargo,etPagina,etDescripcion,etDireccion,etLocalidad,etOrganizacion,etTelefono;
    Switch aSwitch;
    Button btnCrearTarjeta;
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

            imagenUri = data.getData();
            imageButton.setImageURI(imagenUri);

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

            ruta.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Tarjeta tarjeta = new Tarjeta(cargo,pagina,descripcion,direccion,localidad,organizacion,telefono,downloadUrl.toString(),publico);

                    databaseReference.child(usuarioID).setValue(tarjeta);


                    progressDialog.dismiss();

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });

        }else
        {
            Toast.makeText(getApplicationContext(),"Debe llenar todos los campos",Toast.LENGTH_LONG);
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

        progressDialog = new ProgressDialog(this);

        imageButton.setOnClickListener(this);
        btnCrearTarjeta.setOnClickListener(this);

    }


}
