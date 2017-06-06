package com.kokodev.contactame.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kokodev.contactame.Activities.AcercaActivity;

import com.kokodev.contactame.Activities.CrearTarjetaActivity;
import com.kokodev.contactame.Activities.EditarPerfilActivity;
import com.kokodev.contactame.Activities.TarjetaActivity;
import com.kokodev.contactame.Objetos.Contacto;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AjustesFragment extends Fragment implements View.OnClickListener{

    private View view;
    private LinearLayout llAcerca,llSalir,llMiTarjeta,llEditarPerfil;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    public AjustesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);

        llAcerca = (LinearLayout) view.findViewById(R.id.llAcerca);
        llEditarPerfil = (LinearLayout) view.findViewById(R.id.llEditarPerfil);
        llSalir = (LinearLayout) view.findViewById(R.id.llSalir);
        llMiTarjeta = (LinearLayout) view.findViewById(R.id.llMiTarjeta);
        llAcerca.setOnClickListener(this);
        llSalir.setOnClickListener(this);
        llMiTarjeta.setOnClickListener(this);
        llEditarPerfil.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llEditarPerfil:
                pasarUsuario(firebaseAuth.getCurrentUser().getUid());
                break;
            case R.id.llMiTarjeta:
                tieneTarjeta();
                break;
            case R.id.llAcerca:
                startActivity(new Intent(getContext(), AcercaActivity.class));
                break;
            case R.id.llSalir:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false).setMessage(R.string.mensaje_salida);
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseAuth.getInstance().signOut();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //setHasOptionsMenu(true);
    }


    private void tieneTarjeta() {
        final String usuarioID = firebaseAuth.getCurrentUser().getUid().toString();
        databaseReference.child("tarjetas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(usuarioID)){
                    pasarTarjeta(usuarioID);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false).setMessage("No cuenta con una tarjeta de presentación. ¿Desea Crear Una?");
                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getContext(), CrearTarjetaActivity.class);
                            intent.putExtra("usuarioID",usuarioID);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void pasarTarjeta(String usuarioID) {

        databaseReference.child("tarjetas").child(usuarioID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Tarjeta tarjeta = dataSnapshot.getValue(Tarjeta.class);
                Intent intent = new Intent(getContext(), TarjetaActivity.class);
                intent.putExtra("tarjeta",tarjeta);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void pasarUsuario(String usuarioID) {

        databaseReference.child("usuarios").child(usuarioID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contacto contacto = dataSnapshot.getValue(Contacto.class);
                Intent intent = new Intent(getContext(), EditarPerfilActivity.class);
                intent.putExtra("usuario",contacto);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
