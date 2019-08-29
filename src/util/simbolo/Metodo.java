package util.simbolo;

import java.util.ArrayList;

public class Metodo {

    private String identificador, tipoRetorno;
    private ArrayList<String> listaParametros = new ArrayList<>();

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getTipoRetorno() {
        return tipoRetorno;
    }

    public void setTipoRetorno(String tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public ArrayList<String> getListaParametros() {
        return listaParametros;
    }

    public void setListaParametros(ArrayList<String> listaParametros) {
        this.listaParametros = listaParametros;
    }
}
