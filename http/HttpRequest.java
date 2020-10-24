package mytomcat.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 网页请求
 */
public class HttpRequest {
    // 请求方式
    private String method;
    // 请求资源URL
    private String uri;
    // 请求资源URI
    private String url;
    // 协议版本
    private String protocol;
    // 存放请求参数
    private Map<String, List<String>> paramsMap;
    // 存放请求头
    private Map<String, List<String>> headsMap;
    // 换行常量
    private final String CRLF="\r\n";
    // 输入流
    private InputStream is;
    // 请求编码方式,默认UTF-8
    private String characterEncoding = "utf-8";

    protected HttpRequest() {
        paramsMap = new HashMap<>();
        headsMap = new HashMap<>();
    }

    protected HttpRequest(InputStream is) {
        this();
        this.is = is;
        try {
            byte[] buffer = new byte[10240];
            int len = is.read(buffer);
            String requestInfo = null;
            if(len != -1){
                requestInfo = new String(buffer,0,len);
            }
            this.analysisInfo(requestInfo);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 解析请求信息
     */
    private void analysisInfo(String requestInfo){
        if(requestInfo == null||"".equals(requestInfo.trim())){
            return;
        }
        // 获得请求方式及请求参数
        String reqRow = requestInfo.substring(0,requestInfo.indexOf(CRLF));
        int index = reqRow.indexOf("/");
        method = reqRow.substring(0,index).trim();
        String params = null;
        String reqUrl = reqRow.substring(index,reqRow.indexOf("HTTP/")).trim();
        String protocol = reqRow.substring(reqRow.indexOf("HTTP/"));
        this.protocol = protocol;
        if(method.equalsIgnoreCase("post")){
            this.url = reqUrl;
            params = requestInfo.substring(requestInfo.lastIndexOf(CRLF)).trim();
        }else if(method.equalsIgnoreCase("get")){
            // 判断是否含有参数
            if(reqUrl.contains("?")){
                String[] urlArray=reqUrl.split("\\?");
                this.url = urlArray[0];
                params = urlArray[1];
            }else{
                this.url = reqUrl;
            }
        }
        // 获得请求头
        String head = requestInfo.substring(requestInfo.indexOf(CRLF) + 1,requestInfo.lastIndexOf(CRLF));
        // 封装请求头到Map中
        addToMap(head,headsMap,CRLF,":");
        // 不存在请求参数
        if(params == null||params.equals("")){
            return;
        }
        // 将请求参数封装到Map中
        addToMap(params,paramsMap,"&","=");

    }

    /**
     * 封装数据到map
     * @param src 数据源
     * @param map 存放位置
     * @param spiltChar 按指定字符分割
     */
    private void addToMap(String src ,Map<String, List<String>> map ,String... spiltChar){
        //分割 将字符串转成数组
        String [] params=src.split(spiltChar[0]);
        for (String temp : params){
            String key=temp.substring(0,temp.indexOf(spiltChar[1]));
            String value = decode(temp.substring(temp.indexOf(spiltChar[1])+1),characterEncoding);
            // 转换成Map分拣
            if(!map.containsKey(key)){
                List<String> values =new ArrayList<>();
                values.add(value);
                map.put(key,values);
            }else{
                // 存在则往list后面加
                map.get(key).add(value);
            }
            // System.out.println(key+"-->"+value);
            System.out.println(key+"-->"+map.get(key));
        }
    }

    /**
     * 解决中文乱码问题
     * @param value
     * @param code
     * @return
     */
    private String decode(String value,String code){
        try {
            return  java.net.URLDecoder.decode(value, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据页面的name 获取对应的多个值
     * @param name
     */
    public String[] getParameterValues(String name){
        List<String> values = paramsMap.get(name);
        if(values==null){
            return null;
        }else{
            return values.toArray(new String[0]);
        }
    }

    /**
     * 根据页面的name 获取对应的单个值
     * @param name
     */
    public String getParameter(String name){
        String[] values =getParameterValues(name);
        if(null==values){
            return null;
        }
        return values[0];
    }
    /**
     * 根据指定的字符串 获取对应的请求头
     * @param headName
     */
    public String getHeader(String headName){
        List<String> values= headsMap.get(headName);
        if(values==null||values.isEmpty()){
            return null;
        }
        System.out.println(values.get(0));
        return values.get(0).trim();
    }
    /**
     * 获取请求url
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 请求方式
     * @return
     */
    public String getMethod() {
        return method;
    }

    /**
     * 设置编码
     * @param str
     */
    public void setCharacterEncoding(String str){
        this.characterEncoding=str;
    }

    /**
     * 获取网页的输入流
     * @return
     */
    public InputStream getInputStream(){
        return is;
    }

}