package com.example.ander.walmartapi;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button mCerealButton;
    Button mChocolateButton;
    Button mTeaButton;
    ListView mListView;
    ArrayList<String> marrayList;
    ArrayAdapter mArrayAdapter;

    AsyncTask<String, Void, Void> mtask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCerealButton = (Button) findViewById(R.id.cereal);
        mChocolateButton = (Button) findViewById(R.id.chocolate);
        mTeaButton = (Button) findViewById(R.id.tea);

        mListView = (ListView) findViewById(R.id.list_view_item);

        marrayList = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, marrayList);

        getItems("http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=75r33uwkrhumawsxg4w3m9dw");

        mListView.setAdapter(mArrayAdapter);

        ConnectivityManager conMag = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMag.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Toast.makeText(MainActivity.this, "The network is fine", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
        }

        mCerealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayAdapter.clear();
                getItems("http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=75r33uwkrhumawsxg4w3m9dw");
            }
        });

        mChocolateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayAdapter.clear();
                getItems("http://api.walmartlabs.com/v1/search?query=chocolate&format=json&apiKey=75r33uwkrhumawsxg4w3m9dw");
            }
        });

        mTeaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrayAdapter.clear();
                getItems("http://api.walmartlabs.com/v1/search?query=tea&format=json&apiKey=75r33uwkrhumawsxg4w3m9dw");
            }
        });


    }

//    READER

    private String readIt(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream)); // it takes an input string reader arg

        String lineRead;

        // while loop as long as the reader has a line to read
        while ((lineRead = bufferedReader.readLine()) != null) {
            stringBuilder.append(lineRead);
        }

        // the String Builder will return a string of the items
        return stringBuilder.toString();
    }

//  DOWNLOADER

    private void downloadUrl(String myUrl) throws IOException, JSONException {
        InputStream inputStream = null;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // Starts the query
            connection.connect();
            inputStream = connection.getInputStream();

            //1 Converts the InputStream into a string
            String contentAsString = readIt(inputStream);
            //2 converts the string to return a list of specific objects
            parseJson(contentAsString);

            // Makes sure that the InputStream inputStream closed after the app inputStream
            // finished using it.
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


// ASYNC METHOD - FOR DOWNLOADER METHOD

    public void getItems(String webURL) {
        mtask = new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    downloadUrl(strings[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void urlText) {
                super.onPostExecute(urlText);
                mArrayAdapter.notifyDataSetChanged();
            }
        }.execute(webURL);
    }

//  PARSE JSON OBJECTS TO STRING AND INSERT INTO ARRAY

    private void parseJson(String contentAsString) throws JSONException {
        JSONObject root = new JSONObject(contentAsString);
        JSONArray array = root.getJSONArray("items");
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i); // get JSON obj at index i
            marrayList.add(item.getString("name"));
        }
    }

}
