package nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NioTest {

    @Test
    public void 文件管道测试写() throws IOException {
        FileOutputStream fos = new FileOutputStream("data.txt");
        FileChannel channel = fos.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put("慢慢的酸臭味！！！".getBytes());
        byteBuffer.flip();
        channel.write(byteBuffer);
        channel.close();
        System.out.println("写出完毕");
    }

    @Test
    public void 文件管道测试读() throws IOException {
        FileInputStream ins = new FileInputStream("data.txt");
        FileChannel channel = ins.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        channel.read(byteBuffer);
        byteBuffer.flip();
        //byteBuffer.remaining()  因为flip后重置了。position  所有这个返回值就是元素的总长度
        System.out.println(new String(byteBuffer.array(),0,byteBuffer.remaining()));
        channel.close();

    }

    @Test
    public  void 文件复制() throws IOException {
        FileInputStream fis = new FileInputStream("data.txt");
        FileOutputStream fos = new FileOutputStream("data2.txt");
        FileChannel channelread = fis.getChannel();
        FileChannel channelwrite = fos.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int len = -1;
        while ((len = channelread.read(byteBuffer))!=-1){
            byteBuffer.flip();
            channelwrite.write(byteBuffer,byteBuffer.limit());
            byteBuffer.clear(); //清空所有的标记为都会自动重置
        }
        channelwrite.close();
        channelread.close();
        System.out.println("拷贝完了，我的👶🏻");
    }

    @Test
    public  void 文件复制2() throws IOException {
        FileInputStream fis = new FileInputStream("data.txt");
        FileOutputStream fos = new FileOutputStream("data3.txt");
        FileChannel channelread = fis.getChannel();
        FileChannel channelwrite = fos.getChannel();
        channelread.transferTo(0,channelread.size(),channelwrite);
//        channelwrite.transferFrom(channelread,0,channelread.size());
        //这两种方式都是可行的
        channelwrite.close();
        channelread.close();
        System.out.println("拷贝完了，我的👶🏻");
    }

    @Test
    public void 分散与聚集() throws IOException {
        FileInputStream ins = new FileInputStream("data.txt");
        FileChannel channel = ins.getChannel();
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(3);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(100);
        ByteBuffer[] byteBuffers = new ByteBuffer[]{byteBuffer1,byteBuffer2};
        channel.read(byteBuffers);

        byteBuffer1.flip();
        System.out.println(new String(byteBuffer1.array(),0,byteBuffer1.limit()));
        byteBuffer2.flip();
        System.out.println(new String(byteBuffer2.array(),0,byteBuffer2.limit()));

        FileOutputStream fos = new FileOutputStream("data4.txt");
        FileChannel channel1 = fos.getChannel();
        channel1.write(byteBuffers);
        channel1.close();
        channel.close();
    }

    @Test
    public void NIO服务端() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false); //设置为阻塞状态
        ServerSocketChannel bind = serverSocketChannel.bind(new InetSocketAddress(9998));

        Selector selector = Selector.open();
        int clients = 0;
        //serversocketChanel 注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //这个select 会轮训时间，有时间才会放回不然会阻塞
        while (selector.select() > 0){
            //获取注册好的，并且监听状态到达的键
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
           // System.out.println("selectedKeys.size:"+selector.selectedKeys().size());
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //System.out.println("key:"+key.toString());
                if(key.isAcceptable()){ //接受就绪
                    //接受SocketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //设置socketChanel 为非阻塞模式
                    socketChannel.configureBlocking(false);
                    //将socketChannel 注册到 选择器上，并监听 读操作
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    System.out.println("访问连接人数："+  ++clients);
                } else  if (key.isReadable() ){
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer  byteBuffer = ByteBuffer.allocate(1024);
                    int len = -1 ;
                    while ((len = socketChannel.read(byteBuffer)) != -1){
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(),0,byteBuffer.limit()));
                        byteBuffer.clear();
                    }
                }
                //移除处理过的事件
                iterator.remove();

            }
        }

        //这句话不会被打印出来
        System.out.println("不阻塞");
    }


    @Test
    public void NIO客户端() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(9998));
        socketChannel.configureBlocking(false);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("今天吃多了ddd".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        //socketChannel.close();
        byteBuffer.clear();
    }




}
