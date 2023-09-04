import java.util.ArrayList;

public class LobbyInputOutputHandler extends InputOutputHandler {


    @Override
    void handleInput(ServerPlayer player, ArrayList<ServerPlayer> clients) {
        String messages = readPlayer(player);
        System.out.println("Received message: " + messages);
        if (messages == null){
            player.getLobby().removePlayer(player);
            return;
        }
        String [] splitMessages = messages.split("\r\n");
        for (String message: splitMessages ){
            handleMessage(message, clients, player);
        }

    }
    private void handleMessage(String message, ArrayList<ServerPlayer> clients, ServerPlayer player ){
        String[] splitMessage = message.split("\s+");

        switch (splitMessage[0]) {
            case "TEXT_MESSAGE" -> {
                splitMessage = message.split("¶");
                String chatText = splitMessage[1];
                String writerIndex = splitMessage[2];
                String writerName = splitMessage[3];
                sendMessageToAllButClient("RECEIVE_TEXT ¶" + chatText + "¶" + writerIndex + "¶" + writerName, clients, player);
            }
            case "DISCONNECT" ->{
                player.getLobby().removePlayer(player);
            }
            case "LOAD_DATA", "STATES", "CHANGE_PLAYER","INFO:","SWAP", "INIT_AUCTION", "AUCTION", "BUILD", "MORTGAGE", "UN-MORTGAGE", "COMPLETE","BANKRUPTCY" ->{
                sendMessageToAllButClient(message, clients, player);
            }
            case "INITIALIZE_GAME"->{
                sendMessageToAllButClient("INITIALIZE_GAME " + player.getLobby().getInfo().isSavedGame(), clients, player);
            }
            case "READY" ->{
                player.setReady(true);
                if (readyCheck(clients)){
                    sendMessageToAll("BEGIN", clients);
                    sendMessageToAll("UNLOCK_CHATS", clients);
                }
            }
            case "CAN_START?" ->{
                if (player.getLobby().isReady()){
                    sendMessageToClient(player, "BEGIN_SEQUENCE");
                }
                else {
                    sendMessageToClient(player, "NOT_READY");
                }
            }
            case "TRADE", "CANCEL_TRADE", "TRADE_ACCEPT", "TRADE_DECLINE", "TRADE_CANCELLED" ->{
                sendMessageToClient(clients.get(Integer.parseInt(splitMessage[1])), message);
            }
        }
    }
    private boolean readyCheck(ArrayList<ServerPlayer> clients){
        boolean ready = true;
        for (ServerPlayer player: clients){
            if (!player.isReady()) {
                ready = false;
                break;
            }
        }
        return ready;
    }
}
