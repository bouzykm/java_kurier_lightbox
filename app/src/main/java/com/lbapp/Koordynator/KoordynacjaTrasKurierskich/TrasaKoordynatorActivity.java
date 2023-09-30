package com.lbapp.Koordynator.KoordynacjaTrasKurierskich;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp.R;
import com.lbapp._API.Koordynator_AdministracjaTrasKurierskich_ListaTrasRequest;
import com.lbapp._API.Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader;
import com.lbapp._API.Global_SprawdzMasterkuriera_Request;
import com.lbapp._Global.EmergencyNumberSettings;
import com.lbapp._Global._GlobalVariable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class TrasaKoordynatorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //ZMIENNE
    int rok_x, miesiac_x, dzien_x; // obecna godzina;
    static final int DIALOG_ID = 0;
    String login, trasa, dateFormat;
    FloatingActionButton fab;
    TextView tvInfo;
    ListView lvLista;
    RequestQueue requestQueue;
    Response.ErrorListener errorListener;
    ProgressDialog pd;
    AlertDialog.Builder builder;
    Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader d;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.koordynator_administracja_tras_kurierskich_trasa_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        trasa= _GlobalVariable.getTrasa();
        dateFormat= _GlobalVariable.getDateFormat();
        setTitle(trasa+" / "+ dateFormat);

        lvLista = findViewById(R.id.lvLista);
        tvInfo = findViewById(R.id.tvInfo);
        fab = findViewById(R.id.fab);

        try{

            d = new Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader(this, lvLista, tvInfo);
            d.execute();

        } catch (NullPointerException e) {
            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (TrasaKoordynatorActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }catch (RuntimeException e) {
            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (TrasaKoordynatorActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        rok_x = calendar.get(Calendar.YEAR);
        miesiac_x = calendar.get(Calendar.MONTH);
        dzien_x = calendar.get(Calendar.DAY_OF_MONTH);
        showDialogOnButtonClick();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        final TextView tvLogin= header.findViewById(R.id.tvLogin);
        login= _GlobalVariable.getLogin();
        tvLogin.setText(login);

        //domyślny error listener
        errorListener = new Response.ErrorListener(){


            @Override
            public void onErrorResponse(VolleyError error) {

                pd.dismiss();
                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else
                    connected = false;


                if (connected ==true) {
                    try {

                        NetworkResponse networkResponse = error.networkResponse;

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd połączenia z serwerem. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }});
                            builder.show();
                            return;

                        } else if (error instanceof ServerError) {
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd serwera. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }});
                            builder.show();
                            return;
                        } else if (error instanceof NetworkError) {
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd sieci. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }});
                            builder.show();
                            return;
                        } else if (error instanceof ParseError) {
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd parsowania. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }});
                            builder.show();
                            return;
                        }


                        String jsonError = new String(networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String retMessage = jsonObject.getString("retMessage");
                        int status = error.networkResponse.statusCode;

                        if (status == 401) {
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent (TrasaKoordynatorActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }});
                            builder.show();
                            return;

                        }else

                        if (networkResponse != null && networkResponse.data != null) {

                            Intent intent = new Intent (TrasaKoordynatorActivity.this, LoginActivity.class);
                            TrasaKoordynatorActivity.this.startActivity(intent);
                        }else{
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Uwaga");
                            builder.setMessage("" + retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }});
                            builder.show();
                            return;
                        }

                    } catch (NullPointerException e) {
                        builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                        builder.setTitle("Problem z zasięgiem");
                        builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                        builder.setIcon(R.drawable.global_ic_warning);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Odśwież",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader d = new Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader(TrasaKoordynatorActivity.this, lvLista, tvInfo);
                                        d.execute();
                                    }});
                        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                        return;
                    }catch (RuntimeException e) {
                        builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                        builder.setTitle("Problem z zasięgiem");
                        builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                        builder.setIcon(R.drawable.global_ic_warning);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Odśwież",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader d = new Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader(TrasaKoordynatorActivity.this, lvLista, tvInfo);
                                        d.execute();
                                    }});
                        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                    builder.setTitle("Błąd");
                    builder.setMessage("Brak połączenia z Internetem.");
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setCancelable(false);
                    builder.setPositiveButton("ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }});
                    builder.show();
                    return;
                }
            }};



    }

    //kalendarz na liscie adresow
    @Override
    protected Dialog onCreateDialog(int id) {
        DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int rok, int miesiacRoku, int dzienMiesiaca) {

                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else
                    connected = false;


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


                _GlobalVariable globalVariable = new _GlobalVariable();
                final String obecnaData = globalVariable.getDateFormat();
                final String dataDostawy = (rok_x + "-" + (miesiac_xs) + "-" + dzien_xs);
                globalVariable.setDateFormat(dataDostawy);

                pd = new ProgressDialog(TrasaKoordynatorActivity.this);
                pd.setTitle("Pobieranie");
                pd.setMessage("Trwa pobieranie tras...");
                pd.setIcon(R.drawable.global_lightbox_logo);
                pd.setCancelable(false);
                pd.show();


                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            final JSONObject jsonResponse = new JSONObject(response);

                            int retCode = jsonResponse.getInt("retCode");
                            String retMessage = jsonResponse.getString("retMessage");
                            JSONArray arrTrasy = jsonResponse.getJSONArray("arrTrasy");
                            _GlobalVariable globalVariable = new _GlobalVariable();


                            if (retCode == 0) {
                                if (arrTrasy.isNull(0)){

                                    globalVariable.setDateFormat(obecnaData);
                                    builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                                    builder.setTitle("Brak trasy" );
                                    builder.setCancelable(false);
                                    builder.setMessage("Nie ma przypisanej trasy na ten dzień.")
                                            .setIcon(R.drawable.global_ic_warning)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;

                                } else if (arrTrasy.length()==1)
                                {
                                    String trasa = arrTrasy.getString(0);

                                    globalVariable.setTrasa(trasa.toString());
                                    globalVariable.setTablicaTras(null);

                                    Intent intent = new Intent(TrasaKoordynatorActivity.this, TrasaKoordynatorActivity.class);
                                    startActivity(intent);
                                }else{
                                    globalVariable.setTablicaTras(arrTrasy);
                                    Intent intent = new Intent(TrasaKoordynatorActivity.this, WyborTrasyKoordynator.class);
                                    startActivity(intent);
                                }
                            }
                            if (retCode != 0) {
                                builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                                builder.setTitle("Uwaga");
                                builder.setMessage(retMessage);
                                builder.setCancelable(false)
                                        .setIcon(R.drawable.global_ic_warning)
                                        .setNegativeButton("Powrót", null)
                                        .create()
                                        .show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Problem z zasięgiem");
                            builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Odśwież",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader d = new Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader(TrasaKoordynatorActivity.this, lvLista, tvInfo);
                                            d.execute();
                                        }});
                            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                            return;
                        }catch (RuntimeException e) {
                            builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                            builder.setTitle("Problem z zasięgiem");
                            builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                            builder.setCancelable(false);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setPositiveButton("Odśwież",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader d = new Koordynator_AdministracjaTrasKurierskich__Trasa_Downloader(TrasaKoordynatorActivity.this, lvLista, tvInfo);
                                            d.execute();
                                        }});
                            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.show();
                            return;
                        }
                    }
                };

                Koordynator_AdministracjaTrasKurierskich_ListaTrasRequest koordynator_administracjaTrasKurierskich_listaTrasRequest = new Koordynator_AdministracjaTrasKurierskich_ListaTrasRequest(getApplicationContext(), responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(TrasaKoordynatorActivity.this);
                queue.add(koordynator_administracjaTrasKurierskich_listaTrasRequest);
            }

        };
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, rok_x, miesiac_x, dzien_x);
        return null;
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce){
            Intent intent = new Intent (TrasaKoordynatorActivity.this, com.lbapp.Menu.Menu.class);
            TrasaKoordynatorActivity.this.startActivity(intent);
            finish();
            //  return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Naciśnij dwukrotnie, aby wrócić do menu.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
        if (id == R.id.action_logout) {
            Intent intent = new Intent(TrasaKoordynatorActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list) {

        } else if (id == R.id.nav_infoMK) {

            onSprawdzMasterkurieraClicked();

        } else if (id == R.id.nav_wyborTrasy) {
            _GlobalVariable globalVariable = new _GlobalVariable();
            JSONArray arrTrasy = globalVariable.getTablicaTras();



            if (arrTrasy!=null) {
                Intent intent = new Intent(TrasaKoordynatorActivity.this, WyborTrasyKoordynator.class);
                startActivity(intent);

            }else
            {
                builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                builder.setTitle("Brak innej trasy" );
                builder.setCancelable(false);
                builder.setMessage("To jedyna trasa na ten dzień.")
                        .setNegativeButton("Powrót", null)
                        .create()
                        .show();
            }
        } else if (id == R.id.nav_koordEmergencyCall) {

            onKoordEmergencyCallClicked();


         }else if (id == R.id.nav_menu) {
            Intent intent = new Intent(TrasaKoordynatorActivity.this, com.lbapp.Menu.Menu.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(TrasaKoordynatorActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void showDialogOnButtonClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID);
            }
        });
    }

    public void onSprawdzMasterkurieraClicked()
    {
        pd = new ProgressDialog(TrasaKoordynatorActivity.this);
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
                    final String retTelefon = jsonResponse.getString("retTelefon");

                    if (retInfo.equals(""))
                        {
                            retInfo = "Brak danych";
                        }

                    if (retCode == 0) {

                        builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setTitle("Raport o Master Kurierze");
                        builder.setMessage(retInfo);
                        builder.setCancelable(false);

                                if (!retTelefon.equals("")) {
                                    builder.setPositiveButton("Zadzwoń", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
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
                        builder = new AlertDialog.Builder(TrasaKoordynatorActivity.this);
                        builder.setTitle("Błąd " + retCode);
                        builder.setMessage(retMessage);
                        builder.setCancelable(false)
                                .setIcon(R.drawable.global_ic_warning)
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


        Global_SprawdzMasterkuriera_Request global_sprawdzMasterkuriera_request = new Global_SprawdzMasterkuriera_Request(getApplicationContext(), trasa, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(TrasaKoordynatorActivity.this.getApplicationContext());
        queue.add(global_sprawdzMasterkuriera_request);
    }


    public void onKoordEmergencyCallClicked()
    {
        EmergencyNumberSettings.emergencyCallRequest(TrasaKoordynatorActivity.this);
    }

}
