package com.example.spoorthi.imageuploadsample;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    //define global views variable
    public ImageView imageView;

    String absolutePath;

    public Button   selectImage,
            uploadImage;
    public String SERVER = "https://vzcards-api.herokuapp.com/upload_image/?access_token=jUUMHSnuGys5nr6qr8XsNEx6rbUyNu",
            timestamp;

    private static final String TAG = UploadImageActivity.class.getSimpleName();

    private static final int RESULT_SELECT_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //instantiate view
        imageView = (ImageView) findViewById(R.id.imageView);
        selectImage = (Button) findViewById(R.id.selectImage);
        uploadImage = (Button) findViewById(R.id.uploadImage);

        //when selectImage button is pressed
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call the function to select image from album
                selectImage();
            }
        });

        //when uploadImage button is pressed
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get image in bitmap format
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                //execute the async task and upload the image to server
                new Upload(image,"IMG_"+timestamp).execute();
            }
        });

    }

    //function to select a image
    private void selectImage(){
        //open album to select image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
    }


    //This function is called when we pick some image from the album


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null){
            //set the selected image to image variable
            Uri image = data.getData();
            imageView.setImageURI(image);

            absolutePath = getImagePath(image);

            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();

            Toast.makeText(getApplicationContext(),timestamp,Toast.LENGTH_SHORT).show();
        }
    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    //async task to upload image
    private class Upload extends AsyncTask<Void,Void,String>{
        private Bitmap image;
        private String name;

        public Upload(Bitmap image,String name){
            this.image = image;
            this.name = name;
        }

        @Override
        protected String doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //compress the image to jpg format
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            /*
            * encode image to base64 so that it can be picked by saveImage.php file
            * */
            String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            //generate hashMap to store encodedImage and the name
            HashMap<String,String> detail = new HashMap<>();
//            detail.put("photo", name);
            detail.put("photo", encodeImage);

            try{
                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file
                String response = Request.post(SERVER,dataToSend);

//                Request reqq = new Request();
//                String response = reqq.upload(dataToSend);

                //return the response
                return response;

            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"ERROR  "+e);
                return null;
            }
        }



        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
        }
    }
}

