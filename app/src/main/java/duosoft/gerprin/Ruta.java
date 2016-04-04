package duosoft.gerprin;

import java.io.Serializable;

public class Ruta implements Serializable {
    int id;
    int user_id;
    int vehiculo_id;
    String start_time;
    String nombre_user;

    public String getNombre_user() { return nombre_user; }

    public void setNombre_user(String nombre_user) {
        this.nombre_user = nombre_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getVehiculo_id() {
        return vehiculo_id;
    }

    public void setVehiculo_id(int vehiculo_id) {
        this.vehiculo_id = vehiculo_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }
}
