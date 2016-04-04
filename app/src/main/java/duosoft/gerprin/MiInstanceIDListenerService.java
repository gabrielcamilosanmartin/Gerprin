package duosoft.gerprin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MiInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";

    @Override
    public void onTokenRefresh() {
        //obtener nuevamente el token y enviarlo a la aplicacion servidor
        RegitroGcmcAsyncTask regitroGcmcAsyncTask = new RegitroGcmcAsyncTask();
        regitroGcmcAsyncTask.execute();
    }

    private class RegitroGcmcAsyncTask extends AsyncTask<String , String, Object>
    {

        @SuppressLint("CommitPrefEdits")
        @Override
        protected Object doInBackground(String ... params) {
            SharedPreferences preferences=getSharedPreferences("login", Context.MODE_PRIVATE);
            try {

                publishProgress("Obteniendo Registration Token en GCM Servers...");
                String registrationToken = Utilidades.ObtenerRegistrationTokenEnGcm(getApplicationContext());

                publishProgress("Enviando Registration a mi aplicacion servidor...");
                String respuesta = Utilidades.RegistrarseEnAplicacionServidor(getApplicationContext(),registrationToken,preferences.getString("user_id",""));
                preferences.edit().putString("registration_id", registrationToken);
                return respuesta;
            }
            catch (Exception ex){
                return ex;
            }
        }

        protected void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(), progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object result)
        {
            if(result instanceof  String)
            {
                String resulatado = (String)result;
                Toast.makeText(getApplicationContext(), "Registro exitoso.", Toast.LENGTH_SHORT).show();
            }
            else if (result instanceof Exception)//Si el resultado es una Excepcion..hay error
            {
                Exception ex = (Exception) result;
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

}