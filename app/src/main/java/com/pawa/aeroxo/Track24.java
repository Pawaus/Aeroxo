package com.pawa.aeroxo;

import android.os.AsyncTask;
import android.util.Log;

public class Track24 extends AsyncTask<String,String,String> {

    private String s = "";

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(s);
        return;
    }

    @Override
    protected String doInBackground(String... strings) {

        GetQueries getQueries = new GetQueries();
        try {
            s = getQueries.doGet(strings[0]);
            Log.d("Track24","send request");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }
}

