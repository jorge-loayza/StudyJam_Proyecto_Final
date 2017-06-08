package com.kokodev.contactame.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kokodev.contactame.Objetos.Contacto;
import com.kokodev.contactame.Objetos.Tarjeta;
import com.kokodev.contactame.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class TarjetaActivity extends AppCompatActivity {
    private Tarjeta tarjeta;
    private Contacto contacto;
    private TextView tvCargo,tvPagina,tvDescripcion,tvDireccion,tvLocalidad,tvOrganizacion,tvPulico
            ,tvNom,tvEmail,tvTel;
    private ImageView ivTarjeta,ivImgUsuario,ivIcLlamar;
    private Button btnEditarTarjeta;
    private LinearLayout llDatosContacto,llTelefonoContacto;

    private Boolean sw = false;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarjeta);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTarjetas);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);

        tarjeta = (Tarjeta)getIntent().getExtras().getSerializable("tarjeta");

        btnEditarTarjeta = (Button) findViewById(R.id.btnEditarTarjeta);

        if (getIntent().getExtras().getBoolean("vista")){
            btnEditarTarjeta.setVisibility(View.GONE);
        }else{
            getSupportActionBar().setTitle(R.string.mi_tarjeta);
            btnEditarTarjeta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), EditarTarjetaActivity.class);
                    intent.putExtra("tarjeta",tarjeta);
                    startActivity(intent);
                    finish();
                }
            });
        }
        llDatosContacto = (LinearLayout) findViewById(R.id.llDatosContacto);
        llTelefonoContacto = (LinearLayout) findViewById(R.id.llTelefonoContacto);

        if (getIntent().getExtras().getSerializable("contacto") !=null){
            sw = true;
            contacto = (Contacto) getIntent().getExtras().getSerializable("contacto");
        }else{
            llDatosContacto.setVisibility(View.GONE);
            llTelefonoContacto.setVisibility(View.GONE);
        }

        initUI();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mi_tarjeta,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(sw){
            menu.clear();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.opGenerarQR) {
            verificaPermiso();
            return true;
        }
       /* if (id == R.id.opEliminarTarjeta) {
            Toast.makeText(getApplicationContext(),"Debe llenar todos los campos",Toast.LENGTH_LONG).show();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void descargarQR() {
        progressDialog.setMessage(getString(R.string.decargando));
        progressDialog.show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("QR_Tarjetas").child(tarjeta.getId_tarjeta());

        File storagePath = new File(Environment.getExternalStorageDirectory(),"Mi_Tarjeta(Contactame)");
        // Create direcorty if not exists
        if(!storagePath.exists()) {
            storagePath.mkdirs();
        }

        final File myFile = new File(storagePath,"Mi_Tarjeta.jpg");
        if (!myFile.exists()){
            storageReference.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                    MediaScannerConnection.scanFile(getApplicationContext(),
                            new String[] { myFile.getAbsolutePath() }, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                }
                            });

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    Uri data = Uri.parse("file://" + myFile.getAbsoluteFile());
                    intent.setDataAndType(data, "image/*");
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    myFile.delete();
                    Toast.makeText(getApplicationContext(),"Error al Descargar",Toast.LENGTH_LONG).show();
                }
            });
        }else{
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"QR guardado en Mi_Tarjeta(Contactame)/Mi_Tarjeta.jpg",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            Uri data = Uri.parse("file://" + myFile.getAbsoluteFile());
            intent.setDataAndType(data, "image/*");
            startActivity(intent);
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

        setImageTarjeta(getApplicationContext(),tarjeta.getImagenTarjeta());




        if (sw){
            ivImgUsuario = (ImageView) findViewById(R.id.ivImgUsuario);
            ivIcLlamar = (ImageView) findViewById(R.id.ivIcLlamar);
            tvNom = (TextView) findViewById(R.id.tvNom);
            tvEmail = (TextView) findViewById(R.id.tvEmail);
            tvTel = (TextView) findViewById(R.id.tvTel);

            tvNom.setText(contacto.getNombres()+" "+contacto.getApellidos());
            tvEmail.setText(contacto.getCorreo_electronico());
            tvTel.setText(contacto.getTelefono());

            if (contacto.getImagen_usuario() != null){
                setImageUsuario(getApplicationContext(),contacto.getImagen_usuario());
            }

            ivIcLlamar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+contacto.getTelefono()));
                    startActivity(intent);
                }
            });
        }

    }
    public void setImageUsuario(final Context ctx, final String image){
        Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(ivImgUsuario, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(ctx).load(image).into(ivImgUsuario);
            }
        });
    }
    public void setImageTarjeta(final Context ctx, final String image){
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


    public void verificaPermiso() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {

            }
        } else {
            descargarQR();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    descargarQR();
                } else {
                    Toast.makeText(getApplicationContext(),"Debe conceder permisos para guardar la imagen.",Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

}
