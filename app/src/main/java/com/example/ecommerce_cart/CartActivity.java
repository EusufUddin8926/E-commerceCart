package com.example.ecommerce_cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class CartActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "CartActivity";

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initialview();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_cart,new CartFragment());
        transaction.commit();
    }

    private void initialview(){
        drawer =findViewById(R.id.cartactivitydrawer);
        toolbar = findViewById(R.id.cartactivitytoolbar);
        navigationView = findViewById(R.id.cartactivitynaviagtiondrawer);
        Menu menu =navigationView.getMenu();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                drawer.closeDrawer(Gravity.LEFT);
                break;

            case R.id.profile:
                Toast.makeText(this,"Profile Selected ",Toast.LENGTH_SHORT).show();
                break;

            case R.id.about:
                Toast mytoast = Toast.makeText(getBaseContext(),"",Toast.LENGTH_LONG);
                mytoast.setText("About Selected");
                mytoast.show();

                break;

            default:
                break;
        }

        return false;
    }
}