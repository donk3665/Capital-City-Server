package Main;

import java.util.ArrayList;

public class GameLobby {

    private ArrayList<ServerPlayer> clients;
    private LobbyInfo info;
    private InputOutputHandler handler;

    public GameLobby(LobbyInfo info){
        clients = new ArrayList<>();
        this.info = info;
        handler = new LobbyInputOutputHandler();
    }
    public ServerPlayer getHost(){
        for (ServerPlayer player: clients){
            if (player.isHost()){
                return player;
            }
        }
        return null;
    }
    public boolean isReady(){
        return clients.size() == info.getMaxNumPlayers() || !info.isSavedGame();
    }
    public void addClient(ServerPlayer client){
        clients.add(client);
    }
    public LobbyInfo getInfo() {
        return info;
    }
    public void handleInput(ServerPlayer player){
        handler.handleInput(player, clients);
    }
    public boolean closeLobby(){
        return clients.size() == 0;
    }
    public int getNumPlayers(){
        return clients.size();
    }
    private void informPlayersLobbyInfo(){
        String message = "CURRENT_LOBBY " + getNumPlayers();
        handler.sendMessageToAll(message,clients);
    }
    public boolean addPlayerAndSetup(ServerPlayer player){
        int id = info.getFreeID();
        if (id == -1){
            return false;
        }
        player.setLobby(this);
        player.setId(id);

        sendClientInfo(player);
        informPlayersLobbyInfo();
        //handler.sendMessageToClient(player, "CLIENT_INFO " + player.getName() + " " + player.getId());
        return true;
    }
    public void sendClientInfo(ServerPlayer player){
        handler.sendMessageToClient(player, "CLIENT_INFO " + player.getName() + " " + player.getId() + " " + getInfo().getLobbyID());
    }
    private void reassignIDs(int id){
        if (clients.size() == 0){
            return;
        }
        int index = 0;
        for (int i = 1; i< clients.size(); i++){
            if (clients.get(i).getId()> clients.get(index).getId()){
                index = i;
            }
        }
        if (clients.get(index).getId() > id){
            clients.get(index).setId(id);
        }
        sendClientInfo(clients.get(index));
    }
    public void removePlayer(ServerPlayer player){
        if (clients.remove(player)) {
            int id = player.getId();
            info.freeID(id);
            player.setId(-1);
            if (player.isHost()) {
                kickPlayers();
            } else {
                reassignIDs(id);
                player.removeLobby();
                informPlayersLobbyInfo();
            }
        }
    }
    private void kickPlayers(){
        String message = "KICK";
        handler.sendMessageToAll(message,clients);
        for (ServerPlayer player: clients){
            clients.remove(player);
            player.removeLobby();
        }
    }
}
