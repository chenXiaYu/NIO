package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;

public class Client {
    public static void main(String[] args) throws IOException {

        for(int i = 0 ; i < 10 ; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SocketChannel socketChannel = null;
                    try {
                        socketChannel = SocketChannel.open(new InetSocketAddress(9998));
                        socketChannel.configureBlocking(false);
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        byteBuffer.put("今天吃多了".getBytes());
                        byteBuffer.flip();
                        System.out.println("limit:"+byteBuffer.limit());
                        socketChannel.write(byteBuffer);
                        Random random = new Random();
                        int time = random.nextInt(10);
                        System.out.println("睡眠时间"+time+"秒");
                        try {
                            Thread.sleep(time*1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        socketChannel.close();
                        byteBuffer.clear();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

    }
}
