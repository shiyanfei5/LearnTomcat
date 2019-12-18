package ex04.pymont.connector.processor;

import ex04.pymont.connector.http.HttpRequest;
import ex04.pymont.connector.http.HttpResponse;

public class StaticResourceProcessor {

    public void process(HttpRequest request, HttpResponse response){
        response.sendStaticResource();
    }
}
