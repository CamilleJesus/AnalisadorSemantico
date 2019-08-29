package util.simbolo;


public class Constante {

    private String tipo, identificador, valor;

    public Constante(String tipo, String identificador, String valor) {
        this.tipo = tipo;
        this.identificador = identificador;
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
