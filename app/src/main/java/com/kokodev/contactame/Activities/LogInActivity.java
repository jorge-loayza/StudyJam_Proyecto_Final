package com.kokodev.contactame.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kokodev.contactame.R;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "LogInActivity";

    private EditText etCorrreo,etContrasena;
    private Button btnIniciarSesion,btnCrearCuenta;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReferenceUsuarios;

    private ProgressDialog progressDialog;

    private SignInButton btnGoogle;

    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etCorrreo = (EditText) findViewById(R.id.etCorreo);
        etContrasena = (EditText) findViewById(R.id.etContrasena);

        btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        btnGoogle = (SignInButton) findViewById(R.id.btnGoogle);
        btnCrearCuenta = (Button) findViewById(R.id.btnCrearCuenta);

        btnIniciarSesion.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        btnCrearCuenta.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();


        databaseReferenceUsuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
        databaseReferenceUsuarios.keepSynced(true);

        progressDialog = new ProgressDialog(this);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            }
        })
        .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
        .build();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            progressDialog.setMessage("Verificando....");
            progressDialog.show();
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                progressDialog.dismiss();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            verificaExisteUsuario();
                        }
                    }
                });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnIniciarSesion:
                logIn();
                break;
            case R.id.btnCrearCuenta:
                goCrearCuenta();
                break;
            case R.id.btnGoogle:
                signIn();
                break;
        }
    }

    private void goCrearCuenta() {
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        finish();
    }

    private void logIn() {

        String correo = etCorrreo.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();

        if (!TextUtils.isEmpty(correo) && !TextUtils.isEmpty(contrasena)) {

            progressDialog.setMessage("Verificando...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        verificaExisteUsuario();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Usuario no registrado.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Debe llenar ambos campos.",Toast.LENGTH_LONG).show();
        }
    }

    private void verificaExisteUsuario() {

        if (firebaseAuth.getCurrentUser() != null){
            final String usuarioID = firebaseAuth.getCurrentUser().getUid();
            databaseReferenceUsuarios.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(usuarioID)){
                        progressDialog.dismiss();
                        finish();
                        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);

                    }else{
                        progressDialog.dismiss();
                        finish();
                        Intent mainIntent = new Intent(getApplicationContext(),CrearPerfilActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        Toast.makeText(getApplicationContext(),"Necesitas crear una cuenta..",Toast.LENGTH_LONG).show();
                        //FirebaseAuth.getInstance().signOut();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
}
