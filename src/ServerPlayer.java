import java.nio.channels.SocketChannel;

public class ServerPlayer {
    private String name;
    private SocketChannel socket;
    private GameLobby lobby;
    private boolean lobbySearch;
    private int id = -1;
    private boolean ready = false;
    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }


    private boolean isHost;
    public ServerPlayer(SocketChannel socket, String name){
        this.socket = socket;
        this.name = name;
        lobbySearch = false;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SocketChannel getSocket() {
        return socket;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }
    public GameLobby getLobby() {
        return lobby;
    }

    public void setLobby(GameLobby lobby) {
        this.lobby = lobby;
        this.lobby.addClient(this);
    }
    public void removeLobby(){
        this.lobby = null;
    }
    public void setLobbySearch(boolean lobbySearch) {
        this.lobbySearch = lobbySearch;
    }
    public boolean getLobbySearch() {
        return lobbySearch;
    }
    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
