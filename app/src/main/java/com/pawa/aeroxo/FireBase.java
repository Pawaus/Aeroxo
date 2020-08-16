package com.pawa.aeroxo;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pawa.aeroxo.bd.Contact;
import com.pawa.aeroxo.bd.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FireBase {
    private FirebaseAuth mAuth;
    private Map<String,Object> UserData;
    private List<Track> tracks = new ArrayList<>();
    private List<Contact>contacts = new ArrayList<>();
    private MutableLiveData<List<Track>> tracksLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Contact>> contactsLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<String,Object>>userDataLive = new MutableLiveData<>();
    private int NAME = 0;
    private int COL = 1;
    private int COMMENT = 3;
    private int TRACK = 6;
    private int RECEIVER = 5;
    private String fullName;
    LiveData<List<Track>>getTracksLive(){return tracksLiveData;}
    LiveData<List<Contact>>getContactsLive(){return contactsLiveData;}
    LiveData<Map<String,Object>>getUserDataLive(){return userDataLive;}
    public String getFullName(){
        return fullName;
    }
    FireBase(){
        mAuth = FirebaseAuth.getInstance();
        getContactsFromFirebase();
        getUserData();
    }
    public boolean getTracksChina(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("China");
        tracks.clear();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                try {
                    JSONObject reader = new JSONObject(value);
                    JSONArray array = reader.getJSONArray("result");
                    for(int i = 0;i<array.length();i++){
                        JSONArray jsonArray = array.getJSONArray(i);
                        if(jsonArray.getString(RECEIVER).equals("")||
                                jsonArray.getString(TRACK).equals("")||
                                jsonArray.getString(NAME).equals("")){
                            continue;
                        }
                        if(jsonArray.getString(RECEIVER).equals(fullName)){
                            Track track = new Track();
                            track.amount = jsonArray.getString(COL);
                            track.Name = jsonArray.getString(NAME);
                            track.trackNumber = jsonArray.getString(TRACK);
                            tracks.add(track);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateStatusTracks();
                    }
                }).start();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(tracks.size()==0)
            return false;
        else
            return true;
    }
    private void UpdateStatusTracks(){
        for(Track track:tracks){
            Track24 getInformTrack = new Track24();
            String result = "";
            Log.d("Query","FB Query");
            getInformTrack.execute(track.trackNumber);
            try{
                result = getInformTrack.get();
                Log.d("Query","FB get Inform");

                if(result.equals("error")){
                    track.status = "try  to refresh";
                }else
                    track.status = result;
                Log.d("Query",result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //if(tracksLiveData.getValue()!=null)
            //tracksLiveData.getValue().clear();
        tracksLiveData.postValue(tracks);
    }
    public void getContactsFromFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Contacts");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(contacts.size()!=0)
                    return;
                try {
                    JSONObject reader = new JSONObject(value);
                    JSONArray array = reader.getJSONArray("result");
                    for(int i = 0;i<array.length();i++){
                        JSONArray jsonArray = array.getJSONArray(i);
                        String tmp = array.getJSONArray(i).getString(0);
                        Contact contact = new Contact();
                        contact.Email = jsonArray.getString(3);
                        contact.FullName = jsonArray.getString(5);
                        contact.Name = jsonArray.getString(1);
                        contact.Surname = jsonArray.getString(0);
                        contacts.add(contact);
                        if(mAuth.getCurrentUser().getEmail().equals(contact.Email)){
                            fullName = contact.FullName;
                        }
                    }
                    contactsLiveData.setValue(contacts);
                    //TODO:Добавление контактов в базу данных
                    getTracksChina();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public boolean getUserData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String,Object> userData = document.getData();
                        UserData = userData;
                        Log.d("Firebase","Have a doc");
                        userDataLive.setValue(UserData);
                    }else{
                        Log.d("Firebase","No document with name "+mAuth.getCurrentUser().getEmail().toString());
                        //TODO:обработка случая, когда у клиента нет странички на сервере
                    }
                }else{
                    Log.d("Firebase","No document");
                }
            }
        });
        if(UserData==null)
            return false;
        else
            return true;
    }
}
