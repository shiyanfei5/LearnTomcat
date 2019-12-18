package ex04.pymont.startup;

import ex03.pymont.connector.http.HttpConnector;

public final class Bootstrap {


    /**
     * 启动connector
     * @param args
     */
    public static void main(String[] args){
        HttpConnector connector = new HttpConnector();
        connector.run();
    }


}
