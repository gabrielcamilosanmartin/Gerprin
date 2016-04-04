package duosoft.gerprin;


import java.util.List;

public class VolleyList {

    private static List<Vehiculo> vehiculos2;
    private static List<User> users2;
    private static List<Ruta> rutas2;
    private static List<Mensaje> mensaje2;
    private static List<Vehiculo> vehiculos;
    private static List<User> users;
    private static List<Ruta> rutas;
    private static List<Mensaje> mensaje;

    public static List<Ruta> getRutas() {
        return rutas;
    }

    public static void setRutas(List<Ruta> rutas) {
        VolleyList.rutas = rutas;
    }

    public static List<Vehiculo> getVehiculos() {
        return vehiculos;
    }

    public static void setVehiculos(List<Vehiculo> vehiculoList) {
        vehiculos = vehiculoList;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> userList) {
        users = userList;
    }

    public static List<Mensaje> getMensaje() {
        return mensaje;
    }

    public static void setMensaje(List<Mensaje> mensaje) {
        VolleyList.mensaje = mensaje;
    }

    public static List<Vehiculo> getVehiculos2() {
        return vehiculos2;
    }

    public static void setVehiculos2(List<Vehiculo> vehiculos2) {
        VolleyList.vehiculos2 = vehiculos2;
    }

    public static List<User> getUsers2() {
        return users2;
    }

    public static void setUsers2(List<User> users2) {
        VolleyList.users2 = users2;
    }

    public static List<Ruta> getRutas2() {
        return rutas2;
    }

    public static void setRutas2(List<Ruta> rutas2) {
        VolleyList.rutas2 = rutas2;
    }

    public static List<Mensaje> getMensaje2() {
        return mensaje2;
    }

    public static void setMensaje2(List<Mensaje> mensaje2) {
        VolleyList.mensaje2 = mensaje2;
    }
}
