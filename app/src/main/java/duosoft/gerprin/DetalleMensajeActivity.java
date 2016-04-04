package duosoft.gerprin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DetalleMensajeActivity extends AppCompatActivity {
    private final String url = "http://52.23.181.133/index.php/leerNotificacion";

    TextView titulo, body, fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mensaje);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        final Mensaje mensaje = (Mensaje) getIntent().getExtras().getSerializable("mensaje");
        titulo = (TextView) findViewById(R.id.TV_DetalleMensaje_Titulo);
        body = (TextView) findViewById(R.id.TV_DetalleMensaje_Mensaje);
        fecha = (TextView) findViewById(R.id.TV_DetalleMensaje_Fecha);

        titulo.setText(mensaje.getTitulo());
        body.setText(mensaje.getMensaje());
        fecha.setText(mensaje.getFecha());
        Log.i("oisajdoias", "no entro");
        if (!mensaje.isLeido()){
            SharedPreferences mensajes=getSharedPreferences("mensajes", Context.MODE_PRIVATE);
            int cantidadMensajes = mensajes.getInt("cantidad_mensaje", 0);
            cantidadMensajes=cantidadMensajes-1;
            mensajes.edit().putInt("cantidad_mensaje",cantidadMensajes).apply();
            Log.i("oisajdoias", String.valueOf(cantidadMensajes));
            leer(mensaje);
        }


    }

    public void leer(final Mensaje mensaje) {
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("notification_id", String.valueOf(mensaje.getId()));
                return params;
            }
        };
        mRequestQueue.add(request);

    }
}
