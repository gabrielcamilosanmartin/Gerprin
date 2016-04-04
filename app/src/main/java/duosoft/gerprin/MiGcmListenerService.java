package duosoft.gerprin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class MiGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String mensaje = data.getString("message");
        String titulo = data.getString("titulo");
        String fecha = data.getString("fecha");
        Log.i("mensajeee", data.getString("fecha"));

        this.MostrarNotification(mensaje, titulo,fecha);
    }


    private void MostrarNotification(String mensaje, String titulo, String fecha) {
        Mensaje mensaje1 = new Mensaje();
        mensaje1.setTitulo(titulo);
        mensaje1.setLeido(false);
        mensaje1.setFecha(fecha);
        mensaje1.setMensaje(mensaje);
        Intent intent = new Intent(this, DetalleMensajeActivity.class);
        intent.putExtra("mensaje",mensaje1);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon_logo2)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        SharedPreferences preferences=getSharedPreferences("mensajes", Context.MODE_PRIVATE);
        int cantidadMensajes = preferences.getInt("cantidad_mensaje",0);
        preferences.edit().putInt("cantidad_mensaje", cantidadMensajes+1 ).apply();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int idNotificacion = 1;
        notificationManager.notify(1, notificationBuilder.build());
    }
}