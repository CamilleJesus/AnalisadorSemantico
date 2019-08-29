/*
Autores: Camille Jesús e Reinildo Souza
Componente Curricular: EXA869 - MI Processadores de Linguagem de Programação (P03)
Data: 31/03/2019
*/
package model;

import util.token.PalavrasReservadas;
import util.token.Token;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class AnalisadorLexico {

    private ArrayList<String> lexemas, listaErros;
    private ArrayList<Integer> linhas;
    private ArrayList<Token> listaTokens;
    private ArrayList<ArrayList<Token>> listasTokens;

    public AnalisadorLexico() {
        this.lexemas = new ArrayList<>();
        this.linhas = new ArrayList<>();
        this.listaTokens = new ArrayList<>();
        this.listaErros = new ArrayList<>();
        this.listasTokens = new ArrayList<>();
    }

    public ArrayList<ArrayList<Token>> getListasTokens() {
        return this.listasTokens;
    }

    public void mainLexico() {
        File[] arquivos;
        File diretorio = new File("teste/");
        arquivos = diretorio.listFiles();
        int numeroArquivo = 0;

        for (int i = 0; i < arquivos.length; i++){

            if (arquivos[i].toString().contains("entrada")) {

                try {
                    numeroArquivo++;
                    System.out.println("\nIniciando a análise léxica do " + numeroArquivo + "º arquivo de entrada.");
                    lerArquivo(arquivos[i]);
                    identificarLexema();
                    checarTokens();
                    escreverArquivo(numeroArquivo);
                    clonarListaTokens();
                    limparEstruturas();
                    System.out.println("\nFinalizando a análise léxica do " + numeroArquivo + "º arquivo de entrada.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // FAZ A LEITURA DO ARQUIVO .TXT DE ENTRADA E REMOVE OS COMENTÁRIOS
    public void lerArquivo(File nomeArquivo) throws IOException {
        String linha;
        boolean comentarioBloco = false;
        Scanner in = new Scanner(System.in);
        int numeroLinha = 0, linhaComentarioBloco = 0;
        BufferedReader buffer = new BufferedReader(new FileReader(nomeArquivo));

        //Lê todas as linhas do arquivo até o final:
        while ((linha = buffer.readLine()) != null) {
            numeroLinha++;

            if ((linha.contains("/*")) && (linha.contains("*/"))) {
                linha = linha.replaceAll("/\\*.*\\*/", "");
                comentarioBloco = false;
                separarLinha(linha, numeroLinha);
                linhaComentarioBloco = numeroLinha;

                if (linha.contains("//")) {
                    linha = linha.replaceAll("//.*", "");
                }
                separarLinha(linha, numeroLinha);
            } else {

                if (linha.contains("/*")) {
                    linha = linha.replaceAll("/\\*.*", "");
                    comentarioBloco = true;
                    separarLinha(linha, numeroLinha);
                    linhaComentarioBloco = numeroLinha;
                }

                if (comentarioBloco == false) {

                    if (linha.contains("//")) {
                        linha = linha.replaceAll("//.*", "");
                    }
                } else {

                    if (linha.contains("*/")) {
                        linha = linha.replaceAll(".*\\*/", "");
                        comentarioBloco = false;
                    } else {
                        linha = linha.replaceAll(".*", "");
                    }
                }
                separarLinha(linha, numeroLinha);
            }
        }
        buffer.close();

        // SINALIZA O ERRO DE COMENTÁRIO DE BLOCO NÃO FECHADO
        if (comentarioBloco == true) {
            listaErros.add(mensagemErro(linhaComentarioBloco, "comentário de bloco aberto."));
        }
    }


    // QUEBRA O ARQUIVO DE ENTRADA EM VÁRIAS LINHAS
    public void separarLinha(String linha, int numeroLinha) {
        String partes[] = linha.split("\\s+");

        for (int i = 0; i < partes.length; i++) {

            if (!partes[i].isEmpty()) {
                lexemas.add(partes[i]);
                linhas.add(numeroLinha);
            }
        }
    }

    // ANALISA CADA LEXEMA (O ATUAL E O ANTERIOR) ANTES DE CLASSIFICÁ-LO
    public void identificarLexema() {
        StringBuilder lexema = new StringBuilder();
        String auxLexema, classe = "", classeAnterior = "";
        int linhaCadeiaCaracteres = 0, cadeiaCaracteres = 0;

        for (int i = 0; i < lexemas.size(); i++) {
            auxLexema = lexemas.get(i);
            classe = classificarLexema(auxLexema);

            if (classe.equals("VALOR_INESPERADO")) {
                listaErros.add(mensagemErro(linhas.get(i), "valor inesperado." ));
            } else if (classe.equals("CARACTERE_INVALIDO")) {
                listaErros.add(mensagemErro(linhas.get(i), "caractere inválido."));
            } else {

                if ((!classe.equals("CLASSE_INVALIDA")) && (!classe.equals("CADEIA_CARACTERES_INCOMPLETA")) && (!classeAnterior.equals("CADEIA_CARACTERES_INCOMPLETA")) && (!classeAnterior.equals("NUMERO_INCOMPLETO")) && (!classe.equals("VALOR_INESPERADO")) && (!classe.equals("CARACTERE_INVALIDO"))) {
                    listaTokens.add(new Token(classe, auxLexema, linhas.get(i)));
                } else {
                    auxLexema = lexemas.get(i);

                    for (int j = 0; j < auxLexema.length(); j++) {
                        lexema.append(auxLexema.charAt(j));
                        classe = classificarLexema(lexema.toString());

                        if (classe.equals("VALOR_INESPERADO")) {
                            listaErros.add(mensagemErro(linhas.get(i), "número mal formado")); //valor inesperado.
                        } else if (classe.equals("CARACTERE_INVALIDO")) {
                            listaErros.add(mensagemErro(linhas.get(i), "caractere inválido")); //caractere não pertence ao alfabeto.
                        } else {

                            if ((classe.equals("CADEIA_CARACTERES_INCOMPLETA")) && (cadeiaCaracteres == 0)) {
                                linhaCadeiaCaracteres = linhas.get(i);
                                cadeiaCaracteres++;
                            }

                            if (classe.equals("CLASSE_INVALIDA")) {
                                char c = lexema.charAt(lexema.length() - 1);
                                listaTokens.add(new Token(classeAnterior, lexema.substring(0, lexema.length() - 1), linhas.get(i)));
                                lexema.delete(0, lexema.length());
                                lexema.append(c);
                                classeAnterior = classificarLexema(lexema.toString());
                                cadeiaCaracteres = 0;

                                if ((j + 1) == auxLexema.length()) {
                                    listaTokens.add(new Token(classeAnterior, lexema.toString(), linhas.get(i)));
                                }
                            } else {
                                classeAnterior = classe;
                            }
                        }
                    }

                    if (!classe.equals("CADEIA_CARACTERES_INCOMPLETA")) {
                        lexema.delete(0, lexema.length());
                        classeAnterior = "";
                    } else {
                        lexema.append(" ");
                    }
                }
            }
        }

        if (classe.equals("CADEIA_CARACTERES_INCOMPLETA")) {
            listaErros.add(mensagemErro(linhaCadeiaCaracteres, "cadeia de caracteres aberta."));
        }
    }


    // NOMEIA O TOKEN DE ACORDO COM SUA CATEGORIA
    public String classificarLexema(String lexema) {

        if (lexema.matches("[a-zA-Z]+\\w*")) {

            if (PalavrasReservadas.ehReservada(lexema)) {
                return "PALAVRA_RESERVADA";
            }
            return "IDENTIFICADOR";
        } else if (lexema.matches("-?\\d+(\\.(\\d+))?")) {
            return "NUMERO";
        } else if (lexema.matches("-?\\d+\\.?")) {
            return "NUMERO_INCOMPLETO";
        } else if (lexema.matches("(--)|-|(\\+\\+)|\\+|\\*|/")) {
            return "OPERADOR_ARITMETICO";
        } else if (lexema.matches("(<=)|<|(==)|=|(>=)|>|(!=)")) {
            return "OPERADOR_RELACIONAL";
        } else if (lexema.matches("!|(&&)|(\\|\\|)")) {
            return "OPERADOR_LOGICO";
        } else if (lexema.matches(":|;|,|\\(|\\)|\\[|]|\\{|}|\\.")) {
            return "DELIMITADOR";
        } else if (lexema.matches("\"((\\\\\")|[^\"]|\\n)*\"")) {
            return "CADEIA_CARACTERES";
        } else if (lexema.matches("\"((\\\\\")|[^\"]|\\n)*")) {
            return "CADEIA_CARACTERES_INCOMPLETA";
        } else if ((lexema.matches("[a-zA-Z]+[^\\d-+*/<>=!(){}\\[\\]\"\',;]+")) || (lexema.matches("(-)?\\d+\\.*\\d*[^.,;\\-+*/<>=!)\\]]+"))) {
            return "VALOR_INESPERADO";
        } else if (lexema.matches("[^\\n\\w.()|+\\-<>=!/\\\\*\\[\\]{}\"\'\\\\\"]+")) {
            return "CARACTERE_INVALIDO";
        }
        return "CLASSE_INVALIDA";
    }

    // ADICIONA A MENSAGAGEM DE ERRO NO ARQUIVO DE SAÍDA
    public String mensagemErro (long linhaErro, String erro) {
        return ("Erro léxico na linha " + linhaErro + ": " + erro);
    }


    public void checarTokens() {

        for (int i = 0; i < listaTokens.size(); i++) {

            if (i > 2) {

                if ((listaTokens.get(i).getClasse().equals("NUMERO")) && (listaTokens.get(i - 1).getLexema().equals("-"))) {

                    if ((!listaTokens.get(i - 2).getClasse().equals("NUMERO")) || (!listaTokens.get(i - 2).getClasse().equals("IDENTIFICADOR"))) {
                        listaTokens.get(i).setLexema(listaTokens.get(i - 1).getLexema() + listaTokens.get(i).getLexema());
                        listaTokens.remove(i - 1);
                    }
                }
            } else {

                if ((listaTokens.get(1).getClasse().equals("NUMERO")) && (listaTokens.get(0).getClasse().equals("-"))) {
                    listaTokens.get(1).setLexema(listaTokens.get(0).getLexema() + listaTokens.get(1).getLexema());
                    listaTokens.remove(0);
                }

                if ((listaTokens.get(2).getClasse().equals("NUMERO")) && (listaTokens.get(1).getClasse().equals("-"))) {
                    listaTokens.get(2).setLexema(listaTokens.get(1).getLexema() + listaTokens.get(2).getLexema());
                    listaTokens.remove(1);
                }
            }
            Token token = listaTokens.get(i);

            if (token.getClasse().equals("NUMERO_INCOMPLETO")) {
                listaErros.add(mensagemErro(token.getLinha(), "número mal formado")); //valor inesperado.
                listaTokens.remove(i);
            }

            if (token.getClasse().isEmpty()) {
                listaTokens.remove(i);
            }
        }
    }

    // GERA O ARQUIVO DE SAÍDA COM OS DEVIDOS TOKENS E POSSÍVELS ERROS LÉXICOS
    public void escreverArquivo(int numeroArquivo) throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter("teste/saidaLexico" + numeroArquivo + ".txt"));

        for (int i = 0; i < listaTokens.size(); i++) {
            buffWrite.append(listaTokens.get(i).toString() + "\n");
        }

        if (listaErros.isEmpty()) {
            buffWrite.append("\nSucesso!");
        } else {

            for (int i = 0; i < listaErros.size(); i++) {
                buffWrite.append("\n" + listaErros.get(i));
            }
        }
        buffWrite.close();
        System.out.println("\nResultado da análise léxica no arquivo: saidaLexica" + numeroArquivo + ".txt");
    }

    public void clonarListaTokens() {
        ArrayList<Token> auxListaTokens = new ArrayList<>();

        for (Token token : listaTokens) {
            auxListaTokens.add(token);
        }
        auxListaTokens.add(new Token("", "$", listaTokens.size()));
        listasTokens.add(auxListaTokens);
    }

    public void limparEstruturas() {
        lexemas.clear();
        linhas.clear();
        listaTokens.clear();
        listaErros.clear();
    }
}