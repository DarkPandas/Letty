package com.codeagles.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;


/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/3/29
 * Time: 下午5:40
 * <p>
 * Description: netty处理类
 */
public class ChatNettyHandler extends SimpleChannelInboundHandler<ByteBuf> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String content = new String(bytes, "UTF-8");
        System.out.println(content);

        if(content.equalsIgnoreCase("quit")){
            channelHandlerContext.channel().close();
        }else{
            ChatHandler.propagate((SocketChannel) channelHandlerContext.channel(),content);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("one conn active:"+ ctx.channel());
        //channel是在ServerBootstrapAcceptor中放到EventLoopGroup中
        ChatHandler.join((SocketChannel) ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("one conn inactive:"+ ctx.channel());
        ChatHandler.quit((SocketChannel) ctx.channel());
    }
}
