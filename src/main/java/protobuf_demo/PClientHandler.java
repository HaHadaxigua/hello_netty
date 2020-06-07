package protobuf_demo;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PClientHandler extends SimpleChannelInboundHandler<MyDataInfo.ResponseBack> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.ResponseBack msg) throws Exception {
        System.out.println(msg.getBackMoney());
        System.out.println(msg.getBackName());
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MyDataInfo.User user = MyDataInfo.User.newBuilder()
                .setUserName("zhihao.miao").setUserAddr("america").setUserId(32).build();
        ctx.channel().writeAndFlush(user);
    }
}
