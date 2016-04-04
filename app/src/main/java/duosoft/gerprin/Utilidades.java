package duosoft.gerprin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Utilidades {
    public static boolean CheckPlayServices(Activity context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, 9000).show();
            }
            else
            {
                Toast.makeText(context, "Dispositivo no soportado", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }


    public static String ObtenerRegistrationTokenEnGcm(Context context) throws  Exception
    {
        InstanceID instanceID = InstanceID.getInstance(context);
        String token = instanceID.getToken(context.getString(R.string.senderid),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

        return token;
    }
    public static String RegistrarseEnAplicacionServidor(final Context context, final String registrationToken, final String id ) throws  Exception{
        RequestQueue mRequestQueue;
        final String[] respuesta = new String[1];
        respuesta[0]="error";
        mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, "http://52.23.181.133/index.php/modificarConductor",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("entraresponse", response);
                        try {
                            JSONObject json =new JSONObject(response);
                            if (json.getBoolean("exito")){
                                Log.d("entraresponse2", response);
                              respuesta[0] = "Exito";
                            }else{
                                respuesta[0] = "Error";}
                        } catch (JSONException e) {
                            e.printStackTrace();
                         respuesta[0] = "Error";
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        respuesta[0] = "Error";
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("gcm_regid", registrationToken);
                params.put("user_id", id);
                Log.d("entra", registrationToken);
                Log.d("entra",id);
                return params;
            }
        };
        mRequestQueue.add(request);
        Log.d("entra2", respuesta[0]);
        return respuesta[0];
    }

    public static String DameIMEI(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static AlertDialog MostrarAlertDialog(Context activity, String mensaje, String titulo, int icono)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(mensaje);
        builder1.setIcon(icono);
        builder1.setTitle(titulo);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder1.create();
        return alertDialog;
    }
}
