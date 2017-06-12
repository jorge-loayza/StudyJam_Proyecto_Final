package com.kokodev.contactame.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.kokodev.contactame.Adapters.TarjetasAdapter;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class TarjetasFragment extends Fragment {

    private RecyclerView rvTarjetas;

    private SwipeRefreshLayout srlSwipeTarjetas;

    private View view;
    private List<Tarjeta> listaTarjetas;
    List<String> cods;
    private TarjetasAdapter tarjetasAdapter;
    private DatabaseReference databaseReferenceTarjetas;
    private DatabaseReference databaseReferenceTarjetasUsuario;
    private FloatingActionButton fabAgregarTarjeta;
    private FirebaseAuth firebaseAuth;
    private String idUsuario;

    public TarjetasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tarjetas, container, false);
        this.view = view;

        databaseReferenceTarjetas = FirebaseDatabase.getInstance().getReference().child("tarjetas");
        databaseReferenceTarjetas.keepSynced(true);
        databaseReferenceTarjetasUsuario = FirebaseDatabase.getInstance().getReference().child("tarjetas_usuario");
        databaseReferenceTarjetasUsuario.keepSynced(true);
        firebaseAuth = FirebaseAuth.getInstance();
        rvTarjetas = (RecyclerView) view.findViewById(R.id.rvListaTarjetas);
        rvTarjetas.setLayoutManager(new LinearLayoutManager(getContext()));

        srlSwipeTarjetas = (SwipeRefreshLayout) view.findViewById(R.id.srlSwipeTarjetas);
        srlSwipeTarjetas.setColorSchemeResources(R.color.colorAccent);

        srlSwipeTarjetas.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                llenarTarjetas();
            }
        });

        listaTarjetas = new ArrayList<>();
        cods = new ArrayList<>();
        tarjetasAdapter = new TarjetasAdapter(listaTarjetas,getContext());
        idUsuario = firebaseAuth.getCurrentUser().getUid();
        rvTarjetas.setAdapter(tarjetasAdapter);
        verificaTieneTarjetas();


        return view;
    }

    private void verificaTieneTarjetas() {

        databaseReferenceTarjetasUsuario.child(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.getChildrenCount()>0)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false).setMessage(R.string.desea_agregar_tarjetas_de_contactos);
                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            crearTarjetasUsuario();
                            llenarTarjetas();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else{
                    crearTarjetasUsuario();
                    llenarTarjetas();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void crearTarjetasUsuario() {

        DatabaseReference contactosUsuario = FirebaseDatabase.getInstance().getReference().child("contactos_usuario").child(idUsuario);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String key = dataSnapshot.getKey();

                databaseReferenceTarjetas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(key)){
                            databaseReferenceTarjetasUsuario.child(idUsuario).child(key).setValue(true);
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

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        contactosUsuario.addChildEventListener(childEventListener);

    }





    private void llenarTarjetas() {


        listaTarjetas.clear();
        tarjetasAdapter.notifyDataSetChanged();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DatabaseReference tarjetaRef = databaseReferenceTarjetas.child(dataSnapshot.getKey());
                tarjetaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Tarjeta tarjeta = dataSnapshot.getValue(Tarjeta.class);
                        Boolean w = true;
                        for (Tarjeta tar :
                                listaTarjetas) {
                            if (tarjeta.getId_tarjeta().equals(tar.getId_tarjeta())){
                                w= false;
                            }
                        }
                        if (w){
                            listaTarjetas.add(tarjeta);
                            tarjetasAdapter.notifyDataSetChanged();
                            srlSwipeTarjetas.setRefreshing(false);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                llenarTarjetas();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                llenarTarjetas();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReferenceTarjetasUsuario.child(idUsuario).addChildEventListener(childEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.icAgregarTarjeta) {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                    .setPrompt("Scan")
                    .setCameraId(0)
                    .setBeepEnabled(false)
                    .setBarcodeImageEnabled(false)
                    .setOrientationLocked(false)
                    .initiateScan();
            return true;
        }
       if (id == R.id.icActualizarTarjetas) {
           AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
           builder.setCancelable(false).setMessage(R.string.desea_agregar_tarjetas_de_contactos);
           builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {

                   crearTarjetasUsuario();
                   llenarTarjetas();
               }
           });
           builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {

               }
           });
           AlertDialog dialog = builder.create();
           dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tarjetas,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
