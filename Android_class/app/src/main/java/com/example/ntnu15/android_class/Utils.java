package com.example.ntnu15.android_class;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by NTNU15 on 4/28/15.
 */
public class Utils {

    public static void writeFile(Context context, String fileName, String content){
        try {

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(content.getBytes());
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context context, String fileName){
        try {
            FileInputStream fis = context.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            fis.read(buffer);
            fis.close();
            return new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static byte[] bitmapToBytes(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public static String fetch(String urlString) { //從url讀資料   要在manifest加入permission才能使用
        try {
            URL url = new URL(urlString);   //判斷是否為URL
            URLConnection urlConnection = url.openConnection();  //取得連線
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);


            //String result = "";
            StringBuilder builder = new StringBuilder();
            String line;
            while( (line = reader.readLine()) != null){
                //result += line;
                builder.append(line);
            }

            //return result;
            return builder.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject addressToLocation(String address){
        //http://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=true_or_false
        //String.format("http://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=true_or_false")
        try {
            address = URLEncoder.encode(address, "utf-8");  //將文字轉換成 URLEncode 格式
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String queryUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&sensor=false";
        String jsonString = Utils.fetch(queryUrl);
        try {

            JSONObject object = new JSONObject(jsonString);
            JSONObject location = object.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            return location;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void disableStrictMode() {

        StrictMode.ThreadPolicy.Builder builder = new StrictMode.ThreadPolicy.Builder();
        StrictMode.ThreadPolicy threadPoliicy = builder.permitAll().build();
        StrictMode.setThreadPolicy(threadPoliicy);

    }

}
