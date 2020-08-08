package com.pawa.aeroxo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetQueries {
    public String doGet(String url)
            throws Exception {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        //add reuqest header
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
        try {
            JSONObject reader = new JSONObject(response.toString());
            JSONObject data = reader.getJSONObject("data");
            JSONObject lastPoint = data.getJSONObject("lastPoint");
            String lastPos = lastPoint.getString("operation");
            lastPos = lastPos + ", " +lastPoint.getString("eventDateTime");
            return lastPos;

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return response.toString();
    }
}
