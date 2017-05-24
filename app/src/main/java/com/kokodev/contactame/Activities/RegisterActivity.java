package com.kokodev.contactame.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kokodev.contactame.R;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etCorreo,etContrasena;
    private Button btnCerarUsuario,btnCancelar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle(R.string.regitro_de_usuario);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(getApplicationContext(),"Usuario logueado",Toast.LENGTH_LONG);
                } else {

                }
            }
        };


        getSupportActionBar().setTitle("Registro de Usuario");

        etCorreo = (EditText) findViewById(R.id.etCorreo);
        etContrasena = (EditText) findViewById(R.id.etContrasena);
        btnCerarUsuario = (Button) findViewById(R.id.btnCerarUsuario);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);

        btnCerarUsuario.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCerarUsuario:
                crearUsuario();
                break;
            case R.id.btnCancelar:
                finish();
                startActivity(new Intent(getApplicationContext(),LogInActivity.class));
                break;
        }
    }

    private void crearUsuario() {


        String correo = etCorreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (!TextUtils.isEmpty(correo) && !TextUtils.isEmpty(contrasena)){

            if (validarContrasena(contrasena)&&validarCorreo(correo)){
                progressDialog.setMessage(getString(R.string.creando_cuenta));
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.fallo_en_la_autenticacion,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.dismiss();
                            finish();
                            Intent mainIntent = new Intent(getApplicationContext(),CrearPerfilActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            Toast.makeText(getApplicationContext(), R.string.necesitas_crear_una_cuenta,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }else {
            Toast.makeText(getApplicationContext(), R.string.debe_lenar_ambos_campos,Toast.LENGTH_LONG).show();
        }
    }



    private Boolean validarContrasena(String contrasena) {

        if (contrasena.length() < 8) {
            etContrasena.setError(getString(R.string.contraseña_corta));
            return false;
        } else {
            etContrasena.setError(null);
        }

        return true;

    }
    public boolean validarCorreo(String correo){
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError(getString(R.string.correo_invalido));
            return false;
        } else {
            etCorreo.setError(null);
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


}
