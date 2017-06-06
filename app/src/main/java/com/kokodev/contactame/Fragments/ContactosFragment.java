package com.kokodev.contactame.Fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private SwipeRefreshLayout srlSwipeContactos;

    private View view;
    private List<Contacto> listaContactos;
    private ContactosAdapter contactosAdapter;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ProgressDialog progressDialog;


    public ContactosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
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
                    verificarPermisos();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rvContactos = (RecyclerView) view.findViewById(R.id.rvListaContactos);
        rvContactos.setLayoutManager(new LinearLayoutManager(getContext()));

        srlSwipeContactos = (SwipeRefreshLayout) view.findViewById(R.id.srlSwipeContactos);
        srlSwipeContactos.setColorSchemeResources(R.color.colorAccent);
        srlSwipeContactos.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                verificarPermisos();
                llenarContactos();
            }
        });

        listaContactos = new ArrayList<>();
        contactosAdapter = new ContactosAdapter(listaContactos,getContext());


        progressDialog = new ProgressDialog(getContext());
        rvContactos.setAdapter(contactosAdapter);
        llenarContactos();
        return view;

    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_contactos,menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.icActualizar) {
            verificarPermisos();
            llenarContactos();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void verificarPermisos() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            crearContactos();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                crearContactos();
            } else {
                Toast.makeText(getContext(), "Necesitas Otorgar permisos para sincronizar tus contactos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void crearContactos() {
        List<String> contactos = getAllContacts();

        for (final String telefono:contactos) {
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



    private void llenarContactos() {

        listaContactos.clear();
        DatabaseReference contactos = databaseReference.child("contactos_usuario/"+firebaseAuth.getCurrentUser().getUid());
        contactos.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                DatabaseReference usuariosref = databaseReference.child("usuarios/"+dataSnapshot.getKey());
                usuariosref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Contacto contacto = dataSnapshot.getValue(Contacto.class);

                        Boolean w = true;
                        for (Contacto con :
                                listaContactos) {
                            if (con.getIdContacto().equals(contacto.getIdContacto())){
                                w= false;
                                Log.i("dato",contacto.getNombres());
                            }
                        }
                        if (w){
                            listaContactos.add(contacto);
                            contactosAdapter.notifyDataSetChanged();
                        }



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
                llenarContactos();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        srlSwipeContactos.setRefreshing(false);

    }


}
