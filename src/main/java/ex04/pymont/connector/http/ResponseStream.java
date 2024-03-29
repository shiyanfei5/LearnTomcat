package ex04.pymont.connector.http;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 字节输出流
 */
public class ResponseStream extends ServletOutputStream {


    /**
     * Has this stream been closed?
     */
    protected boolean closed = false;


    /**
     * Should we commit the response when we are flushed?
     */
    protected boolean commit ;


    /**
     * The number of bytes which have already been written to this stream.
     */
    protected int count = 0;


    /**
     * The content length past which we will not write, or -1 if there is
     * no defined content length.
     */
    protected int length = -1;


    /**
     * The Response with which this input stream is associated.
     */
    protected HttpResponse response = null;


    /**
     * The underlying output stream to which we should write data.
     */
    protected OutputStream stream = null;

    public ResponseStream(HttpResponse response) throws IOException {

        super();
        closed = false;
        commit = true;
        count = 0;
        this.response = response;
        this.stream = response.getOutputStream();


    }




    @Override
    public void write(int b) throws IOException {
        if (closed)
            throw new IOException("responseStream.write.closed");

        //当length == 0 或者 写入的count >0,报错
        // length == -1时则不报错
        if (  (length == 0)   || (length > 0) && (count >= length))
            throw new IOException("responseStream.write.count");

        response.write(b);
        count++;
        length--;

    }

    boolean closed() {
        return (this.closed);

    }


    /**
     * Reset the count of bytes written to this stream to zero.
     */
    void reset() {
        count = 0;

    }

    public void flush() throws IOException {
        if (closed)
            throw new IOException("responseStream.flush.closed");
        if (commit)
            response.flushBuffer();
    }

    public void close() throws IOException {
        if (closed)
            throw new IOException("responseStream.close.closed");
        response.flushBuffer();
        closed = true;
    }

    public boolean isCommit() {
        return commit;
    }

    public void setCommit(boolean commit) {
        this.commit = commit;
    }
}
