package com.codeagles.marshalling;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/5/2
 * Time: 下午9:21
 * <p>
 * Description:
 */
public class Server {

    public static void main(String[] args) throws InterruptedException {

        EventLoopGroup bGroup = new NioEventLoopGroup();
        EventLoopGroup wGroup = new NioEventLoopGroup();

        ServerBootstrap bs = new ServerBootstrap();
        bs.group(bGroup, wGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        ch.pipeline().addLast(new ServerHandler());
                    }
                });
        ChannelFuture cf = bs.bind(8765).sync();
        cf.channel().closeFuture().sync();
        bGroup.shutdownGracefully();
        wGroup.shutdownGracefully();

    }
}
