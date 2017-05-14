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
import com.kokodev.contactame.R;

public class TarjetaActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton imageButton;
    EditText etCargo;
    Switch aSwitch;
    Button btnListo;

    Uri imagenUri = null;

    StorageReference storageReference;

    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("tarjetas");

        iniciarUI();

        progressDialog = new ProgressDialog(this);

        imageButton.setOnClickListener(this);
        btnListo.setOnClickListener(this);

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
            case R.id.btnListo:

                guardarTarjeta();

                break;
        }
    }

    private void guardarTarjeta() {

        progressDialog.setMessage("Guardando Tarjeta...");


        final String cargo = etCargo.getText().toString().trim();
        final Boolean publico = aSwitch.isChecked();

        if (!TextUtils.isEmpty(cargo) && imagenUri != null){

            progressDialog.show();

            StorageReference ruta = storageReference.child("Imagenes_Tarjetas").child(imagenUri.getLastPathSegment());

            ruta.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference nuevaTarjeta = databaseReference.push();
                    nuevaTarjeta.child("cargo").setValue(cargo);
                    nuevaTarjeta.child("publico").setValue(publico);
                    nuevaTarjeta.child("imagenTarjeta").setValue(downloadUrl.toString());

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

        imageButton = (ImageButton) findViewById(R.id.ibImagenTarjeta);
        etCargo = (EditText) findViewById(R.id.etCargo);
        aSwitch = (Switch) findViewById(R.id.swPublico);
        btnListo = (Button) findViewById(R.id.btnListo);

    }


}
