/*
Autores: Camille Jesús e Reinildo Souza
Componente Curricular: EXA869 - MI Processadores de Linguagem de Programação (P03)
Data: 18/08/2019
*/
package model;

import util.simbolo.Constante;
import util.simbolo.Variavel;
import util.token.Token;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class AnalisadorSintatico {
    private ArrayList<String>  PrimeiroDefConstante, PrimeiroDefMetodo, PrimeiroTipoId, PrimeiroAtribuicao, PrimeiroDefVariavel;
    private ArrayList<String> PrimeiroDefSe, PrimeiroDefEnquanto, PrimeiroDefEscreva, PrimeiroDefLeia, PrimeiroDefResultado;
    private ArrayList<String> PrimeiroOpRelacionalIgual, PrimeiroOpRelacionalOutros, PrimeiroOpAritmeticoAd, PrimeiroOpAritmeticoMul;
    private ArrayList<String> PrimeiroOpUnario, PrimeiroOpPosfixo, listaErros, PrimeiroDefPrincipal;
    private ArrayList<Token> listaTokens;
    private String token, tipoSimbolo, identificador, tipoValor, escopo;
    private int tokenAtual = 0, tokenAnterior = 0, numeroArquivo = 0;
    private long linhaErro = 0;
    private ArrayList<ArrayList<Token>> listasTokens;
    private ArrayList<Constante> tabelaConstantes;
    private ArrayList<Variavel> tabelaVariaveis;

    public AnalisadorSintatico() {
        PrimeiroDefConstante = new ArrayList<>();
        PrimeiroDefPrincipal = new ArrayList<>();
        PrimeiroDefMetodo = new ArrayList<>();
        PrimeiroTipoId = new ArrayList<>();
        PrimeiroAtribuicao = new ArrayList<>();
        PrimeiroDefVariavel = new ArrayList<>();
        PrimeiroDefSe = new ArrayList<>();
        PrimeiroDefEnquanto = new ArrayList<>();
        PrimeiroDefEscreva = new ArrayList<>();
        PrimeiroDefLeia = new ArrayList<>();
        PrimeiroDefResultado = new ArrayList<>();
        PrimeiroOpRelacionalIgual = new ArrayList<>();
        PrimeiroOpRelacionalOutros = new ArrayList<>();
        PrimeiroOpAritmeticoAd = new ArrayList<>();
        PrimeiroOpAritmeticoMul = new ArrayList<>();
        PrimeiroOpUnario = new ArrayList<>();
        PrimeiroOpPosfixo = new ArrayList<>();
        listaErros = new ArrayList<>();
        listaTokens = new ArrayList<>();
        listasTokens = new ArrayList<>();
        tabelaConstantes = new ArrayList<>();
        tabelaVariaveis = new ArrayList<>();

        // DEFINIÇÃO DOS CONJUNTOS PRIMEIROS
        PrimeiroDefConstante.add("constantes");

        PrimeiroDefPrincipal.add("metodo");

        PrimeiroDefMetodo.add("metodo");

        PrimeiroTipoId.add("inteiro");
        PrimeiroTipoId.add("real");
        PrimeiroTipoId.add("texto");
        PrimeiroTipoId.add("boleano");

        PrimeiroAtribuicao.add("++");
        PrimeiroAtribuicao.add("--");
        PrimeiroAtribuicao.add("!");
        PrimeiroAtribuicao.add("verdadeiro");
        PrimeiroAtribuicao.add("falso");
        PrimeiroAtribuicao.add("(");

        PrimeiroDefVariavel.add("variaveis");

        PrimeiroDefSe.add("se");

        PrimeiroDefEnquanto.add("enquanto");

        PrimeiroDefEscreva.add("escreva");

        PrimeiroDefLeia.add("leia");

        PrimeiroDefResultado.add("resultado");

        PrimeiroOpRelacionalIgual.add("==");
        PrimeiroOpRelacionalIgual.add("!=");

        PrimeiroOpRelacionalOutros.add("<");
        PrimeiroOpRelacionalOutros.add(">");
        PrimeiroOpRelacionalOutros.add("<=");
        PrimeiroOpRelacionalOutros.add(">=");

        PrimeiroOpAritmeticoAd.add("+");
        PrimeiroOpAritmeticoAd.add("-");

        PrimeiroOpAritmeticoMul.add("*");
        PrimeiroOpAritmeticoMul.add("/");

        PrimeiroOpUnario.add("++");
        PrimeiroOpUnario.add("--");
        PrimeiroOpUnario.add("!");

        PrimeiroOpPosfixo.add("++");
        PrimeiroOpPosfixo.add("--");
        PrimeiroOpPosfixo.add("[");
        PrimeiroOpPosfixo.add("(");
        PrimeiroOpPosfixo.add(".");
    }

    public void setListaTokens(ArrayList<Token> listaTokens) {
        this.listaTokens = listaTokens;
    }

    public void setListasTokens(ArrayList<ArrayList<Token>> listaListasTokens) {
        this.listasTokens = listaListasTokens;
    }

    public void mainSintatico() {

        for (int i = 0; i < listasTokens.size(); i++) {

            // PEGA LISTA DE TOKENS E INICIALIZA O ANALISADOR SINTÁTICO, DEPOIS ESCREVE OS RESULTADOS NOS ARQUIVOS
            try {
                numeroArquivo = i + 1;
                setListaTokens(listasTokens.get(i));
                procedimentosGramatica();
                escreverArquivo();
                limparEstruturas();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void procedimentosGramatica() {
        proximoToken(); //CHAMA O PRIMEIRO TOKEN DA LISTA
        Inicio();

        if (token.equals("$")) { // CHEGOU SEM ERROS AO FIM DA ANÁLISE SINTÁTICA
            System.out.println("Sucesso na análise sintática do " + numeroArquivo + "º arquivo!");
        } else { // NÃO OBTEVE SUCESSO NA ANÁLISE SINTÁTICA
            System.out.println("Erro na análise sintática do " + numeroArquivo + "º arquivo!");
        }
    }

    public void proximoToken() {
        Token t = listaTokens.get(tokenAtual);
        token = t.getLexema();
        linhaErro = t.getLinha();
        tokenAnterior = tokenAtual;
        tokenAtual++;
    }

    // ESQUELETO DO PROGRAMA + MENSAGENS DE ERRO
    public void Inicio() {

        if (token.equals("programa")) {
            this.escopo = "programa";
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                DefGlobal();

                if (token.equals("}")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "programa"));
        }
    }

    // CHAMADAS DAS FUNÇÕES GERADORAS DE BLOCOS DE CONSTANTES, MÉTODO PRINCIPAL E DECLARAÇÕES DE MÉTODOS
    public void DefGlobal() {

        if (PrimeiroDefConstante.contains(token)) {
            DefConstante();
            DefPrincipal();
            DefGlobal2();
        } else if (PrimeiroDefPrincipal.contains(token)) {
            DefPrincipal();
            DefGlobal2();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "principal"));
        }
    }

    public void DefGlobal2() {

        if (PrimeiroDefMetodo.contains(token)) {
            DefMetodo();
            DefGlobal2();
        }
    }

    // ESTRUTURA DO BLOCO 'CONSTANTES' + MENSAGENS DE ERROS
    public void DefConstante() {

        if (token.equals("constantes")) {
            this.escopo = "constantes";
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                ListaConst();

                if (token.equals("}")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "constantes"));
        }
    }

    // ESTRUTURA DO BLOCO 'METODO PRINCIPAL' COM O GERADOR DE LISTA DE PARÂMETROS E OUTROS + MENSAGENS DE ERROS
    public void DefPrincipal() {

        if (token.equals("metodo")) {
            proximoToken();

            if (token.equals("principal")) {
                this.escopo = "principal";
                proximoToken();

                if (token.equals("(")) {
                    proximoToken();
                    ListaParam();

                    if (token.equals(")")) {
                        proximoToken();

                        if (token.equals(":")) {
                            proximoToken();
                            Tipo();

                            if (token.equals("{")) {
                                proximoToken();
                                Declaracao();

                                if (token.equals("}")) {
                                    proximoToken();
                                } else {
                                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                                }
                            } else {
                                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
                            }
                        } else {
                            listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ":"));
                        }
                    } else {
                        listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
                    }
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "("));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "principal"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "metodo"));
        }
    }

    // ESTRUTURA DE DECLARAÇÃO DE 'MÉTODOS' + MENSAGENS DE ERROS
    public void DefMetodo() {

        if (token.equals("metodo")) {
            proximoToken();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                this.escopo = token;
                proximoToken();

                if (token.equals("(")) {
                    proximoToken();
                    ListaParam();

                    if (token.equals(")")) {
                        proximoToken();

                        if (token.equals(":")) {
                            proximoToken();
                            Tipo();

                            if (token.equals("{")) {
                                proximoToken();
                                Declaracao();

                                if (token.equals("}")) {
                                    proximoToken();
                                } else {
                                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                                }
                            } else {
                                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
                            }
                        } else {
                            listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ":"));
                        }
                    } else {
                        listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
                    }
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "("));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "identificador", "método"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "metodo"));
        }
    }

    // LISTA DE DECLARAÇÕES DE CONSTANTES + MENSAGENS DE ERROS
    public void ListaConst() {

        if (PrimeiroTipoId.contains(token)) {
            Constante();

            if (token.equals(";")) {
                proximoToken();
                ListaConst2();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ";"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "tipo", ""));
        }
    }

    // RECURSÃO DE CONSTANTES
    public void ListaConst2() {

        if (PrimeiroTipoId.contains(token)) {
            ListaConst();
        }
    }

    // DECLARAÇÃO DE UMA CONSTANTE + MENSAGEM DE ERRO
    public void Constante() {

        if (PrimeiroTipoId.contains(token)) {
            TipoId();
            AtribuicaoConst();
            ListaAtribuicaoConst();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "tipo", ""));
        }
    }

    public void ListaAtribuicaoConst() {

        if (token.equals(",")) {
            proximoToken();
            AtribuicaoConst();
        }
    }

    // ESTRUTURA DE ATRIBUIÇÃO DE CONSTANTE + MENSAGENS DE ERROS
    public void AtribuicaoConst() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            identificador = token;

            if (verificarTabela("constante", identificador, escopo).equals("existe")) {
                listaErros.add(mensagemErroSemantico(linhaErro, "repetido", ""));
            }
            proximoToken();

            if (token.equals("=")) {
                proximoToken();
                ValorConst();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "="));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "identificador", "constante"));
        }
    }

    public void Tipo() {

        if ((token.equals("vazio"))) {
            tipoSimbolo = token;
            proximoToken();
        } else if (PrimeiroTipoId.contains(token)) {
            TipoId();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "tipo", ""));
        }
    }

    public void TipoId() {

        if ((token.equals("inteiro")) || (token.equals("real")) || (token.equals("texto")) || (token.equals("boleano"))) {
            tipoSimbolo = token;
            proximoToken();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "tipo", ""));
        }
    }

    public void Valor() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            tipoValor = retornarTipo(listaTokens.get(tokenAnterior).getLexema(), "identificador");

            if (tipoValor.equals("nao_existe")) {
                listaErros.add(mensagemErroSemantico(linhaErro, "nao_declarado", ""));
            } else if (verificarTipo()) {
                tabelaVariaveis.add(new Variavel(tipoSimbolo, identificador, tipoValor));
            }
            proximoToken();
        } else if (token.equals("(")) {
            proximoToken();

            if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
                Expressao();

                if (token.equals(")")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "tipo", "retorno"));
            }
        } else if((listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso"))) {
            ValorConst();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "valor", ""));
        }
    }

    // VALOR DA CONSTANTE DEVE SER NÚMERO, CADEIA DE CARACTERES, VERDADEIRO OU FALSO. - EXIBE MENSAGENS DE ERROS
    public void ValorConst() {

        if ((listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso"))) {
            tipoValor = retornarTipo("", token);
            System.out.println(tipoSimbolo);
            System.out.println(identificador);
            System.out.println(tipoValor);

            if (escopo.equals("constantes")) {

                if (verificarTipo()) {
                    tabelaConstantes.add(new Constante(tipoSimbolo, identificador, tipoValor));
                } else {
                    listaErros.add(mensagemErroSemantico(linhaErro, "tipo", ""));
                }
            }
            proximoToken();
        }  else {
            listaErros.add(mensagemErroSintatico(linhaErro, "valor", ""));
        }
    }

    // LISTA DE PARÂMETROS DE UM MÉTODO + MENSAGEM DE ERRO
    public void ListaParam() {

        if (PrimeiroTipoId.contains(token)) {
            TipoId();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                proximoToken();
                ListaParam2();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "identificador", "parâmetro"));
            }
        }
    }

    // GERADOR DE MÚLTIPLOS PARÂMETROS
    public void ListaParam2() {

        if (token.equals(",")) {
            proximoToken();
            TipoId();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                proximoToken();
                ListaParam2();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "identificador", "parâmetro"));
            }
        }
    }

    public void ListaArg() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Atribuicao();
            ListaArg2();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "valor", ""));
        }
    }

    public void ListaArg2() {

        if (token.equals(",")) {
            proximoToken();
            Atribuicao();
            ListaArg2();
        }
    }

    // GERADORES DE DECLARAÇÃO DE VARIÁVEIS, SE, ENQUANTO, LEIA, ESCREVA, EXPRESSÃO, E RESULTADO
    public void Declaracao() {

        if (PrimeiroDefVariavel.contains(token)) {
            DefVariavel();
            Declaracao();
        } else if (PrimeiroDefSe.contains(token)) {
            DefSe();
            Declaracao();
        } else if (PrimeiroDefEnquanto.contains(token)) {
            DefEnquanto();
            Declaracao();
        } else if (PrimeiroDefEscreva.contains(token)) {
            DefEscreva();
            Declaracao();
        } else if (PrimeiroDefLeia.contains(token)) {
            DefLeia();
            Declaracao();
        } else if (PrimeiroDefResultado.contains(token)) {
            DefResultado();
            Declaracao();
        } else if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            DefExpressao();
            Declaracao();
        }
    }

    // ESTRUTURA DE DEFINIÇÃO DO BLOCO DE VARIÁVEIS + MENSAGENS DE ERROS
    public void DefVariavel() {

        if (token.equals("variaveis")) {
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                ListaVar();

                if (token.equals("}")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "variaveis"));
        }
    }

    // PERMITE A DECLARAÇÃO DE MÚLTIPLAS VARIÁVEIS + MENSAGEM DE RRO
    public void ListaVar() {

        if (PrimeiroTipoId.contains(token)) {
            DeclaracaoVar();
            ListaVar2();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, " ", "Variável esperada não encontrada."));
        }
    }

    // RECURSÃO PARA GERAR A LISTA DE VARIÁVEIS
    public void ListaVar2() {

        if (PrimeiroTipoId.contains(token)) {
            DeclaracaoVar();
            ListaVar2();
        }
    }

    // DECLARAÇÃO DE UMA VARIÁVEL + MENSAGEM DE ERRO
    public void DeclaracaoVar() {

        if (PrimeiroTipoId.contains(token)) {
            TipoId();
            ListaDeclaracaoVar();

            if (token.equals(";")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ";"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "tipo", "variável"));
        }
    }

    public void ListaDeclaracaoVar() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            AtribuicaoVar();
            ListaDeclaracaoVar2();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, " ", "Variável esperada não encontrada."));
        }
    }

    public void ListaDeclaracaoVar2() {

        if (token.equals(",")) {
            proximoToken();
            AtribuicaoVar();
            ListaDeclaracaoVar2();
        }
    }

    public void AtribuicaoVar() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            Declarador();
            AtribuicaoVar2();
        }
    }

    public void AtribuicaoVar2() {

        if (token.equals("=")) {
            proximoToken();
            Inicializacao();
        }
    }

    public void Inicializacao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Atribuicao();
        } else if (token.equals("{")) {
            proximoToken();
            ListaInicializacaoVar();
            Inicializacao2();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "valor", ""));
        }
    }

    public void Inicializacao2() {

        if (token.equals("}")) {
            proximoToken();
        } else if (token.equals(",")) {
            proximoToken();

            if (token.equals("}")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
            }
        } else {   //VERIFICAR
            listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ", ou }"));
        }
    }

    public void ListaInicializacaoVar() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Inicializacao();
            ListaInicializacaoVar2();
        }
    }

    public void ListaInicializacaoVar2() {

        if (token.equals(",")) {
            proximoToken();
            Inicializacao();
            ListaInicializacaoVar2();
        }
    }

    public void Declarador() {

        if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
            identificador = token;

            if (verificarTabela("constante", identificador, escopo).equals("existe")) {
                listaErros.add(mensagemErroSemantico(linhaErro, "repetido", ""));
            } else if (verificarTabela("variavel", identificador, escopo).equals("existe")) {
                listaErros.add(mensagemErroSemantico(linhaErro, "repetido", ""));
            }
            proximoToken();
            Declarador2();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "identificador", "variável"));
        }
    }

    public void Declarador2() {

        if (token.equals("[")) {
            proximoToken();
            Declarador3();
        }
    }

    public void Declarador3() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAtribuicao();

            if (token.equals("]")) {
                proximoToken();
                Declarador2();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "]"));
            }
        } else if(token.equals("]")) {
            proximoToken();
            Declarador2();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "valor", ""));
        }
    }

    public void DefSe() {

        if (token.equals("se")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ExprRelacionalIgual();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals("entao")) {
                        proximoToken();

                        if (token.equals("{")) {
                            proximoToken();
                            Declaracao();

                            if (token.equals("}")) {
                                proximoToken();
                                DefSenao();
                            } else {
                                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                            }
                        } else {
                            listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
                        }
                    } else {
                        listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "entao"));
                    }
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "se"));
        }
    }

    public void DefSenao() {

        if (token.equals("senao")) {
            proximoToken();

            if (token.equals("{")) {
                proximoToken();
                Declaracao();

                if (token.equals("}")) {
                    proximoToken();
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
            }
        }
    }

    public void DefEnquanto() {

        if (token.equals("enquanto")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ExprRelacionalIgual();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals("{")) {
                        proximoToken();
                        Declaracao();

                        if (token.equals("}")) {
                            proximoToken();
                        } else {
                            listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "}"));
                        }
                    } else {
                        listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "{"));
                    }
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "enquanto"));
        }
    }

    public void DefEscreva() {

        if (token.equals("escreva")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ListaArg();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals(";")) {
                        proximoToken();
                    } else {
                        listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ";"));
                    }
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "escreva"));
        }
    }

    public void DefLeia() {

        if (token.equals("leia")) {
            proximoToken();

            if (token.equals("(")) {
                proximoToken();
                ListaArg();

                if (token.equals(")")) {
                    proximoToken();

                    if (token.equals(";")) {
                        proximoToken();
                    } else {
                        listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ";"));
                    }
                } else {
                    listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
                }
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", "("));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "leia"));
        }
    }

    public void DefResultado() {

        if (token.equals("resultado")) {
            proximoToken();
            Expressao();

            if (token.equals(";")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ";"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "palavra", "resultado"));
        }
    }

    public void DefExpressao() {

        if (verificarTabela("constante", token, escopo).equals("existe")) {
            listaErros.add(mensagemErroSemantico(linhaErro, "atribuicao", ""));
        }

        if (verificarTabela("variavel", token, escopo).equals("nao_existe")) {
            listaErros.add(mensagemErroSemantico(linhaErro, "nao_declarado", ""));
        }

        if (token.equals(";")) {
            proximoToken();
        } else if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Expressao();

            if (token.equals(";")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ";"));
            }
        }
    }

    public void Expressao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            Atribuicao();
            Expressao2();
        }
    }

    public void Expressao2() {

        if (token.equals(",")) {
            proximoToken();
            Atribuicao();
            Expressao2();
        }
    }

    public void Atribuicao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAtribuicao();
            Atribuicao2();
        }
    }

    public void Atribuicao2() {

        if (token.equals("=")) {
            proximoToken();
            ExprAtribuicao();
            Atribuicao2();
        }
    }

    public void ExprAtribuicao() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprLogicaOu();
        }
    }

    public void ExprLogicaOu() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprLogicaE();
            ExprLogicaOu2();
        }
    }

    public void ExprLogicaOu2() {

        if (token.equals("||")) {
            proximoToken();
            ExprLogicaE();
            ExprLogicaOu2();
        }
    }

    public void ExprLogicaE() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprRelacionalIgual();
            ExprLogicaE2();
        }
    }

    public void ExprLogicaE2() {

        if (token.equals("&&")) {
            proximoToken();
            ExprRelacionalIgual();
            ExprLogicaE2();
        }
    }

    public void ExprRelacionalIgual() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprRelacionalOutras();
            ExprRelacionalIgual2();
        }
    }

    public void ExprRelacionalIgual2() {

        if (PrimeiroOpRelacionalIgual.contains(token)) {
            OpRelacionalIgual();
            ExprRelacionalOutras();
            ExprRelacionalIgual2();
        }
    }

    public void ExprRelacionalOutras() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAritmeticaAd();
            ExprRelacionalOutras2();
        }
    }

    public void ExprRelacionalOutras2() {

        if (PrimeiroOpRelacionalOutros.contains(token)) {
            OpRelacionalOutros();
            ExprAritmeticaAd();
            ExprRelacionalOutras2();
        }
    }

    public void ExprAritmeticaAd() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprAritmeticaMult();
            ExprAritmeticaAd2();
        }
    }

    public void ExprAritmeticaAd2() {

        if (PrimeiroOpAritmeticoAd.contains(token)) {
            OpAritmeticoAd();
            ExprAritmeticaMult();
            ExprAritmeticaAd2();
        }
    }

    public void ExprAritmeticaMult() {

        if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ExprUnaria();
            ExprAritmeticaMult2();
        }
    }

    public void ExprAritmeticaMult2() {

        if (PrimeiroOpAritmeticoMul.contains(token)) {
            OpAritmeticoMul();
            ExprUnaria();
            ExprAritmeticaMult2();
        }
    }

    public void ExprUnaria() {

        if (PrimeiroOpUnario.contains(token)) {
            OpUnario();
            ExprUnaria();
        } else if ((listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso")) || (token.equals("("))) {
            ExprPosfixa();
        }
    }

    public void ExprPosfixa() {

        if ((listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES")) || (token.equals("verdadeiro")) || (token.equals("falso")) || (token.equals("("))) {
            Valor();
            ExprPosfixa2();
        }
    }

    public void ExprPosfixa2() {

        if (PrimeiroOpPosfixo.contains(token)) {
            OpPosfixo();
            ExprPosfixa2();
        }
    }

    public void OpRelacionalIgual() {

        if ((token.equals("==")) || (token.equals("!="))) {
            proximoToken();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "operador", "relacional de igualdade"));
        }
    }

    public void OpRelacionalOutros() {

        if ((token.equals("<")) || (token.equals(">")) || (token.equals("<=")) || (token.equals(">="))) {
            proximoToken();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "operador", "relacional de comparação"));
        }
    }

    public void OpAritmeticoAd() {

        if ((token.equals("+")) || (token.equals("-"))) {
            proximoToken();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "operador", "aritmético de adição"));
        }
    }

    public void OpAritmeticoMul() {

        if ((token.equals("*")) || (token.equals("/"))) {
            proximoToken();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "operador", "aritmético de multiplicação"));
        }
    }

    public void OpUnario() {

        if ((token.equals("++")) || (token.equals("--")) || (token.equals("!"))) {
            proximoToken();
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "operador", "aritmético de multiplicação"));
        }
    }

    public void OpPosfixo() {

        if ((token.equals("++")) || (token.equals("--"))) {
            proximoToken();
        } else if (token.equals("[")) {
            proximoToken();
            Expressao();

            if (token.equals("]")) {
                proximoToken();
            }
        } else if (token.equals("(")) {
            proximoToken();
            OpPosfixo2();
        } else if (token.equals(".")) {
            proximoToken();

            if (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) {
                proximoToken();
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "valor", ""));
        }
    }

    public void OpPosfixo2() {

        if (token.equals(")")) {
            proximoToken();
        } else if ((PrimeiroAtribuicao.contains(token)) || (listaTokens.get(tokenAnterior).getClasse().equals("IDENTIFICADOR")) || (listaTokens.get(tokenAnterior).getClasse().equals("NUMERO")) || (listaTokens.get(tokenAnterior).getClasse().equals("CADEIA_CARACTERES"))) {
            ListaArg();

            if(token.equals(")")) {
                proximoToken();
            } else {
                listaErros.add(mensagemErroSintatico(linhaErro, "simbolo", ")"));
            }
        } else {
            listaErros.add(mensagemErroSintatico(linhaErro, "valor", ""));
        }
    }

    public String mensagemErroSintatico(long linhaErro, String tipo, String valor) {

        if (tipo.equals("simbolo")) {
            return ("Erro sintático na linha " + linhaErro + ". Símbolo esperado não encontrado: " + valor + ".");
        } else if (tipo.equals("numero")) {
            return ("Erro sintático na linha " + linhaErro + ". Número esperado não encontrado: " + valor + ".");
        } else if (tipo.equals("palavra")) {
            return ("Erro sintático na linha " + linhaErro + ". Palavra reservada esperada não encontrada: " + valor + ".");
        } else if (tipo.equals("identificador")) {
            return ("Erro sintático na linha " + linhaErro + ". Identificador de " + valor + " esperado não encontrado.");
        } else if (tipo.equals("tipo")) {
            return ("Erro sintático na linha " + linhaErro + ". Tipo esperado não encontrado.");
        } else if (tipo.equals("valor")) {
            return ("Erro sintático na linha " + linhaErro + ". Valor esperado não encontrado.");
        } else if (tipo.equals("bloco")) {
            return ("Erro sintático na linha " + linhaErro + ". Bloco de comando " + valor + " esperado não encontrado.");
        } else if (tipo.equals(" ")) {
            return ("Erro sintático na linha " + linhaErro + "." + valor);
        }
        return "";
    }

    public String mensagemErroSemantico(long linhaErro, String tipo, String valor) {

        if (tipo.equals("tipo")) {
            return ("Erro semântico na linha " + linhaErro + ". Tipo incompatível com a declaração.");
        } else if (tipo.equals("repetido")) {
            return ("Erro semântico na linha " + linhaErro + ". Identificador já declarado.");
        } else if (tipo.equals("atribuicao")) {
            return ("Erro semântico na linha " + linhaErro + ". Tentativa de modificação de constante.");
        } else if (tipo.equals("nao_declarado")) {
            return ("Erro semântico na linha " + linhaErro + ". Identificador não declarado.");
        }
        return "";
    }

    public void escreverArquivo() throws IOException {
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter("teste/saidaSintatico" + numeroArquivo + ".txt"));

        if (listaErros.isEmpty()) {
            buffWrite.append("\nSucesso!");
        } else {

            for (int i = 0; i < listaErros.size(); i++) {
                buffWrite.append("\n" + listaErros.get(i));
            }
        }
        buffWrite.close();
        System.out.println("\nResultado da análise sintático no arquivo: saidaSintatico" + numeroArquivo + ".txt");
    }

    public void limparEstruturas() {
        listaTokens.clear();
        listaErros.clear();
        tabelaConstantes.clear();
        tokenAnterior = 0;
        tokenAtual = 0;
        linhaErro = 0;
    }

    public String verificarTabela(String categoria, String identificador, String escopo) {

        switch (categoria) {
            case "constante":

                if (!tabelaConstantes.isEmpty()) {

                    for (Constante constante : tabelaConstantes) {

                        if (identificador.equals(constante.getIdentificador())) {
                            return "existe";
                        }
                    }
                    return "nao_existe";
                } else {
                    return "lista_vazia";
                }
            case "variavel":

                if (!tabelaVariaveis.isEmpty()) {

                    for (Variavel variavel : tabelaVariaveis) {

                        if ((identificador.equals(variavel.getIdentificador())) && (escopo.equals(variavel.getEscopo()))) {
                            return "existe";
                        }
                    }
                    return "nao_existe";
                } else {
                    return "lista_vazia";
                }
        }
        return null;
    }

    public String getTipoTabela() {

        for (Constante constante : tabelaConstantes) {

            if (identificador.equals(constante.getIdentificador())) {
                return constante.getTipo();
            }
        }
        return "inexistente";
    }

    public String retornarTipo(String identificador, String valor) {

        if (valor.equals("identificador")) {

            if (!tabelaConstantes.isEmpty()) {

                for (Constante constante : tabelaConstantes) {

                    if (identificador.equals(constante.getIdentificador())) {
                        return (constante.getTipo());
                    }
                }
            }

            if (!tabelaVariaveis.isEmpty()) {

                for (Variavel variavel : tabelaVariaveis) {

                    if ((identificador.equals(variavel.getIdentificador())) && (escopo.equals(variavel.getEscopo()))) {
                        return (variavel.getTipo());
                    }
                }
            }
            return "nao_existe";
        } else if ((valor.equals("verdadeiro")) || (valor.equals("falso"))) {
            return "boleano";
        } else if (valor.contains("\"")) {
            return "texto";
        } else if (valor.contains(".")) {
            return "real";
        } else {
            return "inteiro";
        }
    }

    public boolean verificarTipo() {

        if (this.tipoSimbolo.equals(tipoValor)) {
            return true;
        }
        return false;
    }
}