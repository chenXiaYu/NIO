package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false); //设置为阻塞状态
        ServerSocketChannel bind = serverSocketChannel.bind(new InetSocketAddress(9998));

        Selector selector = Selector.open();
        int clients = 0;
        //serversocketChanel 注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

       while (true){
           //这个select 会轮训时间，有时间才会放回不然会阻塞
           while (selector.select() > 0){
               //获取注册好的，并且监听状态到达的键
               Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
               // System.out.println("selectedKeys.size:"+selector.selectedKeys().size());
               int i = 0 ;
               while (iterator.hasNext()){
                   i ++;
                   SelectionKey key = iterator.next();
                   //System.out.println("key:"+key.toString());
                   if(key.isAcceptable()){ //接受就绪
                       //接受SocketChannel
                       SocketChannel socketChannel = serverSocketChannel.accept();
                       //设置socketChanel 为非阻塞模式
                       socketChannel.configureBlocking(false);
                       //将socketChannel 注册到 选择器上，并监听 读操作
                       socketChannel.register(selector,SelectionKey.OP_READ|SelectionKey.OP_CONNECT);
                       System.out.println("访问连接人数："+  ++clients);
                   } else  if (key.isReadable() ){
                       SocketChannel socketChannel = (SocketChannel) key.channel();
                       ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                       int len = -1 ;
                       while ((len = socketChannel.read(byteBuffer)) != -1){
                           byteBuffer.flip();
                           String s = new String(byteBuffer.array(), 0, byteBuffer.limit());
                           //关闭的时候估计发了些空消息
                           if(s != null &&  !"".equals(s)){
                               System.out.println(s);
                           }
                           byteBuffer.clear();
                       }
                   }

                   //关闭来取消对事件的监控，不然下次轮训还是会有多个迭代
                   if(!key.isConnectable()){
                        if( key.channel() instanceof  SocketChannel){
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            socketChannel.close();
                            System.out.println("有人离开，总数为："+ --clients);
                        }
                   }

                   //移除处理过的事件
                   iterator.remove();

               }
               System.out.println("时间迭代次数"+ i);
               i = 0;
           }
       }

    }
}
