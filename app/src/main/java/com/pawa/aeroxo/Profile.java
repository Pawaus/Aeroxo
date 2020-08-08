package com.pawa.aeroxo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.graphics.Typeface;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import androidx.room.Room;

import android.os.Handler;

import android.util.Log;
import android.util.TypedValue;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.pawa.aeroxo.bd.Database;
import com.pawa.aeroxo.bd.Track;
import com.pawa.aeroxo.bd.TrackViewModel;

import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class Profile extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView textName,textPost;
    private ImageView profileImage;
    Handler handlerUpdate = new Handler();
    //private Handler handlerUpdateTracks = new Handler();
    private SharedPreferences sharedPreferences;
    private final String NAME = "Name";
    private final String SURNAME = "Surname";
    private final String EMAIL = "Email";
    private final String POST = "Post";
    private final String ADMIN = "Admin";
    private final String UID = "Uid";
    private final String SUMCHECKS = "SumChecks";
    //private Database db;
    private LinearLayout linearInScroll,linearProfile;
    private TrackViewModel trackViewModel;
    private ProfileModelView profileModelView;
    private boolean isUpdateTrack;
    private boolean isLoadTracks;
    private boolean isStartLoadTracks;
    ProgressBar progressBar;
    List<Track>tracksDatabase;
    private AuthFirebase authFirebase;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user!=null) {
            profileModelView = new ViewModelProvider(getActivity()).get(ProfileModelView.class);
            trackViewModel = new ViewModelProvider(getActivity()).get(TrackViewModel.class);
            profileModelView.getTracksFirebase().observe(getActivity(), new Observer<List<Track>>() {
                @Override
                public void onChanged(List<Track> tracks) {
                    //Toast.makeText(getActivity(), "Is up to date", Toast.LENGTH_LONG).show();
                    Log.d("profile", "get tracks");
                    //progressBar.setVisibility(View.INVISIBLE);
                    //offProgress();
                    tracksDatabase = trackViewModel.getTracks().getValue();

                    for (Track track : tracks) {
                        //AddViewTrackInScroll(track);
                        boolean isContain = false;
                        for (Track track1 : tracksDatabase) {
                            if (track.equals(track1))
                                isContain = true;
                        }
                        if (!isContain)
                            trackViewModel.insert(track);
                    }

                }
            });
            profileModelView.getUserData().observe(getActivity(), new Observer<Map<String, Object>>() {
                @Override
                public void onChanged(Map<String, Object> stringObjectMap) {
                    updateUserDataToPreferences(stringObjectMap);
                }
            });
            trackViewModel.getTracks().observe(getActivity(), new Observer<List<Track>>() {
                @Override
                public void onChanged(List<Track> tracks) {
                    Log.d("profile", "on change database");
                    Log.d("profile", String.valueOf(trackViewModel.getTracks().getValue().size()));
                    for (Track track : tracks) {
                        AddViewTrackInScroll(track);
                    }
                }
            });
        }else{
            handlerUpdate.postDelayed(update,40);
            Log.d("profile", "post delay start");
        }
    }
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            if(user!=null) {
                profileModelView = new ViewModelProvider(getActivity()).get(ProfileModelView.class);
                trackViewModel = new ViewModelProvider(getActivity()).get(TrackViewModel.class);
                profileModelView.getTracksFirebase().observe(getActivity(), new Observer<List<Track>>() {
                    @Override
                    public void onChanged(List<Track> tracks) {
                        Toast.makeText(getActivity(), "Is up to date", Toast.LENGTH_LONG).show();
                        Log.d("profile", "get tracks");
                        //progressBar.setVisibility(View.INVISIBLE);
                        //offProgress();
                        tracksDatabase = trackViewModel.getTracks().getValue();

                        for (Track track : tracks) {
                            //AddViewTrackInScroll(track);
                            boolean isContain = false;
                            for (Track track1 : tracksDatabase) {
                                if (track.equals(track1))
                                    isContain = true;
                            }
                            if (!isContain)
                                trackViewModel.insert(track);
                        }

                    }
                });
                profileModelView.getUserData().observe(getActivity(), new Observer<Map<String, Object>>() {
                    @Override
                    public void onChanged(Map<String, Object> stringObjectMap) {
                        updateUserDataToPreferences(stringObjectMap);
                    }
                });
                trackViewModel.getTracks().observe(getActivity(), new Observer<List<Track>>() {
                    @Override
                    public void onChanged(List<Track> tracks) {
                        Log.d("profile", "on change database");
                        Log.d("profile", String.valueOf(trackViewModel.getTracks().getValue().size()));
                        for (Track track : tracks) {
                            AddViewTrackInScroll(track);
                        }
                    }
                });
            }else{
                handlerUpdate.postDelayed(update,40);
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                Log.d("profile", "post delay runnable");
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);

        textName = (TextView)root.findViewById(R.id.viewNameProfile);
        textPost = (TextView)root.findViewById(R.id.viewPost);
        profileImage = (ImageView)root.findViewById(R.id.imageProfile);
        linearInScroll = (LinearLayout)root.findViewById(R.id.linearInScroll);

        authFirebase = new ViewModelProvider(getActivity()).get(AuthFirebase.class);
        authFirebase.getAuth().observe(getActivity(), new Observer<FirebaseAuth>() {
            @Override
            public void onChanged(FirebaseAuth firebaseAuth) {

            }
        });

        //Log.d("DataBase",String.valueOf(trackViewModel.getCount()));
        isUpdateTrack = false;
        isLoadTracks = false;
        isStartLoadTracks = false;
        root.findViewById(R.id.btnSignOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getActivity().getSharedPreferences("isAuthoraized",MODE_PRIVATE);
                Editor editor = sharedPreferences.edit();
                editor.putInt("auth",0);
                editor.apply();
                mAuth.signOut();
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
            }
        });
        setRetainInstance(true);
        Log.d("profile","create");

        progressBar = (ProgressBar)root.findViewById(R.id.progress_circular);
        //progressBar.setVisibility(View.VISIBLE);
        return root;
    }

    /*private Runnable getInformation = new Runnable() {
        @Override
        public void run() {
            if(!FB.getUserData()){
                //handlerUpdateUserData.postDelayed(this,50);
            }else{
                //updateUserDataToPreferences();
            }
        }
    };
    private Runnable getTracks = new Runnable() {
        @Override
        public void run() {
            if (!isLoadTracks) {
                if (!isStartLoadTracks) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db = Room.databaseBuilder(getContext(), Database.class, "database").build();
                            tracksDatabase = db.trackDao().getAll();
                            isLoadTracks = true;
                        }
                    }).start();
                    isStartLoadTracks = true;
                    handlerUpdateTracks.postDelayed(this,50);
                } else {
                    handlerUpdateTracks.postDelayed(this, 50);
                }
            } else {
                Log.d("Track", String.valueOf(tracksDatabase.size()) + " In DB");
                linearInScroll.removeAllViews();
                for (Track track : tracksDatabase) {
                    AddViewTrackInScroll(track);
                }
            }
        }

    };*/
    @Override
    public void onResume() {
        super.onResume();
        Log.d("profile","resume");
        if(mAuth!=null){
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            if(user!=null) {
                sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
                textName.setText(sharedPreferences.getString(NAME,""));
                textPost.setText(sharedPreferences.getString(POST,""));
            }
        }
        //loadFromDB();
    }
    private void updateUserDataToPreferences(Map<String,Object> userData){
        sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        String name = userData.get(NAME).toString();
        String surname = userData.get(SURNAME).toString();
        String post = userData.get(POST).toString();
        String admin = userData.get(POST).toString();
        String sumchecks = userData.get(SUMCHECKS).toString();
        Editor ed = sharedPreferences.edit();
        if(!sharedPreferences.getString(NAME,"").equals(name)){
            textName.setText(userData.get(NAME).toString());
            ed.putString(NAME,name);
        }
        if(!sharedPreferences.getString(SURNAME,"").equals(surname)){
            ed.putString(SURNAME,surname);
        }
        if(!sharedPreferences.getString(EMAIL,"").equals(mAuth.getCurrentUser().getEmail())){
            ed.putString(EMAIL,mAuth.getCurrentUser().getEmail());
        }
        if(!sharedPreferences.getString(POST,"").equals(post)){
            textPost.setText(post);
            ed.putString(POST,post);
        }
        if(!sharedPreferences.getString(ADMIN,"").equals(admin)){
            ed.putString(ADMIN,admin);
        }
        if(!sharedPreferences.getString(UID,"").equals(mAuth.getCurrentUser().getUid())){
            ed.putString(UID,mAuth.getCurrentUser().getUid());
        }
        if(!sharedPreferences.getString(SUMCHECKS,"").equals(sumchecks)){
            ed.putString(SUMCHECKS,sumchecks);
        }
        ed.apply();
    }
    private void AddViewTrackInScroll(Track track){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textNameTrack = new TextView(getContext());
        TextView textComment = new TextView(getContext());
        TextView textStatus = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16,16,16,16);
        textNameTrack.setText(track.Name+"  Кол-во: "+track.amount);
        textNameTrack.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        textComment.setText(track.trackNumber);
        textComment.setTypeface(boldTypeface);
        textComment.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textStatus.setText(track.status);
        textStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        linearLayout.addView(textNameTrack,layoutParams);
        linearLayout.addView(textComment,layoutParams);
        linearLayout.addView(textStatus,layoutParams);
        linearInScroll.addView(linearLayout,layoutParams);
    }
    private void offProgress(){progressBar.setVisibility(View.INVISIBLE);}
    private void loadFromDB(){
        tracksDatabase = trackViewModel.getTracks().getValue();
        for (Track track:tracksDatabase){
            AddViewTrackInScroll(track);
        }
    }
}
