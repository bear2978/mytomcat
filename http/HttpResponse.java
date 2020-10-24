package mytomcat.http;

import java.io.*;
import java.util.Date;
import mytomcat.util.CloseUtil;
import mytomcat.util.FindFileUtil;

public class HttpResponse {

    // 构建响应信息
    private StringBuilder info;
    // 输出流
    private OutputStream os;
    // 响应客户端流
    private BufferedWriter bw;
    // 响应头
    private String heads="";
    // 响应正文
    private String content="";
    // 响应编码方式,默认UTF-8
    private String characterEncoding="utf-8";
    // 响应contentType
    private String contentType="text/html;charset="+characterEncoding;
    // 换行常量
    private final String CRLF="\r\n";
    // 空格常量
    private final String BLACK=" ";

    protected HttpResponse(OutputStream outs) {
        this();
        this.os=outs;
        bw=new BufferedWriter(new OutputStreamWriter(os));
    }

    protected HttpResponse() {
        info = new StringBuilder();
    }

    /**
     * @param code 状态码
     */
    private void createInfo(int code){
        // 响应头 HTTP/1.1 200 OK\r\n
        info.append("HTTP/1.1").append(BLACK).append(code).append(BLACK);
        // 动态处理状态码
        switch(code){
            case 200:
                info.append("OK");
                break;
            case 302:
                info.append("OK");
                break;
            case 404:
                info.append("NOT FOUND");
                break;
            case 500:
                info.append("Server Error");
                break;
        }
        info.append(CRLF);
        // 构建响应头
        info.append("Server:tomcat/192.168.0.1").append(CRLF);
        info.append("Date:").append(new Date()).append(CRLF);
        info.append("Content-Type:"+contentType).append(CRLF);
        info.append(heads);
        // 没有正文
        if(content.equals("")){
            info.append("Content-Length:0").append(CRLF);
        }else{
            info.append("Content-Length:").append(content.getBytes().length).append(CRLF);
            info.append(CRLF);
            // 构建响应正文
            info.append(content);
        }
    }

    protected void pushToClient(int code){
        // 构建响应信息
        createInfo(code);
        // 将信息写给客户端
        try {
            bw.write(info.toString());
            //System.out.println(info.toString());
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CloseUtil.close(bw);
    }
    /**
     * 响应信息到客户端
     * @param s
     */
    public void println(String s){
        this.content+=s;
    }

    /**
     * 设置响应头
     * @param s
     */
    public void setHead(String s){
        this.heads+=s+CRLF;
    }
    /**
     * 获取输出流
     * @return
     */
    public OutputStream getOutputStream(){
        return os;
    }
    /**
     * 设置响应contentType
     * @param s
     */
    public void setContentType(String s){
        this.contentType=s;
    }

    /**
     * 设置编码
     * @param str
     */
    public void setCharacterEncoding(String str){
        this.characterEncoding=str;
    }
    /**
     * 重定向到某个页面
     * @param s
     */
    public void sendRedirect(String s) throws IOException {
        this.content = FindFileUtil.findFile(s);
    }
}