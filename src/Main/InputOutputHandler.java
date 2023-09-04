package Main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public abstract class InputOutputHandler {

    abstract void handleInput(ServerPlayer player, ArrayList<ServerPlayer> clients);
    public void sendMessageToClient(ServerPlayer client, String message){
        System.out.println("Sending message (" + message + ") to client " + client.getName() );
        try {
            client.getSocket().write(ByteBuffer.wrap((message + "\r\n").getBytes()));
        }
        catch (IOException e){
            System.err.println("Message failed to be sent to client " + client.getName());
        }
    }
    public void sendMessageToAll(String message, ArrayList<ServerPlayer> clients){
        for (ServerPlayer client: clients){
            sendMessageToClient(client, message);
        }
    }
    public void sendMessageToAllButClient(String message, ArrayList<ServerPlayer> clients, ServerPlayer player){
        for (ServerPlayer client: clients){
            if (client != player) {
                sendMessageToClient(client, message);
            }
        }
    }
    static ByteBuffer byteBuffer = ByteBuffer.allocate(256);

    public static String readPlayer(ServerPlayer player){

        SocketChannel client = player.getSocket();
        try {
            int numRead = client.read(byteBuffer);
            if (numRead == -1) {
                client.close();
                System.out.println("Client Disconnected!");
                return null;
            } else {
                byteBuffer.flip();
                byte[] read;
                int maxCounter = 0;
                for (int i = 0; i<byteBuffer.limit()-1; i++) {
                    if (byteBuffer.get(i) == 13 &&byteBuffer.get(i+1) == 10){
                        maxCounter = i;
                    }
                }
                read = new byte[maxCounter+2];
                byteBuffer.get(read);
                byteBuffer.compact();

                if (new String(read).equals("")){
                    return null;
                }


                return new String(read).trim();
            }
        }
        catch (IOException e){
            System.err.println("IOException when trying to read from key");
            System.exit(1);
            return null;
        }
    }

}
