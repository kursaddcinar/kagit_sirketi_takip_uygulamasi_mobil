package com.example.odevdeneme1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button btnSign,btnLog;
    private EditText editEmail,editSifre;
    private String txtEmail, txtSifre;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail=(EditText)findViewById    (R.id.txt_log_email);
        editSifre=(EditText) findViewById   (R.id.txt_log_password);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnSign=findViewById(R.id.btn_logToSign);//kayıt butonu
        btnSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sign = new Intent(MainActivity.this, SignIn.class);
                startActivity(sign);
            }
        });

        btnLog=findViewById(R.id.btn_login);//giriş git butonu
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn(v);
            }
        });

    }
   public void logIn(View v){
       txtEmail=editEmail.getText().toString(); //edittextlerden girdileri alıyoruz
       txtSifre=editSifre.getText().toString();

       if(!TextUtils.isEmpty(txtEmail) && !TextUtils.isEmpty(txtSifre) ){   //textlerin boş olmaması kontrol ediliyor
           Task<AuthResult> authResultTask = mAuth.signInWithEmailAndPassword(txtEmail, txtSifre)
                   .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           mUser = mAuth.getCurrentUser();
                           String userID = mUser.getUid();
                           Toast.makeText(MainActivity.this,"LOGIN SUCCESSFUL",Toast.LENGTH_SHORT).show();
                           System.out.println("giris basarili");
                           Intent log = new Intent(MainActivity.this,MainMenu.class);
                           log.putExtra("userID", userID);
                           startActivity(log);
                       }
                   }).addOnFailureListener(this, new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
       }
       else Toast.makeText(this, "EMAIL AND PASSWORD DON'T BE EMPTY", Toast.LENGTH_SHORT).show();
   }
}