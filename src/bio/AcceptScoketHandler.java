package bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class AcceptScoketHandler extends  Thread {
    private Socket socket;
    private InputStream ins;
    public AcceptScoketHandler(Socket socket) throws IOException {
        this.socket = socket;
        ins = socket.getInputStream();
    }

    @Override
    public void run() {
        try {
            while (true){
                // ins.readAllBytes() 这个方法再次不可取，读的最大字节数为Int的最大值，没有这么多数据则会阻塞导致控制台没有输出】
                byte []  buf = new byte[1024];
                int len = -1 ;
                while ((len = ins.read(buf))!=-1){
                    System.out.println("服务端接收到消息："+new String(buf));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
