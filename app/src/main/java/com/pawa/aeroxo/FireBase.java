package com.pawa.aeroxo;

import android.app.Application;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
    private boolean isUpdateContacts;
    private boolean isUpdateTracks;
    private boolean isUpdateUserData;

    LiveData<List<Track>>getTracksLive(){return tracksLiveData;}
    LiveData<List<Contact>>getContactsLive(){return contactsLiveData;}
    LiveData<Map<String,Object>>getUserDataLive(){return userDataLive;}
    FireBase(){
        mAuth = FirebaseAuth.getInstance();
        isUpdateContacts = false;
        isUpdateTracks = false;
        isUpdateUserData = false;
        getContactsFromFirebase();
        getUserData();
    }

    public boolean isUpdateContacts(){return isUpdateContacts;}

    public boolean isUpdateTracks() {return isUpdateTracks;}

    public boolean isUpdateUserData() {return isUpdateUserData;}
/*
    public String getName(){
        return Objects.requireNonNull(UserData.get("Name")).toString();
    }
    public String getSumChecks(){
        return Objects.requireNonNull(UserData.get("SumChecks")).toString();
    }
    public String getPost(){
        return Objects.requireNonNull(UserData.get("Post")).toString();
    }
    public String getSurname(){
        return Objects.requireNonNull(UserData.get("Surname")).toString();
    }
    public String getUid(){
        return Objects.requireNonNull(UserData.get("Uid")).toString();
    }
    public String getAdmin(){
        return Objects.requireNonNull(UserData.get("AdminMode")).toString();
    }
    public List<Track>getTracks(){
        return tracks;
    }
*/
    public boolean getTracksChina(){
        if(contacts.size()==0) {
            getContactsFromFirebase();
            return false;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("China");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(tracks.size()!=0)
                    return;
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
                    isUpdateTracks = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UpdateStatusTracks();
                    }
                }).start();
                //UpdateStatusTracks();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(),"cancelled",Toast.LENGTH_LONG).show();
                return;
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
            getInformTrack.execute("https://api.track24.ru/tracking.json.php?apiKey=b03370759b96d56d48d0541e9402e86e&pretty=true&domain=demo.track24.ru&lng=en&code="+track.trackNumber);
            try{
                result = getInformTrack.get();
                track.status = result;
                Log.d("Main",result);
                //trackViewModel.insert(track);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
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
                    isUpdateContacts = true;
                    contactsLiveData.setValue(contacts);
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
                        isUpdateUserData = true;
                        userDataLive.setValue(UserData);
                    }else{
                        Log.d("Firebase","No document with name "+mAuth.getCurrentUser().getEmail().toString());
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
