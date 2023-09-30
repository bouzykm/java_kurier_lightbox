package com.lbapp.Kurier.Trasa;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.lbapp._API.Global_SprawdzMasterkuriera_Request;
import com.lbapp._API.Kurier_Trasa_WyczyscSzacunki_Request;
import com.lbapp._API.Kurier_Trasa__ListaAdresow_Downloader;
import com.lbapp._API.Kurier_Trasa_ZmienStatusPakowania_Request;
import com.lbapp._API.Kurier_Trasa_ListaTrasRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TrasaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //ZMIENNE
    int rok_x, miesiac_x, dzien_x; // obecna godzina;
    static final int DIALOG_ID = 0;
    String szerokoscGPS, dlugoscGPS, statusPakowania, username, trasa, dateFormat;
    FloatingActionButton fab;
    TextView tvInfo;
    ListView lvLista;
    Button btnStatusPakowania;
    RequestQueue requestQueue;
    Response.ErrorListener errorListener;
    LocationManager locationManager;
    LocationListener locationListener;
    ProgressDialog pd;
    AlertDialog.Builder builder;
    Kurier_Trasa__ListaAdresow_Downloader d;
    boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kurier_trasa_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        trasa = _GlobalVariable.getTrasa();
        dateFormat = _GlobalVariable.getDateFormat();
        setTitle(trasa + " / " + dateFormat);

        lvLista = findViewById(R.id.lvLista);
        tvInfo = findViewById(R.id.tvInfo);
        fab = findViewById(R.id.fab);
        btnStatusPakowania = findViewById(R.id.btnStatusPakowania);

        try {

            d = new Kurier_Trasa__ListaAdresow_Downloader(this, lvLista, btnStatusPakowania, tvInfo);
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
                            Intent intent = new Intent(TrasaActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }
                    });
            builder.show();
            return;
        } catch (RuntimeException e) {
            builder = new AlertDialog.Builder(TrasaActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(TrasaActivity.this, com.lbapp.Menu.Menu.class);
                            startActivity(intent);
                        }
                    });
            builder.show();
            return;
        }

        final Calendar calendar = Calendar.getInstance();
        rok_x = calendar.get(Calendar.YEAR);
        miesiac_x = calendar.get(Calendar.MONTH);
        dzien_x = calendar.get(Calendar.DAY_OF_MONTH);
        showDialogOnButtonClick();

        DrawerLayout drawer = (DrawerLayout) findViewById(com.lbapp.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.lbapp.R.string.navigation_drawer_open, com.lbapp.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(com.lbapp.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        final TextView tvLogin = header.findViewById(com.lbapp.R.id.tvLogin);
        username = _GlobalVariable.getLogin();
        tvLogin.setText(username);

        //domyślny error listener
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                pd.dismiss();
                connected = false;
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
                            builder.setMessage("Błąd połączenia z serwerem. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();
                            return;

                        } else if (error instanceof ServerError) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd serwera. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.show();
                            return;
                        } else if (error instanceof NetworkError) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd sieci. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();
                            return;
                        } else if (error instanceof ParseError) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd parsowania. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("ok",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            builder.show();
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

                            Intent intent = new Intent(TrasaActivity.this, LoginActivity.class);
                            TrasaActivity.this.startActivity(intent);
                        } else {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Uwaga");
                            builder.setMessage("" + retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.show();
                            return;
                        }

                    } catch (NullPointerException e) {
                        builder = new AlertDialog.Builder(TrasaActivity.this);
                        builder.setTitle("Problem z zasięgiem");
                        builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                        builder.setIcon(R.drawable.global_ic_warning);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Odśwież",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Kurier_Trasa__ListaAdresow_Downloader d = new Kurier_Trasa__ListaAdresow_Downloader(TrasaActivity.this, lvLista, btnStatusPakowania, tvInfo);
                                        d.execute();
                                    }
                                });
                        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                        return;
                    } catch (RuntimeException e) {
                        builder = new AlertDialog.Builder(TrasaActivity.this);
                        builder.setTitle("Problem z zasięgiem");
                        builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                        builder.setIcon(R.drawable.global_ic_warning);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Odśwież",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final Kurier_Trasa__ListaAdresow_Downloader d = new Kurier_Trasa__ListaAdresow_Downloader(TrasaActivity.this, lvLista, btnStatusPakowania, tvInfo);
                                        d.execute();
                                    }
                                });
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
                } else {
                    builder = new AlertDialog.Builder(TrasaActivity.this);
                    builder.setTitle("Błąd");
                    builder.setMessage("Brak połączenia z Internetem.");
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setCancelable(false);
                    builder.setPositiveButton("ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    builder.show();
                    return;
                }
            }
        };


    }

    //kalendarz na liscie adresow
    @Override
    protected Dialog onCreateDialog(int id) {
        DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int rok, int miesiacRoku, int dzienMiesiaca) {

                connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                } else
                    connected = false;


                rok_x = rok;
                miesiac_x = miesiacRoku + 1;
                String miesiac_xs = "0";
                if (miesiac_x < 10)
                    miesiac_xs = "0" + miesiac_x;
                else
                    miesiac_xs = "" + miesiac_x;

                dzien_x = dzienMiesiaca;
                String dzien_xs = "0";
                if (dzien_x < 10)
                    dzien_xs = "0" + dzien_x;
                else
                    dzien_xs = "" + dzien_x;


                _GlobalVariable globalVariable = new _GlobalVariable();
                final String obecnaData = globalVariable.getDateFormat();
                final String dataDostawy = (rok_x + "-" + (miesiac_xs) + "-" + dzien_xs);
                globalVariable.setDateFormat(dataDostawy);

                pd = new ProgressDialog(TrasaActivity.this);
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
                                if (arrTrasy.isNull(0)) {

                                    globalVariable.setDateFormat(obecnaData);
                                    builder = new AlertDialog.Builder(TrasaActivity.this);
                                    builder.setTitle("Brak trasy");
                                    builder.setCancelable(false);
                                    builder.setMessage("Nie ma przypisanej trasy na ten dzień dla kuriera.")
                                            .setIcon(R.drawable.global_ic_warning)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;

                                } else if (arrTrasy.length() == 1) {
                                    String trasa = arrTrasy.getString(0);

                                    globalVariable.setTrasa(trasa.toString());
                                    globalVariable.setTablicaTras(null);

                                    Intent intent = new Intent(TrasaActivity.this, TrasaActivity.class);
                                    startActivity(intent);
                                } else {
                                    globalVariable.setTablicaTras(arrTrasy);
                                    Intent intent = new Intent(TrasaActivity.this, WyborTrasyKurier.class);
                                    startActivity(intent);
                                }
                            }
                            if (retCode != 0) {
                                builder = new AlertDialog.Builder(TrasaActivity.this);
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
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Problem z zasięgiem");
                            builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Odśwież",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final Kurier_Trasa__ListaAdresow_Downloader d = new Kurier_Trasa__ListaAdresow_Downloader(TrasaActivity.this, lvLista, btnStatusPakowania, tvInfo);
                                            d.execute();
                                        }
                                    });
                            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            builder.show();
                            return;
                        } catch (RuntimeException e) {
                            builder = new AlertDialog.Builder(TrasaActivity.this);
                            builder.setTitle("Problem z zasięgiem");
                            builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                            builder.setCancelable(false);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setPositiveButton("Odśwież",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            final Kurier_Trasa__ListaAdresow_Downloader d = new Kurier_Trasa__ListaAdresow_Downloader(TrasaActivity.this, lvLista, btnStatusPakowania, tvInfo);
                                            d.execute();
                                        }
                                    });
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

                Kurier_Trasa_ListaTrasRequest kurierTrasaRequest = new Kurier_Trasa_ListaTrasRequest(getApplicationContext(), responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(TrasaActivity.this);
                queue.add(kurierTrasaRequest);
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
        } else if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(TrasaActivity.this, com.lbapp.Menu.Menu.class);
            TrasaActivity.this.startActivity(intent);
            finish();
            //  return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Naciśnij dwukrotnie, aby wrócić do menu.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
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
            Intent intent = new Intent(TrasaActivity.this, LoginActivity.class);
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

        if (id == com.lbapp.R.id.nav_list) {

        } else if (id == R.id.nav_infoMK) {

            onSprawdzMasterkurieraClicked();

        } else if (id == com.lbapp.R.id.nav_wyborTrasy) {
            _GlobalVariable globalVariable = new _GlobalVariable();
            JSONArray arrTrasy = globalVariable.getTablicaTras();


            if (arrTrasy != null) {
                Intent intent = new Intent(TrasaActivity.this, WyborTrasyKurier.class);
                startActivity(intent);

            } else {
                builder = new AlertDialog.Builder(TrasaActivity.this);
                builder.setTitle("Brak innej trasy");
                builder.setCancelable(false);
                builder.setMessage("To jedyna trasa na ten dzień dla kuriera.")
                        .setNegativeButton("Powrót", null)
                        .create()
                        .show();
            }
        } else if (id == R.id.nav_wyczycSzacunki) {

            onWyczyscSzacunkiClicked();


        } else if (id == R.id.nav_listaAdresowEmergencyCall) {

            onListaAdresowEmergencyCallClicked();


        } else if (id == com.lbapp.R.id.nav_menu) {
            Intent intent = new Intent(TrasaActivity.this, com.lbapp.Menu.Menu.class);
            startActivity(intent);
            finish();
        } else if (id == com.lbapp.R.id.nav_logout) {
            Intent intent = new Intent(TrasaActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(com.lbapp.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onStatusPakowaniaClicked(View view) {


        if (btnStatusPakowania.getText().toString().contains("Naciśnij tutaj, gdy zapakujesz torby do samochodu")) {
            builder = new AlertDialog.Builder(TrasaActivity.this);
            builder.setTitle("Potwierdzenie");
            builder.setMessage("Czy na pewno chcesz potwierdzić zapakowanie toreb?");
            builder.setIcon(R.drawable.global_lightbox_logo);
            builder.setCancelable(false);
            builder.setPositiveButton("Tak",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (lvLista.getCount() != 0) {

                                showLocationDialogTrue();

                            } else {
                                pd.dismiss();
                                builder = new AlertDialog.Builder(TrasaActivity.this);
                                builder.setTitle("Uwaga");
                                builder.setMessage("Nie można zmienić statusu pakowania toreb w przypadku braku listy adresowej.");
                                builder.setIcon(R.drawable.global_ic_warning);
                                builder.setCancelable(false);
                                builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                                return;
                            }


                            if (ActivityCompat.checkSelfPermission(TrasaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrasaActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                pd.dismiss();
                                builder = new AlertDialog.Builder(TrasaActivity.this);
                                builder.setMessage("Lokalizacja GPS jest wyłączona. Naciśnij przycisk, aby przejść do ustawień i włączyć lokalizację.\n\nW aparatach Xiaomi należy wybrać opcję \"Wysoka dokładność\".")
                                        .setCancelable(false)
                                        .setPositiveButton("Przejdź do ustawień", new DialogInterface.OnClickListener() {
                                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                            }
                                        });

                                builder.show();
                                return;
                            }
                            Location location = locationManager.getLastKnownLocation("gps");
                            locationListener.onLocationChanged(location);
                        }
                    });
            builder.setNegativeButton("Nie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            builder.show();
        } else {
            builder = new AlertDialog.Builder(TrasaActivity.this);
            builder.setTitle("Potwierdzenie");
            builder.setMessage("Czy na pewno chcesz cofnąć zapakowanie toreb?");
            builder.setIcon(R.drawable.global_lightbox_logo);
            builder.setCancelable(false);
            builder.setPositiveButton("Tak",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showLocationDialogFalse();

                        }
                    });
            builder.setNegativeButton("Nie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });


            builder.show();
            return;
        }
    }

    //    public void onSzacunekPakowaniaClickied(View view){
//        Calendar mcurrentTime = Calendar.getInstance();
//        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//        final int minute = mcurrentTime.get(Calendar.MINUTE);
//
//
//        final TimePickerDialog mTimePicker;
//        mTimePicker = new TimePickerDialog(ListaAdresowActivity.this, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, final int selectedHour, int selectedMinute) {
//
//                pd = new ProgressDialog(ListaAdresowActivity.this);
//                pd.setTitle("Aktualizowanie");
//                pd.setMessage("Trwa aktualizowanie...");
//                pd.setCancelable(false);
//                pd.show();
//
//                String sMinute = "00";
//                if (selectedMinute < 10)
//                    sMinute = "0" + selectedMinute;
//                else
//                    sMinute = String.valueOf(selectedMinute);
//
//                szacunekPakowania = (selectedHour + ":" + sMinute);
//
//                boolean connected = false;
//                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//                    //we are connected to a network
//                    connected = true;
//                } else
//                    connected = false;
//
//
//                if (connected == true) {
//                    if (lvLista.getCount() != 0) {
//
//                        Response.Listener<String> responseListener = new Response.Listener<String>() {
//
//                            @Override
//                            public void onResponse(String response) {
//                                pd.dismiss();
//                                try {
//                                    JSONObject jsonResponse = new JSONObject(response);
//                                    int retCode = jsonResponse.getInt("retCode");
//                                    String retMessage = jsonResponse.getString("retMessage");
//
//                                    btnSzacunekPakowania.setText(szacunekPakowania);
//                                    btnSzacunekPakowania.setTextSize(14);
//                                    btnSzacunekPakowania.setBackground(ContextCompat.getDrawable(ListaAdresowActivity.this, com.lbapp.R.drawable.button_torbatrue));
//
//                                    if (retCode == 0) {
//
//                                    } else {
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Uwaga");
//                                        builder.setMessage(retMessage);
//                                        builder.setCancelable(false)
//                                                .setNegativeButton("Powrót", null)
//                                                .create()
//                                                .show();
//                                        return;
//                                    }
//
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                } catch (NullPointerException e) {
//                                    e.printStackTrace();
//                                }
//
//                                pd.dismiss();
//
//
//                            }
//                        };
//
//                        Response.ErrorListener errorListener = new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                                pd.dismiss();
//
//                                try {
//
//                                    NetworkResponse networkResponse = error.networkResponse;
//
//                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Błąd");
//                                        builder.setMessage("Brak połączenia. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                                        builder.setCancelable(false)
//                                                .setNegativeButton("Powrót", null)
//                                                .create()
//                                                .show();
//                                        return;
//                                    } else if (error instanceof ServerError) {
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Błąd");
//                                        builder.setMessage("Błąd serwera. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                                        builder.setCancelable(false)
//                                                .setNegativeButton("Powrót", null)
//                                                .create()
//                                                .show();
//                                        return;
//                                    } else if (error instanceof NetworkError) {
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Błąd");
//                                        builder.setMessage("Błąd sieci. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                                        builder.setCancelable(false)
//                                                .setNegativeButton("Powrót", null)
//                                                .create()
//                                                .show();
//                                        return;
//                                    } else if (error instanceof ParseError) {
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Błąd");
//                                        builder.setMessage("Błąd parsowania. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                                        builder.setCancelable(false)
//                                                .setNegativeButton("Powrót", null)
//                                                .create()
//                                                .show();
//                                        return;
//                                    }
//
//
//                                    String jsonError = new String(networkResponse.data);
//                                    JSONObject jsonObject = new JSONObject(jsonError);
//                                    String retMessage = jsonObject.getString("retMessage");
//                                    int status = error.networkResponse.statusCode;
//
//                                    if (status == 401) {
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Sesja wygasła");
//                                        builder.setMessage(retMessage);
//                                        builder.setCancelable(false);
//                                        builder.setPositiveButton("Powrót",
//                                                new DialogInterface.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(DialogInterface dialog, int which) {
//                                                        Intent intent = new Intent(ListaAdresowActivity.this, LoginActivity.class);
//                                                        startActivity(intent);
//                                                    }
//                                                });
//                                        builder.show();
//                                        return;
//
//                                    } else if (networkResponse != null && networkResponse.data != null) {
//
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Błąd");
//                                        builder.setMessage("" + retMessage);
//                                        builder.setCancelable(false)
//                                                .setNegativeButton("Powrót", null)
//                                                .create()
//                                                .show();
//                                        return;
//                                    } else {
//                                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                                        builder.setTitle("Błąd " + status);
//                                        builder.setMessage("" + retMessage);
//                                        builder.setCancelable(false)
//                                                .setNegativeButton("Powrót", null)
//                                                .create()
//                                                .show();
//                                        return;
//                                    }
//
//                                } catch (NullPointerException e) {
//                                    e.printStackTrace();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        };
//
//                        _NIEAKTYWNE_Kurier_SzacunekPakowaniaRequest kurierSzacunekPakowaniaRequest = new _NIEAKTYWNE_Kurier_SzacunekPakowaniaRequest(getApplicationContext(), szacunekPakowania, dateFormat, trasa, responseListener, errorListener);
//                        RequestQueue queue = Volley.newRequestQueue(ListaAdresowActivity.this.getApplicationContext());
//                        queue.add(kurierSzacunekPakowaniaRequest);
//
//
////                pd = new ProgressDialog(ListaAdresowActivity.this);
////                pd.setTitle("Pobieranie danych");
////                pd.setMessage("Trwa pobieranie danych...");
////                pd.setCancelable(false);
////                pd.show();
//                    } else {
//                        pd.dismiss();
//                        builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                        builder.setTitle("Uwaga");
//                        builder.setMessage("Nie można ustalić godziny odbioru toreb w przypadku braku listy adresowej.");
//                        builder.setCancelable(false);
//                        builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                        builder.show();
//                        return;
//                    }
//                } else {
//                    pd.dismiss();
//                    builder = new AlertDialog.Builder(ListaAdresowActivity.this);
//                    builder.setTitle("Błąd");
//                    builder.setMessage("Brak połączenia z Internetem.");
//                    builder.setCancelable(false)
//                            .setNegativeButton("Powrót", null)
//                            .create()
//                            .show();
//                    return;
//                }
//
//
//            }
//
//
//        }, hour, minute, true);
//        mTimePicker.show();
//    }
    private void showLocationDialogTrue() {

        pd = new ProgressDialog(TrasaActivity.this);
        pd.setTitle("Aktualizowanie");
        pd.setMessage("Trwa aktualizowanie...");
        pd.setIcon(R.drawable.global_lightbox_logo);
        pd.setCancelable(false);
        pd.show();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                if (location != null) {
                    szerokoscGPS = String.valueOf(location.getLatitude());
                    dlugoscGPS = String.valueOf(location.getLongitude());

                } else {
                    szerokoscGPS = "0";
                    dlugoscGPS = "0";
                }
                final String trasa = _GlobalVariable.getTrasa();
                statusPakowania = "1";

                String dataDostawy = _GlobalVariable.getDateFormat();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                final String godzinaPakowania = dateFormat.format(date);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pd.dismiss();

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int retCode = jsonResponse.getInt("retCode");
                            String retMessage = jsonResponse.getString("retMessage");


                            if (retCode != 0) {
                                pd.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(TrasaActivity.this);
                                builder.setTitle("Uwaga");
                                builder.setMessage(retMessage);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            btnStatusPakowania.setText("Torby zapakowane do samochodu");
                            btnStatusPakowania.setBackground(ContextCompat.getDrawable(TrasaActivity.this, com.lbapp.R.drawable.button_torbatrue));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }
                };

                Kurier_Trasa_ZmienStatusPakowania_Request kurierPakowanieRequest = new Kurier_Trasa_ZmienStatusPakowania_Request(getApplicationContext(), statusPakowania, dataDostawy, dlugoscGPS, szerokoscGPS, godzinaPakowania, trasa, responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(TrasaActivity.this);
                queue.add(kurierPakowanieRequest);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }


        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
                return;
            }

        }
    }

    private void showLocationDialogFalse() {

        pd = new ProgressDialog(TrasaActivity.this);
        pd.setTitle("Aktualizowanie");
        pd.setMessage("Trwa aktualizowanie...");
        pd.setIcon(R.drawable.global_lightbox_logo);
        pd.setCancelable(false);
        pd.show();

        szerokoscGPS = "0";
        dlugoscGPS = "0";
        statusPakowania = "0";
        final String trasa = _GlobalVariable.getTrasa();

        String dataDostawy = _GlobalVariable.getDateFormat();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        final String godzinaPakowania = dateFormat.format(date);


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int retCode = jsonResponse.getInt("retCode");
                    String retMessage = jsonResponse.getString("retMessage");


                    if (retCode != 0) {
                        pd.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(TrasaActivity.this);
                        builder.setTitle("Uwaga");
                        builder.setMessage(retMessage);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {

                    pd.dismiss();
                    btnStatusPakowania.setText("Naciśnij tutaj, gdy zapakujesz torby do samochodu");
                    btnStatusPakowania.setBackground(ContextCompat.getDrawable(TrasaActivity.this, com.lbapp.R.drawable.kurier_trasa_szczegoly_adresu_button_design_false));

                } catch (NullPointerException e) {
                    builder = new AlertDialog.Builder(TrasaActivity.this);
                    builder.setTitle("Problem z zasięgiem");
                    builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Odśwież",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Kurier_Trasa__ListaAdresow_Downloader d = new Kurier_Trasa__ListaAdresow_Downloader(TrasaActivity.this, lvLista, btnStatusPakowania, tvInfo);
                                    d.execute();
                                }
                            });
                    builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                    return;
                } catch (RuntimeException e) {
                    builder = new AlertDialog.Builder(TrasaActivity.this);
                    builder.setTitle("Problem z zasięgiem");
                    builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Odśwież",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Kurier_Trasa__ListaAdresow_Downloader d = new Kurier_Trasa__ListaAdresow_Downloader(TrasaActivity.this, lvLista, btnStatusPakowania, tvInfo);
                                    d.execute();
                                }
                            });
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

        Kurier_Trasa_ZmienStatusPakowania_Request kurierPakowanieRequest = new Kurier_Trasa_ZmienStatusPakowania_Request(getApplicationContext(), statusPakowania, dataDostawy, dlugoscGPS, szerokoscGPS, godzinaPakowania, trasa, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(TrasaActivity.this);
        queue.add(kurierPakowanieRequest);
    }

    public void showDialogOnButtonClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_ID);
            }
        });
    }

    public void onSprawdzMasterkurieraClicked() {
        pd = new ProgressDialog(TrasaActivity.this);
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

                    if (retInfo.equals("")) {
                        retInfo = "Brak danych";
                    }

                    if (retCode == 0) {

                        builder = new AlertDialog.Builder(TrasaActivity.this);
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
                        builder = new AlertDialog.Builder(TrasaActivity.this);
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


        Global_SprawdzMasterkuriera_Request kurier_Trasa_sprawdzMasterkurieraRequest = new Global_SprawdzMasterkuriera_Request(getApplicationContext(), trasa, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(TrasaActivity.this.getApplicationContext());
        queue.add(kurier_Trasa_sprawdzMasterkurieraRequest);
    }

    public void onWyczyscSzacunkiClicked() {
        builder = new AlertDialog.Builder(TrasaActivity.this);
        builder.setTitle("Potwierdzenie");
        builder.setMessage("Czy na pewno chcesz wyczyścić szacunkowe godziny dostaw?");
        builder.setIcon(R.drawable.global_lightbox_logo);
        builder.setCancelable(false);
        builder.setPositiveButton("Tak",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pd = new ProgressDialog(TrasaActivity.this);
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

                                    if (retCode == 0) {
                                        Intent intent = new Intent(TrasaActivity.this, TrasaActivity.class);
                                        startActivity(intent);
                                    } else {
                                        builder = new AlertDialog.Builder(TrasaActivity.this);
                                        builder.setTitle("Błąd " + retCode);
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


                        Kurier_Trasa_WyczyscSzacunki_Request kurier_Trasa_WyczyscSzacunkiRequest = new Kurier_Trasa_WyczyscSzacunki_Request(getApplicationContext(), trasa, responseListener, errorListener);
                        RequestQueue queue = Volley.newRequestQueue(TrasaActivity.this.getApplicationContext());
                        queue.add(kurier_Trasa_WyczyscSzacunkiRequest);

                    }
                });
        builder.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
        return;
    }

    public void onListaAdresowEmergencyCallClicked() {
        EmergencyNumberSettings.emergencyCallRequest(TrasaActivity.this);
    }

}
