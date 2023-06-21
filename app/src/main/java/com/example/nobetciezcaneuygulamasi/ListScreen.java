package com.example.nobetciezcaneuygulamasi;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ListScreen extends AppCompatActivity {

    private ListView lv;
    String city,town,name,phone,address;
    private static String API_URL = "YOUR_API_URL";
    ArrayList<HashMap<String,String>> eczaneList;
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_screen);
        autoCompleteTxt = findViewById(R.id.auto_complete_txt2);
        autoCompleteTxt.setText(MainActivity.item);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item,MainActivity.items);
        autoCompleteTxt.setAdapter(adapterItems);
        eczaneList = new ArrayList<>();
        lv = findViewById(R.id.listview);
        lv.setClickable(true);
        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eczaneList.clear();
                lv.setAdapter(null);
                GetData getData = new GetData();
                getData.execute();
                MainActivity.item = parent.getItemAtPosition(position).toString();
                Toast toast = Toast.makeText(getApplicationContext(),"Seçilen İl: "+MainActivity.item,Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String address_ = ((TextView) view.findViewById(R.id.textview4)).getText().toString() +" "+((TextView) view.findViewById(R.id.textview)).getText().toString() +" "+((TextView) view.findViewById(R.id.textview2)).getText().toString();
                Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address_));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        GetData getData = new GetData();
        getData.execute();
    }
    public class GetData extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... strings) {
            String current = "";
            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(API_URL + MainActivity.item);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(in);

                    int data = isr.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isr.read();
                    }
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            String modifiedJsonString = "{\"result\":" + current + "}";
            return modifiedJsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("result");

                for (int i = 0; i<jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    name = jsonObject1.getString("name");
                    city = jsonObject1.getString("city");
                    town = jsonObject1.getString("town");
                    phone = jsonObject1.getString("phone");
                    address = jsonObject1.getString("address");
                    String city_  = city + " , " + town;
                    String phone_ = phone.trim();

                    HashMap<String,String> eczaneler = new HashMap<>();
                    eczaneler.put("name",name);
                    eczaneler.put("city",city_);
                    eczaneler.put("town",town);
                    eczaneler.put("phone",phone_);
                    eczaneler.put("address",address);
                    eczaneList.add(eczaneler);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            ListAdapter adapter = new SimpleAdapter(
                    ListScreen.this,
                    eczaneList,
                    R.layout.row_layout,
                    new String[] {"name","city","phone","address"},
                    new int[]{R.id.textview,R.id.textview2,R.id.textview3,R.id.textview4});
            lv.setAdapter(adapter);
        }
    }
}