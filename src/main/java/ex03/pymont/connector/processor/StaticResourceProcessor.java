package ex03.pymont.connector.processor;

import ex02.pymont.Request;
import ex02.pymont.Response;
import ex03.pymont.connector.http.HttpRequest;
import ex03.pymont.connector.http.HttpResponse;

public class StaticResourceProcessor {

    public void process(HttpRequest request, HttpResponse response){
        response.sendStaticResource();
    }
}
