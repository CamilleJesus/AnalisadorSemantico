/*
Autores: Camille Jesús e Reinildo Souza
Componente Curricular: EXA869 - MI Processadores de Linguagem de Programação (P03)
Data: 18/08/2019
*/
package controller;

import model.AnalisadorLexico;
import model.AnalisadorSintatico;

public class Compilador {

    private AnalisadorLexico lexico;
    private AnalisadorSintatico sintatico;

    private Compilador() {
        this.lexico = new AnalisadorLexico();
        this.sintatico = new AnalisadorSintatico();
    }

    public static void main(String[] args) {
        Compilador compilador = new Compilador();
        compilador.executar();
    }

    private void executar() {
        lexico.mainLexico();
        sintatico.setListasTokens(lexico.getListasTokens());
        sintatico.mainSintatico();
    }
}