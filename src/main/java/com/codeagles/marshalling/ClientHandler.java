package com.codeagles.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/5/2
 * Time: 下午9:46
 * <p>
 * Description:
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
           ResponseData rd = (ResponseData) msg;
            System.out.println("输出服务器相应内容："+ rd.getId());
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
