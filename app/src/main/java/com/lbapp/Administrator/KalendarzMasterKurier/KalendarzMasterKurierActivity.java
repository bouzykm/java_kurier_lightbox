package com.lbapp.Administrator.KalendarzMasterKurier;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.lbapp._Global._GlobalVariable;
import com.lbapp.R;
import com.lbapp._API.Administrator_KalendarzMasterKurier__Downloader;
import com.lbapp._API.Koordynator_Prognoza_ListaTras_Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static android.widget.Toast.LENGTH_SHORT;

public class KalendarzMasterKurierActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    RequestQueue requestQueue;
    String dateFormat, username;
    int rok_x, miesiac_x, dzien_x;
    ListView lvListaKalendarzMasterKurier;
    FloatingActionButton fabKalendarzMasterKurier;
    AlertDialog.Builder builder;
    Administrator_KalendarzMasterKurier__Downloader d;
    Toast toast;
    static final int DIALOG_ID = 0;
    ProgressDialog pd;
    Response.ErrorListener errorListener;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.administrator_kalendarz_master_kurierow_drawer);
        Toolbar toolbar = findViewById(com.lbapp.R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(com.lbapp.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.lbapp.R.string.navigation_drawer_open, com.lbapp.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(com.lbapp.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        final TextView tvLogin= header.findViewById(com.lbapp.R.id.tvLogin);
        username= _GlobalVariable.getLogin();
        tvLogin.setText(username);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        dateFormat= _GlobalVariable.getDateFormat();
        setTitle("Lista na "+dateFormat);


        lvListaKalendarzMasterKurier = findViewById(R.id.lvListaKalendarzMasterKurier);
        fabKalendarzMasterKurier = findViewById(R.id.fabKalendarzMasterKurier);


        try{
            d = new Administrator_KalendarzMasterKurier__Downloader(this, lvListaKalendarzMasterKurier);
            d.execute();

        } catch (NullPointerException e) {
            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (KalendarzMasterKurierActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }catch (RuntimeException e) {
            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent (KalendarzMasterKurierActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }});
            builder.show();
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        rok_x = calendar.get(Calendar.YEAR);
        miesiac_x = calendar.get(Calendar.MONTH);
        dzien_x = calendar.get(Calendar.DAY_OF_MONTH);
        showKalendarzOnButtonClick();

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
                            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
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
                            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
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
                            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
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
                            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
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
                            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(KalendarzMasterKurierActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builder.show();
                            return;

                        } else if (networkResponse != null && networkResponse.data != null) {

                            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
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
                            builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
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
                    builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
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
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int rok, int miesiacRoku, int dzienMiesiaca) {

                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else
                    connected = false;


                if (connected ==true) {



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
                    final String dataKursu = (rok_x + "-" + (miesiac_xs) + "-" + dzien_xs);
                    globalVariable.setDateFormat(dataKursu);

                    pd = new ProgressDialog(KalendarzMasterKurierActivity.this);
                    pd.setTitle("Pobieranie");
                    pd.setMessage("Trwa pobieranie tras...");
                    pd.setCancelable(false);
                    pd.show();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                final JSONObject jsonResponse = new JSONObject(response);
                                pd.dismiss();
                                int retCode = jsonResponse.getInt("retCode");
                                String retMessage = jsonResponse.getString("retMessage");
                                _GlobalVariable globalVariable = new _GlobalVariable();


                                if (retCode == 0) {
                                     Intent intent = new Intent(KalendarzMasterKurierActivity.this, KalendarzMasterKurierActivity.class);
                                     startActivity(intent);
                                }
                                else if (retCode == 50) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                                    builder.setTitle("Sesja wygasła");
                                    builder.setMessage(retMessage);
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Powrót",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent (KalendarzMasterKurierActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }});
                                    builder.show();
                                    return;


                                }else if (retCode != 0) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                                    builder.setTitle("Błąd " + retCode);
                                    builder.setMessage(retMessage);
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


                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @SuppressWarnings("ConstantConditions")
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();

                            try {
                                NetworkResponse networkResponse = error.networkResponse;
                                String jsonError = new String(networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String retMessage = jsonObject.getString("retMessage");
                                int status = error.networkResponse.statusCode;

                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                                    builder.setTitle("Błąd");
                                    builder.setMessage("Błąd połączenia z serwerem. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                                    builder.setCancelable(false)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;
                                }
                                else if (error instanceof ParseError) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                                    builder.setTitle("Błąd");
                                    builder.setMessage("Błąd parsowania. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                                    builder.setCancelable(false)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;
                                }

                                if (status == 401) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                                    builder.setTitle("Sesja wygasła");
                                    builder.setMessage(retMessage);
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Powrót",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent (KalendarzMasterKurierActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                }});
                                    builder.show();

                                }else

                                if (networkResponse != null && networkResponse.data != null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                                    builder.setTitle("Błąd");
                                    builder.setMessage("" + retMessage);
                                    builder.setCancelable(false)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                                    builder.setTitle("Błąd " + status);
                                    builder.setMessage("" + retMessage);
                                    builder.setCancelable(false)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (RuntimeException e){
                                e.printStackTrace();
                            }


                        }
                    };

                    Koordynator_Prognoza_ListaTras_Request koordynatorPrognoza_TrasaRequest = new Koordynator_Prognoza_ListaTras_Request(getApplicationContext(), responseListener, errorListener);
                    RequestQueue queue = Volley.newRequestQueue(KalendarzMasterKurierActivity.this);
                    queue.add(koordynatorPrognoza_TrasaRequest);

                }else{
                    builder = new AlertDialog.Builder(KalendarzMasterKurierActivity.this);
                    builder.setTitle("Błąd");
                    builder.setMessage("Brak połączenia z Internetem.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Powrót",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }});
                    builder.show();
                    return;
                }
            }
        };
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, rok_x, miesiac_x, dzien_x);
        return null;
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.lbapp.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce){
            Intent intent = new Intent (KalendarzMasterKurierActivity.this, com.lbapp.Menu.Menu.class);
            KalendarzMasterKurierActivity.this.startActivity(intent);
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
        getMenuInflater().inflate(com.lbapp.R.menu.global_dots_options, menu);

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
            Intent intent = new Intent(KalendarzMasterKurierActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void showKalendarzOnButtonClick() {
        fabKalendarzMasterKurier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID);
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == com.lbapp.R.id.nav_list)
        {

        }
        else if (id == com.lbapp.R.id.nav_menu) {
            Intent intent = new Intent(KalendarzMasterKurierActivity.this, com.lbapp.Menu.Menu.class);
            startActivity(intent);
            finish();
        }
        else if (id == com.lbapp.R.id.nav_logout)
        {
            Intent intent = new Intent(KalendarzMasterKurierActivity.this, LoginActivity.class);
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
