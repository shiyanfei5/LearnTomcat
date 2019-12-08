package ex03.pymont.connector.http;


import java.util.HashMap;
import java.util.Map;

/**
 * 错误码函数
 * 一个模块对应一个StringManager，通过StringManager管理错误信息
 */
public class StringManager {
    /**
     *  Map用于存储整个  模块和StringManager的关系
     *  每生成一个StringManager，会将其注册进managerMap
     *  以保存 StringManager和 模块的关系
     */
    private static Map<String,StringManager> managerMap = new HashMap<>();

    public synchronized static StringManager getManager(String packageName){
        StringManager manager = managerMap.get(packageName);
        if(manager == null ){
            manager = new StringManager(packageName);
            managerMap.put(packageName,manager);
        }
        return manager;
    }

    private StringManager(String packageName){


    }








}
