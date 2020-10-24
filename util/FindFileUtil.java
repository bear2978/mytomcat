package mytomcat.util;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class FindFileUtil {

    /**
     * 根据文件名响应对应的文件
     * @param filename
     * @throws IOException
     */
    public static String findFile(String filename) throws IOException {
        System.out.println("请求资源："+filename);
        InputStream is = FindFileUtil.class.getClassLoader().getResourceAsStream(filename);
        // 文件未找到则返回404
        if(is == null){
            return findFile("404.html");
        }
        // 缓冲输入流
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        // 把文件内容拼成字符串作为返回值
        String result="";
        while ((len = bis.read(buffer)) != -1) {
            result+=new String(buffer, 0, len);
        }
        CloseUtil.close(bis);
        return result;
    }
}
