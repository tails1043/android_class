package com.example.ntnu15.android_class;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class OrderDetailActivity extends ActionBarActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        webView = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();

        String note = intent.getStringExtra("note");
        String storeName = intent.getStringExtra("storeName");
        if(intent.hasExtra("file")){
            byte[] file = intent.getByteArrayExtra("file");
        }
        try {
            JSONArray array = new JSONArray(intent.getStringExtra("array"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("debug", note + storeName);
        webView.loadUrl(getStaticMapURL(storeName));


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        GoogleMap googleMap = mapFragment.getMap();
        
        //googleMap.getMapType();


    }

    public String getStaticMapURL(String storeName){

        String address = storeName.split(",")[1];   //用逗號分割，取第二個值

        try {
            String encodeAddress = URLEncoder.encode(address, "utf-8");
            return "https://maps.googleapis.com/maps/api/staticmap?center=" + address + "&zoom=18&size=550x550&maptype=roadmap" +
                    "&markers=color:red%7Clabel:S%7C"+address;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
