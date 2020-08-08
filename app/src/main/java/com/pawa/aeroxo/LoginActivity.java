package com.pawa.aeroxo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar ;
    private TextView textRegistration;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences sharedPreferences;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        textRegistration = findViewById(R.id.textViewRegistr);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.requestClientId))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.textViewRegistr).setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            //loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
            usernameEditText.setError(getString(R.string.errot_input_Email));
            loginButton.setEnabled(true);
            textRegistration.setClickable(false);
        } else if (!isPasswordValid(password)) {
            //loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
            passwordEditText.setError(getString(R.string.error_input_password));
            loginButton.setEnabled(true);
            textRegistration.setClickable(false);
        } else {
            //loginFormState.setValue(new LoginFormState(true));
            loginButton.setEnabled(true);
            textRegistration.setClickable(true);
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.login){
            if(isUserNameValid(usernameEditText.getText().toString())&&isPasswordValid(passwordEditText.getText().toString()))
                login(usernameEditText.getText().toString(),passwordEditText.getText().toString());
        }
        if (view.getId()==R.id.textViewRegistr){
            if(isUserNameValid(usernameEditText.getText().toString())&&isPasswordValid(passwordEditText.getText().toString()))
                registration(usernameEditText.getText().toString(),passwordEditText.getText().toString());
        }
        if(view.getId()==R.id.sign_in_button){
            mAuth.signOut();
            signIn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                loadingProgressBar.setVisibility(View.VISIBLE);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                StartMain();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),R.string.error_signin_google,Toast.LENGTH_LONG).show();
                loadingProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_signin_google,Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    public boolean login(String email,String password){
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        textRegistration.setClickable(false);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setResult(1234);
                            FirebaseUser user = mAuth.getCurrentUser();
                            StartMain();
                        } else {
                            Toast.makeText(LoginActivity.this, "Неверные почта или пароль.",
                                    Toast.LENGTH_SHORT).show();
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            loginButton.setEnabled(true);
                            textRegistration.setClickable(true);

                        }

                    }
                });
        if(mAuth.getCurrentUser()!=null)
            return true;
        else return false;
    }
    public boolean registration(String email,String password){
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        textRegistration.setClickable(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, R.string.registration_pass,
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            setResult(1234);
                            StartMain();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, R.string.registration_fault,
                                    Toast.LENGTH_SHORT).show();
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            loginButton.setEnabled(true);
                            textRegistration.setClickable(true);
                        }
                    }
                });
        if(mAuth.getCurrentUser()!=null)
            return true;
        else return false;
    }
    public void StartMain(){
        sharedPreferences = getSharedPreferences("isAuthoraized", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("auth",1);
        editor.apply();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
