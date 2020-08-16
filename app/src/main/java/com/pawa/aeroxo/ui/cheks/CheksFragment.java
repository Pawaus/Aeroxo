package com.pawa.aeroxo.ui.cheks;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pawa.aeroxo.MainActivity;
import com.pawa.aeroxo.R;
import com.pawa.aeroxo.bd.Check;
import com.pawa.aeroxo.bd.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class CheksFragment extends Fragment implements View.OnClickListener{

    private CheksViewModel cheksViewModel;
    private FirebaseAuth mAuth;
    private static final int CAMERA_PIC_REQUEST=1112;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private SharedPreferences sharedPreferences;
    LinearLayout linearLayoutChecks;


    //@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cheks, container, false);
        linearLayoutChecks = (LinearLayout)root.findViewById(R.id.linearChecks);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cheksViewModel = new ViewModelProvider(getActivity()).get(CheksViewModel.class);
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        cheksViewModel.startUpdate(sharedPreferences.getString("fullName",""));
        cheksViewModel.getChecks().observe(getActivity(), new Observer<List<Check>>() {
            @Override
            public void onChanged(List<Check> checks) {
                linearLayoutChecks.removeAllViews();
                Log.d("checks","checks "+ String.valueOf(checks.size()));
                for(Check check:checks){
                    AddViewOnScroll(check);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        //Работа с камерой и отсылкой данных
        /*if(view.getId()==R.id.buttonCam){
            //Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //if(requestCameraPermission()&&requestWriteStoragePermission()&&requestReadStoragePermission())
                saveFullImage();
            //startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
        if (view.getId()==R.id.buttonSendPhoto){
            getRealtimeData();
        }
        //}*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //при возвращении изображения с камеры
        /*
        if (requestCode==CAMERA_PIC_REQUEST) {
        }*/
    }
    //Сохранение фотографии после съемки
    /*
    private void saveFullImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        outputFileUri = FileProvider.getUriForFile(getActivity(),"com.pawa.aeroxo.provider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }*/
    //Запрос разрешения на Камеру
    /*
    private boolean requestCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return true;
        }
        return false;
    }*/
    //Запрос разрешения на память
    /*
    private boolean requestWriteStoragePermission() {

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return true;
        }
        return false;
    }

    private boolean requestReadStoragePermission() {

        final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return true;
        }
        return false;
    }*/
    //Отсылка фотографии в БД
    /*private void SendPhotoByUri(Uri uri){
        StorageReference riversRef = mStorageRef.child("images/rivers.jpg");
        riversRef.putFile(outputFileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(getActivity(),"uploaded successful",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getActivity(),"upload fail",Toast.LENGTH_LONG).show();
                    }
                });
    }*/

private void AddViewOnScroll(Check check){
    LinearLayout linearLayout = new LinearLayout(getActivity());
    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    TextView textNameCheck = new TextView(getContext());
    TextView textDate = new TextView(getContext());
    TextView textSum = new TextView(getContext());
    TextView textIsViewed = new TextView(getContext());
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    linearLayout.setBackground(getActivity().getDrawable(R.drawable.linearprofile2));
    layoutParams.setMargins(32,32,16,32);
    textNameCheck.setText(check.nameCheck);
    textNameCheck.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
    textDate.setText(check.Sum + " от "+check.Date);
    textDate.setTypeface(boldTypeface);
    textDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
    textIsViewed.setText((check.isViewed)?"Учтен":"Не учтен");
    textIsViewed.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
    linearLayout.addView(textNameCheck,layoutParams);
    linearLayout.addView(textDate,layoutParams);
    linearLayout.addView(textIsViewed,layoutParams);
    linearLayoutChecks.addView(linearLayout,layoutParams);
}

}
