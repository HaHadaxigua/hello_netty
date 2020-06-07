import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 *  一、 堆外内存的概念
 *  二、 内存映射的使用
 *  三、 文件锁的使用
 *  四、 buffer的 scattering & gathering
 */
public class TestBuffer {
    public static void main(String[] args) {
        new TestBuffer().testMemoryMap();
    }

    /**
     *  尝试使用内存映射文件的方式来改变文件内容
     */
    private void testMemoryMap(){
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("t.txt", "rw");
            FileChannel fileChannel = randomAccessFile.getChannel();
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 3, 6);
            mappedByteBuffer.put(0, (byte)'a');
            mappedByteBuffer.put(3, (byte)'b');
            fileChannel.close();
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  尝试对文件的锁定
     */
    private void testChannelLock() throws IOException{
        RandomAccessFile randomAccessFile = new RandomAccessFile("t.txt", "rw");
        FileChannel fileChannel = randomAccessFile.getChannel();
        FileLock fileLock = fileChannel.lock(3,6, false);
        System.out.println(fileLock.isValid());
        System.out.println(fileLock.isShared());
        fileLock.release();
        randomAccessFile.close();
    }

    /**
     * scattering： 将来自一个channel的数据存储在多个buffer中
     */
    private void testChannelScattering(){

    }

}
