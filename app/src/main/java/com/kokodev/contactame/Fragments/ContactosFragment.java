package com.kokodev.contactame.Fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kokodev.contactame.Activities.CrearPerfilActivity;
import com.kokodev.contactame.Adapters.ContactosAdapter;
import com.kokodev.contactame.Adapters.TarjetasAdapter;
import com.kokodev.contactame.Objetos.Contact;
import com.kokodev.contactame.Objetos.Contacto;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactosFragment extends Fragment {

    private RecyclerView rvContactos;
    private View view;
    private List<Contacto> listaContactos;
    private ContactosAdapter contactosAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    private ProgressDialog progressDialog;


    public ContactosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        databaseReference.child("contactos_usuario").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())){
                    crearContactos();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rvContactos = (RecyclerView) view.findViewById(R.id.rvListaContactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(getContext()));
        listaContactos = new ArrayList<>();
        contactosAdapter = new ContactosAdapter(listaContactos,getContext());


        progressDialog = new ProgressDialog(getContext());


        return view;
    }

    private void crearContactos() {
        List<String> contactos = getAllContacts();

        for (final String telefono:contactos) {
            Log.i("tel",telefono);
            DatabaseReference usuarios = databaseReference.child("usuarios");
            usuarios.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        Contacto con = snapshot.getValue(Contacto.class);
                        DatabaseReference refContactos = databaseReference.child("contactos_usuario").child(firebaseAuth.getCurrentUser().getUid());
                        if (con.getTelefono().equals(telefono)){
                            refContactos.child(snapshot.getKey()).setValue(true);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }


    private List<String> getAllContacts() {
        List<String> telefonos = new ArrayList<>();
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{id},null);

            while (phoneCursor.moveToNext()){
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                telefonos.add(phoneNumber);
            }
            phoneCursor.close();
        }
        cursor.close();
        return telefonos;
    }


    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage("Espere...");
        progressDialog.show();
        llenarContactos();
        progressDialog.dismiss();


    }

    private void llenarContactos() {

        DatabaseReference contactos = databaseReference.child("contactos_usuario/"+firebaseAuth.getCurrentUser().getUid());
        contactos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("key",dataSnapshot.getKey());
                DatabaseReference usuariosref = databaseReference.child("usuarios/"+dataSnapshot.getKey());
                usuariosref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Contacto con = dataSnapshot.getValue(Contacto.class);
                        listaContactos.add(con);
                        Log.i("size",listaContactos.size()+"");
                        contactosAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Log.i("size",listaContactos.size()+"");
        rvContactos.setAdapter(contactosAdapter);


       /*
        final List<String> keys = new ArrayList<>();
        if (firebaseAuth.getCurrentUser().getUid()!= null){

            databaseReference.child("contactos_usuario").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    keys.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
            //            Log.i("num",snapshot.getKey());
                        keys.add(snapshot.getKey());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


           databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listaContactos.removeAll(listaContactos);
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        if (keys.contains(snapshot.getKey())){

                            Contacto con = snapshot.getValue(Contacto.class);
                            listaContactos.add(con);
                        }

                    }
                    contactosAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            rvContactos.setAdapter(contactosAdapter);
       }*/

    }


}
