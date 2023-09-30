package com.lbapp.Koordynator.Prognoza;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lbapp.Kurier.Trasa.SzczegolyAdresuAcitivty;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp._API.Koordynator_Prognoza_SzczegolyAdresuPrognozy_DodajUwage_Request;
import com.lbapp._API.Koordynator_Prognoza_SzczegolyAdresuPrognozy_ZmienTrase_Request;
import com.lbapp.R;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

import static android.widget.Toast.LENGTH_SHORT;


public class SzczegolyAdresuPrognozyActivity extends AppCompatActivity {

    String trasa, adresId, adres, firma, kodPocztowy, godzinaOd, godzinaDo, uwagi, kodDoDomofonu, raport, numeryProduktow, telefon, dzielnica, miejscowosc, uwagaPrognozy, symbolTrasy, zmianaNaStale, informacjaZwrotna = "";
    TextView tvDzielnica, tvAdres, tvGodzina, tvFirma, tvUwagi, tvKodDoDomofonu, FirmaLabel, KodDoDomofonuLabel;
    FloatingActionButton fabSzacunek, fabMapa, fabTelefon, fabSMS, fabKomentarz;
    ProgressDialog pd;
    FloatingTextButton ftbZmienTrase, ftbDodajUwage;
    Toast toast;
    Response.Listener<String> responseListener;
    Response.ErrorListener errorListener;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.koordynator_prognoza_szczegoly_adresu_activity);
        Toolbar toolbar = findViewById(com.lbapp.R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(" ");
        zmianaNaStale="false";

        Intent i = this.getIntent();
        adresId = i.getExtras().getString("ADRESID_KEY");
        symbolTrasy = i.getExtras().getString("SYMBOLTRASY_KEY");
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
        numeryProduktow = i.getExtras().getString(("NUMERYPRODUKTOW_KEY"));


        tvAdres = findViewById(R.id.tvAdres);
        tvGodzina = findViewById(R.id.tvGodzina);
        tvDzielnica = findViewById(R.id.tvDzielnica);
        tvFirma = findViewById(R.id.tvFirma);
        FirmaLabel = findViewById(R.id.FirmaLabel);
        KodDoDomofonuLabel = findViewById(R.id.KodDoDomofonuLabel);
        tvUwagi = findViewById(R.id.tvUwagi);
        tvKodDoDomofonu = findViewById(R.id.tvKodDoDomofonu);

        fabSzacunek = findViewById(R.id.fabSzacunek);
        fabMapa = findViewById(R.id.fabMapa);
        fabTelefon = findViewById(R.id.fabTelefon);
        fabSMS = findViewById(R.id.fabSMS);
        fabKomentarz = findViewById(R.id.fabKomentarz);
        ftbZmienTrase = findViewById(R.id.ftbZmienTrase);
            ftbZmienTrase.setTitle("    Zmień trasę ("+symbolTrasy+")    ");
        ftbDodajUwage = findViewById(R.id.ftbDodajUwage);

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


        if (firma.equals("")) {
            tvFirma.setVisibility(View.GONE);
            FirmaLabel.setVisibility(View.GONE);
        }
        if (kodDoDomofonu.equals("")) {
            tvKodDoDomofonu.setVisibility(View.GONE);
            KodDoDomofonuLabel.setVisibility(View.GONE);
        }

        ftbZmienTrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onZmienTraseClicked(v);
            }
        });

        ftbDodajUwage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDodajUwageClicked(v);
            }

        });


        responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int retCode = jsonResponse.getInt("retCode");
                    String retMessage = jsonResponse.getString("retMessage");

                    if (retCode == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
                        builder.setTitle("Potwierdzenie");
                        builder.setMessage(informacjaZwrotna);
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setCancelable(false)
                                .setNegativeButton("Powrót", null)
                                .create()
                                .show();
                        return;

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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
                            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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
                            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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
                            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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
                            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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
                            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(SzczegolyAdresuPrognozyActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builder.show();
                            return;

                        } else if (networkResponse != null && networkResponse.data != null) {

                            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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
                            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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
                    builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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

    public void setURL(String url) {
//        tempUrlId = url;
//        Kurier_UpdateRequest.urlSet(url);
//        Kurier_SzacunekRequest.urlSet(url);
    }

    public void onMapaClicked(View view) {


        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + adres + " " + miejscowosc + " " + kodPocztowy); //fraza, ktora ma zostac wpisana przy otwarciu nawigacji
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void onTelefonClicked(View view) {
        String[] arrTelefony = telefon.split("\\;");
        if (arrTelefony.length == 1) { // jezeli jest jeden nr telefonu - przejdz do ekranu dzwonienia
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + telefon)); // numer telefonu klienta wykorzystany do połączenia
            startActivity(intent);
        }else // jezeli jest wiecej niz jeden nr tel -- dialog z wyborem numeru telefonu
        {
            view = getLayoutInflater().inflate(R.layout._global_wybierz_numer_telefonu_dialog, null);

            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
            builder.setNegativeButton("Powrót", null);
            builder.setTitle("Wybierz numer telefonu");
            builder.setIcon(R.drawable.global_lightbox_logo);
            builder.setView(view);
            builder.create();
            builder.show();

            final ListView lvWybierzNumerTelefonu = view.findViewById(R.id.lvWybierzNumerTelefonu);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    SzczegolyAdresuPrognozyActivity.this,
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
            intent.setData(Uri.parse("sms:" + telefon));  // numer telefonu klienta wykorzystany do wysłania sms
            startActivity(intent);
        }else // jezeli jest wiecej niz jeden nr tel -- dialog z wyborem numeru telefonu
        {
            view = getLayoutInflater().inflate(R.layout._global_wybierz_numer_telefonu_dialog, null);

            builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
            builder.setNegativeButton("Powrót", null);
            builder.setTitle("Wybierz numer telefonu");
            builder.setIcon(R.drawable.global_lightbox_logo);
            builder.setView(view);
            builder.create();
            builder.show();

            final ListView lvWybierzNumerTelefonu = view.findViewById(R.id.lvWybierzNumerTelefonu);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    SzczegolyAdresuPrognozyActivity.this,
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

    public void onDodajUwageClicked(View view) {

        view = getLayoutInflater().inflate(R.layout.koordynator_prognoza_szczegoly_adresu_uwaga_prognozy_dialog, null);
        final EditText etUwagaPrognozy = view.findViewById(R.id.etUwaga);
        informacjaZwrotna = "Uwaga została wysłana";

        AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
        builder.setPositiveButton("Wyślij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                trasa = symbolTrasy;
                uwagaPrognozy = etUwagaPrognozy.getText().toString();

                if (uwagaPrognozy.length()==0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
                    builder.setTitle("Uwaga");
                    builder.setMessage("Nie możesz wysłać pustej wiadomości.");
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setCancelable(false)
                            .setNegativeButton("Powrót", null)
                            .create()
                            .show();
                    return;
                }

                pd = new ProgressDialog(SzczegolyAdresuPrognozyActivity.this);
                pd.setTitle("Pobieranie danych");
                pd.setMessage("Trwa pobieranie danych...");
                pd.setIcon(R.drawable.global_lightbox_logo);
                pd.setCancelable(false);
                pd.show();

                Koordynator_Prognoza_SzczegolyAdresuPrognozy_DodajUwage_Request koordynatorPrognoza_szczegolyAdresuPrognozyDodajUwageRequest = new Koordynator_Prognoza_SzczegolyAdresuPrognozy_DodajUwage_Request(getApplicationContext(), adresId, trasa, uwagaPrognozy, responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(SzczegolyAdresuPrognozyActivity.this.getApplicationContext());
                queue.add(koordynatorPrognoza_szczegolyAdresuPrognozyDodajUwageRequest);

            }
        });
        builder.setNegativeButton("Anuluj", null);
        builder.setTitle("Dodaj uwagę");
        builder.setView(view);
        builder.create();
        builder.show();
    }


    public ArrayList<String> getArrayList(String key){

        //PRZECHOWYWANIE TABLICY LISTY SYMBOLI TRAS W JSONIE -> ZAMIANA Z JSONA NA STRING
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SzczegolyAdresuPrognozyActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void onZmienTraseClicked(View view)
    {
        view = getLayoutInflater().inflate(R.layout.koordynator_prognoza_szczegoly_adresu_zmien_trase_dialog, null);

        builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
        builder.setNegativeButton("Anuluj", null);
        builder.setTitle("Wybierz rodzaj zmiany i naciśnij na symbol trasy");
        builder.setIcon(R.drawable.global_lightbox_logo);
        builder.setView(view);
        builder.create();
        builder.show();

        final ListView lvZmienTrase = view.findViewById(R.id.lvZmienTrase);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        ArrayList<String> listaSymboliTras = getArrayList("symbolTrasy");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                SzczegolyAdresuPrognozyActivity.this,
                android.R.layout.simple_list_item_1,
                listaSymboliTras );

        lvZmienTrase.setAdapter(arrayAdapter);
        lvZmienTrase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String zmienTraseNa = lvZmienTrase.getItemAtPosition(position).toString();


                // no radio buttons are checked
                if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    showAToast("Najpierw wybierz rodzaj zmiany");
                    return;
                }

                pd = new ProgressDialog(SzczegolyAdresuPrognozyActivity.this);
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
                                Intent intent = new Intent(SzczegolyAdresuPrognozyActivity.this, ListaPrognozyActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SzczegolyAdresuPrognozyActivity.this);
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

                Koordynator_Prognoza_SzczegolyAdresuPrognozy_ZmienTrase_Request koordynatorPrognoza_SzczegolyAdresuPrognozy_zmienTraseRequest = new Koordynator_Prognoza_SzczegolyAdresuPrognozy_ZmienTrase_Request(getApplicationContext(), zmianaNaStale, adresId, symbolTrasy, zmienTraseNa, responseListener, errorListener);
                RequestQueue queue = Volley.newRequestQueue(SzczegolyAdresuPrognozyActivity.this.getApplicationContext());
                queue.add(koordynatorPrognoza_SzczegolyAdresuPrognozy_zmienTraseRequest);
            }
        });
    }

    public void onZmianaNaJedenDzienClicked(View view)
    {
        zmianaNaStale="false";
    }

    public void onZmianaNaStaleClicked(View view)
    {
        zmianaNaStale="true";
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, LENGTH_SHORT);
        toast.show();
    }
}

