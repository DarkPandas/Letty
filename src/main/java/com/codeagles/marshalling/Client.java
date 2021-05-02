package com.codeagles.marshalling;

import com.codeagles.util.GzipUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/5/2
 * Time: 下午9:44
 * <p>
 * Description:
 */
public class Client {

    public static void main(String[] args) throws Exception {

        NioEventLoopGroup wGroup = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();

        b.group(wGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        ChannelFuture future = b.connect("127.0.0.1", 8765).sync();
        Channel channel = future.channel();

        for(int i=0 ; i<100; i++){
            RequestData data = new RequestData();
            data.setId(""+i);
            data.setName("我是消息"+i);
            data.setRequestMessage("内容："+ i);
//            String path = System.getProperty("users.dir")
//                    + File.separatorChar + "source" + File.separatorChar + "2.png";
            String path = "/Users/codeagles/IdeaProjects/new/glory-admin/Letty/source/2.png";
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[fis.available()];
            fis.read(bytes);
            fis.close();
            data.setAttachment(GzipUtils.gzip(bytes));
            channel.writeAndFlush(data);

        }

        future.channel().closeFuture().sync();
        wGroup.shutdownGracefully();



    }
}
