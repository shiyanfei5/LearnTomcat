package ex03.pymont.connector.http;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ResponseOutputStream extends ServletOutputStream {


    /**
     * Has this stream been closed?
     */
    protected boolean closed = false;


    /**
     * Should we commit the response when we are flushed?
     */
    protected boolean commit = false;


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

    public ResponseOutputStream(HttpResponse response) {

        super();
        closed = false;
        commit = false;
        count = 0;
        this.response = response;
        //  this.stream = response.getStream();
    }



    @Override
    public void write(int b) throws IOException {

    }
}
