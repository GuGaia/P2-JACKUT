package br.ufal.ic.p2.jackut;

import easyaccept.EasyAccept;
/**
 * A classe br.ufal.ic.p2.jackut.Main � o ponto de entrada do programa. Respons�vel por executar os testes de aceita��o usando a biblioteca EasyAccept.
 *
 * @author Gustavo Gaia
 */
public class Main {
    /**
     * O m�todo principal (ponto de entrada) do programa.
     * Ele executa testes de aceita��o na classe Facade com base nos arquivos de teste fornecidos.
     *
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        String[] args2 = {"br.ufal.ic.p2.jackut.Facade",
                "tests/us1_1.txt", "tests/us1_2.txt",
                "tests/us2_1.txt", "tests/us2_2.txt",
                "tests/us3_1.txt", "tests/us3_2.txt",
                "tests/us4_1.txt", "tests/us4_2.txt",
        };
        EasyAccept.main(args2);
    }
}