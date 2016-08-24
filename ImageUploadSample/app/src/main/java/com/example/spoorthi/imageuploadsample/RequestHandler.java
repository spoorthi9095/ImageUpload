package com.example.spoorthi.imageuploadsample;


import android.util.Log;

import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by spoorthi on 17/8/16.
 */

public class RequestHandler {

    static String multipost(String urlString,String json)
    {
        HttpURLConnection conn = null;
        try {

            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            OutputStream os = conn.getOutputStream();
            byte[] b = json.getBytes();

            os.write(b);
            Log.e("",""+conn.getOutputStream());
            os.close();
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e("responsecode",""+conn.getResponseCode());
                return readStream(conn.getInputStream());
            }
                Log.e("responsecode",""+conn.getResponseCode());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception", "multipart post error " + e + "(" + urlString + ")");
        }
        finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    static String uploadImageOnSigned(String urlString,byte[] b)
    {
        HttpURLConnection conn = null;
        try {
//            String boundary = "------------"+System.currentTimeMillis();
            StringBuilder sb;
            URL url = new URL(urlString);

            System.out.println("byte"+b);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("PUT");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

//            conn.setRequestProperty("Content-Length",Integer.toString(b.toString().getBytes("UTF8").length+1));
//            conn.addRequestProperty(reqEntity.getContentType().getName(), reqEntity.getContentType().getValue());
//            Log.e("Entity Name="+reqEntity.getContentType().getName(),"Entity Value="+reqEntity.getContentType().getValue());

//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("Content-type", "multipart/form-data; boundary="+boundary);

//            Log.e("contentlength",""+Integer.toString(b.toString().getBytes("UTF8").length));

//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(os, "UTF-8"));
//            writer.write(String.valueOf(b));
//
//            writer.flush();
//            writer.close();
//            os.close();

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(b);
//            reqEntity.writeTo(wr);
            wr.flush();
            wr.close();

            conn.connect();

            Log.e("responsecode",""+conn.getResponseCode());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                return readStream(conn.getInputStream());
            }
            else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();

                String response = br.readLine();
                Log.e("responsecode",""+conn.getResponseCode());
                System.out.println("Response : "+response);
                Log.e("response",""+response);
                while ((response = br.readLine()) != null){
                    sb.append(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exception", "" + e + "(" + urlString +")");
        }
        finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


    private static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}