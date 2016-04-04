package duosoft.gerprin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class DetalleVehiculoActivity extends AppCompatActivity {
    TextView marca, modelo, año, imei, imei2;
    ImageView imagenImei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_vehiculo);
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
                Intent intent = new Intent(DetalleVehiculoActivity.this, PanelActivity.class);
                intent.putExtra("sesion", sesion);
                intent.putExtra("vehiculo", vehiculo);
                startActivity(intent);
            }
        });
        marca = (TextView) findViewById(R.id.TV_DetalleVehiculo_Marca);
        modelo = (TextView) findViewById(R.id.TV_DetalleVehiculo_Modelo);
        año = (TextView) findViewById(R.id.TV_DetalleVehiculo_año);
        marca = (TextView) findViewById(R.id.TV_DetalleVehiculo_Marca);
        modelo = (TextView) findViewById(R.id.TV_DetalleVehiculo_Modelo);
        año = (TextView) findViewById(R.id.TV_DetalleVehiculo_año);

        imei();
        marca.setText(vehiculo.getMarca());
        modelo.setText(vehiculo.getModelo());
        año.setText(String.valueOf(vehiculo.getAño()));




    }

    private void imei(){
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");

        imei=(TextView) findViewById(R.id.TV_DetalleVehiculo_imei);
        imei2=(TextView) findViewById(R.id.TV_DetalleVehiculo_imei2);
        imagenImei=(ImageView) findViewById(R.id.IV_DetalleVehiculo_imei);
        if (!sesion.isEnrolador()){
            imei.setVisibility(View.GONE);
            imei2.setVisibility(View.GONE);
            imagenImei.setVisibility(View.GONE);
        }
        else{
            imei.setText(vehiculo.getImei());
        }
    }
    Menu mymenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final User sesion = (User) getIntent().getExtras().getSerializable("sesion");
        getMenuInflater().inflate(R.menu.menu_detalle, menu);
        if (!sesion.isEnrolador()) {
            menu.findItem(R.id.I_Menu_Detalle_Editar).setVisible(false);
            menu.findItem(R.id.I_Menu_Detalle_Eliminar).setVisible(false);
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
            Intent intent = new Intent(DetalleVehiculoActivity.this, EditarVehiculoActivity.class);
            intent.putExtra("sesion", sesion);
            intent.putExtra("vehiculo", vehiculo);
            startActivity(intent);
            return true;
        }
        if (id == R.id.I_Menu_Detalle_Eliminar){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setTitle("Eliminar vehículo");
            alertDialogBuilder.setMessage("¿Seguro que desea eliminar el vehículo?")
                    .setPositiveButton("Eliminar",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            eliminar(vehiculo,sesion);
                        }
                    })
                    .setNegativeButton("Volver", null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }
        if (id == R.id.I_Menu_Exit) {
            Intent intent = new Intent(DetalleVehiculoActivity.this, LogoutActivity.class);
            intent.putExtra("sesion", sesion);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private void eliminar (final Vehiculo vehiculo, final User sesion){
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, "http://52.23.181.133/index.php/eliminarVehiculo",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                Toast.makeText(getApplicationContext(), "Vehículo eliminado correctamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(DetalleVehiculoActivity.this, VehiculoActivity.class);
                                intent.putExtra("sesion", sesion);
                                intent.putExtra("vehiculo", vehiculo);
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


                return params;
            }
        };
        mRequestQueue.add(request);
    }
}
