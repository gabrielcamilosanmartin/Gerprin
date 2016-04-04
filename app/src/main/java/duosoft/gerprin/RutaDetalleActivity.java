package duosoft.gerprin;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RutaDetalleActivity extends FragmentActivity implements OnMapReadyCallback {
    final String urlruta ="http://52.23.181.133/index.php/ruta";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta_detalle);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        final Ruta ruta = (Ruta)getIntent().getExtras().getSerializable("ruta");
        getRuta(ruta.getId());
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
    private void drawPolilyne(PolylineOptions options){
        Polyline polyline = mMap.addPolyline(options);
    }
    public void getRuta(final int ruta_id) {
        String url ="http://52.23.181.133/index.php/ruta";
        RequestQueue mRequestQueue;
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("exito")){

                                JSONArray lista = json.getJSONArray("posiciones");
                                Log.i("asidj", String.valueOf(json));
                                LatLng inicio =new LatLng(lista.getJSONObject(0).getDouble("lat"),lista.getJSONObject(0).getDouble("lon"));
                                LatLng fin =new LatLng(lista.getJSONObject((lista.length()-1)).getDouble("lat"),lista.getJSONObject((lista.length()-1)).getDouble("lon"));
                                drawPolilyne(getPolyline(lista));
                                mMap.addMarker(new MarkerOptions().position(inicio).title("Inicio del recorrido"));
                                mMap.addMarker(new MarkerOptions().position(fin).title("Fin del recorrido"));

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicio, 14));

                            }else {
                                Toast.makeText(getApplicationContext(), "No se pudo obtener la ruta seleccionada, por favor intente más tarde", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("ruta_id", String.valueOf(ruta_id));
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    public String crearpath(JSONArray lista) {
        String path = "";
        try {

            for (int i = 0; i < lista.length(); i++) {
                JSONObject objeto = lista.getJSONObject(i);
                if (i == 0) {
                    path = objeto.getString("lat") + "," + objeto.getString("lon");
                } else {
                    path = path + "|" + objeto.getString("lat") + "," + objeto.getString("lon");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return path;
    }
    public PolylineOptions getPolyline(JSONObject json){
        PolylineOptions POLILINEA = new PolylineOptions();
        try {
            JSONArray lista = json.getJSONArray("snappedPoints");
            for (int i = 0; i < lista.length(); i++){

                JSONObject objeto = lista.getJSONObject(i).getJSONObject("location");
                POLILINEA.add(new LatLng(objeto.getDouble("latitude"), objeto.getDouble("longitude")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return POLILINEA;
    }
    public PolylineOptions getPolyline(JSONArray json){
        PolylineOptions POLILINEA = new PolylineOptions();
        try {

            for (int i = 0; i < json.length(); i++){

                JSONObject objeto = json.getJSONObject(i);
                POLILINEA.add(new LatLng(objeto.getDouble("lat"), objeto.getDouble("lon")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return POLILINEA;
    }
}
