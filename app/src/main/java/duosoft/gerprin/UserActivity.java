package duosoft.gerprin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class UserActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    final String urlLista = "http://52.23.181.133/index.php/conductores";
    String urlAgregar = "http://52.23.181.133/index.php/agregarConductor";
    String urlRevisar = "http://52.23.181.133/index.php/revisarEmail";
    FloatingActionButton añadir;
    SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        añadir = (FloatingActionButton) findViewById(R.id.FAB_User_Añadir);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.SRL_User_Lista);
        //boton volver
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, PanelActivity.class);
                intent.putExtra("sesion", sesion);
                intent.putExtra("vehiculo", vehiculo);
                startActivity(intent);
            }
        });
        //muestra floatbutton si es enrolador
        assert sesion != null;
        Log.i("dueño", String.valueOf(sesion.isDueño()));
        if (!vehiculo.isDueño()) {
            if (!vehiculo.isEnrrolador()) {
                añadir.setVisibility(View.GONE);
            }
        }

        //*********************aca llamado a la funcion para llenar Recycler View**************

        peticionWS(vehiculo);


        // refresca recycler view
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        peticionWS(vehiculo);
                        refreshLayout.setRefreshing(false);

                    }
                }
        );


        //presionar Floating button
        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensaje();
            }
        });
    }
    //crear lista
    public void crearLista(List<User> list) {
        VolleyList.setUsers2(list);
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        RecyclerView rv = (RecyclerView) findViewById(R.id.RV_User_Lista);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);

        Adaptador_user adaptador_user = new Adaptador_user (list);
        rv.setAdapter(adaptador_user);

        //redirige a otra activity y pasa el objeto seleccionado
        rv.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intent = new Intent(UserActivity.this, DetalleUserActivity.class);
                intent.putExtra("vehiculo", vehiculo);
                intent.putExtra("conductor", VolleyList.getUsers2().get(position));
                intent.putExtra("sesion", sesion);
                startActivity(intent);
            }
        }));
    }

    /*comienza peticion*/
    public void peticionWS(final Vehiculo vehiculo) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, urlLista,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //*****agregar respuesta de error del servidor
                            JSONObject json = new JSONObject(response);
                            final List<User> list = getUser(json);
                            VolleyList.setUsers(list);
                            crearLista(list);

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
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    //crea lista que es enviada al adaptador
    public List<User> getUser (JSONObject jsonObject){
        List<User> list =new ArrayList<>();
        try {
            final Vehiculo vehiculo = (Vehiculo)getIntent().getExtras().getSerializable("vehiculo");
            final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
            JSONArray array = jsonObject.getJSONArray("conductores");
            for (int i = 0; i<array.length();i++){
                JSONObject objeto = array.getJSONObject(i);
                if (sesion.getId()!=objeto.getInt("id")) {
                    User user = new User();
                    user.setNombre(objeto.getString("nombre"));
                    user.setApellido(objeto.getString("apellido"));
                    user.setEmail(objeto.getString("email"));
                    user.setId(objeto.getInt("id"));
                    user.setDueño(objeto.getInt("dueno") == 1);
                    user.setHuella(objeto.getInt("tiene_huella")==1);
                    list.add(user);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //**********************************FUNCIONES AGREGAR USUARIO*****************************
    public String mensaje(){
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle(R.string.Agregar_conductor);
        alert.setMessage("Introdusca el email del conductor");
        final EditText emailRevisar = new EditText(this);
        emailRevisar.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        alert.setView(emailRevisar);
        alert.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String emaRev = emailRevisar.getText().toString();
                revisar(vehiculo, sesion, emaRev);
            }
        });
        alert.setNeutralButton("Cancelar", null);
        alert.show();
        return emailRevisar.getText().toString();
    }
    public void revisar(final Vehiculo vehiculo,final User sesion, final String email) {
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, urlRevisar,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject json = new JSONObject(response);
                            if (json.getBoolean("existe")) {

                                //crea ventana de dialogo indicando si el usuario existe y si desea agregarlo si exite genera y el usuario es enrrolador genera una segunda ventana preguntando
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserActivity.this);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setTitle("El conductor existe");
                                alertDialogBuilder.setMessage("¿Desea asociarlo a este vehiculo?")

                                        //agregar si existe
                                        .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //si es enrrolador, pregunta si lo quiere agregar como conductor o propietario
                                                if (sesion.isEnrolador()) {

                                                    final String[] items = {"Conductor", "Dueño"};
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(UserActivity.this);
                                                    alert.setTitle("Seleccione la relación del Usuario con el vehículo")
                                                            .setItems(items, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int item) {
                                                                    try {
                                                                        agregarConductor(item, vehiculo, json.getInt("user_id"));
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            });
                                                    AlertDialog builder = alert.create();
                                                    builder.show();
                                                } else {
                                                    try {
                                                        agregarConductor(0, vehiculo, json.getInt("user_id"));
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }

                                            }
                                        })
                                                //cancelar, si existe
                                        .setNeutralButton("Cancelar", null);
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                            //si el usuario no existe
                            else {
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UserActivity.this);
                                alertDialogBuilder.setCancelable(false);
                                alertDialogBuilder.setTitle("El conductor No existe");
                                alertDialogBuilder.setMessage("¿Desea crear al usuario?")
                                        .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                Intent intent = new Intent(UserActivity.this, NuevoUserActivity.class);
                                                Log.i("prueba", String.valueOf(email));

                                                intent.putExtra("sesion", sesion);
                                                intent.putExtra("email", email);
                                                intent.putExtra("vehiculo", vehiculo);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNeutralButton("Cancelar", null);
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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


                params.put("email", email);


                return params;
            }
        };
        mRequestQueue.add(request);

    }
    public void agregarConductor (final int dueño, final Vehiculo vehiculo, final int user_id){
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST,urlAgregar,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("prueba", response);
                        try {
                            final JSONObject json = new JSONObject(response);
                            if (json.getBoolean("exito")) {
                                Toast.makeText(getApplicationContext(), "Usuario registrado con exito", Toast.LENGTH_LONG).show();
                                peticionWS(vehiculo);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al agregar usuario. Intente nuevamente", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                params.put("user_id", String.valueOf(user_id));
                params.put("dueno", String.valueOf(dueño));



                return params;
            }
        };
        mRequestQueue.add(request);

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
            Intent intent = new Intent(UserActivity.this, LogoutActivity.class);
            intent.putExtra("sesion",sesion);

            startActivity(intent);
        }
        if (id == R.id.I_Menu_Mensaje) {
            Intent intent = new Intent(UserActivity.this, MensajeActivity.class);
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
        List<User> list = new ArrayList<>();

        for (int i = 0; i < VolleyList.getUsers().size(); i++) {
            if ((VolleyList.getUsers().get(i).getNombre()+VolleyList.getUsers().get(i).getNombre()).toLowerCase().contains(newText.toLowerCase())) {
                list.add(VolleyList.getUsers().get(i));
            }

        }
        crearLista(list);
        return false;
    }
}


