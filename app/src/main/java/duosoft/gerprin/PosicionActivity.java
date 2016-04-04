package duosoft.gerprin;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PosicionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    final String url = "http://52.23.181.133/index.php/posicionActual";
    final Handler handler = new Handler();
    Runnable runnable= new Runnable() {
        @Override
        public void run() {
            posicion();
            handler.postDelayed(runnable, 30000);
        }
    };
    protected void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posicion);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        runnable.run();
    }
    public void posicion() {
        final Vehiculo vehiculo = (Vehiculo) getIntent().getExtras().getSerializable("vehiculo");
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                Double lat = json.getJSONObject("posicion").getDouble("lat");
                                Double lon =json.getJSONObject("posicion").getDouble("lon");
                                mMap.clear();
                                LatLng posicionVehiculo = new LatLng(lat,lon );

                                mMap.addMarker(new MarkerOptions().position(posicionVehiculo).title(vehiculo.getPatente()));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 16));
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
                Map<String, String> params = new HashMap<>();

                params.put("vehiculo_id", String.valueOf(vehiculo.getId()));
                return params;
            }
        };
        mRequestQueue.add(request);

    }
}
