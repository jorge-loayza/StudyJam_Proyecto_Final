package com.kokodev.contactame.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class TarjetaActivity extends AppCompatActivity {
    private Tarjeta tarjeta;
    private TextView tvCargo,tvPagina,tvDescripcion,tvDireccion,tvLocalidad,tvOrganizacion,tvPulico;
    private ImageView ivTarjeta;
    private Button btnEditarTarjeta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta);

        getSupportActionBar().setTitle("");

        tarjeta = (Tarjeta)getIntent().getExtras().getSerializable("tarjeta");
        //Log.i("cargo",tarjeta.getCargo());
        initUI();
        if (getIntent().getExtras().getBoolean("vista")){
            btnEditarTarjeta.setVisibility(View.GONE);
        }else{
            getSupportActionBar().setTitle(R.string.mi_tarjeta);
        }
    }

    private void initUI() {
        tvCargo = (TextView) findViewById(R.id.tvCargo);
        tvPagina = (TextView) findViewById(R.id.tvPaginaweb);
        tvDescripcion = (TextView) findViewById(R.id.tvDescripcion);
        tvDireccion = (TextView) findViewById(R.id.tvDireccion);
        tvLocalidad = (TextView) findViewById(R.id.tvLocalidad);
        tvOrganizacion = (TextView) findViewById(R.id.tvOrganizacion);


        ivTarjeta = (ImageView) findViewById(R.id.ivImagenTarjeta);

        tvCargo.setText(tarjeta.getCargo());
        tvPagina.setText(tarjeta.getPagina());
        tvDescripcion.setText(tarjeta.getDescripcion());
        tvDireccion.setText(tarjeta.getDireccion());
        tvLocalidad.setText(tarjeta.getLocalidad());
        tvOrganizacion.setText(tarjeta.getOrganizacion());
        setImage(getApplicationContext(),tarjeta.getImagenTarjeta());

        btnEditarTarjeta = (Button) findViewById(R.id.btnEditarTarjeta);

    }
    public void setImage(final Context ctx, final String image){


        Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ivTarjeta, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(image).into(ivTarjeta);
            }
        });

    }
}
