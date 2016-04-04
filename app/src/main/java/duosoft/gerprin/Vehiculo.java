package duosoft.gerprin;

import java.io.Serializable;

public class Vehiculo implements Serializable {
    private int id;
    private String patente;
    private String marca;
    private String modelo;
    private int año;
    private boolean dueño;
    private boolean enrrolador;
    private String imei;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public boolean isDueño() {
        return dueño;
    }

    public void setDueño(boolean dueño) {
        this.dueño = dueño;
    }

    public boolean isEnrrolador() {
        return enrrolador;
    }

    public void setEnrrolador(boolean enrrolador) {
        this.enrrolador = enrrolador;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

}
