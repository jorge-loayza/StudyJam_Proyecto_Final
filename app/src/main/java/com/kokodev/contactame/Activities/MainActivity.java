package com.kokodev.contactame.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kokodev.contactame.Fragments.AjustesFragment;
import com.kokodev.contactame.Fragments.ContactosFragment;
import com.kokodev.contactame.Fragments.TarjetasFragment;
import com.kokodev.contactame.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private DatabaseReference databaseReferenceUsuarios;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_inicio:
                    getSupportActionBar().setTitle("Contactos");
                    ContactosFragment contactosFragment = new ContactosFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,contactosFragment).commit();
                    return true;
                case R.id.navigation_tarjetas:
                    getSupportActionBar().setTitle("Tarjetas de Contacto");
                    TarjetasFragment tarjetasFragment = new TarjetasFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,tarjetasFragment).commit();
                    return true;
                case R.id.navigation_ajustes:
                    getSupportActionBar().setTitle("Ajustes");
                    AjustesFragment ajustesFragment = new AjustesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,ajustesFragment).commit();
                    return true;
            }
            return false;
        }

    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                }
            }
        };



        databaseReferenceUsuarios = FirebaseDatabase.getInstance().getReference().child("usuarios");
        //databaseReferenceUsuarios.keepSynced(true);

        verificaExisteUsuario();



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
}
