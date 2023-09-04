package Main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class SampleClient {
    public static void main(String[] args) {
        String message = "asdasdasd";
        System.out.println(Arrays.toString(message.split("\r\n")));


        SocketChannel socket = null;
        try {
            socket = SocketChannel.open();
            SocketChannel finalSocket = socket;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    finalSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
//            100.20.92.101
//            44.225.181.72
//            44.227.217.144
            socket.socket().connect((new InetSocketAddress("127.0.0.1", 60000)));

           //PrintWriter out = new PrintWriter(socket.write(), true);
            int i = 0;
//            while (i!=1000){
//                i = i+1;
//                socket.write(ByteBuffer.wrap((i+"\r\n").getBytes()) );
//                Thread.sleep(1000);
//            }
            socket.write(ByteBuffer.wrap(("Hello"+"\r\n" + "How are you doing").getBytes()) );
        }
        catch (IOException e){
            System.err.println("IO exception called on sockets");
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        }


    }
}
