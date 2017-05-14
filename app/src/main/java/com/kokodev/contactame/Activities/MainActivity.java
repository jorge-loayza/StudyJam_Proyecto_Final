package com.kokodev.contactame.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.kokodev.contactame.Fragments.ContactosFragment;
import com.kokodev.contactame.Fragments.TarjetasFragment;
import com.kokodev.contactame.R;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_inicio:
                    ContactosFragment contactosFragment = new ContactosFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,contactosFragment).commit();
                    return true;
                case R.id.navigation_tarjetas:
                    TarjetasFragment tarjetasFragment = new TarjetasFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,tarjetasFragment).commit();
                    return true;
                case R.id.navigation_ajustes:

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
