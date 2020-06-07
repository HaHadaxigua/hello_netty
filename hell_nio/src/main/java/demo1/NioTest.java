package demo1;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.SecureRandom;

public class NioTest {
    public static void main(String[] args) {
        IntBuffer intBuffer = IntBuffer.allocate(128);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(secureRandom.nextInt());
        }
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }
}
