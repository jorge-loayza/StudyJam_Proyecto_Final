package com.kokodev.contactame.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kokodev.contactame.Objetos.Contact;
import com.kokodev.contactame.Objetos.Contacto;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.regex.Pattern;

public class EditarPerfilActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etNombre,etApellidos,etTelefono;
    private ImageButton ibImagenPerfil;
    private Button btnEditarPerfil,btnCancelarEdicion;
    private Contacto usuario;
    public static final int GALLERY_REQUEST = 1;

    private Uri imagenUri = null;


    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReferenceUsuario;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        getSupportActionBar().setTitle(R.string.edicion_perfil);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("Imagenes_Usuarios");
        databaseReferenceUsuario = FirebaseDatabase.getInstance().getReference().child("usuarios");

        usuario = (Contacto)getIntent().getExtras().getSerializable("usuario");

        initUI();

    }

    private void initUI() {

        etNombre = (EditText) findViewById(R.id.etNombres);
        etApellidos = (EditText) findViewById(R.id.etApellidos);
        etTelefono = (EditText) findViewById(R.id.etTelefono);
        ibImagenPerfil = (ImageButton) findViewById(R.id.ibImagenPerfil);
        btnEditarPerfil = (Button) findViewById(R.id.btnEditarPerfil);
        btnCancelarEdicion = (Button) findViewById(R.id.btnCancelarEdicion);



        etNombre.setText(usuario.getNombres());
        etApellidos.setText(usuario.getApellidos());
        etTelefono.setText(usuario.getTelefono());
        if (usuario.getImagen_usuario() != null){
            setImage(getApplicationContext(),usuario.getImagen_usuario());
        }

        ibImagenPerfil.setOnClickListener(this);
        btnEditarPerfil.setOnClickListener(this);
        btnCancelarEdicion.setOnClickListener(this);

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
            case R.id.btnCancelarEdicion:
                finish();
                break;
            case R.id.btnEditarPerfil:
                editarPerfil();
                break;
        }
    }

    private void editarPerfil() {
        final String nombre = etNombre.getText().toString().trim();
        final String apellidos = etApellidos.getText().toString().trim();
        final String telefono = etTelefono.getText().toString().trim();

        final String usuarioID =firebaseUser.getUid();

        if (!TextUtils.isEmpty(nombre) &&!TextUtils.isEmpty(apellidos)&&!TextUtils.isEmpty(telefono)){
            if (validarNombre(nombre) && validarTelefono(telefono) && validarNombre(apellidos)){
                progressDialog.setMessage("Guardando Cambios...");
                progressDialog.show();

                if (imagenUri == null){
                    databaseReferenceUsuario.child(usuarioID).child("nombres").setValue(nombre);
                    databaseReferenceUsuario.child(usuarioID).child("apellidos").setValue(apellidos);
                    databaseReferenceUsuario.child(usuarioID).child("telefono").setValue(telefono);
                    progressDialog.dismiss();
                    Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(mainIntent);
                    Toast.makeText(getApplicationContext(), R.string.perfil_editado,Toast.LENGTH_LONG).show();
                }else{
                    StorageReference ruta = storageReference.child(usuarioID);
                    ruta.putFile(imagenUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                            databaseReferenceUsuario.child(usuarioID).child("nombres").setValue(nombre);
                            databaseReferenceUsuario.child(usuarioID).child("apellidos").setValue(apellidos);
                            databaseReferenceUsuario.child(usuarioID).child("telefono").setValue(telefono);
                            databaseReferenceUsuario.child(usuarioID).child("imagen_usuario").setValue(downloadUrl);

                            progressDialog.dismiss();

                            Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(mainIntent);
                            Toast.makeText(getApplicationContext(), R.string.perfil_editado,Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }else{
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

    private boolean validarTelefono(String telefono) {
        if (!Patterns.PHONE.matcher(telefono).matches()|| telefono.length()<7) {
            etTelefono.setError(getString(R.string.telefono_inválido));
            return false;
        } else {
            etTelefono.setError(null);
        }

        return true;
    }

    private boolean validarNombre(String nombre) {
        Pattern patron = Pattern.compile("^[a-zA-Z .]+$+");
        if (!patron.matcher(nombre).matches() || nombre.length() > 30) {
            etNombre.setError(getString(R.string.nombre_invalido));
            return false;
        } else {
            etNombre.setError(null);
        }

        return true;
    }
    public void setImage(final Context ctx, final String image){


        Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ibImagenPerfil, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(image).into(ibImagenPerfil);
            }
        });

    }

}
