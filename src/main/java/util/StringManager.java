package util;


import java.util.*;

/**
 * 错误码函数
 * 一个模块对应一个StringManager，通过StringManager管理错误信息
 */
public class StringManager {
    /**
     *  类属性，用于记录 实例和模块的关系
     *  存储  模块和StringManager的关系
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

    //-------------------------------------------------------
    //默认使用US进行locale
    private static Locale locale = new Locale("es");
    //----------------------------------------------------

    //资源Bundle,用于解决资源properties的国际化的问题
    private ResourceBundle bundle;

    public StringManager(String packageName){
        bundle = ResourceBundle.getBundle( packageName + ".LocalStrings",locale);
    }

    public String getString(String key) {
        if (key == null) {
            String msg = "key is null";
            throw new NullPointerException(msg);
        }

        String str = null;

        try {
            str = bundle.getString(key);
        } catch (MissingResourceException mre) {
            str = "Cannot find message associated with key '" + key + "'";
        }

        return str;
    }

    public void setBundle(Locale arg){
        locale = arg;
    }

    public static void main(String[] args){
        StringManager s = StringManager.getManager("ex03.pymont.connector.http");
        System.out.println(s.getString("httpConnector.noAddress"));
        SocketInput
    }





}
