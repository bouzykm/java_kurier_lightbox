package com.lbapp.Kurier.Trasa;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lbapp._Global.AdresObject;
import com.lbapp._Global.BarcodeScanner;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp._Global._GlobalVariable;
import com.lbapp.R;
import com.lbapp._API.Kurier_Trasa_SzczegolyAdresu_DodajKomentarz_Request;
import com.lbapp._API._PORT;
import com.lbapp._API.Kurier_Trasa_SzczegolyAdresu_DodajSzacunek_Request;
import com.lbapp._API.Kurier_Trasa_SzczegolyAdresu_ZmienStatusDostarczenia_Request;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;


public class SzczegolyAdresuAcitivty extends AppCompatActivity {

    String trasa, tempUrlId, adresId, adres, firma, kodPocztowy, godzinaOd, godzinaDo, uwagi, kodDoDomofonu, raport, szerokoscGPS, dlugoscGPS, numeryProduktow, telefon, dzielnica, miejscowosc, komentarz,
            login, informacjaZwrotna = "";
    TextView tvDzielnica, tvAdres, tvGodzina, tvFirma, tvUwagi, tvKodDoDomofonu, tvNumeryProduktow, FirmaLabel, UwagiLabel, KodDoDomofonuLabel, TorbaProduktLabel;
    ProgressDialog pd;
    LocationManager locationManager;
    LocationListener locationListener;
    FloatingTextButton ftbDostarcz, ftbNieDostarcz, ftbTorba;
    FloatingActionButton fabSzacunek, fabMapa, fabTelefon, fabSMS, fabKomentarz;
    Animation fabOpen, fabClose, rotateClockwise, rotateAnticlockwise;
    AlertDialog.Builder builder;
    String port = _PORT.getPort();
    int zostawTorbe;
    RequestQueue queue;
    Response.Listener<String> responseListener;
    Response.ErrorListener errorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kurier_trasa_szczegoly_adresu_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(" ");

        Intent i = this.getIntent();
        adresId = i.getExtras().getString("ADRESID_KEY");
        adres = i.getExtras().getString("ADRES_KEY");
        miejscowosc = i.getExtras().getString("MIEJSCOWOSC_KEY");
        firma = i.getExtras().getString("FIRMA_KEY");
        dzielnica = i.getExtras().getString("DZIELNICA_KEY");
        kodPocztowy = i.getExtras().getString("KOD_KEY");
        godzinaOd = i.getExtras().getString("GODZINAOD_KEY");
        godzinaDo = i.getExtras().getString("GODZINADO_KEY");
        telefon = i.getExtras().getString("TELEFON_KEY");
        uwagi = i.getExtras().getString("UWAGI_KEY");
        kodDoDomofonu = i.getExtras().getString("KODDODOMOFONU_KEY");
        raport = i.getExtras().getString("RAPORT_KEY");
        szerokoscGPS = i.getExtras().getString("SZEROKOSCGPS_KEY");
        dlugoscGPS = i.getExtras().getString("DLUGOSCGPS_KEY");
        numeryProduktow = i.getExtras().getString("NUMERYPRODUKTOW_KEY");
        komentarz = i.getExtras().getString("KOMENTARZ_KEY");
        zostawTorbe = i.getExtras().getInt("ZOSTAWTORBE_KEY");

        tvAdres = findViewById(R.id.tvAdres);
        tvGodzina = findViewById(R.id.tvGodzina);
        tvDzielnica = findViewById(R.id.tvDzielnica);
        tvFirma = findViewById(R.id.tvFirma);
        FirmaLabel = findViewById(R.id.FirmaLabel);
        tvUwagi = findViewById(R.id.tvUwagi);
        UwagiLabel = findViewById(R.id.UwagiLabel);
        tvKodDoDomofonu = findViewById(R.id.tvKodDoDomofonu);
        KodDoDomofonuLabel = findViewById(R.id.KodDoDomofonuLabel);
        tvNumeryProduktow = findViewById(R.id.tvNumeryProduktow);
        TorbaProduktLabel = findViewById(R.id.TorbaProduktLabel);


        fabSzacunek = findViewById(R.id.fabSzacunek);
        fabMapa = findViewById(R.id.fabMapa);
        fabTelefon = findViewById(R.id.fabTelefon);
        fabSMS = findViewById(R.id.fabSMS);
        fabKomentarz = findViewById(R.id.fabKomentarz);


        //animacje dla floating action button
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotateClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        rotateAnticlockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);

        ftbDostarcz = findViewById(R.id.ftbDostarcz);
        ftbNieDostarcz = findViewById(R.id.ftbNieDostarcz);
        ftbTorba = findViewById(R.id.ftbTorba);

        tvAdres.setText(adres);
        tvAdres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvGodzina.setText(godzinaOd + "-" + godzinaDo);
        tvDzielnica.setText(dzielnica);
        tvFirma.setText(firma);
        tvUwagi.setText(uwagi);
        tvKodDoDomofonu.setText(kodDoDomofonu);
        tvNumeryProduktow.setText(numeryProduktow);

        trasa = _GlobalVariable.getTrasa();
        login = _GlobalVariable.getLogin();

        //odpowiedz na requesty-------------------------------------------------------------------------------------------------------------------------------
        queue = Volley.newRequestQueue(SzczegolyAdresuAcitivty.this);

        responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int retCode = jsonResponse.getInt("retCode");
                    String retMessage = jsonResponse.getString("retMessage");

                    if (retCode == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
                        builder.setTitle("Potwierdzenie");
                        builder.setMessage(informacjaZwrotna);
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .create()
                                .show();
                        return;

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
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
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };

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
//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            builder = new AlertDialog.Builder(AdresySzczegolyAcitivty.this);
//                            builder.setTitle("Błąd");
//                            builder.setMessage("Brak połączenia. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                            builder.setIcon(R.drawable.global_ic_warning);
//                            builder.setCancelable(false)
//                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    })
//                                    .create()
//                                    .show();
//                            return;
//                        } else if (error instanceof ServerError) {
//                            builder = new AlertDialog.Builder(AdresySzczegolyAcitivty.this);
//                            builder.setTitle("Błąd");
//                            builder.setMessage("Błąd serwera. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                            builder.setIcon(R.drawable.global_ic_warning);
//                            builder.setCancelable(false)
//                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    })
//                                    .create()
//                                    .show();
//                            return;
//                        } else if (error instanceof NetworkError) {
//                            builder = new AlertDialog.Builder(AdresySzczegolyAcitivty.this);
//                            builder.setTitle("Błąd");
//                            builder.setMessage("Błąd sieci. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                            builder.setIcon(R.drawable.global_ic_warning);
//                            builder.setCancelable(false)
//                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    })
//                                    .create()
//                                    .show();
//                            return;
//                        } else if (error instanceof ParseError) {
//                            builder = new AlertDialog.Builder(AdresySzczegolyAcitivty.this);
//                            builder.setTitle("Błąd");
//                            builder.setMessage("Błąd parsowania. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
//                            builder.setIcon(R.drawable.global_ic_warning);
//                            builder.setCancelable(false)
//                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//
//                                        }
//                                    })
//                                    .create()
//                                    .show();
//                            return;
//                        }

                        String jsonError = new String(networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String retMessage = jsonObject.getString("retMessage");
                        int status = error.networkResponse.statusCode;

                        if (status == 401) {
                            builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(SzczegolyAdresuAcitivty.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builder.show();
                            return;

                        } else if (networkResponse != null && networkResponse.data != null) {

                            builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
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
                            builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
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
                    builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
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

        //----------------------------------------------------------------------------------------------------------------------------------------------------

        if (raport.equals("0"))
            ftbNieDostarcz.setVisibility(View.GONE);
        else
            ftbDostarcz.setVisibility(View.GONE);

        if (firma.equals("")) {
            tvFirma.setVisibility(View.GONE);
            FirmaLabel.setVisibility(View.GONE);
        }

        if (kodDoDomofonu.equals("")) {
            tvKodDoDomofonu.setVisibility(View.GONE);
            KodDoDomofonuLabel.setVisibility(View.GONE);
        }

        if (zostawTorbe == 0)
            ftbTorba.setVisibility(View.GONE);


        ftbTorba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onZostawTorbeClicked(v);
            }
        });

        ftbDostarcz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDostarczClicked(v);
            }
        });

        ftbNieDostarcz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNieDostarczClicked(v);
            }

        });


    }

    public void setURL(String url) {
        tempUrlId = url;
        Kurier_Trasa_SzczegolyAdresu_ZmienStatusDostarczenia_Request.urlSet(url);
        Kurier_Trasa_SzczegolyAdresu_DodajSzacunek_Request.urlSet(url);
    }

    public void onSzacunekClicked(View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SzczegolyAdresuAcitivty.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                pd = new ProgressDialog(SzczegolyAdresuAcitivty.this);
                pd.setTitle("Pobieranie danych");
                pd.setMessage("Trwa pobieranie danych...");
                pd.setIcon(R.drawable.global_lightbox_logo);
                pd.setCancelable(false);
                pd.show();

                tempUrlId = port + "/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/Szacunek/" + adresId;
                setURL(tempUrlId);

                //czas zawsze z dwucyfrową godziną i dwucyfrową minutą
                String sMinute = "00";
                if (selectedMinute < 10)
                    sMinute = "0" + selectedMinute;
                else
                    sMinute = String.valueOf(selectedMinute);

                String szacunekDostawy = (selectedHour + ":" + sMinute);
                AdresObject l = null;
                l = new AdresObject();
                l.setSzacunekDostawy(szacunekDostawy);

                //inny response niz domyslny
                responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int retCode = jsonResponse.getInt("retCode");
                            String retMessage = jsonResponse.getString("retMessage");

                            if (retCode == 0) {

                                Intent intent = new Intent(SzczegolyAdresuAcitivty.this, TrasaActivity.class);
                                startActivity(intent);

                                return;

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
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
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                };

                Kurier_Trasa_SzczegolyAdresu_DodajSzacunek_Request kurierSzacunekRequest = new Kurier_Trasa_SzczegolyAdresu_DodajSzacunek_Request(getApplicationContext(), szacunekDostawy, trasa, responseListener, errorListener);
                queue.add(kurierSzacunekRequest);


            }
        }, hour, minute, true);
        mTimePicker.show();
    }

    public void onTelefonClicked(View view) {
        String[] arrTelefony = telefon.split("\\;");
        if (arrTelefony.length == 1) { // jezeli jest jeden nr telefonu - przejdz do ekranu dzwonienia
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + telefon));
            startActivity(intent);
        }else // jezeli jest wiecej niz jeden nr tel -- dialog z wyborem numeru telefonu
        {
            view = getLayoutInflater().inflate(R.layout._global_wybierz_numer_telefonu_dialog, null);

            builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
            builder.setNegativeButton("Powrót", null);
            builder.setTitle("Wybierz numer telefonu");
            builder.setIcon(R.drawable.global_lightbox_logo);
            builder.setView(view);
            builder.create();
            builder.show();

            final ListView lvWybierzNumerTelefonu = view.findViewById(R.id.lvWybierzNumerTelefonu);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    SzczegolyAdresuAcitivty.this,
                    android.R.layout.simple_list_item_1,
                    arrTelefony);

            lvWybierzNumerTelefonu.setAdapter(arrayAdapter);
            lvWybierzNumerTelefonu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + lvWybierzNumerTelefonu.getItemAtPosition(position)));
                    startActivity(intent);
                }
            });

        }
    }

    public void onSMSClicked(View view) {
        String[] arrTelefony = telefon.split("\\;");
        if (arrTelefony.length == 1) { // jezeli jest jeden nr telefonu - przejdz do ekranu dzwonienia
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("sms:" + telefon));
            startActivity(intent);
        }else // jezeli jest wiecej niz jeden nr tel -- dialog z wyborem numeru telefonu
        {
            view = getLayoutInflater().inflate(R.layout._global_wybierz_numer_telefonu_dialog, null);

            builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
            builder.setNegativeButton("Powrót", null);
            builder.setTitle("Wybierz numer telefonu");
            builder.setIcon(R.drawable.global_lightbox_logo);
            builder.setView(view);
            builder.create();
            builder.show();

            final ListView lvWybierzNumerTelefonu = view.findViewById(R.id.lvWybierzNumerTelefonu);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    SzczegolyAdresuAcitivty.this,
                    android.R.layout.simple_list_item_1,
                    arrTelefony);

            lvWybierzNumerTelefonu.setAdapter(arrayAdapter);
            lvWybierzNumerTelefonu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + lvWybierzNumerTelefonu.getItemAtPosition(position)));
                    startActivity(intent);
                }
            });

        }
    }

    public void onMapaClicked(View view) {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + adres + " " + miejscowosc + " " + kodPocztowy);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void onKomentarzClicked(View view) {
        view = getLayoutInflater().inflate(com.lbapp.R.layout.kurier_trasa_szczegoly_adresu_komentarz_dialog, null);
        final EditText etKomentarzNowe = view.findViewById(com.lbapp.R.id.etKomentarzNowe);

        builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
        builder.setPositiveButton("Wyślij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                pd = new ProgressDialog(SzczegolyAdresuAcitivty.this);
                pd.setTitle("Pobieranie danych");
                pd.setMessage("Trwa pobieranie danych...");
                pd.setIcon(R.drawable.global_lightbox_logo);
                pd.setCancelable(false);
                pd.show();

                komentarz = etKomentarzNowe.getText().toString();
                informacjaZwrotna = "Komentarz został wysłany";

                Kurier_Trasa_SzczegolyAdresu_DodajKomentarz_Request kurierKomentarzRequest = new Kurier_Trasa_SzczegolyAdresu_DodajKomentarz_Request(getApplicationContext(), adresId, trasa, komentarz, responseListener, errorListener);
                queue.add(kurierKomentarzRequest);

            }
        });
        builder.setNegativeButton("Anuluj", null);
        builder.setTitle("Dodaj komentarz");
        builder.setView(view);
        builder.create();
        builder.show();
    }

    public void onDostarczClicked(View view) {

        pd = new ProgressDialog(SzczegolyAdresuAcitivty.this);
        pd.setTitle("Pobieranie danych");
        pd.setMessage("Trwa pobieranie danych...");
        pd.setIcon(R.drawable.global_lightbox_logo);
        pd.setCancelable(false);
        pd.show();

            if (ActivityCompat.checkSelfPermission(SzczegolyAdresuAcitivty.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SzczegolyAdresuAcitivty.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                pd.dismiss();
                builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
                builder.setMessage("Lokalizacja GPS jest wyłączona. Naciśnij przycisk, aby przejść do ustawień i włączyć lokalizację.\n\nW aparatach Xiaomi należy wybrać opcję \"Wysoka dokładność\".")
                        .setCancelable(false)
                        .setPositiveButton("Przejdź do ustawień", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });

                final AlertDialog alert = builder.create();
                alert.show();
                return;
            }
            locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {

                    tempUrlId = port + "/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/Adres/" + adresId;
                    setURL(tempUrlId);
                    raport = "1";

                    if (location != null) {
                        szerokoscGPS = String.valueOf(location.getLatitude());
                        dlugoscGPS = String.valueOf(location.getLongitude());

                    } else {
                        szerokoscGPS = "0";
                        dlugoscGPS = "0";
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    final String godzinaDostarczenia = dateFormat.format(date).toString();

                    //inny response niz domyslny
                    responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int retCode = jsonResponse.getInt("retCode");
                                String retMessage = jsonResponse.getString("retMessage");

                                if (retCode == 0) {

                                    Intent intent = new Intent(SzczegolyAdresuAcitivty.this, TrasaActivity.class);
                                    startActivity(intent);

                                    return;

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
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
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    Kurier_Trasa_SzczegolyAdresu_ZmienStatusDostarczenia_Request kurierUpdateRequest = new Kurier_Trasa_SzczegolyAdresu_ZmienStatusDostarczenia_Request(getApplicationContext(), raport, dlugoscGPS, szerokoscGPS, godzinaDostarczenia, trasa, responseListener, errorListener);
                    queue.add(kurierUpdateRequest);


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

            Location location = locationManager.getLastKnownLocation("gps");
            locationListener.onLocationChanged(location);

    }

    public void onNieDostarczClicked(View view) {

            pd = new ProgressDialog(SzczegolyAdresuAcitivty.this);
            pd.setTitle("Pobieranie danych");
            pd.setMessage("Trwa pobieranie danych...");
            pd.setCancelable(false);
            pd.show();

            tempUrlId = port + "/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/Adres/" + adresId;
            setURL(tempUrlId);

            raport = "0";
            szerokoscGPS = "0";
            dlugoscGPS = "0";

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            final String godzinaDostarczenia = dateFormat.format(date).toString();


            //inny response niz domyslny
            responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    pd.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int retCode = jsonResponse.getInt("retCode");
                        String retMessage = jsonResponse.getString("retMessage");

                        if (retCode == 0) {

                            Intent intent = new Intent(SzczegolyAdresuAcitivty.this, TrasaActivity.class);
                            startActivity(intent);

                            return;

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuAcitivty.this);
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
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            };

            Kurier_Trasa_SzczegolyAdresu_ZmienStatusDostarczenia_Request kurierUpdateRequest = new Kurier_Trasa_SzczegolyAdresu_ZmienStatusDostarczenia_Request(getApplicationContext(), raport, dlugoscGPS, szerokoscGPS, godzinaDostarczenia, trasa, responseListener, errorListener);
            queue.add(kurierUpdateRequest);

    }

    public void onZostawTorbeClicked(View view) {
        Intent intent = new Intent(SzczegolyAdresuAcitivty.this, BarcodeScanner.class);
        intent.putExtra("adresId", adresId);
        intent.putExtra("trasa", trasa);
        intent.putExtra("status", 1);
        SzczegolyAdresuAcitivty.this.startActivity(intent);
    }
}

