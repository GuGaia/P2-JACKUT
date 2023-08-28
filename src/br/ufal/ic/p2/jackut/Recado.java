package br.ufal.ic.p2.jackut;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * A classe Recado representa uma mensagem enviada por um remetente.
 *
 * @author Gustavo Gaia
 */
public class Recado {
    private String remetente;
    private String mensagem;

    public Recado(){};
    /**
     * Construtor da classe Recado.
     *
     * @param remetente O remetente da mensagem.
     * @param mensagem O conteúdo da mensagem.
     */
    @JsonCreator
    public Recado(@JsonProperty("remetente") String remetente, @JsonProperty("mensagem") String mensagem) {
        this.remetente = remetente;
        this.mensagem = mensagem;
    }
    /**
     * Obtém o remetende da mensagem
     * @return remetende da mensagem
     */
    public String getRemetente() {
        return remetente;
    }
    /**
     * Obtém a mensagem
     * @return a mensagem
     */
    public String getMensagem() {
        return mensagem;
    }

}
