package duosoft.gerprin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class NuevoUserActivity extends AppCompatActivity {
    TextInputLayout nombre, apellido, email, contraseña1, contraseña2;
    CheckBox dueño;
    TextView textView;
    ProgressBar progressBar;
    String url = "http://52.23.181.133/index.php/crearConductor";
    ImageView imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_user);
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        final String correo = (String) getIntent().getExtras().getString("email");
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        //boton volver
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NuevoUserActivity.this, UserActivity.class);
                intent.putExtra("sesion", sesion);
                intent.putExtra("vehiculo", vehiculo);
                startActivity(intent);

            }
        });
        //definir layout
        nombre      = (TextInputLayout) findViewById(R.id.TIL_NuevoUser_Nombre);
        apellido    = (TextInputLayout) findViewById(R.id.TIL_NuevoUser_Apellido);
        contraseña1 = (TextInputLayout) findViewById(R.id.TIL_NuevoUser_Contraseña);
        contraseña2 = (TextInputLayout) findViewById(R.id.TIL_NuevoUser_Contraseña2);
        dueño       = (CheckBox) findViewById(R.id.CB_NuevoUser_Dueño);
        textView    = (TextView) findViewById(R.id.TV_NuevoUser_Dueño);
        imagen      = (ImageView) findViewById(R.id.IV_NuevoUser_User);
        email       = (TextInputLayout) findViewById(R.id.TIL_NuevoUser_Email);
        email.getEditText().setText(correo);
        progressBar = (ProgressBar) findViewById(R.id.PB_NuevoUser_Progressbar);
        if (!sesion.isEnrolador()) {
            dueño.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }

    }
    //**************************************INICIO VALIDACIONES*************************************
    //comprueba email y envia mensaje de error si existe
    protected String validarEmail (String email){
        if (email.matches("")){
            return "Debe ingresar una email";
        }
        if (email.contains("@") && email.contains(".")){
            return "";
        }

        return "el email ingresada no es vaida";
    }

    //comprueba nombre y envia mensaje de error si existe
    protected String validarNombre(String nombre){
        if (nombre.matches("")){
            return "Debe ingresar un nombre";
        }
        return "";
    }

    //comprueba apellido y envia mensaje de error si existe
    protected String validarApellido(String apellido){
        if (apellido.matches("")){
            return "Debe ingresar una Apellido";
        }
        return "";
    }

    //comprueba contraseña y envia mensaje de error si existe
    protected String validarContraseña(String contraseña){
        if (contraseña.matches("")){
            return "Debe ingresar una contraseña";
        }
        return "";
    }
    protected String validarContraseña2(String contraseña, String contraseña2){
        if (contraseña2.matches("")){
            return "Debe ingresar una contraseña";
        }
        else if (!contraseña.matches(contraseña2)){
            return "Las contraseñas no coinciden";
        }
        return "";
    }
    //***************************************FIN VALIDACIONES***************************************
    //visible Progress bar
    public void visibilityLoadOn(){
        email.setVisibility(View.GONE);
        nombre.setVisibility(View.GONE);
        apellido.setVisibility(View.GONE);
        contraseña1.setVisibility(View.GONE);
        contraseña2.setVisibility(View.GONE);
        imagen.setVisibility(View.GONE);
        dueño.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }
    public  void visibilityLoadOff(){
        email.setVisibility(View.VISIBLE);
        nombre.setVisibility(View.VISIBLE);
        apellido.setVisibility(View.VISIBLE);
        contraseña1.setVisibility(View.VISIBLE);
        contraseña2.setVisibility(View.VISIBLE);
        imagen.setVisibility(View.VISIBLE);
        dueño.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
    public void registrar(final User sesion,final Vehiculo vehiculo, final String email, final String nombre, final String apellido, final String contraseña, final boolean dueño) {
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                Toast.makeText(getApplicationContext(), "Vehículo agregado correctamente", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(NuevoUserActivity.this, UserActivity.class);
                                intent.putExtra("sesion", sesion);
                                intent.putExtra("vehiculo", vehiculo);
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

                params.put("nombre", nombre);
                params.put("apellido", apellido);
                params.put("email", email);
                params.put("password", contraseña);
                params.put("vehiculo_id", String.valueOf(vehiculo.getId()));
                if (dueño) {
                    params.put("dueno", "1");
                } else {
                    params.put("dueno", "0");
                }
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
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        int id= item.getItemId();
        if (id == R.id.I_Menu_Nuevo_Aceptar){
            String nom = nombre.getEditText().getText().toString();
            String ape = apellido.getEditText().getText().toString();
            String ema = email.getEditText().getText().toString();
            String con1 = contraseña1.getEditText().getText().toString();
            String con2 = contraseña2.getEditText().getText().toString();
            boolean due = dueño.isChecked();

            //validar los campos
            String errorEmail=validarEmail(ema);
            String errorNombre=validarNombre(nom);
            String errorApellido=validarApellido(ape);
            String errorContraseña1=validarContraseña(con1);
            String errorContraseña2=validarContraseña2(con1,con2);
            //mostrar errores
            email.setError(errorEmail);
            nombre.setError(errorNombre);
            apellido.setError(errorApellido);
            contraseña1.setError(errorContraseña1);
            contraseña2.setError(errorContraseña2);
            //entrar si no hay errores
            if (errorEmail.matches("") && errorNombre.matches("") && errorApellido.matches("") && errorContraseña1.matches("") && errorContraseña2.matches("")){
                visibilityLoadOn();
                registrar(sesion, vehiculo,ema, nom, ape, con1, due);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}