package ex04.pymont.connector.http.request;

import ex04.pymont.connector.http.SocketInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpRequestStream extends InputStream {

    private boolean isChunk;

    private int chunkPos;   //下一个该读的位置
    private byte[] chunkBuffer; //缓冲区，需要注意 chunkBuffer的size可能大于chunkLength
    private int chunkLength;    //chunk中数据的大小


    private int contentLength;  //待读取的内容长度，使用content-Length下的初始值，不断递减
    private InputStream stream;

    private int count; //读取总字节数


    public HttpRequestStream(HttpRequest request){

        String transferEncoding = request.getTransferEncoding();
        isChunk = transferEncoding!=null && transferEncoding.contains("chunked");
        chunkLength = -1 ;//初始化为-1;
        chunkPos = 0 ;  //都初始化为0
        count = 0;
        if(!isChunk){
            // 若不为chunk，设置content-length，
            // 即 chunked一旦设置优先生效
            contentLength = request.getContentLength();
        }
        stream = request.getStream();
    }

    @Override
    public int read() throws IOException {
        if(!isChunk){
            //判断何时退出
            if(count >= contentLength || contentLength == 0){
                return -1;
            }
            int b = stream.read();
            if( b > 0){     //读到内容
                count++;
            }
            return b;
        }else{
            // 判断何时退出,读到为0时
            if(chunkLength == 0 ){
                return -1;
            }
            int remainig = chunkRemaining();    //第一次时由于都为-1，进行填充
            if(remainig <= 0){
                if(fillChunk() <= 0 ){      //执行一次填充，若填充数量==0或<0
                    return -1;
                }
            }
            count++;
            return (int) chunkBuffer[chunkPos++];
        }
    }


    @Override
    public int read(byte[] b , int off, int len) throws IOException{
        // 加强健壮性，异常判定
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        // 开始进行read操作
        if(!isChunk){
            //判断是否返回-1,
            if(count >= contentLength || contentLength == 0){
                return -1;
            }
            // 获取本次可读的长度
            int availd =  count+len <= contentLength ? len:contentLength-count;
            int realLength =  stream.read(b,off,availd);
            if(realLength>0){
                count+= realLength;
                return realLength;
            }else{
                return -1;
            }
        }else{
            if( chunkLength == 0 ){ //何时退出，当读到chunk的size==0时推出
                return -1;
            }
            int remainig = chunkRemaining();    //第一次时由于都为-1.表示chunk中可读的数据大小
            if(remainig <= 0){
                if(fillChunk() <= 0 ){      //执行一次填充，若填充数量==0或<0
                    return -1;
                }else{
                    //确实填充了数据
                    remainig = chunkRemaining();
                }
            }
            //读取内容
            int avalid = remainig > len ? len:remainig;
            System.arraycopy(
                    chunkBuffer,chunkPos,
                    b,off,avalid
            );
            chunkPos += avalid;
            return avalid;
        }
    }

    /**
     * 获取chunk可读的个数
     * @return
     */
    public int chunkRemaining() {
        return chunkLength - chunkPos;
    }


    /**
     * 填充一次chunk的内容到chunkBuffer,
     * 先解析chunksize，再填充chunkBuffer,
     * @return
     */
    public int fillChunk()  throws IOException{
        byte[] byteline = readLine();   //读chunk行
        try{
            chunkLength = Integer.parseInt( new String( byteline),16 );
        } catch (NumberFormatException e){
            e.printStackTrace();
            chunkLength = 0;
            return 0;
        }
        chunkPos = 0;   //从0开始
        if(chunkLength == 0 ){
            //说明是结尾，再读一次读完结尾内容的\r\n,即可退出
            int a = stream.read();
            if( a == (byte) '\r'){
                a =  stream.read();
                if( a == (byte) '\n'){
                    return 0;
                } else{
                    throw new IOException("读取请求体chunk结尾失败，未找到结束符");
                }
            }
        } else{
            int count  = 0 ;    //总数量 或者为下次插入数组的其实位置
            //判断chunkBuffer的大小是否满足此次chunk获取，不满足则 扩大,chunkBuffer可能为空，短路
            if( chunkBuffer == null  || chunkLength > chunkBuffer.length){
                chunkBuffer = new byte[ chunkLength];
            }
            //开始填充缓冲区
            while(count < chunkLength){
                count += stream.read(chunkBuffer,count,chunkLength - count);    //要读入的数量
            }
            stream.read();  //读取完该chunk内容最后的/r
            stream.read();   //读取完该chunk内容最后的/n
        }
        return chunkLength;
    }

    /**
     * 读取一行以/r/n换行符为分割
     */
    public byte[] readLine() throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  //数据源为内存
        while(true){
            int chr = stream.read();
            //判断是否进行退出
            if( chr == (byte) '\r' ){
                //再次判断下一个是否为Ln
                int end = stream.read();
                if( end ==  (byte) '\n'){
                    break;
                }else{
                    byteArrayOutputStream.write(chr);  //说明判断失败，不是退出，则将两者多写入
                    byteArrayOutputStream.write(end);
                    continue;           // 进入下次循环
                }
            }
            //不为结束符
            byteArrayOutputStream.write(chr);
        }
        byte[] res = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return  res;
    }
}
