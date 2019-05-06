package com.example.listview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameView;
    private EditText passwordView;
    private Button loginBtn;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private FirebaseAuth mAuth;

    private String username;
    private String password;

    private Intent intent;
    private String dRole, dPassword, dEmail;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();

        mAuth = FirebaseAuth.getInstance();

        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_button);

        usernameView.addTextChangedListener(loginTextWatcher);
        passwordView.addTextChangedListener(loginTextWatcher);

        intent = new Intent(this, MainActivity.class);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameView.getText().toString();
                password = passwordView.getText().toString();

                    databaseRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = (usernameView.getText().toString().substring(0
                                    ,usernameView.getText().toString().indexOf("@"))).replace("."," ");

                                Set users = ((Map) dataSnapshot.getValue()).keySet();

                                boolean isFindUser = false;

                                for (Object i : users){
                                    if(i.toString().toLowerCase().contains(name.toLowerCase())){
                                        name = i.toString();
                                        Map user = (Map) ((Map) dataSnapshot.getValue()).get(name);

                                        dPassword = user.get("Password").toString();
                                        dRole = user.get("Role").toString();
                                        dEmail = user.get("Email").toString();

                                        if (password.equals(dPassword) && username.toLowerCase().equals(dEmail.toLowerCase())) {
                                            isFindUser = true; break;
                                        }
                                    }
                                }
                                if (isFindUser){

                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                    intent.putExtra("username",name);
                                    intent.putExtra("role",dRole);
                                    MainActivity.signIn = true;
                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();startActivity(intent);
                                }

                                //check if Authentication Failed
                                else {
                                    Toast.makeText(getApplicationContext(), "The username or password is incorrect", Toast.LENGTH_LONG).show();
                                    passwordView.setText("");
                                }
                            }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Database Error", Toast.LENGTH_SHORT).show();
                            usernameView.setText("");
                            passwordView.setText("");
                        }
                    });
//                }
            }
        });
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = usernameView.getText().toString().trim();
            String passwordInput = passwordView.getText().toString().trim();

            loginBtn.setEnabled(!usernameInput.isEmpty() && ! passwordInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(usernameInput).matches());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
