package com.lbapp.Menu;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbapp.Administrator.KalendarzMasterKurier.KalendarzMasterKurierActivity;
import com.lbapp.BuildConfig;
import com.lbapp.Koordynator.KoordynacjaTrasKurierskich.WyborTrasyKoordynator;
import com.lbapp.Kurier.Trasa.WyborTrasyKurier;
import com.lbapp.MasterKurier.Trasa.WyborTrasyMasterkurierActivity;
import com.lbapp.Koordynator.Prognoza.WyborTrasyPrognoza;
import com.lbapp.Wiadomosci.WiadomosciActivity;
import com.lbapp._API.Global_SprawdzWiadomosci_LiczbaNieprzeczytanych_Request;
import com.lbapp._API.Koordynator_AdministracjaTrasKurierskich_ListaTrasRequest;
import com.lbapp._API.Menu_SprawdzZmianyAplikacjiRequest;
import com.lbapp._Global.EmergencyNumberSettings;
import com.lbapp._Global.UpdateApk;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp._Global._GlobalVariable;
import com.lbapp.Kurier.Trasa.TrasaActivity;
import com.lbapp.Services.ServiceGPS;
import com.lbapp._API.Global_UpdateApkRequest;
import com.lbapp._API.Kurier_Trasa_ListaTrasRequest;
import com.lbapp.Koordynator.Prognoza.ListaPrognozyActivity;
import com.lbapp.R;
import com.lbapp._API.MK_Trasa_ListaTras_Request;
import com.lbapp._API.Koordynator_Prognoza_ListaTras_Request;
import java.lang.reflect.Type;


public class Menu extends AppCompatActivity {

    int year,
            month,
            day,
            ROLE_OF_COORDINATOR = 4,
            ROLE_OF_MASTERCOURIER = 16,
            ROLE_OF_SUPERADMINISTRATOR = 32768,
            ROLE_OF_ADMINISTRATOR = 65536,
            retRole,
            retUnreadMessagesNumber;
    LocationManager locationManager;
    LocationListener listener;
    String login;
    Response.ErrorListener errorListener;
    ProgressDialog pd;
    AlertDialog.Builder builder;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public static final String BROADCAST_ACTION = "broadcastIntent";
    Intent broadcastIntent;
    TextView tvMessagesCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(com.lbapp.R.layout.menu_activity);

        SharedPreferences sharedPreferencesUnrMsg = getSharedPreferences("retUnreadMessagesNumberPreferences", Context.MODE_PRIVATE);
        retUnreadMessagesNumber = sharedPreferencesUnrMsg.getInt("retUnreadMessagesNumber", 0);
        tvMessagesCounter = findViewById(R.id.tvMessagesCounter);
        tvMessagesCounter.setText(String.valueOf(retUnreadMessagesNumber));
        if (tvMessagesCounter.getText().equals("0"))
            tvMessagesCounter.setVisibility(View.GONE);

        login = _GlobalVariable.getLogin();
        broadcastIntent = new Intent(BROADCAST_ACTION);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // czarna czcionka statusbar
        getWindow().setStatusBarColor(Color.WHITE); // biale tlo statusbar

        SharedPreferences sharedPreferencesRetRole = getSharedPreferences("retRolePreferences", Context.MODE_PRIVATE);
        retRole = sharedPreferencesRetRole.getInt("retRole", 0);





        //TYLKO PO ZALOGOWANIU
        if (getIntent().getExtras() != null) {
            //SPRAWDZENIE ZADŁUŻENIA TOREB I WIADOMOŚCI
            Intent intentAfterLogin = this.getIntent();
            int retShowBagDebt = intentAfterLogin.getExtras().getInt("retWyswietlZadluzenie");
            String retBagDebtMessage = intentAfterLogin.getExtras().getString("retTrescZadluzenia");
            int retShowLightboxMessage = intentAfterLogin.getExtras().getInt("retWyswietlWiadomosc");
            String retLightboxMessage = intentAfterLogin.getExtras().getString("retTrescWiadomosci");
            int retUnreadMessagesNumber = intentAfterLogin.getExtras().getInt("retUnreadMessagesNumber");


            if (retShowBagDebt > 0) {
                builder = new AlertDialog.Builder(Menu.this);
                builder.setTitle("Powiadomienie");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.global_ic_warning);
                builder.setMessage(retBagDebtMessage)
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
            }
            if (retShowLightboxMessage > 0) {
                builder = new AlertDialog.Builder(Menu.this);
                builder.setTitle("Wiadomość");
                builder.setCancelable(false);
                builder.setIcon(R.drawable.global_lightbox_logo);
                builder.setMessage(retLightboxMessage)
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
            }
        }



        //WLACZENIE CYKLICZNEGO SPRAWDZANIA KOORDYNATOW GPS
        if (locationManager == null) {

            Intent background = new Intent(this, ServiceGPS.class);
            Log.v("GPS_SERVICE", "START");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(background);
            } else {
                this.startService(background);
            }
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, listener);
        }

        //domyslny errorlistener
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
                            builder = new AlertDialog.Builder(Menu.this);
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
                            builder = new AlertDialog.Builder(Menu.this);
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
                            builder = new AlertDialog.Builder(Menu.this);
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
                            builder = new AlertDialog.Builder(Menu.this);
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
                            builder = new AlertDialog.Builder(Menu.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Menu.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builder.show();
                            return;

                        } else if (networkResponse != null && networkResponse.data != null) {

                            builder = new AlertDialog.Builder(Menu.this);
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
                            builder = new AlertDialog.Builder(Menu.this);
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
                    builder = new AlertDialog.Builder(Menu.this);
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


    public void onCourierClicked(View view) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog =
                new DatePickerDialog(Menu.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int rok, int miesiacRoku, int dzienMiesiaca) {

                                year = rok;
                                month = miesiacRoku + 1;
                                String _month = "0";
                                if (month < 10)
                                    _month = "0" + month;
                                else
                                    _month = "" + month;
                                day = dzienMiesiaca;
                                String _day = "0";
                                if (day < 10)
                                    _day = "0" + day;
                                else
                                    _day = "" + day;

                                String dataDostawy = (year + "-" + (_month) + "-" + _day);
                                _GlobalVariable globalVariable = new _GlobalVariable();
                                globalVariable.setDateFormat(dataDostawy);

                                pd = new ProgressDialog(Menu.this);
                                pd.setTitle("Pobieranie");
                                pd.setMessage("Trwa pobieranie tras...");
                                pd.setIcon(R.drawable.global_lightbox_logo);
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
                                            JSONArray arrTrasy = jsonResponse.getJSONArray("arrTrasy");
                                            _GlobalVariable globalVariable = new _GlobalVariable();


                                            if (retCode == 0) {
                                                if (arrTrasy.isNull(0)) {
                                                    builder = new AlertDialog.Builder(Menu.this);
                                                    builder.setTitle("Brak trasy");
                                                    builder.setCancelable(false);
                                                    builder.setIcon(R.drawable.global_ic_warning);
                                                    builder.setMessage("Nie ma przypisanej trasy na ten dzień dla kuriera.")
                                                            .setNegativeButton("Powrót", null)
                                                            .create()
                                                            .show();
                                                    return;

                                                } else if (arrTrasy.length() == 1) {
                                                    String trasa = arrTrasy.getString(0);

                                                    globalVariable.setTablicaTras(arrTrasy);
                                                    globalVariable.setTrasa(trasa);

                                                    Intent intent = new Intent(Menu.this, TrasaActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    globalVariable.setTablicaTras(arrTrasy);

                                                    Intent intent = new Intent(Menu.this, WyborTrasyKurier.class);
                                                    startActivity(intent);
                                                }
                                            } else if (retCode == 50) {
                                                builder = new AlertDialog.Builder(Menu.this);
                                                builder.setTitle("Sesja wygasła");
                                                builder.setMessage(retMessage);
                                                builder.setIcon(R.drawable.global_ic_warning);
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("Powrót",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(Menu.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                builder.show();
                                                return;


                                            } else if (retCode != 0) {
                                                builder = new AlertDialog.Builder(Menu.this);
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


                                Kurier_Trasa_ListaTrasRequest kurierTrasaRequest = new Kurier_Trasa_ListaTrasRequest(getApplicationContext(), responseListener, errorListener);
                                RequestQueue queue = Volley.newRequestQueue(Menu.this);
                                queue.add(kurierTrasaRequest);
                            }
                        }, year, month, day);
        datePickerDialog.show();
    }

    public void onMastercourierClicked(final View view) {
        if ((retRole & ROLE_OF_MASTERCOURIER) != ROLE_OF_MASTERCOURIER && (retRole & ROLE_OF_SUPERADMINISTRATOR) != ROLE_OF_SUPERADMINISTRATOR && (retRole & ROLE_OF_ADMINISTRATOR) != ROLE_OF_ADMINISTRATOR) // jeżeli nie ma uprawnień masterkuriera / admina / superadmina
        {
            builder = new AlertDialog.Builder(Menu.this);
            builder.setTitle("Brak uprawnień");
            builder.setMessage("Nie posiadasz uprawnień MasterKuriera.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false)
                    .setNegativeButton("Powrót", null)
                    .create()
                    .show();
            return;
        }

        String[] wybor = {"Trasa masterkurierska", "Sprawdź grafik na 7 dni"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Opcje MasterKuriera")
                .setIcon(R.drawable.global_lightbox_logo)
                .setItems(wybor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Trasa masterkurierska
                            {
                                onMastercourierRouteClicked(view);
                                break;
                            }
                            case 1: // Sprawdź swój grafik na najbliższe 7 dni
                            {
                                onMastercourierDutyClicked(view);
                                break;
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void onMastercourierRouteClicked(View view) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog =
                new DatePickerDialog(Menu.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int __year, int __month, int __day) {


                                year = __year;
                                month = __month + 1;
                                String _month = "0";
                                if (month < 10)
                                    _month = "0" + month;
                                else
                                    _month = "" + month;
                                day = __day;
                                String _day = "0";
                                if (day < 10)
                                    _day = "0" + day;
                                else
                                    _day = "" + day;

                                String dataDostawy = (year + "-" + (_month) + "-" + _day);
                                _GlobalVariable globalVariable = new _GlobalVariable();
                                globalVariable.setDateFormat(dataDostawy);

                                pd = new ProgressDialog(Menu.this);
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
                                                listTrasyId.add((jsonResponse.getInt("trasaId")));
                                                listTrasy.add((jsonResponse.getString("trasa")));
                                            }

                                            _GlobalVariable globalVariable = new _GlobalVariable();


                                            if (retCode == 0) {
                                                if (arrTrasy.isNull(0)) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                                                    builder.setTitle("Brak trasy");
                                                    builder.setCancelable(false);
                                                    builder.setMessage("Nie ma przypisanej listy na ten dzień dla Master Kuriera.")
                                                            .setIcon(R.drawable.global_ic_warning)
                                                            .setNegativeButton("Powrót", null)
                                                            .create()
                                                            .show();
                                                    return;

                                                } else if (arrTrasy.length() == 1) {
                                                    int trasaId = listTrasyId.get(0);
                                                    String trasa = listTrasy.get(0);

                                                    globalVariable.setTrasaMK(trasa);
                                                    globalVariable.setListTrasMK(listTrasy);
                                                    globalVariable.setTrasaIdMK(trasaId);

                                                    Intent intent = new Intent(Menu.this, com.lbapp.MasterKurier.Trasa.TrasaActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    globalVariable.setListTrasMK(listTrasy);
                                                    globalVariable.setListTrasIdMK(listTrasyId);

                                                    Intent intent = new Intent(Menu.this, WyborTrasyMasterkurierActivity.class);
                                                    startActivity(intent);
                                                }
                                            } else if (retCode == 50) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                                                builder.setTitle("Sesja wygasła");
                                                builder.setMessage(retMessage);
                                                builder.setCancelable(false);
                                                builder.setIcon(R.drawable.global_ic_warning);
                                                builder.setPositiveButton("Powrót",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(Menu.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                builder.show();
                                                return;
                                            } else if (retCode != 0) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
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


                                MK_Trasa_ListaTras_Request mk_trasaListaTrasRequest = new MK_Trasa_ListaTras_Request(getApplicationContext(), responseListener, errorListener);
                                RequestQueue queue = Volley.newRequestQueue(Menu.this);
                                queue.add(mk_trasaListaTrasRequest);

                            }
                        }, year, month, day);
        datePickerDialog.show();
    }

    public void onMastercourierDutyClicked(View view) {
        Intent intent = new Intent(Menu.this, com.lbapp.MasterKurier.Grafik.GrafikActivity.class);
        startActivity(intent);
    }


    public void onCoordinatorClicked(final View view) {
        if ((retRole & ROLE_OF_COORDINATOR) != ROLE_OF_COORDINATOR && (retRole & ROLE_OF_SUPERADMINISTRATOR) != ROLE_OF_SUPERADMINISTRATOR && (retRole & ROLE_OF_ADMINISTRATOR) != ROLE_OF_ADMINISTRATOR) // jeżeli nie ma uprawnień koordynatora / superadmina / admina
        {
            builder = new AlertDialog.Builder(Menu.this);
            builder.setTitle("Brak uprawnień");
            builder.setMessage("Nie posiadasz uprawnień Koordynatora.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false)
                    .setNegativeButton("Powrót", null)
                    .create()
                    .show();
            return;
        }

        String[] wybor = {"Prognoza", "Koordynacja tras kurierskich"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Opcje Koordynatora")
                .setIcon(R.drawable.global_lightbox_logo)
                .setItems(wybor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Prognoza
                            {
                                onCoordinatorPrognozaClicked(view);
                                break;
                            }
                            case 1: // Koordynacja tras kurierskich
                            {
                                onCoordinatorAdministerCourierRoutes(view);
                                break;
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onCoordinatorPrognozaClicked(View view) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog =
                new DatePickerDialog(Menu.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int rok, int miesiacRoku, int dzienMiesiaca) {

                                year = rok;
                                month = miesiacRoku + 1;
                                String _month = "0";
                                if (month < 10)
                                    _month = "0" + month;
                                else
                                    _month = "" + month;
                                day = dzienMiesiaca;
                                String _day = "0";
                                if (day < 10)
                                    _day = "0" + day;
                                else
                                    _day = "" + day;

                                String dataDostawy = (year + "-" + (_month) + "-" + _day);
                                _GlobalVariable globalVariable = new _GlobalVariable();
                                globalVariable.setDateFormat(dataDostawy);

                                pd = new ProgressDialog(Menu.this);
                                pd.setTitle("Pobieranie");
                                pd.setMessage("Trwa pobieranie tras...");
                                pd.setIcon(R.drawable.global_lightbox_logo);
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
                                            JSONArray arrTrasy = jsonResponse.getJSONArray("arrTrasy");
                                            _GlobalVariable globalVariable = new _GlobalVariable();


                                            if (retCode == 0) {
                                                if (arrTrasy.isNull(0)) {
                                                    builder = new AlertDialog.Builder(Menu.this);
                                                    builder.setTitle("Brak prognozy");
                                                    builder.setCancelable(false);
                                                    builder.setMessage("Nie ma przypisanej listy prognozowej na ten dzień dla koordynatora.")
                                                            .setIcon(R.drawable.global_ic_warning)
                                                            .setNegativeButton("Powrót", null)
                                                            .create()
                                                            .show();
                                                    return;

                                                } else if (arrTrasy.length() == 1) {
                                                    String trasa = arrTrasy.getString(0);

                                                    globalVariable.setTablicaTrasKoor(arrTrasy);
                                                    globalVariable.setTrasaKoor(trasa);

                                                    Intent intent = new Intent(Menu.this, ListaPrognozyActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    globalVariable.setTablicaTrasKoor(arrTrasy);

                                                    Intent intent = new Intent(Menu.this, WyborTrasyPrognoza.class);
                                                    startActivity(intent);
                                                }
                                            } else if (retCode == 50) {
                                                builder = new AlertDialog.Builder(Menu.this);
                                                builder.setTitle("Sesja wygasła");
                                                builder.setMessage(retMessage);
                                                builder.setCancelable(false);
                                                builder.setIcon(R.drawable.global_ic_warning);
                                                builder.setPositiveButton("Powrót",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(Menu.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                builder.show();
                                                return;


                                            } else if (retCode != 0) {
                                                builder = new AlertDialog.Builder(Menu.this);
                                                builder.setTitle("Błąd");
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

                                Koordynator_Prognoza_ListaTras_Request koordynatorPrognoza_TrasaRequest = new Koordynator_Prognoza_ListaTras_Request(getApplicationContext(), responseListener, errorListener);
                                RequestQueue queue = Volley.newRequestQueue(Menu.this);
                                queue.add(koordynatorPrognoza_TrasaRequest);

                            }
                        }, year, month, day);
        datePickerDialog.show();
    }

    public void onCoordinatorAdministerCourierRoutes(View view) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog =
                new DatePickerDialog(Menu.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int rok, int miesiacRoku, int dzienMiesiaca) {

                                year = rok;
                                month = miesiacRoku + 1;
                                String _month = "0";
                                if (month < 10)
                                    _month = "0" + month;
                                else
                                    _month = "" + month;
                                day = dzienMiesiaca;
                                String _day = "0";
                                if (day < 10)
                                    _day = "0" + day;
                                else
                                    _day = "" + day;

                                String dataDostawy = (year + "-" + (_month) + "-" + _day);
                                _GlobalVariable globalVariable = new _GlobalVariable();
                                globalVariable.setDateFormat(dataDostawy);

                                pd = new ProgressDialog(Menu.this);
                                pd.setTitle("Pobieranie");
                                pd.setMessage("Trwa pobieranie tras...");
                                pd.setIcon(R.drawable.global_lightbox_logo);
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
                                            JSONArray arrTrasy = jsonResponse.getJSONArray("arrTrasy");
                                            _GlobalVariable globalVariable = new _GlobalVariable();


                                            if (retCode == 0) {
                                                if (arrTrasy.isNull(0)) {
                                                    builder = new AlertDialog.Builder(Menu.this);
                                                    builder.setTitle("Brak trasy");
                                                    builder.setCancelable(false);
                                                    builder.setIcon(R.drawable.global_ic_warning);
                                                    builder.setMessage("Nie ma przypisanej trasy na ten dzień.")
                                                            .setNegativeButton("Powrót", null)
                                                            .create()
                                                            .show();
                                                    return;

                                                } else if (arrTrasy.length() == 1) {
                                                    String trasa = arrTrasy.getString(0);

                                                    globalVariable.setTablicaTras(arrTrasy);
                                                    globalVariable.setTrasa(trasa);

                                                    Intent intent = new Intent(Menu.this, TrasaActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    globalVariable.setTablicaTras(arrTrasy);

                                                    Intent intent = new Intent(Menu.this, WyborTrasyKoordynator.class);
                                                    startActivity(intent);
                                                }
                                            } else if (retCode == 50) {
                                                builder = new AlertDialog.Builder(Menu.this);
                                                builder.setTitle("Sesja wygasła");
                                                builder.setMessage(retMessage);
                                                builder.setIcon(R.drawable.global_ic_warning);
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("Powrót",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent intent = new Intent(Menu.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                builder.show();
                                                return;


                                            } else if (retCode != 0) {
                                                builder = new AlertDialog.Builder(Menu.this);
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


                                Koordynator_AdministracjaTrasKurierskich_ListaTrasRequest koordynator_administracjaTrasKurierskich_listaTrasRequest = new Koordynator_AdministracjaTrasKurierskich_ListaTrasRequest(getApplicationContext(), responseListener, errorListener);
                                RequestQueue queue = Volley.newRequestQueue(Menu.this);
                                queue.add(koordynator_administracjaTrasKurierskich_listaTrasRequest);
                            }
                        }, year, month, day);
        datePickerDialog.show();
    }

    public void onAdministratorClicked(final View view) {
        if ((retRole & ROLE_OF_ADMINISTRATOR) != ROLE_OF_ADMINISTRATOR && (retRole & ROLE_OF_SUPERADMINISTRATOR) != ROLE_OF_SUPERADMINISTRATOR) // jeżeli nie ma uprawnień admina / superadmina
        {
            builder = new AlertDialog.Builder(Menu.this);
            builder.setTitle("Brak uprawnień");
            builder.setMessage("Nie posiadasz uprawnień Administratora.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false)
                    .setNegativeButton("Powrót", null)
                    .create()
                    .show();
            return;
        }

        String[] wybor = {"Kalendarz MasterKurierów"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Funkcje administracyjne")
                .setIcon(R.drawable.global_lightbox_logo)
                .setItems(wybor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // kalendarz masterkurierów
                            {
                                onAdministratorMastercouriersCalendarClicked(view);
                                break;
                            }
                            case 1: //
                            {
                            }
                            break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onAdministratorMastercouriersCalendarClicked(View view) {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog =
                new DatePickerDialog(Menu.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int __year, int __month, int __day) {

                                year = __year;
                                month = __month + 1;
                                String _month = "0";
                                if (month < 10)
                                    _month = "0" + month;
                                else
                                    _month = "" + month;
                                day = __day;
                                String _day = "0";
                                if (day < 10)
                                    _day = "0" + day;
                                else
                                    _day = "" + day;

                                String dataDostawy = (year + "-" + (_month) + "-" + _day);
                                _GlobalVariable globalVariable = new _GlobalVariable();
                                globalVariable.setDateFormat(dataDostawy);

                                pd = new ProgressDialog(Menu.this);
                                pd.setTitle("Pobieranie");
                                pd.setMessage("Trwa pobieranie kalendarza...");
                                pd.setIcon(R.drawable.global_lightbox_logo);
                                pd.setCancelable(false);
                                pd.show();

                                Intent intent = new Intent(Menu.this, KalendarzMasterKurierActivity.class);
                                startActivity(intent);
                            }
                        }, year, month, day);
        datePickerDialog.show();
    }

    public void onMessagesClicked(final View view) {
        Intent intent = new Intent(Menu.this, WiadomosciActivity.class);
        startActivity(intent);
    }


    public void onSettingsClicked(final View view) {
        String[] wybor = {"Sprawdź aktualizacje"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Ustawienia")
                .setIcon(R.drawable.menu_ic_settings)
                .setItems(wybor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Sprawdź aktualizacje
                            {
                                onSettingsUpdateApkClicked(view);
                                break;
                            }
                            case 1: //
                            {
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onSettingsUpdateApkClicked(View view) {
        pd = new ProgressDialog(Menu.this);
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
                    int retNewestVersionCode = jsonResponse.getInt("retNewestVersionCode");

                    if (retCode == 0 && BuildConfig.VERSION_CODE < retNewestVersionCode) {
                        builder = new AlertDialog.Builder(Menu.this);
                        builder.setTitle("Informacja");
                        builder.setMessage("Dostępna jest opcjonalna aktualizacja aplikacji. Czy chcesz ją zainstalować?");
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UpdateApk update = new UpdateApk(Menu.this);
                                update.execute();
                            }
                        });
                        builder.setNegativeButton("Nie", null);
                        builder.setCancelable(false)
                                .create()
                                .show();
                        return;

                    } else {
                        builder = new AlertDialog.Builder(Menu.this);
                        builder.setTitle("Informacja");
                        builder.setMessage("Brak nowych aktualizacji aplikacji.");
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setCancelable(false)
                                .setNegativeButton("Powrót", null)
                                .create()
                                .show();
                        return;
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };

        Global_UpdateApkRequest global_updateApkRequest = new Global_UpdateApkRequest(this, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(global_updateApkRequest);

    }

    public void onHelpClicked(final View view) {
        String[] wybor = {"Instrukcja dla Kurierów", "Instrukcja dla MasterKurierów", "Instrukcja dla Koordynatorów"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
        builder.setTitle("Pomoc")
                .setIcon(R.drawable.menu_ic_help)
                .setItems(wybor, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // instrukcja dla kurierow
                            {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pub.serwer1851335.home.pl/pub/app-redhatbox/kljHIUH9087HNKDnYCD9Vjsy589/instrukcja_kurier.pdf"));
                                startActivity(browserIntent);
                                break;
                            }
                            case 1: // instrukcja dla masterkurierow
                            {
                                if ((retRole & ROLE_OF_MASTERCOURIER) != ROLE_OF_MASTERCOURIER && (retRole & ROLE_OF_SUPERADMINISTRATOR) != ROLE_OF_SUPERADMINISTRATOR && (retRole & ROLE_OF_ADMINISTRATOR) != ROLE_OF_ADMINISTRATOR) // jeżeli nie ma uprawnień masterkuriera / admina / superadmina
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                                    builder = new AlertDialog.Builder(Menu.this);
                                    builder.setTitle("Brak uprawnień");
                                    builder.setMessage("Nie posiadasz uprawnień MasterKuriera.");
                                    builder.setIcon(R.drawable.global_ic_warning);
                                    builder.setCancelable(false)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;
                                }

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pub.serwer1851335.home.pl/pub/app-redhatbox/kljHIUH9087HNKDnYCD9Vjsy589/instrukcja_masterkurier.pdf"));
                                startActivity(browserIntent);
                                break;
                            }
                            case 2: // instrukcja dla koordynatorow
                            {
                                if ((retRole & ROLE_OF_COORDINATOR) != ROLE_OF_COORDINATOR && (retRole & ROLE_OF_SUPERADMINISTRATOR) != ROLE_OF_SUPERADMINISTRATOR && (retRole & ROLE_OF_ADMINISTRATOR) != ROLE_OF_ADMINISTRATOR) // jeżeli nie ma uprawnień koordynatora / superadmina / admina
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                                    builder = new AlertDialog.Builder(Menu.this);
                                    builder.setTitle("Brak uprawnień");
                                    builder.setMessage("Nie posiadasz uprawnień Koordynatora.");
                                    builder.setIcon(R.drawable.global_ic_warning);
                                    builder.setCancelable(false)
                                            .setNegativeButton("Powrót", null)
                                            .create()
                                            .show();
                                    return;
                                }

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://pub.serwer1851335.home.pl/pub/app-redhatbox/kljHIUH9087HNKDnYCD9Vjsy589/instrukcja_koordynator.pdf"));
                                startActivity(browserIntent);
                                break;
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onWhatsNewClicked(final View view) {

        pd = new ProgressDialog(Menu.this);
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
                    String arrZmiany = jsonResponse.getString("arrZmiany");
                    String _strListOfVersions = "";

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<WersjaAplikacjiModel>>(){}.getType();
                    List<WersjaAplikacjiModel> listWersjeAplikacji = gson.fromJson(arrZmiany, type);
                    for (WersjaAplikacjiModel wersjaAplikacji : listWersjeAplikacji){
                        _strListOfVersions = _strListOfVersions + ("Wersja: " + wersjaAplikacji.nazwaWersji + "(" + wersjaAplikacji.wersja + ")" + "\nData wdrożenia: "+ wersjaAplikacji.dataWdrozenia.substring(0, 10) + "\nOpis: " + wersjaAplikacji.opis) + "\n\n";
                    }

                    if (retCode == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                        builder.setTitle("Zmiany w aplikacji");
                        builder.setCancelable(false);
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setMessage(_strListOfVersions)
                                .setPositiveButton("Powrót", null)
                                .create()
                                .show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                        builder.setTitle("Uwaga");
                        builder.setCancelable(false);
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setMessage(retMessage)
                                .setPositiveButton("Powrót", null)
                                .create()
                                .show();
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };

        Menu_SprawdzZmianyAplikacjiRequest menu_sprawdzZmianyAplikacjiRequest = new Menu_SprawdzZmianyAplikacjiRequest(Menu.this, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(menu_sprawdzZmianyAplikacjiRequest);
    }


    public void onMenuEmergencyCallClicked(View view) {
        EmergencyNumberSettings.emergencyCallRequest(Menu.this);
    }

    public void onLogoutClicked(View view) {
        Log.v("GPS_SERVICE", "STOP");
        locationManager.removeUpdates(listener); //zatrzymaj serwis gps
        Intent intent = new Intent(Menu.this, LoginActivity.class);
        Menu.this.startActivity(intent);
        finish();
    }

    //POWRÓT PRZYCISKIEM FIZYCZNYM "WSTECZ"
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onResume() {
        super.onResume();
        // .... other stuff in my onResume ....
        this.doubleBackToExitPressedOnce = false;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Log.v("GPS_SERVICE", "STOP");
            locationManager.removeUpdates(listener);
            Intent intent = new Intent(Menu.this, LoginActivity.class);
            Menu.this.startActivity(intent);
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Naciśnij dwukrotnie, aby się wylogować.", Toast.LENGTH_SHORT).show();

    }


    //************************** GPS SERVICE **********************************
    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("GPS_SERVICE", "Location changed");
            if (isBetterLocation(loc, null)) {
                loc.getLatitude();
                loc.getLongitude();
                broadcastIntent.putExtra("Latitude", loc.getLatitude());
                broadcastIntent.putExtra("Longitude", loc.getLongitude());
                broadcastIntent.putExtra("Provider", loc.getProvider());
                sendBroadcast(broadcastIntent);

            }


        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Włącz usługę lokalizacji", Toast.LENGTH_LONG).show();
        }


        public void onProviderEnabled(String provider) {

        }


        public void onStatusChanged(String provider, int status, Bundle extras) {
        }


    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    //***********************************************************************



}
