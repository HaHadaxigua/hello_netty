# NIO
## REVIEW original IO
### when we read a stream
1. open a stream 
2. while more information
3. read information
4. close stream

### when we wirte a stream
1. open a stream
2. while more information
3. write information
4. close stream 

### some tips 
JDK use decorator to implement stream class(FilterInputStream/FilterOutputStream).
We can upgrade stream class.

## REVIEW NIO
the main part of nio.
- selector
- channel
- buffer

###  buffer
the buffer class is a abstract class, and has a lots of subclass.
Their are 7 kind's wrapped buffer. only without boolean.
 
- clear(): <br> 
method in buffer is used to initialize buffer.
set position to zero, limit to capacity.

- slice(): <br>
create a new buffer, the content is the shared subsequence of the original buffer's content.
data between position and limit(didn't include limit) 
if you modify the data on new buffer, the data in original buffer will be changed.

- asReadOnlyBuffer(): <br>
create a readOnly buffer.<br>
the way to prevent write:just throw exception.
we can translate normal buffer to readOnlyBuffer, but not the other way around.

- wrap(byte[] array): <br>
use a existing array to creat buffer.but the buffer still use the original array's data.
it's dangerous, if we can direct modifying original data.


#### special buffer
![](https://txyzrx-1258985237.cos.ap-shanghai.myqcloud.com/167cac38a23be8c5.jpg)

1. DirectByteBuffer 堆外内存
其中使用了并未开源的jdk代码。可以去参考openJDK,规范还是一致的。
其中使用unsafe.allocateMemory(size).这是一个本地方法。
底层的数据其实是维护在内核缓存中，而不是jvm里，DirectByteBuffer里维护了一个引用address指向了数据，从而操作数据；
由于DirectByteBuffer分配与native memory中，不在heap区，所以不会受到heap区的gc影响，但分配和释放需要更多的成本；


2. HeapByteBuffer 堆内内存
是在jvm堆上的一个buffer,底层本质是一个数组。内容维护在jvm里，所以把内容写进buffer里速度会快些；并且Java堆内存的管理，是由gc去管理的，更简洁；


HeapByteBuffer与DirectByteBuffer的创建，都是通过ByteBuffer中的方法来创建的：
```java
public static ByteBuffer allocate(int capacity)
public static ByteBuffer allocateDirect(int capacity)
```
