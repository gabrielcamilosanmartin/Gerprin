package duosoft.gerprin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

public class RutaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    final String url = "http://52.23.181.133/index.php/rutas";
    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        //boton volver
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RutaActivity.this, PanelActivity.class);
                intent.putExtra("sesion", sesion);
                intent.putExtra("vehiculo", vehiculo);
                startActivity(intent);
            }
        });

        //*********************aca llamado a la funcion para llenar Recycler View**************

        peticionWS(sesion,vehiculo);

        // refresca recycler view
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.SRL_Ruta_Lista);
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        peticionWS(sesion,vehiculo);
                        refreshLayout.setRefreshing(false);

                    }
                }
        );
    }
    //crear lista
    public void crearLista(List<Ruta> list, final User user,final Vehiculo vehiculo) {
        VolleyList.setRutas2(list);
        RecyclerView rv = (RecyclerView) findViewById(R.id.RV_Ruta_Lista);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        Adaptador_ruta adaptador_ruta = new Adaptador_ruta(list);
        rv.setAdapter(adaptador_ruta);

        //redirige a otra activity y pasa el objeto seleccionado
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(RutaActivity.this, RutaDetalleActivity.class);
                intent.putExtra("vehiculo", vehiculo);
                intent.putExtra("ruta", VolleyList.getRutas2().get(position));
                intent.putExtra("sesion", user);
                startActivity(intent);
            }
        }));
    }

    /*comienza peticion*/
    public void peticionWS(final User sesion,final Vehiculo vehiculo) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("rutas", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("exito")) {
                                final List<Ruta> list = getRuta(json);
                                VolleyList.setRutas(list);
                                crearLista(list, sesion, vehiculo);
                            }else{ Toast.makeText(getApplicationContext(),json.getString("error"),Toast.LENGTH_LONG).show();}
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
                params.put("vehiculo_id", String.valueOf(vehiculo.getId()));
                params.put("user_id", String.valueOf(sesion.getId()));
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    //crea lista que es enviada al adaptador
    public List<Ruta> getRuta (JSONObject jsonObject){
        List<Ruta> list =new ArrayList<>();
        try {
            JSONArray array = jsonObject.getJSONArray("rutas");
            for (int i = 0; i<array.length();i++){
                JSONObject objeto = array.getJSONObject(i);
                Ruta ruta = new Ruta();
                ruta.setId(objeto.getInt("id"));
                ruta.setUser_id(objeto.getInt("user_id"));
                ruta.setVehiculo_id(objeto.getInt("vehiculo_id"));
                ruta.setNombre_user(objeto.getString("nombre") + " " + objeto.getString("apellido"));
                ruta.setStart_time(getFecha(objeto.getString("start_time")));

                list.add(ruta);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public String getFecha (String fecha){
        String año=fecha.substring(0, 4);
        String mes=fecha.substring(5,7);
        switch (mes) {
            case "01":
                mes = "ene.";
                break;
            case "02":
                mes = "feb.";
                break;
            case "03":
                mes = "mar.";
                break;
            case "04":
                mes = "abr.";
                break;
            case "05":
                mes = "may.";
                break;
            case "06":
                mes = "jun.";
                break;
            case "07":
                mes = "jul.";
                break;
            case "08":
                mes = "ago.";
                break;
            case "09":
                mes = "sept.";
                break;
            case "10":
                mes = "oct.";
                break;
            case "11":
                mes = "nov.";
                break;
            case "12":
                mes = "dic.";
                break;
        }


        String dia=fecha.substring(8, 9);
        if (dia.matches("0")){dia=fecha.substring(9,10);}
        else{dia=fecha.substring(8,10);}
        String hora = fecha.substring(11,16);
        return hora+" - "+dia +" de "+mes+" de "+año;
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
            Intent intent = new Intent(RutaActivity.this, LogoutActivity.class);
            intent.putExtra("sesion",sesion);
            startActivity(intent);
        }
        if (id == R.id.I_Menu_Mensaje) {
            Intent intent = new Intent(RutaActivity.this, MensajeActivity.class);
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
        Vehiculo vehiculo =(Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        List<Ruta> list = new ArrayList<>();

        for (int i = 0; i < VolleyList.getRutas().size(); i++) {
            if (VolleyList.getRutas().get(i).getStart_time().contains(newText.toLowerCase())) {
                list.add(VolleyList.getRutas().get(i));
            }

        }
        crearLista(list, sesion,vehiculo);
        return false;
    }

}
