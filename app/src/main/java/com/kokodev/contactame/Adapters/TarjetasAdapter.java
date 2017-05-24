package com.kokodev.contactame.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kokodev.contactame.Fragments.TarjetasFragment;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by koko on 13-05-17.
 */

public class TarjetasAdapter extends RecyclerView.Adapter<TarjetasAdapter.TarjetasViewHolder>{

    List<Tarjeta> tarjetas;
    Context ctx;

    public TarjetasAdapter(List<Tarjeta> tarjetas,Context context) {
        this.tarjetas = tarjetas;
        this.ctx = context;
    }

    @Override
    public TarjetasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tarjeta_row,parent,false);
        TarjetasViewHolder tarjetasViewHolder = new TarjetasViewHolder(view);
        return tarjetasViewHolder;
    }

    @Override
    public void onBindViewHolder(TarjetasViewHolder holder, int position) {

        Tarjeta tarjeta = tarjetas.get(position);
        holder.tvCargo.setText(tarjeta.getCargo());
        holder.setImage(ctx,tarjeta.getImagenTarjeta());

    }

    @Override
    public int getItemCount() {
        return tarjetas.size();
    }

    public static class TarjetasViewHolder extends RecyclerView.ViewHolder{

        TextView tvCargo;
        ImageView ivImagenTarjeta;
        View mView;

        public TarjetasViewHolder(View itemView) {
            super(itemView);

            tvCargo = (TextView) itemView.findViewById(R.id.tvCargoTarjeta);
            ivImagenTarjeta = (ImageView) itemView.findViewById(R.id.ivImagenTarjeta);
            mView = itemView;
        }

        public void setCargo(String cargo){
            //TextView tvCargo = (TextView) mView.findViewById(R.id.tvCargoTarjeta);
            tvCargo.setText(cargo);
        }

        public void setImage(final Context ctx, final String image){
            //final ImageView tarjetsImagen = (ImageView) mView.findViewById(R.id.ivImagenTarjeta);

            //Picasso.with(ctx).load(image).into(tarjetsImagen);

            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ivImagenTarjeta, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(ivImagenTarjeta);
                }
            });

        }
    }
}
