package protobuf_demo;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PServerHandler extends SimpleChannelInboundHandler<MyDataInfo.User> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.User msg) throws Exception {
        System.out.println("user name:" + msg.getUserName());
        System.out.println("user id:" + msg.getUserId());
        System.out.println("user addr:" + msg.getUserAddr());

        MyDataInfo.ResponseBack responseBack = MyDataInfo.ResponseBack.newBuilder()
                .setBackName("bank of america").setBackMoney(999).build();
        ctx.channel().writeAndFlush(responseBack);
    }
}
