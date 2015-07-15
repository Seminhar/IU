package com.myim.NetService;

import android.content.Context;
import android.os.Environment;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by PC on 2015-05-20.
 */
public class HttpFileUpload {

    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 10000000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码


    public static String download(String urlString,String subFolder,Context context ) {

        String rtn=null;
        //urlString = "http://192.168.21.106:9090/multimsg/2.png";
        String path = subFolder;
        // String fileName = "3.png";
        String fileName = new File(urlString).getName();
        if(fileName==null)
            fileName="error";

        try {


   String location ;
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                location = context.getExternalFilesDir(null).getAbsolutePath();
            }
            else
                location = context.getCacheDir().getAbsolutePath();
            //String SDCard = Environment.getExternalStorageDirectory() + "";
            //   String SDCard = context.getFilesDir().getAbsolutePath();
            String pathName = location + "/" + path + "/" + fileName;//文件存储路径

            File file = new File(pathName);

            if (file.exists()) {
                System.out.println("exits");
                rtn = pathName;
            } else {
                OutputStream output = null;
                URL url = null;
                url = new URL(urlString);
                String dir = location + "/"+path;
                new File(dir).mkdirs();//新建文件夹
                file.createNewFile();//新建文件
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                output = new FileOutputStream(file);
                //读取大文件

                byte[] buffer = new byte[4 * 1024];
                int len;
                InputStream input = urlConnection.getInputStream();
                while ((len=input.read(buffer))!= -1) {
                    output.write(buffer,0,len);
                }
                output.flush();
                output.close();
                rtn=pathName;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }


        return  rtn;



    }




    public static String uploadFile(File file,String type) {
        String rtn="false";
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String RequestURL = Constant.HTTP_HOST +  "plugins/myofplugin/short";
        if(type.equals("img"))
        {
            BOUNDARY = BOUNDARY + "imggim";
        }


        URL url = null;
        try {
            url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);

            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                OutputStream outputSteam = conn.getOutputStream();


                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + BOUNDARY + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */

                int res = conn.getResponseCode();
                if (res == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String result = bufferedReader.readLine();
                    System.out.println("read: "+result);
                    if(result.equals("true")) {
                        rtn = Constant.HTTP_HOST+"multimsg/"+BOUNDARY ;
                    }
                }


            }

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (ProtocolException e) {

            e.printStackTrace();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        System.out.println("Last: " + rtn);
        return rtn;
    }

    public static String getThumbnailUrl(String filename)
    {
        return  Constant.HTTP_HOST+"thumbnail/"+filename ;
    }

}
