package com.example.spoorthi.imageuploadsample;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadImageActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String UPLOAD_URL = "https://test2-beautifulphotosproject.herokuapp.com/get_signed_url/";
    public static final String UPLOAD_KEY = "image_list";

    private Button buttonUpload;


    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        imageView = (ImageView) findViewById(R.id.imageView);


        buttonUpload.setOnClickListener(this);
    }

    private void uploadImage(){
        class UploadImage extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;

            @Override
            protected String doInBackground(Void... voids)
            {
                /*-------------convert image to byte array using ByteArrayOutputStream-----*/

                Bitmap bitmap1= BitmapFactory.decodeResource(UploadImageActivity.this.getResources(),
                        R.drawable.lighthouse);
//                Bitmap bitmap2= BitmapFactory.decodeResource(MainActivity.this.getResources(),
//                        R.drawable.medallion);
//                Bitmap bitmap3= BitmapFactory.decodeResource(MainActivity.this.getResources(),
//                        R.drawable.nike_jacket);
                ArrayList<Bitmap> bitmaps = new ArrayList<>();
                String filename = "filename";
                ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
//                ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
//                ByteArrayOutputStream bos3 = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.PNG, 100, bos1);
//                bitmap2.compress(Bitmap.CompressFormat.PNG, 100, bos2);
//                bitmap3.compress(Bitmap.CompressFormat.PNG, 100, bos3);
                bitmaps.add(bitmap1);
//                bitmaps.add(bitmap2);
//                bitmaps.add(bitmap3);

                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                ContentBody contentPart = null;
                ArrayList<ByteArrayOutputStream> bos = new ArrayList<>();
                byte[] byteArray = bos1.toByteArray();
                System.out.println("byte"+byteArray);
                bos.add(bos1);
//                bos.add(bos2);
//                bos.add(bos3);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                ArrayList<String> name = new ArrayList<>();

                HashMap<String,ArrayList<String>> img = new HashMap<>();

                /*---------------- sending multiple imagenames to get signed urls------------- */

                for (int i=0;i<bitmaps.size();i++)
                {
                    name.add(filename+i+".png");
                    contentPart = new ByteArrayBody(bos.get(i).toByteArray(), filename+i+".jpg");
//                    contentPart = new ByteArrayBody();
//                    Log.e("content",""+contentPart);
                    reqEntity.addPart(contentPart.getFilename(),contentPart);
//                    Log.e("reqentity",""+reqEntity);
                }
                img.put(UPLOAD_KEY,name);

//                Log.e("img",""+img);

                String json;
                json = new Gson().toJson(img);

                Log.e("json",""+json);

                String response = RequestHandler.multipost(UPLOAD_URL,json);

                Log.e("response",""+response);

                String link = null;
                String link2 = null;

                /*=================retrive the signed url======================*/

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        String s = String.valueOf(i);
                        link = jsonArray.getJSONObject(i).getString(s);
                        link2 = jsonArray.getJSONObject(i).getString("id");
                        Log.e("link",""+link);
                        Log.e("link2",""+link2);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /*============send the image in byteArray for upload===================*/

                String result = RequestHandler.uploadImageOnSigned(link,byteArray);


//                Log.e("response",""+response);
//                return response;
                Log.e("result",""+result);
                return result;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(UploadImageActivity.this, "Uploading Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute();
    }

    @Override
    public void onClick(View v) {

        if(v == buttonUpload){
            uploadImage();
        }
    }

}