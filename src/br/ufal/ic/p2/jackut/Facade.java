package br.ufal.ic.p2.jackut;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import java.io.*;
/**
 * A classe Facade oferece uma interface para gerenciar usu�rios e suas intera��es dentro sistema.
 *
 * @author Gustavo Gaia
 */
public class Facade {
    /**
     * Mensagem de erro que indica que um usu�rio n�o foi encontrado no sistema.
     */
    public static final String USER_NOT_FOUND= "Usu�rio n�o cadastrado.";
    private Map<String, User> users; // Mapa para armazenar usu�rios
    private Map<String, User> sessions; // Mapa para armazenar sess�es

    /**
     * Construtor da classe Facade.
     * Inicializa os mapas de usu�rios e sess�es e carrega os dados existente do sistema, se dispon�veis.
     */
    public Facade() {
        this.users = new HashMap<>();
        this.sessions = new HashMap<>();
        carregarSistema();
    }
    /**
     * M�todo zerarSistema exclui todos os dados do sistema, limpando os mapas de usu�rios, sess�es e arquivo de dados.
     */
    public void zerarSistema(){
        users.clear();
        sessions.clear();
        File usersData = new File("usuarios.json");
        usersData.delete();
    }
    /**
     * Carrega os dados do sistema a partir de um arquivo JSON, se ele existir, � inicializado no construtor.
     * Os dados carregados incluem usu�rios, amigos, solicita��es de amizade e mensagens.
     */
    public void carregarSistema(){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File usersData = new File("usuarios.json");

            if(usersData.exists()){
                List<User> listaUsuarios = objectMapper.readValue(usersData, new TypeReference<List<User>>() {});

                for (User user : listaUsuarios) {
                    criarUsuario(user.getLogin(), user.getPassword(), user.getName());
                    User newUser = users.get(user.getLogin());
                    newUser.setFriends(user.getFriends());
                    newUser.setFriendSolicitation(user.getFriendSolicitation());
                    newUser.setMessageBox(user.getMessageBox());
                    for(Map.Entry<String, String> entry : user.getAttributes().entrySet()){
                        newUser.setAttributes(entry.getKey(), entry.getValue());}
                }
            }
        } catch (IOException e){
            System.err.println("Erro ao carregar dados do JSON.");
            e.printStackTrace();
        }
    }
    /**
     * Cria um novo usu�rio com as informa��es fornecidas e o adiciona ao sistema.
     *
     * @param login O login do novo usu�rio.
     * @param senha A senha do novo usu�rio.
     * @param nome O nome do novo usu�rio.
     * @throws RuntimeException Se o login ou a senha forem inv�lidos ou se um usu�rio com o mesmo login j� existir.
     */
    public void criarUsuario(String login, String senha, String nome){
        if (!users.containsKey(login)) {
            if(login == null) throw new RuntimeException("Login inv�lido.");
            if (senha == null) throw new RuntimeException("Senha inv�lida.");
            User user = new User(login, senha, nome);
            users.put(login, user);
        }
        else throw new RuntimeException("Conta com esse nome j� existe.");
    }
    /**
     * Obt�m o valor de um atributo espec�fico para um usu�rio desejado a partir de seu login.
     *
     * @param login O login do usu�rio.
     * @param atributo O atributo desejado ("nome", "senha", "login" ou atributo extra).
     * @return O valor do atributo solicitado.
     * @throws RuntimeException Se o usu�rio n�o for encontrado.
     */
    public String getAtributoUsuario(String login, String atributo){
        if(users.get(login) != null){
            if (Objects.equals(atributo, "nome")) return users.get(login).getName();
            else if (Objects.equals(atributo, "senha")) return users.get(login).getPassword();
            else if (Objects.equals(atributo, "login")) return users.get(login).getLogin();
            else return users.get(login).getExtraAttribute(atributo);
        }
        else{ throw new RuntimeException(USER_NOT_FOUND);}
    }

    /**
     * Abre uma sess�o para um usu�rio autenticado.
     *
     * @param login O login do usu�rio.
     * @param senha A senha do usu�rio.
     * @return O ID da sess�o.
     * @throws RuntimeException Se o login ou a senha forem inv�lidos.
     */
    public String abrirSessao (String login, String senha) {
        User user = users.get(login);
        if(user != null && user.verificarSenha(senha)){
            String id = generateSessionId(login);
            sessions.put(id, user);
            return id;
        }
        else throw new RuntimeException("Login ou senha inv�lidos.");
    }
    /**
     * Gera um ID �nico de sess�o combinando o login e o momento da cria��o.
     *
     * @param login O login do usu�rio.
     */
    private String generateSessionId(String login) {
        long timestamp = System.currentTimeMillis();
        return login + "_" + timestamp;
    }
    /**
     * Edita o perfil de um usu�rio autenticado.
     *
     * @param Id O ID da sess�o do usu�rio.
     * @param atributo O atributo a ser editado ("nome", "senha", "login" ou atributo extra).
     * @param valor O novo valor para o atributo.
     * @throws RuntimeException Se o usu�rio n�o for encontrado.
     */
    public void editarPerfil(String Id, String atributo, String valor){
        User user = sessions.get(Id);
        if (user != null) {
            if (Objects.equals(atributo, "nome")) user.setName(valor);
            else if (Objects.equals(atributo, "senha")) user.setPassword(valor);
            else if (Objects.equals(atributo, "login")) {
                if (!users.containsKey(valor)) {
                    user.setLogin(valor);
                } else throw new RuntimeException("Login inv�lido.");
            }
            else user.setExtraAttributes(atributo, valor);
        }
        else throw new RuntimeException(USER_NOT_FOUND);
    }

    /**
     * Verifica se um usu�rio � amigo de outro usu�rio.
     *
     * @param login O login do primeiro usu�rio.
     * @param amigo O login do segundo usu�rio.
     * @return `true` se forem amigos, `false` caso contr�rio.
     */
    public boolean ehAmigo(String login, String amigo){
        return users.get(login).getFriends().contains(amigo);
    }

    /**
     * Adiciona um usu�rio autenticado como amigo de outro usu�rio enviando uma solicita��o, deve ser confirmada
     * pelo recebedor do pedido.
     *
     * @param id O ID da sess�o do usu�rio.
     * @param amigo O login do amigo a ser adicionado.
     * @throws RuntimeException Se algum dos usu�rios n�o for encontrado.
     */
    public void adicionarAmigo(String id, String amigo){
        User user = sessions.get(id);
        if (user == null)throw new RuntimeException(USER_NOT_FOUND);
        User friendUser = users.get(amigo);
        if (friendUser != null) {
            if (Objects.equals(user.getLogin(), amigo))
                throw new RuntimeException("Usu�rio n�o pode adicionar a si mesmo como amigo.");
            else if (user.getFriendSolicitation().contains(amigo)) {
                user.addFriends(amigo);
                friendUser.addFriends(user.getLogin());
            } else if (friendUser.getFriendSolicitation().contains(user.getLogin()))
                throw new RuntimeException("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
            else if (ehAmigo(user.getLogin(), amigo))
                throw new RuntimeException("Usu�rio j� est� adicionado como amigo.");
            else {
                friendUser.addFriendSolicitation(user.getLogin());
            }
        }
        else{ throw new RuntimeException(USER_NOT_FOUND);}
    }
    /**
     * Obt�m a lista de amigos de um usu�rio.
     *
     * @param login O login do usu�rio.
     * @return Uma representa��o da lista de amigos.
     */
    public String getAmigos(String login){
        User user = users.get(login);
        ArrayList<String> friends = user.getFriends();
        return friends.isEmpty() ? "{}" : "{" + String.join(",", friends) + "}";
    }
    /**
     * Envia um recado de um usu�rio para outro.
     *
     * @param id O ID da sess�o do remetente.
     * @param destinatario O login do destinat�rio do recado.
     * @param mensagem O conte�do da mensagem.
     * @throws RuntimeException Se os usu�rios n�o forem encontrados.
     */
    public void enviarRecado(String id, String destinatario, String mensagem){
        User sender = sessions.get(id);
        User receiver = users.get(destinatario);
        if (sender == receiver) throw new RuntimeException("Usu�rio n�o pode enviar recado para si mesmo.");
        if(sender != null){
            if(receiver != null){
                Recado recado = new Recado(sender.getLogin(), mensagem);
                receiver.receiveMessage(recado);
            }
            else throw new RuntimeException(USER_NOT_FOUND);
        }
        else throw new RuntimeException(USER_NOT_FOUND);
    }
    /**
     * L� o primeiro recado da caixa de mensagens de um usu�rio.
     *
     * @param id O ID da sess�o do usu�rio.
     * @return O conte�do do recado lido.
     * @throws RuntimeException Se n�o houver recados na caixa de mensagens.
     */
    public String lerRecado(String id){
        User user = sessions.get(id);
        Recado recado = user.getMessageBox().poll();
        if(recado == null) throw new RuntimeException("N�o h� recados.");
        else return recado.getMensagem();

    }
    /**
     * Encerra o sistema, salvando os dados em um arquivo JSON.
     */
    public void encerrarSistema() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File usersData = new File("usuarios.json");

            List<User> listaUsuarios = new ArrayList<>(users.values());

            objectMapper.writeValue(usersData, listaUsuarios);

            System.out.println("Todos os dados foram salvos.");
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados.");
            e.printStackTrace();
        }
    }
}

