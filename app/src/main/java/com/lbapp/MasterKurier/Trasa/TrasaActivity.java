package com.lbapp.MasterKurier.Trasa;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.lbapp._Global.EmergencyNumberSettings;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp._Global._GlobalVariable;
import com.lbapp.R;
import com.lbapp._API.MK_Trasa_SprawdzStrzegowo_Request;
import com.lbapp._API.MK_Trasa_ListaTras_Request;
import com.lbapp._API.MK_Trasa__ListaWezlow_Downloader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;


public class TrasaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String dateFormat, login, trasa;
    int rok_x, miesiac_x, dzien_x;
    Toast toast;
    AlertDialog.Builder builder;
    ListView lvListaMK;
    TextView tvInfo;
    MK_Trasa__ListaWezlow_Downloader d;
    List listTrasyId;
    ProgressDialog pd;
    Response.ErrorListener errorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mk_trasa_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dateFormat= _GlobalVariable.getDateFormat();
        trasa= _GlobalVariable.getTrasaMK();
        listTrasyId = _GlobalVariable.getListTrasIdMK();
        setTitle("Trasa na "+dateFormat);

        pd = new ProgressDialog(TrasaActivity.this);
        FloatingActionButton fabData = findViewById(R.id.fab);
        FloatingActionButton fabInfoTorbyBoxy = findViewById(R.id.fabInfoTorbyBoxy);

        tvInfo = findViewById(com.lbapp.R.id.tvInfo);
            tvInfo.setText(trasa);
        lvListaMK = findViewById(R.id.lvListaMK);

        try{

            d = new MK_Trasa__ListaWezlow_Downloader(this, lvListaMK);
            d.execute();

        } catch (NullPointerException e) {
            builder = new AlertDialog.Builder(TrasaActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (TrasaActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }catch (RuntimeException e) {
            builder = new AlertDialog.Builder(TrasaActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (TrasaActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }

        fabData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDateClicked();
            }
        });

        fabInfoTorbyBoxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrasaActivity.this, Trasa_ListaTrasKurierskichActivity.class);
                startActivity(intent);
            }
        });



        //domyslny listener
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                } else
                    connected = false;


                if (connected == true) {
                    try {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Brak połączenia. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else if (error instanceof ServerError) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd serwera. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else if (error instanceof NetworkError) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd sieci. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else if (error instanceof ParseError) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd parsowania. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        }

                        String jsonError = new String(networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String retMessage = jsonObject.getString("retMessage");
                        int status = error.networkResponse.statusCode;

                        if (status == 401) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(TrasaActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builder.show();
                            return;

                        } else if (networkResponse != null && networkResponse.data != null) {

                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("" + retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("" + retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    builder = new AlertDialog.Builder(TrasaActivity.this);
                    builder.setTitle("Błąd");
                    builder.setMessage("Brak połączenia z Internetem.");
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setCancelable(false)
                            .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                    return;
                }
            }
        };

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        final TextView tvLogin= header.findViewById(com.lbapp.R.id.tvLogin);
        login= _GlobalVariable.getLogin();
        tvLogin.setText(login);
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.lbapp.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce){
            Intent intent = new Intent (TrasaActivity.this, com.lbapp.Menu.Menu.class);
            TrasaActivity.this.startActivity(intent);
            finish();
            //  return;
        }

        this.doubleBackToExitPressedOnce = true;
        showAToast("Naciśnij dwukrotnie, aby wrócić do menu.");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void onDateClicked(){

        final Calendar c = Calendar.getInstance();
        rok_x = c.get(Calendar.YEAR);
        miesiac_x = c.get(Calendar.MONTH);
        dzien_x = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog =
                new DatePickerDialog(TrasaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int rok, int miesiacRoku, int dzienMiesiaca) {

                                rok_x = rok;
                                miesiac_x = miesiacRoku + 1;
                                String miesiac_xs="0";
                                if (miesiac_x <10)
                                    miesiac_xs="0"+miesiac_x;
                                else
                                    miesiac_xs=""+miesiac_x;
                                dzien_x = dzienMiesiaca;
                                String dzien_xs="0";
                                if (dzien_x <10)
                                    dzien_xs="0"+dzien_x;
                                else
                                    dzien_xs=""+dzien_x;

                                String dataDostawy = (rok_x + "-" + (miesiac_xs) + "-" + dzien_xs);
                                _GlobalVariable globalVariable = new _GlobalVariable();
                                globalVariable.setDateFormat(dataDostawy);

                                pd.setTitle("Pobieranie");
                                pd.setMessage("Trwa pobieranie tras...");
                                pd.setIcon(R.drawable.global_lightbox_logo);
                                pd.setCancelable(false);
                                pd.show();


                                Response.Listener<String> responseListener = new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            pd.dismiss();

                                            JSONObject jsonResponse = new JSONObject(response);
                                            int retCode = jsonResponse.getInt("retCode");
                                            String retMessage = jsonResponse.getString("retMessage");
                                            JSONArray arrTrasy = jsonResponse.getJSONArray("arrTrasy");

                                            int arrSize = arrTrasy.length();
                                            List<Integer> listTrasyId = new ArrayList(arrSize);
                                            List<String> listTrasy = new ArrayList(arrSize);

                                            for (int i = 0; i < arrSize; ++i) {
                                                jsonResponse = arrTrasy.getJSONObject(i);
                                                listTrasyId.add( (jsonResponse.getInt("trasaId")));
                                                listTrasy.add( (jsonResponse.getString("trasa")));
                                            }

                                            _GlobalVariable globalVariable = new _GlobalVariable();


                                            if (retCode == 0) {
                                                if (arrTrasy.isNull(0)){
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(TrasaActivity.this);
                                                    builder.setTitle("Brak trasy" );
                                                    builder.setCancelable(false);
                                                    builder.setIcon(R.drawable.global_ic_warning);
                                                    builder.setMessage("Nie ma przypisanej listy na ten dzień dla Master Kuriera.")
                                                            .setNegativeButton("Powrót", null)
                                                            .create()
                                                            .show();
                                                    return;

                                                }
                                                else if (arrTrasy.length()==1)
                                                {
                                                    int trasaId = listTrasyId.get(0);
                                                    String trasa = listTrasy.get(0);

                                                    globalVariable.setTrasaMK(trasa);
                                                    globalVariable.setListTrasMK(listTrasy);
                                                    globalVariable.setTrasaIdMK(trasaId);

                                                    Intent intent = new Intent(TrasaActivity.this, TrasaActivity.class);
                                                    startActivity(intent);
                                                }
                                                else{
                                                    globalVariable.setListTrasMK(listTrasy);
                                                    globalVariable.setListTrasIdMK(listTrasyId);

                                                    Intent intent = new Intent(TrasaActivity.this, WyborTrasyMasterkurierActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                            else if (retCode == 50) {
                                                builder = new AlertDialog.Builder(TrasaActivity.this);
                                                builder.setTitle("Sesja wygasła");
                                                builder.setMessage(retMessage);
                                                builder.setIcon(R.drawable.global_ic_warning);
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("Powrót",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent (TrasaActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }});
                                                builder.show();
                                                return;


                                            }else if (retCode != 0) {
                                                builder = new AlertDialog.Builder(TrasaActivity.this);
                                                builder.setTitle("Uwaga");
                                                builder.setMessage(retMessage);
                                                builder.setIcon(R.drawable.global_ic_warning);
                                                builder.setCancelable(false)
                                                        .setNegativeButton("Powrót", null)
                                                        .create()
                                                        .show();
                                                return;
                                            }

                                        }
                                        catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                MK_Trasa_ListaTras_Request mk_trasaListaTrasRequest = new MK_Trasa_ListaTras_Request(getApplicationContext(), responseListener, errorListener);
                                RequestQueue queue = Volley.newRequestQueue(TrasaActivity.this);
                                queue.add(mk_trasaListaTrasRequest);



                            }
                        }, rok_x, miesiac_x, dzien_x);
        datePickerDialog.show();

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
        if (id == com.lbapp.R.id.action_logout) {
            Intent intent = new Intent(TrasaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == com.lbapp.R.id.nav_list)
        {

        }
        else if (id == com.lbapp.R.id.nav_wyborTrasy)
        {
            _GlobalVariable globalVariable = new _GlobalVariable();
            List arrTrasy = globalVariable.getListTrasMK();


            if (arrTrasy.size()>1 ) {
                Intent intent = new Intent(TrasaActivity.this, WyborTrasyMasterkurierActivity.class);
                startActivity(intent);

            } else if (arrTrasy.size()==1) {
                builder = new AlertDialog.Builder(TrasaActivity.this);
                builder.setTitle("Brak innej listy");
                builder.setCancelable(false);
                builder.setMessage("To jedyna lista na ten dzień dla Master Kuriera.")
                        .setNegativeButton("Powrót", null)
                        .create()
                        .show();

            }
        }
        else if (id == R.id.nav_sprawdzStrzegowo) {
            onSprawdzStrzegowoClicked();
        }
        else if (id == com.lbapp.R.id.nav_mkEmergencyCall) {
            onMKEmergencyCallClicked();
        }
        else if (id == com.lbapp.R.id.nav_menu) {
            Intent intent = new Intent(TrasaActivity.this, com.lbapp.Menu.Menu.class);
            startActivity(intent);
            finish();
        }
        else if (id == com.lbapp.R.id.nav_logout)
        {
            Intent intent = new Intent(TrasaActivity.this, LoginActivity.class);
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

    public void onSprawdzStrzegowoClicked()
    {
        pd.setTitle("Pobieranie danych");
        pd.setMessage("Trwa pobieranie danych...");
        pd.setIcon(R.drawable.global_lightbox_logo);
        pd.setCancelable(false);
        pd.show();


        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int retCode = jsonResponse.getInt("retCode");
                    String retMessage = jsonResponse.getString("retMessage");
                    String retInfo = jsonResponse.getString("retInfo");
                    String retInfo2 = jsonResponse.getString("retInfo2");
                    final String retTelefon = jsonResponse.getString("retTelefon");

                    if (retInfo2.equals(""))
                    {
                        retInfo2 = "\n\nNie potwierdził jeszcze obecności w Strzegowie.";
                    }

                    if (retCode == 0) {

                        builder = new AlertDialog.Builder(TrasaActivity.this);
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setTitle("Raport ze Strzegowa");
                        builder.setMessage(retInfo+". "+"\n\n"+retInfo2);
                        builder.setCancelable(false);

                                if (!retTelefon.equals("")) {
                                builder.setPositiveButton("Zadzwoń", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                    {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + retTelefon));
                                    startActivity(intent);
                                    }
                                });
                                }

                                builder.setNegativeButton("Powrót", null)


                                .create()
                                .show();
                        return;
                    } else {
                        builder = new AlertDialog.Builder(TrasaActivity.this);
                        builder.setTitle("Uwaga");
                        builder.setMessage(retMessage);
                        builder.setIcon(R.drawable.global_ic_warning);
                        builder.setCancelable(false)
                                .setNegativeButton("Powrót", null)
                                .create()
                                .show();
                        return;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


            }
        };

        MK_Trasa_SprawdzStrzegowo_Request mkTrasa_sprawdzStrzegowoRequest = new MK_Trasa_SprawdzStrzegowo_Request(getApplicationContext(), responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(TrasaActivity.this.getApplicationContext());
        queue.add(mkTrasa_sprawdzStrzegowoRequest);
    }

    public void onMKEmergencyCallClicked()
    {
        EmergencyNumberSettings.emergencyCallRequest(TrasaActivity.this);
    }
}
