package com.pawa.aeroxo;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONsheets {
    public String[][] data;
    private JSONObject localJson;


    JSONsheets(JSONObject jsonObject){
        //TODO:json парсить тут
        localJson = jsonObject;
    }
    public String getStatus(){
        try {
            return localJson.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String getLastStatus(){

        try {
            JSONObject data = null;
            data = localJson.getJSONObject("data");
            JSONObject lastPoint = data.getJSONObject("lastPoint");
            String lastPos = lastPoint.getString("operation");
            lastPos = lastPos + ", " +lastPoint.getString("eventDateTime");
            return lastPos;
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }

    }
}
