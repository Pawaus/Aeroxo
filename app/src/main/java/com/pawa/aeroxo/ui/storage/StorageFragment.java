package com.pawa.aeroxo.ui.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.pawa.aeroxo.BarcodeCaptureActivity;
import com.pawa.aeroxo.MainActivity;
import com.pawa.aeroxo.R;
import com.pawa.aeroxo.ui.cheks.CheksFragment;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StorageFragment extends Fragment {

    private StorageViewModel storageViewModel;
    TextView textName,textVendor;
    Button oneButton,fourButton;
    String idProduct = "";
    //com.google.android.material.textfield.TextInputEditText editVal;
    private static final int RC_BARCODE_CAPTURE = 9001;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        storageViewModel =
                ViewModelProviders.of(this).get(StorageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_storage, container, false);
        textName = (TextView)root.findViewById(R.id.textName);
        textVendor = (TextView)root.findViewById(R.id.vendorCode);
        oneButton = (Button)root.findViewById(R.id.oneButton);
        fourButton= (Button)root.findViewById(R.id.fourButton);
      //  editVal = (com.google.android.material.textfield.TextInputEditText)root.findViewById(R.id.value);
        View.OnClickListener onOneClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textName.setText("1 press");
                sendData("1");
            }
        };
        View.OnClickListener onFourClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textName.setText("4Press");
                sendData("4");
            }
        };
        oneButton.setOnClickListener(onOneClick);
        fourButton.setOnClickListener(onFourClick);
        Intent intent = new Intent(StorageFragment.this.getActivity(), BarcodeCaptureActivity.class);
        //startActivityForResult(intent,RC_BARCODE_CAPTURE);
        return root;
    }

    void sendData(String value){
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Adding Item","Please wait");
        //final String id = etCol1.getText().toString().trim();
        //EditText val = (EditText)findViewById(R.id.editNumber);
        //final String value = editVal.getText().toString().trim();
        final String val = value;
        final String incDec = "1";
        if(value.length() == 0) {
            Toast.makeText(getActivity(), "Введите количество", Toast.LENGTH_LONG).show();
            loading.cancel();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwL3Wbfcgb0Lra16PwuQzRPGI7_thtvUC1Mi9TFzR8cJ61hScQ/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(getActivity(),"error to request",Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","setValueById");
                parmas.put("id",idProduct);
                parmas.put("act",incDec);
                parmas.put("value",val);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        queue.add(stringRequest);
        //Toast.makeText(getActivity(),"id: "+idProduct+" value: "+val.getText().toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==RC_BARCODE_CAPTURE){
            if(resultCode== CommonStatusCodes.SUCCESS){
                if (data!=null){
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String All = barcode.displayValue;
                    try{
                        JSONObject reader = new JSONObject(All);
                        textName.setText(reader.getString("Name"));
                        textVendor.setText(reader.getString("VendorCode"));
                        idProduct = reader.getString("ID");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
