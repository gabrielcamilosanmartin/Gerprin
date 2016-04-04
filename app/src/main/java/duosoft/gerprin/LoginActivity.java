package duosoft.gerprin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_USER_ID = "user_id";
    private static final String PROPERTY_EMAIL = "user";
    private static final String PROPERTY_CONTRASEÑA = "contraseña";

    final String url = "http://52.23.181.133/index.php/login";
    Button entrar;
    TextView olvido;
    TextInputLayout email, contraseña;
    ProgressBar progressBar;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //layouts
        entrar = (Button) findViewById(R.id.B_Login_Entrar);
        olvido = (TextView) findViewById(R.id.TV_Login_Olvido);
        email = (TextInputLayout) findViewById(R.id.TIL_Login_Email);
        contraseña = (TextInputLayout) findViewById(R.id.TIL_Login_Contraseña);
        progressBar = (ProgressBar) findViewById(R.id.PB_Login_Progressbar);

    }
    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
        recordarSesion(prefs);
    }

    //accion al presionar entrar
    public void login(View v) {
        final SharedPreferences prefs = getSharedPreferences("login",Context.MODE_PRIVATE);

        String ema = email.getEditText().getText().toString();
        String con = contraseña.getEditText().getText().toString();


        //validar email y contraseña
        String errorEmail=validarEmail(ema);
        String errorContraseña=validarContraseña(con);
        //mostrar errores
        email.setError(errorEmail);
        contraseña.setError(errorContraseña);

        //entrar si no hay errores
        if (errorContraseña.matches("") && errorEmail.matches("")){
            visibilityLoadOn();
            entrar(ema, con, prefs);

        }
    }
    //comprueba email y envia mensaje de error si existe
    protected String validarEmail (String email){
        if (email.matches("")){
            return "Debe ingresar un email";
        }
            if (!email.contains("@") || !email.contains(".") ){
                return "Debe ingresar un email valido";
            }

        return "";
    }
    //comprueba contraseña y envia mensaje de error si existe
    protected String validarContraseña (String contraseña){
        if (contraseña.matches("")){
            return "Debe ingresar una contraseña";
        }
        return "";
    }
    //visible Progress bar
    public void visibilityLoadOn(){
        email.setVisibility(View.GONE);
        contraseña.setVisibility(View.GONE);
        entrar.setVisibility(View.GONE);
        olvido.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    //Ocultar Progress Bar
    public void visibilityLoadOff(){
        email.setVisibility(View.VISIBLE);
        contraseña.setVisibility(View.VISIBLE);
        entrar.setVisibility(View.VISIBLE);
        olvido.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    public void entrar(final String email, final String contraseña, final SharedPreferences prefs) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                User user = new User(json.getJSONObject("usuario"));
                                String idGuardado = prefs.getString(PROPERTY_REG_ID, "");
                                prefs.edit().putString(PROPERTY_USER_ID, String.valueOf(user.getId())).apply();
                                if (idGuardado.length() == 0){
                                    RegitroGcmcAsyncTask tarea2 = new RegitroGcmcAsyncTask();
                                    tarea2.execute();
                                }
                                prefs.edit().putString(PROPERTY_EMAIL,email).apply();
                                prefs.edit().putString(PROPERTY_CONTRASEÑA,contraseña).apply();
                                revisarMensajes(String.valueOf(user.getId()));
                                Intent intent = new Intent(LoginActivity.this ,VehiculoActivity.class);
                                intent.putExtra("sesion", user);
                                startActivity(intent);
                            }else{
                                visibilityLoadOff();
                                Toast.makeText(getApplicationContext(),json.getString("error"),Toast.LENGTH_LONG).show();
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

                params.put("email", email);
                params.put("password", contraseña);
                return params;
            }
        };
        mRequestQueue.add(request);

    }
    public void recordarSesion (SharedPreferences prefs){

        String emailGuardado      = prefs.getString(PROPERTY_EMAIL, "");
        String contraseñaGuardado = prefs.getString(PROPERTY_CONTRASEÑA, "");
        if (emailGuardado.length() != 0 || contraseñaGuardado.length() != 0){
            entrar(emailGuardado, contraseñaGuardado, prefs);
        }else{ visibilityLoadOff();}
    }

    private class RegitroGcmcAsyncTask extends AsyncTask<String , String, Object> {

        @Override
        protected void onPreExecute() {
            olvido.setVisibility(View.VISIBLE);
            olvido.setText("Registrando en aplicacion servidor...");
        }

        @Override
        protected Object doInBackground(String ... params) {
            SharedPreferences preferences=getSharedPreferences("login", Context.MODE_PRIVATE);

            try {

                String registrationToken = Utilidades.ObtenerRegistrationTokenEnGcm(getApplicationContext());
                String user_id=preferences.getString(PROPERTY_USER_ID, "");
                String respuesta = Utilidades.RegistrarseEnAplicacionServidor(getApplicationContext(),registrationToken,user_id);
                preferences.edit().putString(PROPERTY_REG_ID, registrationToken).apply();
                return respuesta;
            }
            catch (Exception ex){
                return ex;
            }
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Object result)
        {

            if(result instanceof  String)
            {
                String resulatado = (String)result;
                Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_LONG).show();
            }
            else if (result instanceof Exception)//Si el resultado es una Excepcion..hay error
            {
                Exception ex = (Exception) result;
                Toast.makeText(getApplicationContext(), "Error al configurar la sesion", Toast.LENGTH_LONG).show();
            }
        }

    }
    public void revisarMensajes(final String id) {
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        String url = "http://52.23.181.133/index.php/notificaciones";
        StringRequest array = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            SharedPreferences mensajes=getSharedPreferences("mensajes", Context.MODE_PRIVATE);
                            int cantidadMensajes = mensajes.getInt("cantidad_mensaje",0);
                            JSONObject json = new JSONObject(response);
                            JSONArray array = json.getJSONArray("notificaciones");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject objeto = array.getJSONObject(i);
                                if (objeto.getInt("leido") != 1) {
                                    cantidadMensajes=cantidadMensajes+1;
                                }
                            }
                            mensajes.edit().putInt("cantidad_mensaje", cantidadMensajes ).apply();
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
                params.put("user_id", id);
                return params;
            }
        };
        mRequestQueue.add(array);
    }


}
