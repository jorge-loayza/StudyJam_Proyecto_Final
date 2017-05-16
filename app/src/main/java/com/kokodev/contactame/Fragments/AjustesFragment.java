package com.kokodev.contactame.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.kokodev.contactame.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AjustesFragment extends Fragment {

    private TextView tvLogOut;

    public AjustesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);
        tvLogOut = (TextView) view.findViewById(R.id.tvLogOut);

        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"saliendo..",Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
            }
        });
        return view;
    }

}
