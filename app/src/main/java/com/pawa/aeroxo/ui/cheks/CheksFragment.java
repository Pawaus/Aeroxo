package com.pawa.aeroxo.ui.cheks;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public class CheksFragment extends Fragment implements View.OnClickListener{

    private CheksViewModel cheksViewModel;
    private FirebaseAuth mAuth;
    private static final int CAMERA_PIC_REQUEST=1112;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private Uri outputFileUri;
    private StorageReference mStorageRef;



    ImageView image;
    EditText textEmail,textPasswd;
    private Button btnAuth,btnReg,btnGetData,btnSendData,btnCam;
    String fromBase;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }else{
            requestReadStoragePermission();
            requestWriteStoragePermission();
            requestCameraPermission();
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cheksViewModel =
                ViewModelProviders.of(this).get(CheksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cheks, container, false);
        //image =(ImageView) root.findViewById(R.id.imageView);
        //textName = (TextView)root.findViewById(R.id.textName);
        mAuth = FirebaseAuth.getInstance();
        //btnCam = (Button)root.findViewById(R.id.buttonCam);
        //btnCam.setOnClickListener(this);
        requestReadStoragePermission();
        requestWriteStoragePermission();
        requestCameraPermission();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        outputFileUri = null;
        //root.findViewById(R.id.buttonSendPhoto).setOnClickListener(this);
        //root.findViewById(R.id.buttonSendData).setOnClickListener(this);
        return root;
    }

    @RequiresPermission(Manifest.permission.CAMERA)

    @Override
    public void onClick(View view) {
        /*if(view.getId()==R.id.buttonCam){
            //Intent cameraIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //if(requestCameraPermission()&&requestWriteStoragePermission()&&requestReadStoragePermission())
                saveFullImage();
            //startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
        if (view.getId()==R.id.buttonSendPhoto){
            getRealtimeData();
        }
        if(view.getId()==R.id.buttonSendData){
            /*FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("myArray");
            incVal(fromBase,myRef);*/

        //}*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CAMERA_PIC_REQUEST) {
            assert data != null;
            //Bitmap thumbnail = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            //image.setImageBitmap(thumbnail);
            image.setImageURI(outputFileUri);
        }
    }
    private void saveFullImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        outputFileUri = FileProvider.getUriForFile(getActivity(),"com.pawa.aeroxo.provider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }
    private boolean requestCameraPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return true;
        }
        return false;
    }
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
    }
    private void SendPhotoByUri(Uri uri){
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
    }

    private void getRealtimeData(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("myArray");
        //final String[] fromBase = new String[1];
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
          //      fromBase[0] = dataSnapshot.getValue(String.class);
                fromBase = value;
                //incVal(value,myRef);
                //Toast.makeText(getActivity(),"recieved",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(),"cancelled",Toast.LENGTH_LONG).show();
                return;
            }
        });
        //incVal(fromBase,myRef);
        /**/

    }
    public void incVal(String getfromBase,DatabaseReference myRef){
        try {
            JSONObject reader = new JSONObject(getfromBase);
            JSONArray array = reader.getJSONArray("result");
            JSONArray result = new JSONArray();
            for(int i = 0;i<array.length();i++){
                String tmp = array.getJSONArray(i).getString(0);
                if(tmp.equals("mtr1")){
                    int t = array.getJSONArray(i).getInt(2);
                    Toast.makeText(getActivity(),Integer.toString(t),Toast.LENGTH_LONG).show();
                    JSONArray inArray = array.getJSONArray(i);
                    JSONArray resultStringTable = new JSONArray();
                    resultStringTable.put(inArray.get(0));
                    resultStringTable.put(inArray.get(1));
                    resultStringTable.put(inArray.getInt(2) +10);
                    resultStringTable.put(inArray.get(3));
                    resultStringTable.put(inArray.get(4));
                    result.put(i,resultStringTable);

                }else {
                    result.put(i,array.getJSONArray(i));
                }
            }
            JSONObject toServer = new JSONObject();
            toServer.put("result",result);
            myRef.setValue(toServer.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
