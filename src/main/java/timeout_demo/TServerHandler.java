package timeout_demo;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 *  Adapter适配器模式
 */
public class TServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            String idleType = null;

            switch (idleStateEvent.state()) {
                case WRITER_IDLE:
                    idleType = "写空闲";
                    break;
                case READER_IDLE:
                    idleType = "读空闲";
                    break;
                case ALL_IDLE:
                    idleType = "读写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "超时事件：" + idleType);
            ctx.channel().close();
        }
    }
}
