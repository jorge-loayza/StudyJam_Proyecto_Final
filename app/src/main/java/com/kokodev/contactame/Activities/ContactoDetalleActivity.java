package com.kokodev.contactame.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.kokodev.contactame.Objetos.Contact;
import com.kokodev.contactame.Objetos.Contacto;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ContactoDetalleActivity extends AppCompatActivity {

    private TextView tvNombres,tvApellidos,tvCorreo,tvTelefono;
    private ImageView ivContacto,ivLlamar;
    private Contacto contacto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto_detalle);
        contacto = (Contacto)getIntent().getExtras().getSerializable("contacto");
        initUI();

    }

    private void initUI() {
        tvNombres = (TextView) findViewById(R.id.tvNombres);
        tvApellidos = (TextView) findViewById(R.id.tvApellidos);
        tvCorreo = (TextView) findViewById(R.id.tvCorreo);
        tvTelefono = (TextView) findViewById(R.id.tTelefono);
        ivContacto = (ImageView) findViewById(R.id.ivImagenContacto);
        ivLlamar = (ImageView) findViewById(R.id.ivLlamar);

        tvNombres.setText(contacto.getNombres());
        tvApellidos.setText(contacto.getApellidos());
        tvCorreo.setText(contacto.getCorreo_electronico());
        tvTelefono.setText(contacto.getTelefono());
        if (contacto.getImagen_usuario() != null){
            setImage(getApplicationContext(),contacto.getImagen_usuario());
        }

        ivLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+contacto.getTelefono()));
                startActivity(intent);
            }
        });

    }

    public void setImage(final Context ctx, final String image){
        Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ivContacto, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(image).into(ivContacto);
            }
        });

    }
}
