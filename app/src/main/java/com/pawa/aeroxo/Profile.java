package com.pawa.aeroxo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.graphics.Typeface;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.pawa.aeroxo.bd.Database;
import com.pawa.aeroxo.bd.Track;
import com.pawa.aeroxo.bd.TrackViewModel;

import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class Profile extends Fragment  {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView textName,textPost;
    private de.hdodenhof.circleimageview.CircleImageView profileImage;
    private SharedPreferences sharedPreferences;
    private final String NAME = "Name";
    private final String SURNAME = "Surname";
    private final String EMAIL = "Email";
    private final String POST = "Post";
    private final String ADMIN = "Admin";
    private final String UID = "Uid";
    private final String SUMCHECKS = "SumChecks";
    private LinearLayout linearInScroll,linearProfile;
    private TrackViewModel trackViewModel;
    private ProfileModelView profileModelView;
    ProgressBar progressBar;
    List<Track>tracksDatabase;
    private boolean isUpdate;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        textName.setText(sharedPreferences.getString(NAME,""));
        textPost.setText(sharedPreferences.getString(POST,""));
        sharedPreferences = getActivity().getSharedPreferences("photoUrl",MODE_PRIVATE);
        Glide.with(getActivity()).load(Uri.parse(sharedPreferences.getString("url",""))).into(profileImage);
        profileModelView = new ViewModelProvider(getActivity()).get(ProfileModelView.class);
        trackViewModel = new ViewModelProvider(getActivity()).get(TrackViewModel.class);
        isUpdate = false;
        profileModelView.getTracksFirebase().observe(getActivity(),updateTracks );
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
                if(!isUpdate) {
                    linearInScroll.removeAllViews();
                    for (Track track : tracks) {
                        AddViewTrackInScroll(track);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        textName = (TextView)root.findViewById(R.id.viewNameProfile);
        textPost = (TextView)root.findViewById(R.id.viewPost);
        profileImage = (de.hdodenhof.circleimageview.CircleImageView)root.findViewById(R.id.imageProfile);
        linearInScroll = (LinearLayout)root.findViewById(R.id.linearInScroll);
        root.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"Logout",Toast.LENGTH_LONG).show();
                sharedPreferences = getActivity().getSharedPreferences("isAuthoraized",MODE_PRIVATE);
                Editor editor = sharedPreferences.edit();
                editor.putInt("auth",0);
                editor.putString("url","null");
                editor.apply();
                mAuth.signOut();
                trackViewModel.deleteAll();
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
            }
        });
        root.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*profileModelView.getTracksFirebase().removeObserver(updateTracks);
                profileModelView = new ViewModelProvider(getActivity()).get(ProfileModelView.class);
                profileModelView.getTracksFirebase().observe(getActivity(),updateTracks);*/
                progressBar.setVisibility(View.VISIBLE);
                linearInScroll.removeAllViews();
                trackViewModel.deleteAll();
                profileModelView.updateTracks();
            }
        });
        setRetainInstance(true);
        Log.d("profile","create");
        progressBar = (ProgressBar)root.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        return root;
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
        LinearLayout linearLayout = new LinearLayout(getActivity());
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

    private Observer<List<Track>> updateTracks = new Observer<List<Track>>() {
        @Override
        public void onChanged(List<Track> tracks) {
            isUpdate = true;
            Log.d("profile","tracks in FB "+ String.valueOf(tracks.size()));
            if(tracks.size()==0) {
                tracks = trackViewModel.getTracks().getValue();
                //return;
            }
            Log.d("profile","tracks in FB "+ String.valueOf(tracks.size()));
            Toast.makeText(getActivity(), "Is up to date", Toast.LENGTH_LONG).show();
            Log.d("profile", "get tracks");
            progressBar.setVisibility(View.INVISIBLE);
            offProgress();
            tracksDatabase = trackViewModel.getTracks().getValue();
            //TODO: выводить кнопочку обновить, если треки поменялись
            linearInScroll.removeAllViews();
            for (Track track : tracks) {
                AddViewTrackInScroll(track);
            }
                    trackViewModel.deleteAll();
                    for(Track track:tracks){
                        trackViewModel.insert(track);
                    }

        }
    };

    private void offProgress(){progressBar.setVisibility(View.INVISIBLE);}

}
