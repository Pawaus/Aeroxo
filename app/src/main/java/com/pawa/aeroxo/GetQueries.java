package com.pawa.aeroxo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetQueries {
    public String doGet(String trackNum)
            throws Exception {
        String response;
        response = Query(trackNum);
        try {
            JSONObject reader = new JSONObject(response.toString());
            JSONsheets parcer = new JSONsheets(reader);
            if (parcer.getStatus().equals("ok")){
                Log.d("Query","start parce");
                if(!parcer.getLastStatus().equals("error"))
                    return parcer.getLastStatus();
                else
                    return "error";
            }else{
                return "error";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }
    private String Query(String trackNumber){
        String urlApi = "https://api.track24.ru/tracking.json.php?apiKey=b03370759b96d56d48d0541e9402e86e&pretty=true&domain=demo.track24.ru&lng=en&code=";
        HttpURLConnection connection = null;
        String returnValue = null;
        try {

            URL obj = new URL(urlApi+trackNumber);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(100);

            Log.d("Query","send query "+ trackNumber);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();
            connection.disconnect();
            Log.d("Query","get Request");
            returnValue = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responceCode = 0;
        try {
            responceCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(responceCode!=HttpURLConnection.HTTP_OK){
            return Query(trackNumber);
        }else
            return returnValue;
    }
}
