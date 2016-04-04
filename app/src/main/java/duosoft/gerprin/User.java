package duosoft.gerprin;

import android.app.Application;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User extends Application implements Serializable{
    private int id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private boolean enrolador;
    private boolean dueño;
    private boolean huella;


    public User(JSONObject jsonObject) {
        try {
            this.setId(Integer.valueOf(jsonObject.getString("id")));
            this.setNombre(jsonObject.getString("nombre"));
            this.setApellido(jsonObject.getString("apellido"));
            this.setEmail(jsonObject.getString("email"));
            this.setEnrolador(Integer.valueOf(jsonObject.getString("enrolador")) == 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnrolador() {
        return enrolador;
    }

    public void setEnrolador(boolean enrolador) {
        this.enrolador = enrolador;
    }

    public Boolean isDueño() {
        return dueño;
    }

    public void setDueño(Boolean dueño) {
        this.dueño = dueño;
    }

    public void setDueño(boolean dueño) {
        this.dueño = dueño;
    }

    public boolean isHuella() {
        return huella;
    }

    public void setHuella(boolean huella) {
        this.huella = huella;
    }
}
