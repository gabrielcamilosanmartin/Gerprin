package duosoft.gerprin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;

public class VehiculoActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    final String url = "http://52.23.181.133/index.php/vehiculos";
    FloatingActionButton añadir;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        añadir = (FloatingActionButton) findViewById(R.id.FAB_Vehiculo_Añadir);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.SRL_Vehiculo_Lista);

        //muestra floatbutton si es enrolador
        assert sesion != null;
        if (!sesion.isEnrolador()) {
            añadir.setVisibility(GONE);
        }
        //*********************aca llamado a la funcion para llenar Recycler View**************

        peticionWS(sesion);


        // refresca recycler view
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        peticionWS(sesion);
                        refreshLayout.setRefreshing(false);
                    }
                }
        );


        //presionar Floating button
        añadir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehiculoActivity.this, NuevoVehiculoActivity.class);
                intent.putExtra("sesion", sesion);
                startActivity(intent);
            }
        });

    }

    //crear lista
    public void crearLista(List<Vehiculo> list, final User user) {
        VolleyList.setVehiculos2(list);
        RecyclerView rv = (RecyclerView) findViewById(R.id.RV_Vehiculo_Lista);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        final Adaptador_vehiculo adaptador_vehiculo = new Adaptador_vehiculo(list);
        rv.setAdapter(adaptador_vehiculo);

        //redirige a otra activity y pasa el objeto seleccionado
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                for (int i = 0; i < VolleyList.getVehiculos2().size(); i++) {
                    Log.i("asd", VolleyList.getVehiculos2().get(i).getPatente() + " " + String.valueOf(i));
                }
                Intent intent = new Intent(VehiculoActivity.this, PanelActivity.class);
                intent.putExtra("vehiculo", VolleyList.getVehiculos2().get(position));
                intent.putExtra("sesion", user);


                startActivity(intent);
            }
        }));

    }

    /*comienza peticion*/
    public void peticionWS(final User sesion) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //*****agregar respuesta de error del servidor******
                            JSONObject json = new JSONObject(response);
                            final List<Vehiculo> list = getVehiculos(json, sesion);
                            VolleyList.setVehiculos(list);
                            crearLista(list, sesion);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Se ha producido un error al establecer comunicación con los servidores de Gerprin. Inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Se ha producido un error al establecer comunicación con los servidores de Gerprin. Inténtelo de nuevo más tarde", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", String.valueOf(sesion.getId()));
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    //crea lista que es enviada al adaptador
    public List<Vehiculo> getVehiculos(JSONObject jsonObject, User user) {
        List<Vehiculo> list = new ArrayList<>();
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        try {
            JSONArray array = jsonObject.getJSONArray("vehiculos");

            for (int i = 0; i < array.length(); i++) {

                JSONObject objeto = array.getJSONObject(i);
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(objeto.getInt("id"));
                vehiculo.setPatente(objeto.getString("patente"));
                vehiculo.setMarca(objeto.getString("marca"));
                vehiculo.setModelo(objeto.getString("modelo"));
                vehiculo.setAño(objeto.getInt("ano"));
                vehiculo.setEnrrolador(user.isEnrolador());
                if (sesion.isEnrolador()){
                vehiculo.setImei(objeto.getString("imei"));}
                try {
                    vehiculo.setDueño(objeto.getInt("dueno") == 1);
                } catch (JSONException e) {
                    vehiculo.setDueño(false);
                }
                list.add(vehiculo);


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return list;
    }


    //*****************************Buscador*****************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listas, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.SV_Menu_Buscador));
        searchView.setOnQueryTextListener(this);
        if (revisarMensajes()>0){
            menu.findItem(R.id.I_Menu_Mensaje).setIcon(R.drawable.ic_action_nuevomensaje);
        }
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        if (id == R.id.I_Menu_Exit) {
            Intent intent = new Intent(VehiculoActivity.this, LogoutActivity.class);
            intent.putExtra("sesion", sesion);
            startActivity(intent);
        }
        if (id == R.id.I_Menu_Mensaje) {
            Intent intent = new Intent(VehiculoActivity.this, MensajeActivity.class);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        List<Vehiculo> list = new ArrayList<>();

        for (int i = 0; i < VolleyList.getVehiculos().size(); i++) {
            if (VolleyList.getVehiculos().get(i).getPatente().contains(newText.toUpperCase())) {
                list.add(VolleyList.getVehiculos().get(i));
            }
        }
        crearLista(list, sesion);
        return false;
    }
}
