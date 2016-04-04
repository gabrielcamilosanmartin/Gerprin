package duosoft.gerprin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class MensajeActivity extends AppCompatActivity {
    final String url = "http://52.23.181.133/index.php/notificaciones";
    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");

        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.SRL_Mensaje_Lista);
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
    }
    @Override
    protected void onResume (){
        super.onResume();
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        peticionWS(sesion);
    }
    public void crearLista(final List<Mensaje> list, final User user) {
        RecyclerView rv = (RecyclerView) findViewById(R.id.RV_Mensaje_Lista);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        Adaptador_mensaje adaptador_mensaje = new Adaptador_mensaje(list);
        rv.setAdapter(adaptador_mensaje);

        //redirige a otra activity y pasa el objeto seleccionado
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(MensajeActivity.this, DetalleMensajeActivity.class);
                intent.putExtra("mensaje", list.get(position));
                startActivity(intent);
            }
        }));

    }
    public void peticionWS(final User sesion) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            final List<Mensaje> list = getMensaje(json);
                            VolleyList.setMensaje(list);
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
    public List<Mensaje> getMensaje (JSONObject jsonObject){
        List<Mensaje> list =new ArrayList<>();
        try {
            JSONArray array = jsonObject.getJSONArray("notificaciones");
            for (int i = 0; i<array.length();i++){
                JSONObject objeto = array.getJSONObject(i);
                Mensaje mensaje = new Mensaje();
                mensaje.setId(objeto.getInt("id"));
                mensaje.setMensaje(objeto.getString("mensaje"));
                mensaje.setFecha(getFecha(objeto.getString("created_at")));
                mensaje.setTitulo(objeto.getString("titulo"));
                mensaje.setLeido(objeto.getInt("leido") == 1);

                list.add(mensaje);

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


        String dia=fecha.substring(8,9);
        if (dia.matches("0")){dia=fecha.substring(9,10);}
        else{dia=fecha.substring(8,10);}
        String hora = fecha.substring(11,16);
        return hora+" - "+dia +" de "+mes+" de "+año;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mensajes, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        if (id == R.id.I_Menu_Exit) {
            Intent intent = new Intent(MensajeActivity.this, LogoutActivity.class);
            intent.putExtra("sesion", sesion);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
