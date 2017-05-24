package com.kokodev.contactame.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kokodev.contactame.Adapters.TarjetasAdapter;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TarjetasFragment extends Fragment {

    private RecyclerView rvTarjetas;
    private View view;
    private List<Tarjeta> listaTarjetas;
    private TarjetasAdapter tarjetasAdapter;
    private DatabaseReference databaseReference;

    public TarjetasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tarjetas, container, false);
        this.view = view;

        databaseReference = FirebaseDatabase.getInstance().getReference().child("tarjetas");
        databaseReference.keepSynced(true);
        rvTarjetas = (RecyclerView) view.findViewById(R.id.rvListaTarjetas);
        rvTarjetas.setLayoutManager(new LinearLayoutManager(getContext()));
        listaTarjetas = new ArrayList<>();
        tarjetasAdapter = new TarjetasAdapter(listaTarjetas,getContext());
       // init();
        //cargarTarjetas();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
    }



    @Override
    public void onStart() {
        super.onStart();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaTarjetas.removeAll(listaTarjetas);
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    Tarjeta tar = snapshot.getValue(Tarjeta.class);
                    listaTarjetas.add(tar);
                }
                tarjetasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rvTarjetas.setAdapter(tarjetasAdapter);


    }

}
