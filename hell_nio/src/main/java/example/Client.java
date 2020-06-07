package example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

public class Client {
    private InetSocketAddress SERVER_ADDRESS;
    private static ByteBuffer wBuffer;
    private static ByteBuffer rBuffer;
    private static Selector selector;
    private static final Charset charset = StandardCharsets.UTF_8;

    public static void main(String[] args) {
        new Client(8888);
    }

    public Client(int port) {
        wBuffer = ByteBuffer.allocate(512);
        rBuffer = ByteBuffer.allocate(512);
        SERVER_ADDRESS = new InetSocketAddress("localhost", port);
        init();
    }

    private void init() {
        try {
            selector = Selector.open();
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(SERVER_ADDRESS);
            System.out.println("is connected?"+socketChannel.isConnected());
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

    private void handle(SelectionKey selectionKey) {
        try {
            if (selectionKey.isConnectable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                if (socketChannel.isConnectionPending()) {
                    socketChannel.finishConnect();
                    System.out.println("完成连接");
                    // 新开一个线程 等待键盘输入
                    new Thread(() -> {
                        while(true){
                           try {
                               Scanner scanner = new Scanner(System.in);
                               String sendText = scanner.nextLine();
                               System.out.println("me:"+sendText);
                               wBuffer.put(charset.encode(sendText));
                               wBuffer.flip();
                               socketChannel.write(wBuffer);
                               wBuffer.clear();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                        }

                    }).start();
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
            }else if(selectionKey.isReadable()){
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                rBuffer.clear();
                int readCount = socketChannel.read(rBuffer);
                if(readCount>0){
                    String receivedMsg = new String(rBuffer.array(), 0 , readCount);
                    System.out.println("receive msg:"+receivedMsg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
