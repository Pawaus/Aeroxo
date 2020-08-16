package com.pawa.aeroxo.ui.cheks;

import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pawa.aeroxo.bd.Check;
import com.pawa.aeroxo.bd.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CheksViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<Check>>checksLD;
    private List<Check>checks = new ArrayList<>();
    private final int NAMECHECK = 0;
    private final int DATE = 1;
    private final int SUM = 2;
    private final int OWNER = 3;
    private final int STORAGECHECK = 4;
    private final int STATUS = 5;
    private final int COMMENT = 6;
    private final int ISVIEWED = 7;
    private String fullName;


    public CheksViewModel() {
        //mText = new MutableLiveData<>();
        //mText.setValue("Скоро тут что-то будет...");

    }
    public void startUpdate(String FullName){
        fullName = FullName;
        checksLD = new MutableLiveData<>();
        updateChecks();
    }

    public LiveData<String> getText() {
        return mText;
    }
    private void updateChecks(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Checks");
        checks.clear();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try {
                    JSONObject reader = new JSONObject(value);
                    JSONArray array = reader.getJSONArray("result");
                    for(int i = 0;i<array.length();i++){
                        JSONArray jsonArray = array.getJSONArray(i);
                        if(jsonArray.getString(OWNER).equals(fullName)){
                            Check check = new Check();
                            check.nameCheck = jsonArray.getString(NAMECHECK);
                            check.Date = jsonArray.getString(DATE);
                            check.Sum  = Float.parseFloat(jsonArray.getString(SUM));
                            check.Owner = jsonArray.getString(OWNER);
                            check.storageCheck = jsonArray.getString(STORAGECHECK);
                            check.Status = jsonArray.getString(STATUS);
                            check.Comment = jsonArray.getString(COMMENT);
                            check.isViewed = Boolean.parseBoolean(jsonArray.getString(ISVIEWED));
                            checks.add(check);
                        }
                    }
                    checksLD.setValue(checks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public LiveData<List<Check>>getChecks(){return checksLD;}
}