package br.ufal.ic.p2.jackut;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * A classe User representa um usuário do sistema de gerenciamento.
 *
 * @author Gustavo Gaia
 */
public class User {
    private String name;// Nome do usuário
    private String login;// Login do usuário
    private String password;// Senha do usuário
    private ArrayList<String> friends;// Lista de amigos do usuário
    private ArrayList<String> friendSolicitation;// Lista de solicitações de amizade pendentes
    private Map<String, String> attributes;// Atributos extras do usuário
    private Queue<Recado> messageBox;// Caixa de mensagens do usuário

    /**
     * Construtor da classe User
     */
    public User() {
    }
    /**
     * Construtor da classe User nas configurações para armazenamento JSON.
     *
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @param nome O nome do usuário.
     */
    @JsonCreator
    public User(@JsonProperty("login") String login, @JsonProperty("senha") String senha, @JsonProperty("nome") String nome) {
        this.login = login;
        this.password = senha;
        this.name = nome;
        friends = new ArrayList<>();
        friendSolicitation = new ArrayList<>();
        messageBox = new LinkedList<>();
        attributes = new HashMap<>();
    }
    /**
     * Verifica se a senha fornecida corresponde à senha do usuário.
     *
     * @param senha A senha a ser verificada.
     * @return `true` se a senha estiver correta, `false` caso contrário.
     */
    public boolean verificarSenha(String senha){
        return Objects.equals(senha, this.password);
    }

    /**
     * Obtém o nome do usuário
     * @return nome do usuário
     */
    public String getName() {
        return name;
    }
    /**
     * Obtém o login do usuário
     * @return login do usuário
     */
    public String getLogin() {
        return login;
    }
    /**
     * Obtém a senha do usuário
     * @return senha do usuário
     */
    public String getPassword() {
        return password;
    }
    /**
     * Obtém a lista de amigos do usuário
     * @return lista de amigos do usuário
     */
    public ArrayList<String> getFriends() {
        return friends;
    }
    /**
     * Obtém a lista de solicitações de amizade do usuário
     * @return lista de solicitações de amizade do usuário
     */
    public ArrayList<String> getFriendSolicitation() {
        return friendSolicitation;
    }
    /**
     * Obtém a caixa de mensagens do usuário
     * @return caixa de mensagens do usuário
     */
    public Queue<Recado> getMessageBox() {
        return messageBox;
    }
    /**
     * Atualiza o nome do usuário
     * @param name novo nome do usuário
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Atualiza o login do usuário
     * @param login novo login do usuário
     */
    public void setLogin(String login) {
        this.login = login;
    }
    /**
     * Atualiza a senha do usuário
     * @param password nova senha do usuário
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Atualiza a lista de amigos do usuário
     * @param friends nova lista de amigos do usuário
     */
    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }
    /**
     * Atualiza a lista de solicitações de amizade do usuário
     * @param friendSolicitation nova lista de solicitações de amizade do usuário
     */
    public void setFriendSolicitation(ArrayList<String> friendSolicitation) {
        this.friendSolicitation = friendSolicitation;
    }
    /**
     * Atualiza a caixa de mensagem do usuário
     * @param messageBox caixa de mensagem do usuário
     */
    public void setMessageBox(Queue<Recado> messageBox) {
        this.messageBox = messageBox;
    }
    /**
     * Cria ou altera novos atributos que podem ser preenchidos pelo usuário.
     *
     * @param attribute o atributo a ser modificado ou criado.
     * @param content conteudo do atributo.
     */
    public void setExtraAttributes(String attribute, String content) {
        if (attributes.containsKey(attribute)) attributes.replace(attribute, content);
        else attributes.put(attribute, content);
    }
    /**
     * Obtem o novo atributo preenchido pelo usuário.
     *
     * @param attribute o atributo preenchido a ser retornado.
     */
    public String getExtraAttribute(String attribute) {
        if (attributes.containsKey(attribute)) return attributes.get(attribute);
        else throw new RuntimeException("Atributo não preenchido.");
    }

    /**
     * Obtém os atributos extras do usuário em um mapa.
     *
     * @return Um mapa contendo os atributos extras e seus valores.
     */
    @JsonAnyGetter
    public Map<String, String> getAttributes() {
        return attributes;
    }
    /**
     * Define os atributos extras do usuário a partir de um mapa.
     *
     * @param attribute O nome do atributo.
     * @param value O valor do atributo.
     */
    @JsonAnySetter
    public void setAttributes(String attribute, String value) {
        this.attributes.put(attribute, value);
    }

    /**
     * Adiciona um amigo à lista de amigos do usuário, removendo a solicitação de amizade, se existir.
     *
     * @param friend O login do amigo a ser adicionado.
     */
    public void addFriends(String friend) {
        this.friendSolicitation.remove(friend);
        this.friends.add(friend);
    }
    /**
     * Adiciona uma solicitação de amizade à lista de solicitações pendentes.
     *
     * @param friendSolicitation O login do usuário que enviou a solicitação de amizade.
     */
    public void addFriendSolicitation(String friendSolicitation) {
        this.friendSolicitation.add(friendSolicitation);
    }
    /**
     * Adiciona um recado à caixa de mensagens do usuário.
     *
     * @param recado O recado a ser adicionado.
     */
    public void receiveMessage(Recado recado){
        this.messageBox.add(recado);
    }
}
