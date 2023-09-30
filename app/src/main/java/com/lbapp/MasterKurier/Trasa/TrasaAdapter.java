package com.lbapp.MasterKurier.Trasa;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import com.lbapp.Logowanie.LoginActivity;
import com.lbapp._Global._GlobalVariable;
import com.lbapp.R;
import com.lbapp._API.MK_Trasa_ZmienStatusDostarczenia_Request;
import com.lbapp._API._PORT;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.LENGTH_SHORT;

public class TrasaAdapter extends BaseAdapter {

    Context c;
    ArrayList<WezelObject> listaWezlow;
    LayoutInflater inflater;
    Toast toast;
    ProgressDialog pd;
    LocationManager locationManager;
    LocationListener locationListener;
    String statusDostarczenia, szerokoscGPS, dlugoscGPS, tempUrlId, port = _PORT.getPort();
    int wezelId, trasaId= _GlobalVariable.getTrasaIdMK();
    AlertDialog.Builder builder;
    
    public TrasaAdapter(Context c, ArrayList<WezelObject> listaWezlow) {
        this.c = c;
        this.listaWezlow = listaWezlow;
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return listaWezlow.size();
    }

    @Override
    public Object getItem(int position) {
        return listaWezlow.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaWezlow.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null)
        {
            convertView=inflater.inflate(com.lbapp.R.layout.mk_trasa_model,parent,false);
        }

        final TextView tvWezel=convertView.findViewById(R.id.tvWezel);
        final TextView tvRaportMK=convertView.findViewById(R.id.tvRaportMK);
        final TextView tvLiczbaToreb=convertView.findViewById(R.id.tvLiczbaToreb);

        final WezelObject s = (WezelObject) this.getItem(position);

        tvWezel.setText(listaWezlow.get(position).getWezel());
        tvRaportMK.setText(listaWezlow.get(position).getStatusDostarczenia());
        tvLiczbaToreb.setText(listaWezlow.get(position).getLiczbaToreb());

        if(tvRaportMK.getText().toString().equals("1")){
            tvRaportMK.setText("✓");
            tvRaportMK.setTextColor(Color.rgb(0,128,0));
            tvRaportMK.setTypeface(null, Typeface.BOLD);
            tvRaportMK.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);

        }
        else if (tvRaportMK.getText().toString().equals("0"))
        {
            tvRaportMK.setText("✗");
            tvRaportMK.setTextColor(Color.RED);
            tvRaportMK.setTypeface(null, Typeface.BOLD);
            tvRaportMK.setTextSize(TypedValue.COMPLEX_UNIT_SP, 45);
        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                wezelId = s.getId();

                //JEŻELI NIE DOSTARCZONO
                if(tvRaportMK.getText().toString().equals("✗"))
                {
                    builder = new AlertDialog.Builder(c);
                    builder.setIcon(R.drawable.global_lightbox_logo);
                    builder.setTitle("Potwierdzenie");
                    builder.setMessage("Czy chcesz potwierdzić obecność w węźle "+tvWezel.getText().toString()+"?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Tak",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onPotwierdzClicked(view);
                                }});
                    builder.setNegativeButton("Nie", null);
                    builder.show();
                    return;

                }

                //JEŻELI DOSTARCZONO
                else if (tvRaportMK.getText().toString().equals("✓"))
                {
                    builder = new AlertDialog.Builder(c);
                    builder.setIcon(R.drawable.global_lightbox_logo);
                    builder.setTitle("Potwierdzenie");
                    builder.setMessage("Czy chcesz cofnąć potwierdzenie obecności w węźle "+tvWezel.getText().toString()+"?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Tak",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onNiePotwierdzClicked(view);
                                }});
                    builder.setNegativeButton("Nie", null);
                    builder.show();
                    return;
                }
            }
        });

        return convertView;
    }

    public void showAToast (String message){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(c, message, LENGTH_SHORT);
        toast.show();
    }

    public void onPotwierdzClicked(View view) {

            pd = new ProgressDialog(c);
            pd.setTitle("Pobieranie danych");
            pd.setMessage("Trwa pobieranie danych...");
            pd.setIcon(R.drawable.global_lightbox_logo);
            pd.setCancelable(false);
            pd.show();


            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


            locationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);
            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                pd.dismiss();
                builder = new AlertDialog.Builder(c);
                builder.setMessage("Lokalizacja GPS jest wyłączona. Naciśnij przycisk, aby przejść do ustawień i włączyć lokalizację.\n\nW aparatach Xiaomi należy wybrać opcję \"Wysoka dokładność\".")
                        .setCancelable(false)
                        .setPositiveButton("Przejdź do ustawień", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                c.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });

                builder.create();
                builder.show();
                return;
            }
            locationListener = new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {

                    tempUrlId = port + "/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/AdresMK/" + wezelId;
                    setURL(tempUrlId);
                    statusDostarczenia = "1";

                    if (location!=null) {
                        szerokoscGPS = String.valueOf(location.getLatitude());
                        dlugoscGPS = String.valueOf(location.getLongitude());

                    }else
                    {
                        szerokoscGPS = "0";
                        dlugoscGPS = "0";
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    final String godzinaDostarczenia = dateFormat.format(date).toString();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            pd.dismiss();
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                int retCode = jsonResponse.getInt("retCode");
                                String retMessage = jsonResponse.getString("retMessage");

                                if (retCode != 0) {
                                    builder = new AlertDialog.Builder(c);
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


                            Intent intent = new Intent(c, TrasaActivity.class);
                            c.startActivity(intent);
                            //finish();


                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.dismiss();
                            boolean connected = false;
                            ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                                        builder = new AlertDialog.Builder(c);
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
                                        builder = new AlertDialog.Builder(c);
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
                                        builder = new AlertDialog.Builder(c);
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
                                        builder = new AlertDialog.Builder(c);
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
                                        builder = new AlertDialog.Builder(c);
                                        builder.setTitle("Sesja wygasła");
                                        builder.setMessage(retMessage);
                                        builder.setIcon(R.drawable.global_ic_warning);
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Powrót",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(c, LoginActivity.class);
                                                        c.startActivity(intent);
                                                    }
                                                });
                                        builder.show();
                                        return;

                                    } else if (networkResponse != null && networkResponse.data != null) {

                                        builder = new AlertDialog.Builder(c);
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
                                        builder = new AlertDialog.Builder(c);
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
                                builder = new AlertDialog.Builder(c);
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

                    MK_Trasa_ZmienStatusDostarczenia_Request mkTrasa_zmienStatusDostarczeniaRequest = new MK_Trasa_ZmienStatusDostarczenia_Request(c.getApplicationContext(), statusDostarczenia, dlugoscGPS, szerokoscGPS, godzinaDostarczenia, trasaId, responseListener, errorListener);
                    RequestQueue queue = Volley.newRequestQueue(c);
                    queue.add(mkTrasa_zmienStatusDostarczeniaRequest);
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
                    c.startActivity(intent);

                }
            };

            Location location = locationManager.getLastKnownLocation("gps");
            locationListener.onLocationChanged(location);

    }

    public void onNiePotwierdzClicked(View view){

            pd = new ProgressDialog(c);
            pd.setTitle("Pobieranie danych");
            pd.setMessage("Trwa pobieranie danych...");
            pd.setCancelable(false);
            pd.show();

            tempUrlId = port + "/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/AdresMK/" + wezelId;
            setURL(tempUrlId);

            statusDostarczenia = "0";
            szerokoscGPS = "0";
            dlugoscGPS = "0";


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            final String godzinaDostarczenia = dateFormat.format(date).toString();

            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    pd.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int retCode = jsonResponse.getInt("retCode");
                        String retMessage = jsonResponse.getString("retMessage");

                        if (retCode != 0) {
                            builder = new AlertDialog.Builder(c);
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


                    Intent intent = new Intent(c, TrasaActivity.class);
                    c.startActivity(intent);
                    //finish();


                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                                builder = new AlertDialog.Builder(c);
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
                                builder = new AlertDialog.Builder(c);
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
                                builder = new AlertDialog.Builder(c);
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
                                builder = new AlertDialog.Builder(c);
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
                                builder = new AlertDialog.Builder(c);
                                builder.setTitle("Sesja wygasła");
                                builder.setMessage(retMessage);
                                builder.setIcon(R.drawable.global_ic_warning);
                                builder.setCancelable(false);
                                builder.setPositiveButton("Powrót",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(c, LoginActivity.class);
                                                c.startActivity(intent);
                                            }
                                        });
                                builder.show();
                                return;

                            } else if (networkResponse != null && networkResponse.data != null) {

                                builder = new AlertDialog.Builder(c);
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
                                builder = new AlertDialog.Builder(c);
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
                        builder = new AlertDialog.Builder(c);
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

            MK_Trasa_ZmienStatusDostarczenia_Request mkTrasa_zmienStatusDostarczeniaRequest = new MK_Trasa_ZmienStatusDostarczenia_Request(c.getApplicationContext(), statusDostarczenia, dlugoscGPS, szerokoscGPS, godzinaDostarczenia, trasaId, responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(c);
            queue.add(mkTrasa_zmienStatusDostarczeniaRequest);

    }

    public void setURL(String url) {
        tempUrlId = url;

        MK_Trasa_ZmienStatusDostarczenia_Request.urlSet(url);
    }
}
