package com.codeagles.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * @author : Codeagles
 * Date: 2021/3/29
 * Time: 下午1:50
 * <p>
 * Description: 基于Netty-NIO聊天 服务端程序
 */
public class ChatNettyServer {
    public static void main(String[] args) throws IOException {
        //1.声明线程池，专人干专事
        //专门接受accept事件的线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //专门处理读写消息的线程池
        EventLoopGroup worksGroup = new NioEventLoopGroup();
        try {
        //2.创建服务端启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //3.设置线程池
        serverBootstrap.group(bossGroup, worksGroup)
                //4. 设置ServerSocketChannel类型(必须)
                .channel(NioServerSocketChannel.class)
                //5. 设置参数-可选
                .option(ChannelOption.SO_BACKLOG, 100)
                //设置Handler(可选)
                .handler(new LoggingHandler(LogLevel.INFO))
                //7. 设置SocketChannel对应的Handler
                .childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                    @Override
                    protected void initChannel(io.netty.channel.socket.SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new LoggingHandler(LogLevel.INFO));
                        channelPipeline.addLast(new ChatNettyHandler());
                    }
                });
            //8.绑定端口
            ChannelFuture cf = serverBootstrap.bind(8080).sync();
            // 9. 等待服务端监听端口关闭，这里会阻塞主线程
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            //10. 关闭线程池
            bossGroup.shutdownGracefully();
            worksGroup.shutdownGracefully();
        }
        //至此服务端开启
        System.out.println("chat server start...");



    }
}
