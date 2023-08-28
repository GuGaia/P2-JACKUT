package br.ufal.ic.p2.jackut;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * A classe User representa um usu�rio do sistema de gerenciamento.
 *
 * @author Gustavo Gaia
 */
public class User {
    private String name;// Nome do usu�rio
    private String login;// Login do usu�rio
    private String password;// Senha do usu�rio
    private ArrayList<String> friends;// Lista de amigos do usu�rio
    private ArrayList<String> friendSolicitation;// Lista de solicita��es de amizade pendentes
    private Map<String, String> attributes;// Atributos extras do usu�rio
    private Queue<Recado> messageBox;// Caixa de mensagens do usu�rio

    /**
     * Construtor da classe User
     */
    public User() {
    }
    /**
     * Construtor da classe User nas configura��es para armazenamento JSON.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @param nome O nome do usu�rio.
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
     * Verifica se a senha fornecida corresponde � senha do usu�rio.
     *
     * @param senha A senha a ser verificada.
     * @return `true` se a senha estiver correta, `false` caso contr�rio.
     */
    public boolean verificarSenha(String senha){
        return Objects.equals(senha, this.password);
    }

    /**
     * Obt�m o nome do usu�rio
     * @return nome do usu�rio
     */
    public String getName() {
        return name;
    }
    /**
     * Obt�m o login do usu�rio
     * @return login do usu�rio
     */
    public String getLogin() {
        return login;
    }
    /**
     * Obt�m a senha do usu�rio
     * @return senha do usu�rio
     */
    public String getPassword() {
        return password;
    }
    /**
     * Obt�m a lista de amigos do usu�rio
     * @return lista de amigos do usu�rio
     */
    public ArrayList<String> getFriends() {
        return friends;
    }
    /**
     * Obt�m a lista de solicita��es de amizade do usu�rio
     * @return lista de solicita��es de amizade do usu�rio
     */
    public ArrayList<String> getFriendSolicitation() {
        return friendSolicitation;
    }
    /**
     * Obt�m a caixa de mensagens do usu�rio
     * @return caixa de mensagens do usu�rio
     */
    public Queue<Recado> getMessageBox() {
        return messageBox;
    }
    /**
     * Atualiza o nome do usu�rio
     * @param name novo nome do usu�rio
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Atualiza o login do usu�rio
     * @param login novo login do usu�rio
     */
    public void setLogin(String login) {
        this.login = login;
    }
    /**
     * Atualiza a senha do usu�rio
     * @param password nova senha do usu�rio
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Atualiza a lista de amigos do usu�rio
     * @param friends nova lista de amigos do usu�rio
     */
    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }
    /**
     * Atualiza a lista de solicita��es de amizade do usu�rio
     * @param friendSolicitation nova lista de solicita��es de amizade do usu�rio
     */
    public void setFriendSolicitation(ArrayList<String> friendSolicitation) {
        this.friendSolicitation = friendSolicitation;
    }
    /**
     * Atualiza a caixa de mensagem do usu�rio
     * @param messageBox caixa de mensagem do usu�rio
     */
    public void setMessageBox(Queue<Recado> messageBox) {
        this.messageBox = messageBox;
    }
    /**
     * Cria ou altera novos atributos que podem ser preenchidos pelo usu�rio.
     *
     * @param attribute o atributo a ser modificado ou criado.
     * @param content conteudo do atributo.
     */
    public void setExtraAttributes(String attribute, String content) {
        if (attributes.containsKey(attribute)) attributes.replace(attribute, content);
        else attributes.put(attribute, content);
    }
    /**
     * Obtem o novo atributo preenchido pelo usu�rio.
     *
     * @param attribute o atributo preenchido a ser retornado.
     */
    public String getExtraAttribute(String attribute) {
        if (attributes.containsKey(attribute)) return attributes.get(attribute);
        else throw new RuntimeException("Atributo n�o preenchido.");
    }

    /**
     * Obt�m os atributos extras do usu�rio em um mapa.
     *
     * @return Um mapa contendo os atributos extras e seus valores.
     */
    @JsonAnyGetter
    public Map<String, String> getAttributes() {
        return attributes;
    }
    /**
     * Define os atributos extras do usu�rio a partir de um mapa.
     *
     * @param attribute O nome do atributo.
     * @param value O valor do atributo.
     */
    @JsonAnySetter
    public void setAttributes(String attribute, String value) {
        this.attributes.put(attribute, value);
    }

    /**
     * Adiciona um amigo � lista de amigos do usu�rio, removendo a solicita��o de amizade, se existir.
     *
     * @param friend O login do amigo a ser adicionado.
     */
    public void addFriends(String friend) {
        this.friendSolicitation.remove(friend);
        this.friends.add(friend);
    }
    /**
     * Adiciona uma solicita��o de amizade � lista de solicita��es pendentes.
     *
     * @param friendSolicitation O login do usu�rio que enviou a solicita��o de amizade.
     */
    public void addFriendSolicitation(String friendSolicitation) {
        this.friendSolicitation.add(friendSolicitation);
    }
    /**
     * Adiciona um recado � caixa de mensagens do usu�rio.
     *
     * @param recado O recado a ser adicionado.
     */
    public void receiveMessage(Recado recado){
        this.messageBox.add(recado);
    }
}
