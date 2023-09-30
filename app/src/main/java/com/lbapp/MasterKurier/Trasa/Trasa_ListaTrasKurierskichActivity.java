package com.lbapp.MasterKurier.Trasa;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp._Global._GlobalVariable;
import com.lbapp.R;
import com.lbapp._API.MK_Trasa__ListaTrasKurierskich_Downloader;

import static android.widget.Toast.LENGTH_SHORT;

public class Trasa_ListaTrasKurierskichActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String username;
    AlertDialog.Builder builder;
    Toast toast;
    MK_Trasa__ListaTrasKurierskich_Downloader d;
    ListView lvListaMKTrasa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_trasa_lista_tras_kurierskich_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setTitle("Szczegóły trasy");


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final FloatingActionButton fab = findViewById(R.id.fab);
        lvListaMKTrasa = findViewById(R.id.lvListaMKTrasa);

        try{

            d = new MK_Trasa__ListaTrasKurierskich_Downloader(this, lvListaMKTrasa);
            d.execute();

        } catch (NullPointerException e) {
            builder = new AlertDialog.Builder(Trasa_ListaTrasKurierskichActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (Trasa_ListaTrasKurierskichActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }catch (RuntimeException e) {
            builder = new AlertDialog.Builder(Trasa_ListaTrasKurierskichActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (Trasa_ListaTrasKurierskichActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }

        lvListaMKTrasa.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @SuppressLint("RestrictedApi")
              @Override
              public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                  if (fab.getVisibility() == View.GONE)
                      fab.setVisibility(View.VISIBLE);
                  else
                      fab.setVisibility(View.GONE);
              }
          });

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Trasa_ListaTrasKurierskichActivity.this, TrasaActivity.class);
                        Trasa_ListaTrasKurierskichActivity.this.startActivity(intent);
                        finish();
                    }
                });

        View header=navigationView.getHeaderView(0);
        final TextView tvLogin= header.findViewById(com.lbapp.R.id.tvLogin);
        username= _GlobalVariable.getLogin();
        tvLogin.setText(username);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global_dots_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == com.lbapp.R.id.nav_list) {
            Intent intent = new Intent(Trasa_ListaTrasKurierskichActivity.this, TrasaActivity.class);
            startActivity(intent);
            finish();
        } else if (id == com.lbapp.R.id.nav_menu) {
            Intent intent = new Intent(Trasa_ListaTrasKurierskichActivity.this, com.lbapp.Menu.Menu.class);
            startActivity(intent);
            finish();
        } else if (id == com.lbapp.R.id.nav_logout) {
            Intent intent = new Intent(Trasa_ListaTrasKurierskichActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(com.lbapp.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, LENGTH_SHORT);
        toast.show();
    }
}
