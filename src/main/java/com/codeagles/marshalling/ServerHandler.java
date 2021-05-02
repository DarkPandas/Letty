package com.codeagles.marshalling;

import com.codeagles.util.GzipUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/5/2
 * Time: 下午9:24
 * <p>
 * Description:
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接受request请求，并进行业务处理
        RequestData rd = (RequestData) msg;
        System.out.println("id:"+ rd.getId() + ",name: "+ rd.getName()+", message:"+ rd.getRequestMessage());
        byte[] ungzip = GzipUtils.ungzip(rd.getAttachment());
//        String path = System.getProperty("users.dir")
//                + File.separatorChar + "receive" + File.separatorChar + "2.png";
        String path = "/Users/codeagles/IdeaProjects/new/glory-admin/Letty/receive/2.png";


        FileOutputStream fos = new FileOutputStream(path);
        fos.write(ungzip);
        fos.close();

        //回送相应数据
        ResponseData responseData = new ResponseData();
        responseData.setId("response:"+ rd.getId());
        responseData.setName("response:"+ rd.getName());
        responseData.setResponseMessage("response:"+ rd.getRequestMessage());

        ctx.writeAndFlush(responseData);

    }
}
