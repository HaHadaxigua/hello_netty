package example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    private int port;
    private ByteBuffer wBuffer;
    private ByteBuffer rBuffer;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private static final Charset charset = StandardCharsets.UTF_8;
    private static HashMap<String, SocketChannel> clientMaps;

    public static void main(String[] args) {
        new Server(8888);
    }

    /**
     * 创建服务端对象的时候 先对其进行初始化
     */
    public Server(int port) {
        this.port = port;
        init();
    }

    private void init() {
        try {
            wBuffer = ByteBuffer.allocate(512);
            rBuffer = ByteBuffer.allocate(512);
            clientMaps = new HashMap<>();
            selector = Selector.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(inetSocketAddress);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("server is running");
            while (true) {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.forEach(selectionKey -> handle(selectionKey));
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理事件
     *
     * @param selectionKey
     */
    private void handle(SelectionKey selectionKey) {
        try {
            if (selectionKey.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel client = serverSocketChannel.accept();
                client.configureBlocking(false);
                client.register(selector, selectionKey.OP_READ);
                System.out.println("a client is connecting:"+getClientName(client));
                clientMaps.put(getClientName(client), client);
            }else if(selectionKey.isReadable()){
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                rBuffer.clear();
                int readCount = socketChannel.read(rBuffer);
                if(readCount>0){
                    rBuffer.flip();
                    String receiveMsg = String.valueOf(charset.decode(rBuffer));
                    System.out.println("receiveMsg:"+receiveMsg);
                    dispatch(socketChannel,receiveMsg);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void dispatch(SocketChannel socketChannel, String receiveMsg) {
        if(!clientMaps.isEmpty()){
            for(Map.Entry<String, SocketChannel> entry: clientMaps.entrySet()){
                SocketChannel temp = entry.getValue();
                if(!socketChannel.equals(temp)){
                    wBuffer.clear();
                    wBuffer.put(charset.encode(getClientName(socketChannel)+":"+receiveMsg ));
                    wBuffer.flip();
                    try {
                        temp.write(wBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getClientName(SocketChannel socketChannel) {
        Socket socket = socketChannel.socket();
        return "[" + socket.getInetAddress().toString().substring(1) + ":" + Integer.toHexString(socketChannel.hashCode()) + "]";


    }


}
