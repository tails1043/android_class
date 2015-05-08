package com.example.ntnu15.android_class;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private static final int REQUEST_CODE_ORDER_ACTIVITY = 0;

    private Button button;
    private EditText editText;
    private CheckBox checkBox;
    private ListView listView;
    private Spinner spinner;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private JSONArray orderInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bJDGykyTKBVUTLhxJYDAYFozFHnVtHySdC7lb0YN", "dt61u5zFUDWUK21epwMFDBrYFQjszPwKDOEngRal");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();


        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        listView = (ListView) findViewById(R.id.listView);
        spinner = (Spinner) findViewById(R.id.spinner);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);  //設定設定檔
        editor = sp.edit();

        editText.setText(sp.getString("text", ""));                   //取得設定檔內容並輸出
        checkBox.setChecked(sp.getBoolean("checkbox", false));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = editText.getText().toString();
                editor.putString("text", text);
                editor.commit();                             //取得editText的內容並存入設定檔sp

                Log.d("debug", "keyCode = " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    send();
                    return true;  //讓其他 listner 不執行
                }
                return false;
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  //取得checkBox的狀態並存入設定檔sp
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("checkbox", isChecked);
                editor.commit();
            }
        });

        updateHistory();
        setStoreName();

    }

    private void send(){

        String text = editText.getText().toString();

        if(checkBox.isChecked()){
            text = "*****";
        }


        try {
            JSONObject all = new JSONObject();
            all.put("note", text);
            all.put("order", orderInfo);
            all.put("storeName", (String) spinner.getSelectedItem());

            //Utils.writeFile(this, "history", text + "\n");
            Utils.writeFile(this, "history", all.toString() + "\n");
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            editText.setText("");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateHistory();

    }

    private int getDrinkNumber(JSONArray array){
        return new Random().nextInt();
    }

    private void updateHistory() {

        String[] rawdata = Utils.readFile(this, "history").split("\n");

        List<Map<String, String>> data = new ArrayList<>();

        for(int i=0 ; i < rawdata.length ; i++){
            try {
                JSONObject object = new JSONObject(rawdata[i]);
                String storeName = object.getString("storeName");
                String note = object.getString("note");
                JSONArray order = object.getJSONArray("order");

                Map<String, String> item = new HashMap<>();
                item.put("storeName", storeName);
                item.put("note", note);
                item.put("order", String.valueOf(getDrinkNumber(order)));

                data.add(item);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rawdata);

        String[] from = {"storeName", "note", "drinkNumber"};
        int[] to = {R.id.storeName, R.id.note, R.id.number};
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listview_item, from, to);

        listView.setAdapter(adapter);

    }

    public void setStoreName(){

        //String [] storeNames = {"NTU", "NTNU", "NTUST"};
        String [] storeNames = getResources().getStringArray(R.array.storeName);  //從xml中取得 storeName

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, storeNames);

        spinner.setAdapter(adapter);

     }

    public void goToOrderActivity(View view) {

        Intent intent = new Intent();
        intent.setClass(this, OrderActivity.class);
        //startActivity(intent);   //跳到 Activity 不回傳值
        intent.putExtra("storeName", (String) spinner.getSelectedItem());  //跳到 Activity 時傳出資料
        startActivityForResult(intent, REQUEST_CODE_ORDER_ACTIVITY);  //跳到 Activity 並接收資料


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //複寫此 function 接收回傳的 data
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ORDER_ACTIVITY){
            if(resultCode == RESULT_OK){
                String jsonArrayString = data.getStringExtra("orders");
                try {
                    orderInfo = new JSONArray(jsonArrayString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, jsonArrayString, Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
