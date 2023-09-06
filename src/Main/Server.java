package Main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server {
    private ArrayList<ServerPlayer> clients;
    private ArrayList<GameLobby> lobbies;
    private HashMap<SocketChannel,  ServerPlayer> playerHash;
    private HashMap<String, GameLobby> idSet;

    private static int clientCounter;

    public Server (){
        clients = new ArrayList<>();
        lobbies = new ArrayList<>();
        playerHash = new HashMap<>();
        idSet = new HashMap<>();
        clientCounter = 0;
        GameLobby.setServer(this);
    }

    public void run(){
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(60000));
            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {

                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        register(selector, serverSocket);
                    }

                    if (key.isReadable()) {
                        handleReadableKeys(key);


                    }
                    iter.remove();
                }
            }
        }
        catch (IOException e){
            System.err.println("IO exception called on sockets");
            System.exit(1);
        }
    }
    public void handleReadableKeys(SelectionKey key){
        GameLobby keyLobby = playerHash.get((SocketChannel)key.channel()).getLobby();
        if (keyLobby == null){
            setUpClientLobbies(key);
        }
        else {
            keyLobby.handleInput(playerHash.get((SocketChannel)key.channel()));
            if (keyLobby.closeLobby()){
                lobbies.remove(keyLobby);
                idSet.remove(keyLobby.getInfo().getLobbyID());
                informAllListenersLobby(keyLobby);
            }
        }
    }
    public void setUpClientLobbies(SelectionKey key){
        ServerPlayer player = playerHash.get((SocketChannel) key.channel());
        String messages = InputOutputHandler.readPlayer(player);
        System.out.println("Received message: " + messages);
        if (messages == null){
            return;
        }
        String [] splitMessages = messages.split("\r\n");
        for (String message: splitMessages) {
            String[] splitMessage = message.split("\s+");
            switch (splitMessage[0]) {
                case "HOST" -> {
                    LobbyInfo info = new LobbyInfo(Integer.parseInt(splitMessage[1]), Integer.parseInt(splitMessage[2]), splitMessage[3], splitMessage[4], generateUniqueId());
                    GameLobby lobby = new GameLobby(info);
                    lobby.addPlayerAndSetup(player);
                    player.setHost(true);
                    idSet.put(info.getLobbyID(), lobby);
                    lobbies.add(lobby);
                    informAllListenersLobby(lobby);
                }
                case "GET" -> {
                    informPlayerMessage(player, "SENDING_LOBBIES");
                    player.setLobbySearch(true);
                    informListener(player);
                }
                case "JOIN" -> {
                    boolean connectionSuccess = getLobbyFromID(splitMessage[1]).addPlayerAndSetup(player);
                    if (!connectionSuccess){
                        informPlayerMessage(player, "JOIN FAIL");
                    }
                    else {
                        player.setLobbySearch(false);
                        informAllListenersLobby(player.getLobby());
                    }
                }
            }
        }
    }
    public GameLobby getLobbyFromID(String id){
        for (GameLobby lobby: lobbies){
            if (lobby.getInfo().getLobbyID().equals(id)){
                return lobby;
            }
        }
        System.err.println("Could not find lobby");
        return null;
    }

    public void informListener(ServerPlayer player){
        for (GameLobby lobby: lobbies){
            if (!lobby.getInfo().isActive()){
                informPlayerLobby(lobby, player);
            }
        }
    }

    public void informPlayerLobby(GameLobby lobby, ServerPlayer player){
        LobbyInfo info = lobby.getInfo();
        String message = "LOBBY "+ lobby.getNumPlayers() + " "+info.getMaxNumPlayers() + " " + info.getNumOfRounds() + " " + info.getGameMode() + " " + info.getLobbyID();

        informPlayerMessage(player, message);
    }
    public void informPlayerMessage(ServerPlayer player, String message){
        System.out.println("Sent message: (" + message + ") to" + player.getName());
        try{
            writeToSocket(player.getSocket(), message);
        }
        catch (IOException e){
            System.err.println("Failed to write to player: "+player.getName());
        }
    }
    public void informAllListenersLobby(GameLobby lobby){
        for (ServerPlayer player: clients){
            if (player.getLobbySearch()){
                informPlayerLobby(lobby, player);
            }
        }
    }
    public void writeToSocket(SocketChannel socketChannel, String message) throws IOException{
        socketChannel.write(ByteBuffer.wrap((message+"\r\n").getBytes()));
    }
    public String generateUniqueId(){
        String id = null;
        while (id == null || idSet.containsKey(id)) {
            id = String.valueOf((int)(Math.random() * 10000000));
        }
        return id;
    }


    public void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        ServerPlayer player = new ServerPlayer(client, "Player"+clientCounter);
        clients.add(player);
        playerHash.put(client, player);
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        client.write(ByteBuffer.wrap(("CONNECTED\r\n").getBytes()));
        System.out.println("Client " + clientCounter + " connected");
        clientCounter+=1;
    }
    public void removePlayer(ServerPlayer player){
        clients.remove(player);
    }
    public void removeLobby(GameLobby lobby){
        lobbies.remove(lobby);
    }
}
