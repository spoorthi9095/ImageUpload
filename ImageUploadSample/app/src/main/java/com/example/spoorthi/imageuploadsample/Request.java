package com.example.spoorthi.imageuploadsample;

/**
 * Created by spoorthi on 18/8/16.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * class to make Http Request to the web
 */
public class Request {

    private static final String TAG = Request.class.getSimpleName();

    public static String post(String serverUrl, String dataToSend) {
        try {
            String boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(serverUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            //set timeout of 30 seconds
            con.setConnectTimeout(1000 * 30);
            con.setReadTimeout(1000 * 30);
            //method
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            con.setDoOutput(true);
            con.connect();

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            //make request
            writer.write(dataToSend);
            writer.flush();
            writer.close();
            os.close();

            //get the response
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //read the response
                StringBuilder sb = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String line;

                //loop through the response from the server
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                //return the response
                return sb.toString();
            } else {
                Log.e(TAG, "ERROR - Invalid response code from server " + responseCode);
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR " + e);
            return null;
        }
    }

    public String upload(String selectedPath) throws IOException {

        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;

        String pathToOurFile = selectedPath;
        String urlServer = "https://vzcards-api.herokuapp.com/upload_image/?access_token=jUUMHSnuGys5nr6qr8XsNEx6rbUyNu";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(
                    pathToOurFile));

            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream
                    .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                            + pathToOurFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.e("response code",""+serverResponseCode);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex)
        {

        }
        return connection.getResponseMessage();
    }
}
