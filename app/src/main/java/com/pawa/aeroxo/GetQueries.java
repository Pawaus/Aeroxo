package com.pawa.aeroxo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetQueries {
    public String doGet(String trackNum)
            throws Exception {
        String urlApi = "https://api.track24.ru/tracking.json.php?apiKey=b03370759b96d56d48d0541e9402e86e&pretty=true&domain=demo.track24.ru&lng=en&code=";
        URL obj = new URL(urlApi+trackNum);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();
        connection.disconnect();
        try {
            JSONObject reader = new JSONObject(response.toString());
            JSONsheets parcer = new JSONsheets(reader);
            if (parcer.getStatus().equals("ok")){
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
}
