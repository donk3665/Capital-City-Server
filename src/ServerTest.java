//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.Set;
//
//public class ServerTest {
//
//
//    public static void main(String[] args) throws IOException {
//        Selector selector = Selector.open();
//        ServerSocketChannel serverSocket = ServerSocketChannel.open();
//        serverSocket.socket().bind(new InetSocketAddress(60000));
//        serverSocket.configureBlocking(false);
//        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
//
//        while (true) {
//            selector.select();
//            Set<SelectionKey> selectedKeys = selector.selectedKeys();
//            Iterator<SelectionKey> iter = selectedKeys.iterator();
//            while (iter.hasNext()) {
//
//                SelectionKey key = iter.next();
//
//                if (key.isAcceptable()) {
//                    register(selector, serverSocket);
//                }
//
//                if (key.isReadable()) {
//                    handleReadableKeys(key);
//                }
//                iter.remove();
//            }
//        }
//    }
//    static ByteBuffer byteBuffer = ByteBuffer.allocate(256);
//
//    public static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
//        SocketChannel client = serverSocket.accept();
//        client.configureBlocking(false);
//        client.register(selector, SelectionKey.OP_READ);
//    }
//    public static void handleReadableKeys(SelectionKey key) throws IOException {
//        SocketChannel channel = (SocketChannel) key.channel();
//        int numRead = channel.read(byteBuffer);
//        if (numRead == -1){
//            channel.close();
//            return;
//        }
//        byteBuffer.flip();
//        byte[] read;
//        int maxCounter = 0;
//        for (int i = 0; i<byteBuffer.limit()-1; i++) {
//            if (byteBuffer.get(i) == 13 &&byteBuffer.get(i+1) == 10){
//                maxCounter = i;
//            }
//        }
//        read = new byte[maxCounter+2];
//        byteBuffer.get(read);
//        System.out.println(new String(read));
//        byteBuffer.compact();
//
//        System.out.println(byteBuffer.position());
//    }
//
//}
//
