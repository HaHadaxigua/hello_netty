package file_demo;

import io.netty.channel.*;

import java.io.File;
import java.io.RandomAccessFile;

public class FileServerHandler extends SimpleChannelInboundHandler<String> {
    private final String CR = System.getProperty("line.separator");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        File file = new File(msg);  //
        if (file.exists()) {
            if (!file.isFile()) {
                ctx.writeAndFlush("Not a file:" + msg);
                return;
            }
            ctx.writeAndFlush(file + " " + file.length() + CR);
            RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r"); // 创建randomAccessFile
            FileRegion fileRegion = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
            ctx.write(fileRegion);
            ctx.writeAndFlush(CR);
            randomAccessFile.close();
        } else
            ctx.writeAndFlush("File not find:" + file + CR);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有新的连接:" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "throw a exception:" + cause.getMessage());
        cause.printStackTrace();
        ctx.channel().close();
    }
}
