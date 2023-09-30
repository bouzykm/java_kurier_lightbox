package com.lbapp._Global;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.lbapp.Menu.Menu;
import com.lbapp.R;
import com.lbapp._API.Global_SprawdzWiadomosci_LiczbaNieprzeczytanych_Request;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class GetUnreadMessagesNumber extends AppCompatActivity {

    static ProgressDialog pd;

    //domyslny listener
    public static void getUnreadMessagesNumber(final Context c, String login) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int retCode = jsonResponse.getInt("retCode");
                    int retUnreadMessagesNumber = jsonResponse.getInt("retLiczbaWiadomosciNieprzeczytanych");
                    SharedPreferences sharedPreferencesUnrMsg = c.getSharedPreferences("retUnreadMessagesNumberPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorUnrMsg = sharedPreferencesUnrMsg.edit();
                    editorUnrMsg.putInt("retUnreadMessagesNumber", retUnreadMessagesNumber);
                    editorUnrMsg.apply();

                    if (retCode == 0) {
                        Log.d("UNR_MSG_COUNT", String.valueOf(retUnreadMessagesNumber));
                    } else {
                        editorUnrMsg = sharedPreferencesUnrMsg.edit();
                        editorUnrMsg.putInt("retUnreadMessagesNumber", 0);
                        editorUnrMsg.apply();

                        Toast.makeText(c, "Liczba nieprzeczytanych wiadomości nie została pobrana", Toast.LENGTH_SHORT);
                    }
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                SharedPreferences sharedPreferencesUnrMsg = c.getSharedPreferences("retUnreadMessagesNumberPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorUnrMsg = sharedPreferencesUnrMsg.edit();
                editorUnrMsg.putInt("retUnreadMessagesNumber", 0);
                editorUnrMsg.apply();

                Toast.makeText(c, "Liczba nieprzeczytanych wiadomości nie została pobrana", Toast.LENGTH_SHORT).show();
//                NetworkResponse networkResponse = error.networkResponse;
//                String jsonError = new String(networkResponse.data);
//                Log.e("UNR_MSG_ERR", jsonError);
            }
        };

        Global_SprawdzWiadomosci_LiczbaNieprzeczytanych_Request global_sprawdzWiadomosci_liczbaNieprzeczytanych = new Global_SprawdzWiadomosci_LiczbaNieprzeczytanych_Request(c, login, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(c.getApplicationContext());
        queue.add(global_sprawdzWiadomosci_liczbaNieprzeczytanych);

    }

}
