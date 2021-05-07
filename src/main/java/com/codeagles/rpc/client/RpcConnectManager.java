package com.codeagles.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Codeagles
 * Date: 2021/5/7
 * Time: 下午11:11
 * <p>
 * Description:
 */
@Slf4j
public class RpcConnectManager {

    private static volatile RpcConnectManager RPC_CONNECT_MANAGER = new RpcConnectManager();

    private RpcConnectManager(){}

    public static RpcConnectManager getInstance(){
        return RPC_CONNECT_MANAGER;
    }
    /* 一个连接的地址，对应一个实际的业务处理器client1 */
    private Map<InetSocketAddress, RpcClientHandler> connectedHandlerMap = new ConcurrentHashMap<>();

    /* 用于异步的连接提交 */
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16,16,60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

    //1. 异步创建连接 线程池 真正的发起连接，连接失败监听，连接成功监听
    //2. 对于连接进来的资源做一个缓存（做一个管理）updateConnectedServer

    /**
     * 发起连接
     * @param serverAddress
     */
    public void connect(final String serverAddress) {
        List<String> allServerAddress = Arrays.asList(serverAddress.split(","));
        updateConnectedServer(allServerAddress);
    }

    /**
     * $更新缓存信息 并 异步发起连接
     * @param allServerAddress
     */
    public void updateConnectedServer(List<String> allServerAddress){
        if(CollectionUtils.isNotEmpty(allServerAddress)){
            //1. 解析allServerAddress地址，并临时存储到set集合中
            HashSet<InetSocketAddress> newAllServerNodeSet = new HashSet<InetSocketAddress>();
            for (int i = 0; i < allServerAddress.size(); i++) {
                String[] split = allServerAddress.get(i).split(":");
                if(split.length == 2){
                    String host = split[0];
                    Integer port = Integer.parseInt(split[1]);
                    final InetSocketAddress remotePeer = new InetSocketAddress(host, port);
                    newAllServerNodeSet.add(remotePeer);
                }

            }

            // 2. 调用并建立连接 发起远程连接操作(异步)
            for (InetSocketAddress serverNodeAddress : newAllServerNodeSet) {
                if(!connectedHandlerMap.keySet().contains(serverNodeAddress)){
                    connectAsync(serverNodeAddress);
                }
            }

            // 3. 如果allServerAddres列表里不存在的链接地址，那么需要从缓存中剔除
            // TODO

        }else {
            //
            log.error("no available server address!");
        }
    }

    /**
     * 异步发起连接
     * @param remotePeer
     */
    private void connectAsync(final InetSocketAddress remotePeer) {
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b
                        .group(eventLoopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new RpcClientInitializer());
                connect(b, remotePeer);
            }
        });
    }

    private void connect(final Bootstrap b, InetSocketAddress remoetPeer){

    }
}
