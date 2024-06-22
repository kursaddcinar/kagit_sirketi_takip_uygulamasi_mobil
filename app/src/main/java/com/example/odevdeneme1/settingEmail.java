package com.example.odevdeneme1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class settingEmail extends AppCompatActivity {
    Button btn1,btn2;
    EditText editCurrentEmail,editNewEmail,editNewEmailAgain,editPasswordEmail;
    String   txtNewEmail,txtNewEmailAgain,txtPasswordEmail;
    String TAG = "settingEmail";
    FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_email);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        user=FirebaseAuth.getInstance().getCurrentUser();

        editCurrentEmail    =findViewById(R.id.txt_current_email_settingemail);
        editNewEmail        =findViewById(R.id.txt_new_email_settingemail);
        editNewEmailAgain   =findViewById(R.id.txt_new_email_again);
        editPasswordEmail   =findViewById(R.id.txt_password_settingemail);

        getEmail(); //mevcut e-maili getir fonksiyonu

        btn1=findViewById(R.id.btn_change_email_settingemail);//change butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });

        btn2=findViewById(R.id.btn_back_settingemail);//geri çıkma butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(settingEmail.this, Settings.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void getEmail(){
        Log.d(TAG, "getEmail() called");
        if (user != null) {
            String email = user.getEmail();
            System.out.println(email);
            if (email != null){
                editCurrentEmail.setText(email);
            }
        }
        else{
            Log.d(TAG, "USER HAS NOT BEEN FOUND");
        }
    }
    public void changeEmail(){
        Log.d(TAG, "changeEmail() called");
        txtNewEmail         =   editNewEmail.getText().toString();
        txtNewEmailAgain    =   editNewEmailAgain.getText().toString();
        txtPasswordEmail    =   editPasswordEmail.getText().toString();

        final String currentPassword = editPasswordEmail.getText().toString().trim();
        //textler boşmu kontrol et
        if (txtNewEmail.isEmpty() || txtNewEmailAgain.isEmpty() || currentPassword.isEmpty()) {
            Toast.makeText(settingEmail.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!txtNewEmail.equals(txtNewEmailAgain)) {//eğer mevcut email ile eski email aynı değilse
            Toast.makeText(settingEmail.this, "New emails do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {//güvenlik için giirilen giriş bilgileri doğru mu kontrol et
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updateEmail(txtNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User email address updated.");
                                    Toast.makeText(settingEmail.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                    editNewEmail.setText("");
                                    editNewEmailAgain.setText("");
                                    editPasswordEmail.setText("");
                                    getEmail();
                                } else {
                                    Log.w(TAG, "Error updating email: ", task.getException());
                                    Toast.makeText(settingEmail.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.w(TAG, "WRONG EMAIL OR PASSWORD: ", task.getException());
                        Toast.makeText(settingEmail.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}