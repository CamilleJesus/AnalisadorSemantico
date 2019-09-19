package util.simbolo;

import java.util.ArrayList;

public class FlagMetodo {

    private String identificador;
    private long linha;
    private ArrayList<String> listaArgs = new ArrayList<>();

    public FlagMetodo(String identificador, long linha) {
        this.identificador = identificador;
        this.linha = linha;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String token) {
        this.identificador = token;
    }

    public long getLinha() {
        return linha;
    }

    public void setLinha(long linha) {
        this.linha = linha;
    }

    public ArrayList<String> getListaArgumentos() {
        return listaArgs;
    }

    public void setListaArgumentos(ArrayList<String> listaArgs) {
        this.listaArgs = listaArgs;
    }

    public void clonarListaArgumento(ArrayList<String> listaArgs) {

        for (String parametro : listaArgs) {
            this.listaArgs.add(parametro);
        }
    }
}
