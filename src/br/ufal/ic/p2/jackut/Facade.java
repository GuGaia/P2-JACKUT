package br.ufal.ic.p2.jackut;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import java.io.*;
/**
 * A classe Facade oferece uma interface para gerenciar usuários e suas interações dentro sistema.
 *
 * @author Gustavo Gaia
 */
public class Facade {
    /**
     * Mensagem de erro que indica que um usuário não foi encontrado no sistema.
     */
    public static final String USER_NOT_FOUND= "Usuário não cadastrado.";
    private Map<String, User> users; // Mapa para armazenar usuários
    private Map<String, User> sessions; // Mapa para armazenar sessões

    /**
     * Construtor da classe Facade.
     * Inicializa os mapas de usuários e sessões e carrega os dados existente do sistema, se disponíveis.
     */
    public Facade() {
        this.users = new HashMap<>();
        this.sessions = new HashMap<>();
        carregarSistema();
    }
    /**
     * Método zerarSistema exclui todos os dados do sistema, limpando os mapas de usuários, sessões e arquivo de dados.
     */
    public void zerarSistema(){
        users.clear();
        sessions.clear();
        File usersData = new File("usuarios.json");
        usersData.delete();
    }
    /**
     * Carrega os dados do sistema a partir de um arquivo JSON, se ele existir, é inicializado no construtor.
     * Os dados carregados incluem usuários, amigos, solicitações de amizade e mensagens.
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
     * Cria um novo usuário com as informações fornecidas e o adiciona ao sistema.
     *
     * @param login O login do novo usuário.
     * @param senha A senha do novo usuário.
     * @param nome O nome do novo usuário.
     * @throws RuntimeException Se o login ou a senha forem inválidos ou se um usuário com o mesmo login já existir.
     */
    public void criarUsuario(String login, String senha, String nome){
        if (!users.containsKey(login)) {
            if(login == null) throw new RuntimeException("Login inválido.");
            if (senha == null) throw new RuntimeException("Senha inválida.");
            User user = new User(login, senha, nome);
            users.put(login, user);
        }
        else throw new RuntimeException("Conta com esse nome já existe.");
    }
    /**
     * Obtém o valor de um atributo específico para um usuário desejado a partir de seu login.
     *
     * @param login O login do usuário.
     * @param atributo O atributo desejado ("nome", "senha", "login" ou atributo extra).
     * @return O valor do atributo solicitado.
     * @throws RuntimeException Se o usuário não for encontrado.
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
     * Abre uma sessão para um usuário autenticado.
     *
     * @param login O login do usuário.
     * @param senha A senha do usuário.
     * @return O ID da sessão.
     * @throws RuntimeException Se o login ou a senha forem inválidos.
     */
    public String abrirSessao (String login, String senha) {
        User user = users.get(login);
        if(user != null && user.verificarSenha(senha)){
            String id = generateSessionId(login);
            sessions.put(id, user);
            return id;
        }
        else throw new RuntimeException("Login ou senha inválidos.");
    }
    /**
     * Gera um ID único de sessão combinando o login e o momento da criação.
     *
     * @param login O login do usuário.
     */
    private String generateSessionId(String login) {
        long timestamp = System.currentTimeMillis();
        return login + "_" + timestamp;
    }
    /**
     * Edita o perfil de um usuário autenticado.
     *
     * @param Id O ID da sessão do usuário.
     * @param atributo O atributo a ser editado ("nome", "senha", "login" ou atributo extra).
     * @param valor O novo valor para o atributo.
     * @throws RuntimeException Se o usuário não for encontrado.
     */
    public void editarPerfil(String Id, String atributo, String valor){
        User user = sessions.get(Id);
        if (user != null) {
            if (Objects.equals(atributo, "nome")) user.setName(valor);
            else if (Objects.equals(atributo, "senha")) user.setPassword(valor);
            else if (Objects.equals(atributo, "login")) {
                if (!users.containsKey(valor)) {
                    user.setLogin(valor);
                } else throw new RuntimeException("Login inválido.");
            }
            else user.setExtraAttributes(atributo, valor);
        }
        else throw new RuntimeException(USER_NOT_FOUND);
    }

    /**
     * Verifica se um usuário é amigo de outro usuário.
     *
     * @param login O login do primeiro usuário.
     * @param amigo O login do segundo usuário.
     * @return `true` se forem amigos, `false` caso contrário.
     */
    public boolean ehAmigo(String login, String amigo){
        return users.get(login).getFriends().contains(amigo);
    }

    /**
     * Adiciona um usuário autenticado como amigo de outro usuário enviando uma solicitação, deve ser confirmada
     * pelo recebedor do pedido.
     *
     * @param id O ID da sessão do usuário.
     * @param amigo O login do amigo a ser adicionado.
     * @throws RuntimeException Se algum dos usuários não for encontrado.
     */
    public void adicionarAmigo(String id, String amigo){
        User user = sessions.get(id);
        if (user == null)throw new RuntimeException(USER_NOT_FOUND);
        User friendUser = users.get(amigo);
        if (friendUser != null) {
            if (Objects.equals(user.getLogin(), amigo))
                throw new RuntimeException("Usuário não pode adicionar a si mesmo como amigo.");
            else if (user.getFriendSolicitation().contains(amigo)) {
                user.addFriends(amigo);
                friendUser.addFriends(user.getLogin());
            } else if (friendUser.getFriendSolicitation().contains(user.getLogin()))
                throw new RuntimeException("Usuário já está adicionado como amigo, esperando aceitação do convite.");
            else if (ehAmigo(user.getLogin(), amigo))
                throw new RuntimeException("Usuário já está adicionado como amigo.");
            else {
                friendUser.addFriendSolicitation(user.getLogin());
            }
        }
        else{ throw new RuntimeException(USER_NOT_FOUND);}
    }
    /**
     * Obtém a lista de amigos de um usuário.
     *
     * @param login O login do usuário.
     * @return Uma representação da lista de amigos.
     */
    public String getAmigos(String login){
        User user = users.get(login);
        ArrayList<String> friends = user.getFriends();
        return friends.isEmpty() ? "{}" : "{" + String.join(",", friends) + "}";
    }
    /**
     * Envia um recado de um usuário para outro.
     *
     * @param id O ID da sessão do remetente.
     * @param destinatario O login do destinatário do recado.
     * @param mensagem O conteúdo da mensagem.
     * @throws RuntimeException Se os usuários não forem encontrados.
     */
    public void enviarRecado(String id, String destinatario, String mensagem){
        User sender = sessions.get(id);
        User receiver = users.get(destinatario);
        if (sender == receiver) throw new RuntimeException("Usuário não pode enviar recado para si mesmo.");
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
     * Lê o primeiro recado da caixa de mensagens de um usuário.
     *
     * @param id O ID da sessão do usuário.
     * @return O conteúdo do recado lido.
     * @throws RuntimeException Se não houver recados na caixa de mensagens.
     */
    public String lerRecado(String id){
        User user = sessions.get(id);
        Recado recado = user.getMessageBox().poll();
        if(recado == null) throw new RuntimeException("Não há recados.");
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

