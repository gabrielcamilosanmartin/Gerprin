package duosoft.gerprin;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

public class PanelActivity extends AppCompatActivity {
    final String urlEstado = "http://52.23.181.133/index.php/parking";
    final String urlActivar = "http://52.23.181.133/index.php/parkingRemoto";
    Button posicion, rutas, conductores;
    Switch parking;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        assert vehiculo != null;
        getSupportActionBar().setTitle(vehiculo.getPatente());
        getSupportActionBar().setSubtitle("Patente");
        //boton volver
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, VehiculoActivity.class);
                intent.putExtra("sesion", sesion);
                startActivity(intent);
            }
        });
        //definir layout
        posicion = (Button) findViewById(R.id.B_Panel_Mapa);
        rutas = (Button) findViewById(R.id.B_Panel_Rutas);
        conductores = (Button) findViewById(R.id.B_Panel_Conductores);
        parking = (Switch) findViewById(R.id.Switch_Panel_ModoParking);
        //***************************************MODO PARKING***************************************
        runnable.run();
        parking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(parking.isChecked()){
                    Toast.makeText(getApplicationContext(), "Acción ingresada. Procesando solicitud de activación", Toast.LENGTH_LONG).show();
                    parking.setChecked(false);


                    activarParking("1");

                }else{
                    Toast.makeText(getApplicationContext(), "Acción ingresada. Procesando solicitud de desactivación", Toast.LENGTH_LONG).show();
                    parking.setChecked(true);
                    activarParking("0");
                }
            }
        });
        //***************************************BOTONES***************************************
        //boton posicion
        posicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PanelActivity.this, PosicionActivity.class);
                intent.putExtra("sesion",sesion);
                intent.putExtra("vehiculo",vehiculo);
                startActivity(intent);
            }
        });
    //boton rutas
    rutas.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(PanelActivity.this, RutaActivity.class);
            intent.putExtra("sesion",sesion);
            intent.putExtra("vehiculo",vehiculo);
            startActivity(intent);
        }
    });
        //boton conductores
        conductores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, UserActivity.class);
                intent.putExtra("sesion",sesion);
                intent.putExtra("vehiculo",vehiculo);
                startActivity(intent);
            }
        });
    }

        //*************************FUNCIONES MODO PARKING*************************************
Runnable runnable= new Runnable() {
    @Override
    public void run() {
        estadoParking();
        handler.postDelayed(runnable, 5000);
    }
};
    protected void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);

    }
    protected void onResume(){
        super.onResume();
        runnable.run();
    }
    // revisar estado de parking
    public  void estadoParking (){
        final Vehiculo vehiculo = (Vehiculo)getIntent().getExtras().getSerializable("vehiculo");

        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, urlEstado,
                new Response.Listener<String>() {
                    @TargetApi(ICE_CREAM_SANDWICH)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                parking.setChecked(json.getInt("parking")==1);
                            }

                        } catch (JSONException e) {
                            }


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

                assert vehiculo != null;
                params.put("vehiculo_id", String.valueOf(vehiculo.getId()));
                return params;
            }
        };
        mRequestQueue.add(request);
    }
    //activar parking()
    public void activarParking(final String estado){
        final Vehiculo vehiculo = (Vehiculo)getIntent().getExtras().getSerializable("vehiculo");
        final User sesion = (User)getIntent().getExtras().getSerializable("sesion");
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, urlActivar,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Se ha producido un error al establecer comunicación con los servidores de Gerprin. Inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("vehiculo_id", String.valueOf(vehiculo.getId()));
                params.put("user_id", String.valueOf(sesion.getId()));
                params.put("activar", estado);

                return params;
            }
        };
        mRequestQueue.add(request);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_panel, menu);
        if (revisarMensajes()>0){
            menu.findItem(R.id.I_Menu_Panel_Mensaje).setIcon(R.drawable.ic_action_nuevomensaje);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        int id= item.getItemId();
        if (id == R.id.I_Menu_Panel_Exit) {
            Intent intent = new Intent(PanelActivity.this, LogoutActivity.class);
            intent.putExtra("sesion", sesion);
            startActivity(intent);
        }
        if (id == R.id.I_Menu_Panel_Informacion){
            Intent intent = new Intent(PanelActivity.this, DetalleVehiculoActivity.class);
            intent.putExtra("sesion", sesion);
            intent.putExtra("vehiculo", vehiculo);
            startActivity(intent);
            return true;
        }
        if (id == R.id.I_Menu_Panel_Mensaje) {
            Intent intent = new Intent(PanelActivity.this, MensajeActivity.class);
            intent.putExtra("sesion", sesion);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private int revisarMensajes(){
        SharedPreferences preferences=getSharedPreferences("mensajes", Context.MODE_PRIVATE);
        int cantidadMensajes = preferences.getInt("cantidad_mensaje",0);
        return cantidadMensajes;

    }


}
