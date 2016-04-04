package duosoft.gerprin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NuevoVehiculoActivity extends AppCompatActivity {
    String url= "http://52.23.181.133/index.php/agregarVehiculo";
    TextInputLayout patente, marca, modelo, año, imei;
    ProgressBar progressBar;
    ImageView vehiculo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_vehiculo);

        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        //boton volver
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NuevoVehiculoActivity.this, VehiculoActivity.class);
                intent.putExtra("sesion", sesion);
                startActivity(intent);

            }
        });
        //definir layout
        patente = (TextInputLayout) findViewById(R.id.TIL_Nuevo_Patente);
        marca = (TextInputLayout) findViewById(R.id.TIL_Nuevo_Marca);
        modelo = (TextInputLayout) findViewById(R.id.TIL_Nuevo_Modelo);
        año = (TextInputLayout) findViewById(R.id.TIL_Nuevo_Año);
        imei = (TextInputLayout) findViewById(R.id.TIL_Nuevo_imei);
        progressBar = (ProgressBar) findViewById(R.id.PB_Nuevo_Progressbar);
        vehiculo = (ImageView) findViewById(R.id.IV_Nuevo_Vehiculo);
    }
    //**************************************INICIO VALIDACIONES*************************************
    //comprueba patente y envia mensaje de error si existe
    protected String validarPatente (String patente){
        if (patente.matches("")){
            return "Debe ingresar una patente";
        }
        if ((patente.matches("[A-Z]{4}[0-9]{2}")) || (patente.matches("[A-Z]{2}[0-9]{4}"))){
            return "";
        }

        return "La patente ingresada no es vaida";
    }

    //comprueba marca y envia mensaje de error si existe
    protected String validarMarca(String marca){
        if (marca.matches("")){
            return "Debe ingresar una marca";
        }
        return "";
    }

    //comprueba modelo y envia mensaje de error si existe
    protected String validarModelo(String modelo){
        if (modelo.matches("")){
            return "Debe ingresar una modelo";
        }
        return "";
    }

    //comprueba año y envia mensaje de error si existe
    protected String validarAño(String año){
        final Calendar c = Calendar.getInstance();
        int now = c.get(Calendar.YEAR);

        if (año.matches("")){
            return "Debe ingresar un año";
        }
        if (año.matches("[0-9]{4}")) {
            if (Integer.valueOf(año) >= 1920 && Integer.valueOf(año) <= now + 1) {
                return "";
            }
        }
        return "Ingrese un año valido";
    }
    //***************************************FIN VALIDACIONES***************************************
    //visible Progress bar
    public void visibilityLoadOn(){
        patente.setVisibility(View.GONE);
        marca.setVisibility(View.GONE);
        modelo.setVisibility(View.GONE);
        año.setVisibility(View.GONE);
        vehiculo.setVisibility(View.GONE);
        imei.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    public  void visibilityLoadOff(){
        patente.setVisibility(View.VISIBLE);
        marca.setVisibility(View.VISIBLE);
        modelo.setVisibility(View.VISIBLE);
        año.setVisibility(View.VISIBLE);
        vehiculo.setVisibility(View.VISIBLE);
        imei.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void registrar(final User sesion, final String patente, final String marca, final String modelo, final String año, final String imei) {
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("prueba", response);
                        try {
                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                Toast.makeText(getApplicationContext(), "Vehículo agregado correctamente", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(NuevoVehiculoActivity.this, VehiculoActivity.class);
                                intent.putExtra("sesion", sesion);
                                visibilityLoadOff();
                                startActivity(intent);

                            }else{
                                visibilityLoadOff();
                                Toast.makeText(getApplicationContext(), json.getString("error"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            visibilityLoadOff();
                            Toast.makeText(getApplicationContext(), "Se ha producido un error al establecer comunicación con los servidores de Gerprin. Inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        visibilityLoadOff();
                        Toast.makeText(getApplicationContext(), "Se ha producido un error al establecer comunicación con los servidores de Gerprin. Inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("patente", patente);
                params.put("marca", marca);
                params.put("modelo", modelo);
                params.put("ano", año);
                params.put("imei", imei);
                return params;
            }
        };
        mRequestQueue.add(request);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nuevo, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        int id= item.getItemId();
        if (id == R.id.I_Menu_Nuevo_Aceptar){
            String pat = patente.getEditText().getText().toString().replace(" ", "").toUpperCase();
            String mar = marca.getEditText().getText().toString();
            String mod = modelo.getEditText().getText().toString();
            String ano = año.getEditText().getText().toString();
            String im  = imei.getEditText().getText().toString();
            //validar los campos
            String errorPatente=validarPatente(pat);
            String errorMarca=validarMarca(mar);
            String errorModelo=validarModelo(mod);
            String errorAño=validarAño(ano);
            //mostrar errores
            patente.setError(errorPatente);
            marca.setError(errorMarca);
            modelo.setError(errorModelo);
            año.setError(errorAño);
            //entrar si no hay errores
            if (errorPatente.matches("") && errorMarca.matches("") && errorModelo.matches("") && errorAño.matches("")){
                visibilityLoadOn();
                registrar(sesion,pat,mar,mod,ano,im);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
