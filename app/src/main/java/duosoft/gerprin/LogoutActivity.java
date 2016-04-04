package duosoft.gerprin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LogoutActivity extends AppCompatActivity {
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_EMAIL = "user";
    private static final String PROPERTY_CONTRASEÑA = "contraseña";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        SharedPreferences prefs = getSharedPreferences("login",Context.MODE_PRIVATE);
        SharedPreferences mensajes=getSharedPreferences("mensajes", Context.MODE_PRIVATE);
        mensajes.edit().remove("cantidad_mensaje").apply();
        prefs.edit().remove(PROPERTY_REG_ID).apply();
        prefs.edit().remove(PROPERTY_EMAIL).apply();
        prefs.edit().remove(PROPERTY_CONTRASEÑA).apply();
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");

        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, "http://52.23.181.133/index.php/modificarConductor",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("gcm_regid", "NULL");
                params.put("user_id", String.valueOf(sesion.getId()));
                return params;
            }
        };
        mRequestQueue.add(request);
        Intent intent = new Intent(LogoutActivity.this ,LoginActivity.class);
        startActivity(intent);
    }
}
