package com.codeagles.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.socket.SocketChannel;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created with IntelliJ IDEA.
 * @author codeagles
 * Date: 2021/3/29
 * Time: 下午1:46
 * <p>
 * Description:
 * 基于Netty-NIO聊天
 * 聊天处理类
 *
 */
public class ChatHandler {
    //存活的连接
    private static final Map<SocketChannel, String> USER_MAP = new ConcurrentHashMap<>();


    /**
     * 发送消息
     *
     * @param socketChannel
     * @param msg
     */
    private static void send(SocketChannel socketChannel, String msg) {
        try {
            ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
            ByteBuf writeBuffer = allocator.buffer(msg.getBytes().length);
            writeBuffer.writeCharSequence(msg, Charset.defaultCharset());
            socketChannel.writeAndFlush(writeBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加入群聊
     *
     * @param socketChannel
     */
    public static void join(SocketChannel socketChannel) {
        //有人加入，分配userID
        String userId = "" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        //发送通知自己加入的信息
        send(socketChannel, "您 " + userId + " 已加入群聊");
        for (SocketChannel channel : USER_MAP.keySet()) {
            //发送其他客户端
            send(channel, "用户" + userId + "已加入群聊");
        }
        USER_MAP.put(socketChannel, userId);
    }

    /**
     * 退出群聊
     *
     * @param socketChannel
     */
    public static void quit(SocketChannel socketChannel) {
        //有人加入，分配userID
        String userId = "" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
        //发送通知自己加入的信息
        send(socketChannel, "您 " + userId + " 退出群聊");
        //删除自己的渠道连接
        USER_MAP.remove(socketChannel);
        //遍历通知其他客户端
        for (SocketChannel channel : USER_MAP.keySet()) {
            //发送其他客户端
            send(channel, "用户" + userId + "退出群聊");
        }

    }

    /**
     * 扩散说话的内容
     *
     * @param socketChannel
     * @param content
     */
    public static void propagate(SocketChannel socketChannel, String content) {
        String userId = USER_MAP.get(socketChannel);
        //防止发送自己，过滤一次
        for (SocketChannel channel : USER_MAP.keySet()) {
            if (channel != socketChannel) {
                send(channel, userId + ": " + content + "\n\r");
            }
        }

    }

}
