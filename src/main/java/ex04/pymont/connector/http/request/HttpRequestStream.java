package ex04.pymont.connector.http.request;

import ex04.pymont.connector.http.SocketInputStream;

import java.io.IOException;
import java.io.InputStream;

public class HttpRequestStream extends InputStream {

    private boolean isChunk;

    private int chunkPos;   //下一个该读的位置
    private byte[] chunkBuffer; //复用的chunk大小
    private int chunkLength;    //chunk大小

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

    public int chunkRemaining() {
        return chunkLength - chunkPos;
    }







    /**
     * 返回该chunk的大小
     * @return
     */
    public int fillChunk(){
        chunkLength = 99;
        return 1;
    }


}
