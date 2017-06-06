package com.kokodev.contactame.Activities;


import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kokodev.contactame.Fragments.AjustesFragment;
import com.kokodev.contactame.Fragments.BuscarFragment;
import com.kokodev.contactame.Fragments.ContactosFragment;
import com.kokodev.contactame.Fragments.TarjetasFragment;
import com.kokodev.contactame.R;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;

    private Fragment contactosFragment,tarjetasFragment,ajustesFragment,buscarFragment;

    private DatabaseReference databaseReferenceUsuarios;
    private Boolean sw = false;

    private SoundPool spTap;
    private int resTap;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_inicio:
                    if (resTap != 0) {
                        spTap.play(resTap,1,1,0,0,1);
                    }
                    getSupportActionBar().setTitle(R.string.contactos);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,contactosFragment).commit();
                    return true;
                case R.id.navigation_tarjetas:
                    if (resTap != 0) {
                        spTap.play(resTap,1,1,0,0,1);
                    }
                    getSupportActionBar().setTitle(R.string.tarjetas_de_contactos);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,tarjetasFragment).commit();
                    return true;
                /*case R.id.navigation_buscar:
                    if (resTap != 0) {
                        spTap.play(resTap,1,1,0,0,1);
                    }
                    getSupportActionBar().setTitle(R.string.buscar_tarjetas);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,buscarFragment).commit();
                    return true;*/
                case R.id.navigation_ajustes:
                    if (resTap != 0) {
                        spTap.play(resTap,1,1,0,0,1);
                    }
                    getSupportActionBar().setTitle(R.string.ajustes);
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,ajustesFragment).commit();
                    return true;
            }

            return false;
        }

    };

    private void createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        spTap = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();

        chargeSoundPool();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        spTap = new SoundPool(15, AudioManager.STREAM_MUSIC,0);

        chargeSoundPool();
    }

    public void chargeSoundPool() {
        resTap = spTap.load(getApplicationContext(),R.raw.tap_sound,1);

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactosFragment = new ContactosFragment();
        tarjetasFragment = new TarjetasFragment();
        ajustesFragment = new AjustesFragment();
        buscarFragment = new BuscarFragment();


        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(getApplicationContext(),LogInActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    finish();

                }else {
                    if (!sw){
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,contactosFragment).commit();
                        sw = true;
                    }


                }
            }
        };


        databaseReferenceUsuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
        databaseReferenceUsuarios.keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        verificaExisteUsuario();


        getSupportActionBar().setTitle(R.string.contactos);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        createSoundPool();
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


    private void verificaExisteUsuario() {
        if (firebaseAuth.getCurrentUser() != null){
            final String usuarioID = firebaseAuth.getCurrentUser().getUid();
            databaseReferenceUsuarios.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(usuarioID)){
                        finish();
                        Intent mainIntent = new Intent(getApplicationContext(),CrearPerfilActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }
    public boolean esAlfaNumerica(final String cadena) {
        for(int i = 0; i < cadena.length(); ++i) {
            char caracter = cadena.charAt(i);

            if(!Character.isLetterOrDigit(caracter)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        final String codigo;
        if (result != null){
            if (result.getContents() == null){
                Toast.makeText(getApplicationContext(), R.string.se_cancelo_lectura_qr,Toast.LENGTH_LONG).show();
            }else{
                codigo = result.getContents().toString().trim();
                Log.i("codigo",codigo);
                Pattern patron = Pattern.compile("^[a-zA-Z0-9]+$");
                if (esAlfaNumerica(codigo)) {

                    Query query = databaseReference.child("tarjetas");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getKey();
                            if (dataSnapshot.hasChild(codigo)){
                                databaseReference.child("tarjetas_usuario").child(firebaseAuth.getCurrentUser().getUid()).child(codigo).setValue(true);
                                Toast.makeText(getApplicationContext(), R.string.se_agrego_tarjeta,Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Trjeta no registrada.",Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Toast.makeText(getApplicationContext(), R.string.qr_invalido,Toast.LENGTH_LONG).show();
                }



            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
