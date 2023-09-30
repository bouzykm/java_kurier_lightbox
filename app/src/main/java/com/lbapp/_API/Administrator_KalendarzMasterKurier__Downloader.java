package com.lbapp._API;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.ListView;

import com.lbapp._Global._GlobalVariable;
import com.lbapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by maciej on 11.07.2017.
 */

public class Administrator_KalendarzMasterKurier__Downloader extends AsyncTask<String,Void,String>{

    Context c;
    String sessionToken;
    ListView lvListaKalendarzMasterKurier;
    ProgressDialog pd;
    String port = _PORT.getPort();
    String dateFormat= _GlobalVariable.getDateFormat();
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    Date date = new Date();
    String dataKursu = sdf.format(date);


    public Administrator_KalendarzMasterKurier__Downloader(Context c, ListView lvListaKalendarzMasterKurier) {
        this.c = c;
        this.lvListaKalendarzMasterKurier = lvListaKalendarzMasterKurier;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(c);
        pd.setTitle("Pobieranie");
        pd.setMessage("Trwa pobieranie danych...");
        pd.setIcon(R.drawable.global_lightbox_logo);
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {

        String data=downloadData();
        return data;
    }

    @Override
    protected void onPostExecute(String s) {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;


        if (connected ==true) {
        try {
            super.onPostExecute(s);

            pd.dismiss();
            JSONObject jsonObject = new JSONObject(s);
            int retCode = jsonObject.getInt("retCode");
            String retMessage = jsonObject.getString("retMessage");

            if (retCode>0){
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Uwaga");
                builder.setMessage(""+retMessage);
                builder.setIcon(R.drawable.global_ic_warning);
                builder.setCancelable(false);
                builder.setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }});
                builder.show();
            }

            if (s == null) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle("Błąd");
                    builder.setCancelable(false);
                    builder.setMessage(""+retMessage)
                            .setIcon(R.drawable.global_ic_warning)
                            .setNegativeButton("Powrót", null)
                            .create()
                            .show();
                    builder.show();
                } catch (NullPointerException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle("Problem z zasięgiem");
                    builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                    builder.setCancelable(false);
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setPositiveButton("Odśwież",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c, lvListaKalendarzMasterKurier);
                                    d.execute();
                                }});
                    builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }catch (RuntimeException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle("Problem z zasięgiem");
                    builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                    builder.setCancelable(false);
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setPositiveButton("Odśwież",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c, lvListaKalendarzMasterKurier);
                                    d.execute();
                                }});
                    builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }

            } else {
                Administrator_KalendarzMasterKurier__Parser p = new Administrator_KalendarzMasterKurier__Parser(c, s, lvListaKalendarzMasterKurier);
                p.execute();
            }

        }catch (NullPointerException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Odśwież",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c, lvListaKalendarzMasterKurier);
                            d.execute();
                        }});
            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
            } catch (JSONException e) {
            e.printStackTrace();
        }catch (RuntimeException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Problem z zasięgiem");
            builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setCancelable(false);
            builder.setPositiveButton("Odśwież",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c, lvListaKalendarzMasterKurier);
                            d.execute();
                        }});
            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setTitle("Błąd");
            builder.setMessage("Brak połączenia z Internetem.");
            builder.setCancelable(false);
            builder.setIcon(R.drawable.global_ic_warning);
            builder.setPositiveButton("Powrót",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }});
            builder.show();
            pd.dismiss();
    }}

    private String downloadData()
    {


        HttpURLConnection con= null;
        try {
            URL url=new URL(port+"/lkls0987sdf98sdfNLNJXJLOIlkhsd7UX098zsd98f7sd/AdministratorKalendarzMasterKurier");
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setDoInput(true);

            SharedPreferences sharedPreferences = c.getSharedPreferences("sessionTokenPreferences", Context.MODE_PRIVATE);
            sessionToken = sharedPreferences.getString("sessionToken", "");

//            SharedPreferences sharedPreferencesSymbol = c.getSharedPreferences("retSymbolKoordynatoraPreferences", Context.MODE_PRIVATE);
//            final String retSymbolKoordynatora = sharedPreferencesSymbol.getString("retSymbolKoordynatora", "");


            con.setRequestProperty("sessionToken", sessionToken);
            con.setRequestProperty("dataKursu", dataKursu);
        } catch (IOException e) {
            e.printStackTrace();
        }



        if(con==null)
        {
            return null;
        }

        InputStream is=null;
        try {
            is=new BufferedInputStream(con.getInputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            String line = null;
            StringBuffer response = new StringBuffer();

            if (br!=null){
                while ((line=br.readLine()) != null)
                {
                    response.append(line+"\n");
                }
                br.close();
            }else{
                return null;
            }

            return response.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Problem z zasięgiem");
                builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                builder.setIcon(R.drawable.global_ic_warning);
                builder.setCancelable(false);
                builder.setPositiveButton("Odśwież",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c, lvListaKalendarzMasterKurier);
                                d.execute();
                            }});
                builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
        }catch (RuntimeException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Problem z zasięgiem");
        builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
        builder.setIcon(R.drawable.global_ic_warning);
        builder.setCancelable(false);
        builder.setPositiveButton("Odśwież",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c,  lvListaKalendarzMasterKurier);
                        d.execute();
                    }});
        builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (NullPointerException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle("Problem z zasięgiem");
                    builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Odśwież",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c, lvListaKalendarzMasterKurier);
                                    d.execute();
                                }});
                    builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }catch (RuntimeException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle("Problem z zasięgiem");
                    builder.setMessage("Lista nie została pobrana. Spróbuj jeszcze raz.");
                    builder.setIcon(R.drawable.global_ic_warning);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Odśwież",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Administrator_KalendarzMasterKurier__Downloader d = new Administrator_KalendarzMasterKurier__Downloader(c, lvListaKalendarzMasterKurier);
                                    d.execute();
                                }});
                    builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            }
        }
        return null;
    }

}
