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
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokodev.contactame.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CrearPerfilActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etNombre;
    private ImageButton ibImagenPerfil;
    private Button btnCrearPerfil;

    public static final int GALLERY_REQUEST = 1;

    private Uri imagenUri = null;

    private DatabaseReference databaseReferenceUsuario;

    private FirebaseAuth firebaseAuth;

    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_perfil);
        getSupportActionBar().setTitle("Crear perfil");
        if (getIntent().getExtras() != null){
            GoogleSignInAccount c = (GoogleSignInAccount) getIntent().getExtras().getSerializable("cuenta");
            Toast.makeText(getApplicationContext(),c.getDisplayName(),Toast.LENGTH_LONG).show();
        }



        databaseReferenceUsuario = FirebaseDatabase.getInstance().getReference().child("usuarios");
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("Imagenes_Usuarios");

        etNombre = (EditText) findViewById(R.id.etNombres);
        ibImagenPerfil = (ImageButton) findViewById(R.id.ibImagenPerfil);
        btnCrearPerfil = (Button) findViewById(R.id.btnCrearPerfil);

        ibImagenPerfil.setOnClickListener(this);
        btnCrearPerfil.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibImagenPerfil:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
                break;
            case R.id.btnCrearPerfil:
                iniciarRegistro();
                break;
        }
    }

    private void iniciarRegistro() {
        final String nombre = etNombre.getText().toString().trim();
        final String usuarioID =firebaseAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(nombre) && imagenUri != null){

            progressDialog.setMessage("Creando Perfil...");
            progressDialog.show();
            databaseReferenceUsuario.child(usuarioID);

            StorageReference ruta = storageReference.child(usuarioID);
            ruta.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                    databaseReferenceUsuario.child(usuarioID).child("nombres").setValue(nombre);
                    databaseReferenceUsuario.child(usuarioID).child("imagen_usuario").setValue(downloadUrl);

                    progressDialog.dismiss();

                    Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(mainIntent);
                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imagenUri = data.getData();
            CropImage.activity(imagenUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imagenUri = result.getUri();
                ibImagenPerfil.setImageURI(imagenUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }



}
