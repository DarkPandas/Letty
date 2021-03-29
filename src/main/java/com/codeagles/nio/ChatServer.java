package com.codeagles.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * @author : Codeagles
 * Date: 2021/3/29
 * Time: 下午1:50
 * <p>
 * Description: 基于NIO聊天 服务端程序
 */
public class ChatServer {
    public static void main(String[] args) throws IOException {
        //创建NIO的selector
        Selector selector = Selector.open();
        //创建ServerSocketChannel进行通信
        ServerSocketChannel serverSocketChannel= ServerSocketChannel.open();
        //绑定通信监听端口
        serverSocketChannel.bind(new InetSocketAddress(8080));
        //设置非阻塞模式-NIO是同步非阻塞IO
        serverSocketChannel.configureBlocking(false);
        //将channel注册到selector上，并注册accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //至此服务端开启
        System.out.println("chat server start...");

        while(true){
            //阻塞在select
            selector.select();
            // 如果使用的是select(timeout)或selectNow()需要判断返回值是否大于0
            // 有就绪的Channel
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //遍历keys
            Iterator<SelectionKey> iterable = selectionKeys.iterator();
            while(iterable.hasNext()){
                SelectionKey selectionKey = iterable.next();
                //如果是accept事件
                if(selectionKey.isAcceptable()){
                    ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = ssc.accept();
                    System.out.println("accept new conn: "+ socketChannel.getRemoteAddress());
                    socketChannel.configureBlocking(false);
                    // 将SocketChannel注册到Selector上，并注册读事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    //加入群聊
                    ChatHandler.join(socketChannel);
                }else if(selectionKey.isReadable()){
                    //如果是读取事件
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    //将数据读取到buffer中
                    int length = socketChannel.read(byteBuffer);
                    if(length > 0){
                        byteBuffer.flip();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        //将数据读取到byte数组中
                        byteBuffer.get(bytes);

                        //由于换行符会一起传送过来，做替换
                        String content = new String(bytes, "UTF-8").replace("\r\n", "");
                        if(content.equalsIgnoreCase("quit")){
                            //退出群聊
                            ChatHandler.quit(socketChannel);
                            selectionKey.cancel();
                            socketChannel.close();
                        }else{
                            //扩散
                            ChatHandler.propagate(socketChannel, content);
                        }
                    }
                }
                iterable.remove();

            }
        }

    }
}
