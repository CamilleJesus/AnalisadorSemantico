package util.simbolo;

import java.util.ArrayList;

public class Metodo {

    private String identificador, tipoRetorno;
    private ArrayList<String> listaParam = new ArrayList<>();

    public Metodo(String identificador, String tipoRetorno) {
        this.identificador = identificador;
        this.tipoRetorno = tipoRetorno;
    }

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
        return listaParam;
    }

    public void setListaParametros(ArrayList<String> listaParam) {
        this.listaParam = listaParam;
    }

    public void clonarListaParametro(ArrayList<String> listaParam) {

        for (String parametro : listaParam) {
            this.listaParam.add(parametro);
        }
    }
}
