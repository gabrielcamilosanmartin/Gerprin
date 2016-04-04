package duosoft.gerprin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class DetalleUserActivity extends AppCompatActivity {
    TextView huella, email, rol;
    ImageView huellaImagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_user);
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final User conductor = (User) getIntent().getExtras().getSerializable("conductor");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        Toolbar toolbar = (Toolbar) findViewById(R.id.mitoolbar);
        setSupportActionBar(toolbar);
        assert vehiculo != null;
        getSupportActionBar().setTitle(conductor.getNombre() + " " + conductor.getApellido());
        getSupportActionBar().setSubtitle("Nombre");
        //boton volver
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleUserActivity.this, UserActivity.class);
                intent.putExtra("sesion", sesion);
                intent.putExtra("vehiculo", vehiculo);
                startActivity(intent);
            }
        });
        huella = (TextView) findViewById(R.id.TV_DetalleUser_huella);
        email = (TextView) findViewById(R.id.TV_DetalleUser_Email);
        rol = (TextView) findViewById(R.id.TV_DetalleUser_Tipo);
        huellaImagen=(ImageView) findViewById(R.id.IV_DetalleUser_huella);

        if (conductor.isHuella()){
            huellaImagen.setImageResource(R.drawable.ic_action_huella_ok);
            huella.setText("Usuario con huella asociada");
        }else{
            huellaImagen.setImageResource(R.drawable.ic_action_huella_notok);
            huella.setText("Usuario sin huella asociada");
        }
        email.setText(conductor.getEmail());
        if (conductor.isDueño()){
                 rol.setText(Html.fromHtml("Usuario <b>dueño</b> del vehículo"));
        }else{
            rol.setText(Html.fromHtml("Usuario <b>conductor</b> del vehículo"));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final User conductor = (User) getIntent().getExtras().getSerializable("conductor");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        if (!(sesion.isEnrolador() || (vehiculo.isDueño() && !conductor.isDueño()))) {
            menu.findItem(R.id.I_Menu_Detalle_Eliminar).setVisible(false);
        }
        if (!sesion.isEnrolador()){
            menu.findItem(R.id.I_Menu_Detalle_Editar).setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        final User conductor = (User) getIntent().getExtras().getSerializable("conductor");
        int id= item.getItemId();
        if (id == R.id.I_Menu_Detalle_Editar){
            Intent intent = new Intent(DetalleUserActivity.this, EditarUserActivity.class);
            intent.putExtra("sesion", sesion);
            intent.putExtra("vehiculo", vehiculo);
            intent.putExtra("conductor", conductor);
            startActivity(intent);
            return true;
        }
        if (id == R.id.I_Menu_Detalle_Eliminar){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setTitle("Eliminar conductor");
            alertDialogBuilder.setMessage("¿Seguro que desea quitar este usuario del vehículo?")
                    .setPositiveButton("Eliminar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            eliminar(vehiculo,sesion, conductor);
                        }
                    })
                    .setNegativeButton("Volver", null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        if (id == R.id.I_Menu_Exit) {
            Intent intent = new Intent(DetalleUserActivity.this, LogoutActivity.class);
            intent.putExtra("sesion", sesion);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private void eliminar (final Vehiculo vehiculo, final User sesion,final User conductor){
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, "http://52.23.181.133/index.php/eliminarConductor",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                Toast.makeText(getApplicationContext(), "Conductor eliminado correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(DetalleUserActivity.this, UserActivity.class);
                                intent.putExtra("sesion", sesion);
                                intent.putExtra("vehiculo", vehiculo);
                                intent.putExtra("conducto", conductor);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), json.getString("error"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Se ha producido un error al establecer comunicación con los servidores de Gerprin. Inténtelo de nuevo más tarde", Toast.LENGTH_LONG).show();
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
                Map<String, String> params = new HashMap<String, String>();


                params.put("vehiculo_id", String.valueOf(vehiculo.getId()));
                params.put("user_id", String.valueOf(conductor.getId()));


                return params;
            }
        };
        mRequestQueue.add(request);
    }
}
