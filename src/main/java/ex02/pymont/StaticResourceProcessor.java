package ex02.pymont;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class StaticResourceProcessor {

    public void process(Request request, Response response){
        response.sendStaticResource();
    }
}
