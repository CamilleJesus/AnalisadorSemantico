package util.simbolo;


public class Variavel extends Constante {

    private String escopo;
    private Integer dimensaoMatriz, tamanhoMatriz;

    public Variavel(String tipo, String identificador, String valor) {
        super(tipo, identificador, valor);
    }

    public String getEscopo() {
        return escopo;
    }

    public void setEscopo(String escopo) {
        this.escopo = escopo;
    }

    public Integer getDimensaoMatriz() {
        return dimensaoMatriz;
    }

    public void setDimensaoMatriz(Integer dimensaoMatriz) {
        this.dimensaoMatriz = dimensaoMatriz;
    }

    public Integer getTamanhoMatriz() {
        return tamanhoMatriz;
    }

    public void setTamanhoMatriz(Integer tamanhoMatriz) {
        this.tamanhoMatriz = tamanhoMatriz;
    }
}
