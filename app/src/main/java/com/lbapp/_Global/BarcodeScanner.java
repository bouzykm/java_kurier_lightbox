package com.lbapp._Global;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.zxing.Result;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp.R;
import com.lbapp._API.Kurier_Trasa_SzczegolyAdresu_DodajSkanTorby_Request;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    String id, informacjaZwrotna, szerokoscGPS, dlugoscGPS;
    int status;
    Toast toast;
    ZXingScannerView scannerView;
    Response.Listener<String> responseListener;
    Response.ErrorListener errorListener;
    RequestQueue queue;
    LocationListener locationListener;
    LocationManager locationManager;
    AlertDialog.Builder builder;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        showAToast("Nakieruj aparat telefonu na kod kreskowy. Nastąpi automatyczne skanowanie");
        status = getIntent().getIntExtra("status", 0);




        //odpowiedz na requesty-------------------------------------------------------------------------------------------------------------------------------
        queue = Volley.newRequestQueue(BarcodeScanner.this);

        responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int retCode = jsonResponse.getInt("retCode");
                    String retMessage = jsonResponse.getString("retMessage");

                    if (retCode == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeScanner.this);
                        builder.setTitle("Potwierdzenie");
                        builder.setMessage(informacjaZwrotna);
                        builder.setIcon(R.drawable.global_lightbox_logo);
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onBackPressed();
                                    }
                                })
                                .create()
                                .show();
                        return;

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeScanner.this);
                        builder.setTitle("Uwaga");
                        builder.setMessage(retMessage);
                        builder.setIcon(R.drawable.global_ic_warning);
                        builder.setCancelable(false)
                                .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onBackPressed();
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


                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            builder = new AlertDialog.Builder(BarcodeScanner.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Brak połączenia. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else if (error instanceof ServerError) {
                            builder = new AlertDialog.Builder(BarcodeScanner.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd serwera. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else if (error instanceof NetworkError) {
                            builder = new AlertDialog.Builder(BarcodeScanner.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd sieci. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else if (error instanceof ParseError) {
                            builder = new AlertDialog.Builder(BarcodeScanner.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("Błąd parsowania. Gdy problem nie ustąpi, skontaktuj się z administratorem.");
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
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
                            builder = new AlertDialog.Builder(BarcodeScanner.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(BarcodeScanner.this, LoginActivity.class);
                                            startActivity(intent);

                                        }
                                    });
                            builder.show();
                            return;

                        } else if (networkResponse != null && networkResponse.data != null) {

                            builder = new AlertDialog.Builder(BarcodeScanner.this);
                            builder.setTitle("Błąd");
                            builder.setMessage("" + retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
                                        }
                                    })
                                    .create()
                                    .show();
                            return;
                        } else {
                            builder = new AlertDialog.Builder(BarcodeScanner.this);
                            builder.setTitle("Błąd " + status);
                            builder.setMessage("" + retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false)
                                    .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onBackPressed();
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
                    builder = new AlertDialog.Builder(BarcodeScanner.this);
                    builder.setTitle("Błąd");
                    builder.setMessage("Brak połączenia z Internetem.");
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setCancelable(false)
                            .setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            })
                            .create()
                            .show();
                    return;
                }
            }
        };

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

        //----------------------------------------------------------------------------------------------------------------------------------------------------
    }


    //bezposrednio po wykonaniu skanowania
    @Override
    public void handleResult(Result result) {

        id = String.valueOf(result);

        pd = new ProgressDialog(BarcodeScanner.this);
        pd.setTitle("Pobieranie danych");
        pd.setMessage("Trwa pobieranie danych...");
        pd.setIcon(R.drawable.global_lightbox_logo);
        pd.setCancelable(false);
        pd.show();

        switch(status)
        {
            case 1: onZostawTorbeClicked(); // kurier - skanuj i zostaw torbe
                break;
        }

    }

    private void onZostawTorbeClicked()
    {
        informacjaZwrotna = "Poprawnie zeskanowano torbę";

        String adresId = getIntent().getStringExtra("adresId");
        String trasa = getIntent().getStringExtra("trasa");

        Kurier_Trasa_SzczegolyAdresu_DodajSkanTorby_Request kurierTrasaSzczegolyAdresu_dodajSkanTorbyRequest = new Kurier_Trasa_SzczegolyAdresu_DodajSkanTorby_Request(getApplicationContext(), id, adresId, trasa, responseListener, errorListener);
        queue.add(kurierTrasaSzczegolyAdresu_dodajSkanTorbyRequest);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
