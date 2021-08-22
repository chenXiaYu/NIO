package bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

import static bio.BIOTest.PORT;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost",PORT));
            OutputStream outputStream = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
            while (true){
                System.out.println("请输入信息：");
                String msg = scanner.nextLine();
                if(!"".equals(msg)){
                    if("\\quit".equals(msg)){
                        System.out.println("准备退出...");
                        socket.close();
                    }
                    System.out.println("正在投递信息~~~"+msg);
                    outputStream.write(msg.getBytes());
                    outputStream.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
