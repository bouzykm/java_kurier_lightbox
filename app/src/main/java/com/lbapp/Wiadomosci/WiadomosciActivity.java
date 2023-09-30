package com.lbapp.Wiadomosci;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.navigation.NavigationView;
import com.lbapp.Administrator.KalendarzMasterKurier.KalendarzMasterKurierActivity;
import com.lbapp.Kurier.Trasa.TrasaActivity;
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp.R;
import com.lbapp._API.Administrator_KalendarzMasterKurier__Downloader;
import com.lbapp._API.Wiadomosci__Downloader;
import com.lbapp._Global.GetUnreadMessagesNumber;
import com.lbapp._Global._GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

public class WiadomosciActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    String dateFormat, login;
    ListView lvWiadomosci;
    AlertDialog.Builder builder;
    Wiadomosci__Downloader d;
    Response.ErrorListener errorListener;
    ProgressDialog pd;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wiadomosci_activity);
        Toolbar toolbar = findViewById(com.lbapp.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) { // wyswietl strzalke powrotu do menu
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        }

        login = _GlobalVariable.getLogin();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        dateFormat = _GlobalVariable.getDateFormat();
        setTitle("Wiadomości");


        lvWiadomosci = findViewById(R.id.lvWiadomosci);

        try {
            d = new Wiadomosci__Downloader(this, lvWiadomosci);
            d.execute();

        } catch (NullPointerException e) {
            builder = new AlertDialog.Builder(WiadomosciActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(WiadomosciActivity.this, WiadomosciActivity.class);
                            startActivity(intent);
                        }
                    });
            builder.show();
            return;
        } catch (RuntimeException e) {
            builder = new AlertDialog.Builder(WiadomosciActivity.this);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana.");
            builder.setCancelable(false);
            builder.setPositiveButton("Spróbuj ponownie",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(WiadomosciActivity.this, WiadomosciActivity.class);
                            startActivity(intent);
                        }
                    });
            builder.show();
            return;
        }

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
                            builder = new AlertDialog.Builder(WiadomosciActivity.this);
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
                            builder = new AlertDialog.Builder(WiadomosciActivity.this);
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
                            builder = new AlertDialog.Builder(WiadomosciActivity.this);
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
                            builder = new AlertDialog.Builder(WiadomosciActivity.this);
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
                            builder = new AlertDialog.Builder(WiadomosciActivity.this);
                            builder.setTitle("Sesja wygasła");
                            builder.setMessage(retMessage);
                            builder.setIcon(R.drawable.global_ic_warning);
                            builder.setCancelable(false);
                            builder.setPositiveButton("Powrót",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(WiadomosciActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                            builder.show();
                            return;

                        } else if (networkResponse != null && networkResponse.data != null) {

                            builder = new AlertDialog.Builder(WiadomosciActivity.this);
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
                            builder = new AlertDialog.Builder(WiadomosciActivity.this);
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
                    builder = new AlertDialog.Builder(WiadomosciActivity.this);
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


    public void showAToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            GetUnreadMessagesNumber.getUnreadMessagesNumber(WiadomosciActivity.this, login);
            Intent intent = new Intent(WiadomosciActivity.this, com.lbapp.Menu.Menu.class);
            startActivity(intent);
            finish(); // wyjdz z sekcji wiadomosci po nacisnieciu strzalki powrotu
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
            GetUnreadMessagesNumber.getUnreadMessagesNumber(WiadomosciActivity.this, login);
            Intent intent = new Intent(WiadomosciActivity.this, com.lbapp.Menu.Menu.class);
            startActivity(intent);
            finish();
        }
}
