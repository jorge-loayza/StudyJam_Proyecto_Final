package com.kokodev.contactame.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kokodev.contactame.Activities.TarjetaActivity;
import com.kokodev.contactame.Fragments.TarjetasFragment;
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
        TarjetasViewHolder tarjetasViewHolder = new TarjetasViewHolder(view,ctx);
        return tarjetasViewHolder;
    }

    @Override
    public void onBindViewHolder(TarjetasViewHolder holder, int position) {

        Tarjeta tarjeta = tarjetas.get(position);
        holder.setTarjeta(tarjeta);
        holder.tvOrganizacion.setText(tarjeta.getOrganizacion());
        holder.tvCargo.setText(tarjeta.getCargo());
        holder.setImage(ctx,tarjeta.getImagenTarjeta());

    }

    @Override
    public int getItemCount() {
        return tarjetas.size();
    }






    public static class TarjetasViewHolder extends RecyclerView.ViewHolder{

        TextView tvOrganizacion;
        TextView tvCargo;
        ImageView ivImagenTarjeta,ivBorrarTarjeta;
        private Tarjeta tarjeta;
        private Contacto contacto;
        private DatabaseReference databaseReferenceContacto;
        View mView;
        Context ctx;


        public TarjetasViewHolder(View itemView, Context context) {
            super(itemView);
            tvOrganizacion = (TextView) itemView.findViewById(R.id.tvOrganizacionTarjeta);
            tvCargo = (TextView) itemView.findViewById(R.id.tvCargoTarjeta);
            ivImagenTarjeta = (ImageView) itemView.findViewById(R.id.ivImagenTarjeta);
            ivBorrarTarjeta = (ImageView) itemView.findViewById(R.id.ivBorrarTarjeta);
            mView = itemView;
            this.ctx = context;
            ivBorrarTarjeta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tarjetas_usuario").child(usuario.getUid());
                    databaseReference.child(tarjeta.getId_tarjeta()).removeValue();
                    Toast.makeText(ctx, R.string.se_elimino_tarjeta,Toast.LENGTH_SHORT).show();
                }
            });



        }

        public void setTarjeta(final Tarjeta tarjeta) {
            this.tarjeta = tarjeta;
            databaseReferenceContacto = FirebaseDatabase.getInstance().getReference().child("usuarios").child(tarjeta.getId_tarjeta());
            databaseReferenceContacto.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contacto = dataSnapshot.getValue(Contacto.class);
                    mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mView.getContext(), TarjetaActivity.class);
                            intent.putExtra("tarjeta",tarjeta);
                            intent.putExtra("contacto",contacto);
                            intent.putExtra("vista",true);
                            mView.getContext().startActivity(intent);
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setCargo(String cargo){
            //TextView tvCargo = (TextView) mView.findViewById(R.id.tvCargoTarjeta);
            tvCargo.setText(cargo);
        }
        public void setOrganizacion(String organizacion){
            //TextView tvCargo = (TextView) mView.findViewById(R.id.tvCargoTarjeta);
            tvOrganizacion.setText(organizacion);
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
