package bio;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class BIOTest {

    public static  final  int PORT = 9998;
    @Test
    public void 服务端() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        while (true){
            Socket accept = serverSocket.accept();
            new AcceptScoketHandler(accept).start();
        }
    }

    @Test
    public void 客户端(){
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost",PORT));
            String msg = "今晚约不？";
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg.getBytes());
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void  缓冲区复习(){
        ByteBuffer byteBuffer =  ByteBuffer.allocate(10); //分配是个字节
        System.out.println(byteBuffer.capacity()); // 10
        System.out.println(byteBuffer.limit()); // 10
        System.out.println(byteBuffer.position()); // 0
        System.out.println(byteBuffer.remaining()); // 10
        System.out.println("--------");
        String name = "chenxiayu";
        //放数据
        byteBuffer.put(name.getBytes(),0 , name.length());

        System.out.println(byteBuffer.capacity()); // 10
        System.out.println(byteBuffer.limit()); // 10
        System.out.println(byteBuffer.position()); // 4
        System.out.println(byteBuffer.remaining()); // 6
        System.out.println("----------");
        //取数据
        byte [] values = new byte[byteBuffer.position()];
        byteBuffer.flip();  //这个操作会把position回归到0  ，  会把mark 标记为-1
        System.out.println(byteBuffer.capacity()); // 10
        System.out.println(byteBuffer.limit()); // 4
        System.out.println(byteBuffer.position()); // 0
        System.out.println(byteBuffer.remaining()); // 4
        byteBuffer.get(values); // 全部读出来
        System.out.println(new String(values));
        System.out.println("-----------");
        byteBuffer.rewind();  //将位置设置为零 , 开始重置状态
        byteBuffer.limit(10);  //   limit / position  / mark 注意先后顺序
        byteBuffer.position(4);  // 这个标记影响到后续写的具体位置
        System.out.println("剩余大小:"+byteBuffer.remaining());
//        byteBuffer.mark(); //这个只是打标记
//        byteBuffer.reset(); 如果mark为-1则会报错   flip 后如果需要在这个对象继续写那个可以手动  rewind
        byteBuffer.put("sssf".getBytes(), 0, 3); //只放三个
        //注意这里放完后position 只会到 7  ，如果执行flip后续的数据不会读出来
        byteBuffer.position(10); //部分修改后全部读取可以把位置移到最后然后切换只读模式
        byteBuffer.flip();
        byte [] values2 = new byte[byteBuffer.limit()];
        System.out.println("剩余大小:"+byteBuffer.remaining());
        System.out.println(byteBuffer.position()); // 0
        System.out.println(byteBuffer.limit()); // 9
        byteBuffer.get(values2); // 全部读出来
        System.out.println(new String(values2));
    }
}
