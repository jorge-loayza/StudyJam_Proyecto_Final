package com.kokodev.contactame.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kokodev.contactame.Activities.ContactoDetalleActivity;
import com.kokodev.contactame.Activities.TarjetaActivity;
import com.kokodev.contactame.Objetos.Contacto;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by koko on 13-05-17.
 */

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactosViewHolder> {

    List<Contacto> contactos;
    Context ctx;

    public ContactosAdapter(List<Contacto> contactos, Context ctx) {
        this.contactos = contactos;
        this.ctx = ctx;
    }

    @Override
    public ContactosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacto_row,parent,false);
        ContactosViewHolder contactosViewHolder = new ContactosViewHolder(view);
        return contactosViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactosViewHolder holder, final int position) {

        Contacto contacto = contactos.get(position);
        holder.setContacto(contacto);
        holder.tvNombre.setText(contacto.getNombres());
        holder.tvCorreo.setText(contacto.getCorreo_electronico());
        if (contacto.getImagen_usuario() != null){
            holder.setImage(ctx,contacto.getImagen_usuario());
        }



    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }













    public static class ContactosViewHolder extends RecyclerView.ViewHolder{

        TextView tvNombre,tvCorreo;
        ImageView ivImagenUsuario,ivLlamar;
        Contacto contacto;
        View view;



        public ContactosViewHolder(final View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombreContacto);
            tvCorreo = (TextView) itemView.findViewById(R.id.tvCorreoContacto);
            ivImagenUsuario = (ImageView) itemView.findViewById(R.id.ivImagenContacto);
            ivLlamar = (ImageView) itemView.findViewById(R.id.ivLlamar);
            this.view = itemView;
            ivLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //int pos = getAdapterPosition();
                    //Toast.makeText(itemView.getContext(),"posicion"+pos, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+contacto.getTelefono()));
                    view.getContext().startActivity(intent);
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(view.getContext(), ContactoDetalleActivity.class);
                    intent.putExtra("contacto",contacto);
                    view.getContext().startActivity(intent);
                }
            });

        }

        public void setNombre(String nombre){
            tvNombre.setText(nombre);
        }


        public void setContacto(Contacto contacto) {
            this.contacto = contacto;
        }

        public void setImage(final Context context, final String imagen_usuario) {
            Picasso.with(context).load(imagen_usuario).networkPolicy(NetworkPolicy.OFFLINE).into(ivImagenUsuario, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(context).load(imagen_usuario).into(ivImagenUsuario);
                }
            });
        }
    }
}
