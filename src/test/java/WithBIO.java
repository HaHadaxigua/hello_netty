import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WithBIO {
    public static void main(String[] args) throws IOException {
        ExecutorService pond = Executors.newFixedThreadPool(100);
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8888));
        while(!Thread.currentThread().isInterrupted()){
            Socket socket = serverSocket.accept();
            pond.submit(new ConnectIOnHandler(socket));//为新的连接创建新的线程
        }
    }
    static class ConnectIOnHandler extends Thread {
        private Socket socket;
        public ConnectIOnHandler(Socket socket){
            this.socket = socket;
        }
        public void run()  {
            while(!Thread.currentThread().isInterrupted()&&!socket.isClosed()){ //死循环处理读写事件
                try {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream =  socket.getOutputStream();
                    int readMsg  = inputStream.read();//读取数据    返回的是0-255的ascii码，
                    while(readMsg!=-1){               // -1说明读到了结尾
                        System.out.println((char) readMsg);
                    }
                    String sendMsg = LocalDateTime.now().toString();
                    outputStream.write(sendMsg.getBytes()); // 向socket中写数据
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
