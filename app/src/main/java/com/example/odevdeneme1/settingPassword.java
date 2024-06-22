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

public class settingPassword extends AppCompatActivity {
    Button btn1,btn2;
    EditText editEmail,editNewPassword,editNewPasswordAgain,editCurrentPassword;
    String   txtEmail,txtNewPassword,txtNewPasswordAgain,txtCurrentPassword;
    String TAG = "settingPassword";
    FirebaseUser user;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        user=FirebaseAuth.getInstance().getCurrentUser();

        editEmail    =findViewById(R.id.txt_current_email_settingpassword);
        editCurrentPassword        =findViewById(R.id.txt_password_settinpassword);
        editNewPassword   =findViewById(R.id.txt_new_password_settinpassword);
        editNewPasswordAgain   =findViewById(R.id.txt_new_password_again_settingpassword);

        getEmail(); //veritabanından kullanıcı e-mail çekme(kullanıcıya kolaylık açısından)

        btn1=findViewById(R.id.btn_change_password_settingpassword);//change butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();   //fonksiyonun çağırılması
            }
        });

        btn2=findViewById(R.id.btn_back_settingpassword);//geri çıkma butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(settingPassword.this, Settings.class);
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
                editEmail.setText(email);
            }
        }
        else{
            Log.d(TAG, "USER HAS NOT BEEN FOUND");
        }
    }
    public void changePassword(){
        Log.d(TAG, "changePassword() called");
        txtNewPassword          =   editNewPassword.getText().toString();
        txtNewPasswordAgain     =   editNewPasswordAgain.getText().toString();
        txtEmail                =   editEmail.getText().toString();
        txtCurrentPassword      =   editCurrentPassword.getText().toString();

        if (txtNewPassword.isEmpty() || txtNewPasswordAgain.isEmpty() || txtEmail.isEmpty() || txtCurrentPassword.isEmpty()) {
            Toast.makeText(settingPassword.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!txtNewPassword.equals(txtNewPasswordAgain)) {
            Toast.makeText(settingPassword.this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        if (user != null) {
            //tekrardan giriş doğrulaması yapıyoruz ayrıca güvenlik için tekrar şifre istiyoruz
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), txtCurrentPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(txtNewPassword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User password updated.");
                                            Toast.makeText(settingPassword.this,"USER PASSWORD UPDATED SUCCESFULLY",Toast.LENGTH_SHORT).show();
                                            editCurrentPassword.setText("");    //edittextleri temizliyoruz
                                            editNewPassword.setText("");
                                            editNewPasswordAgain.setText("");
                                        }
                                        else {
                                            Log.w(TAG, "Error updating email: ", task.getException());
                                            Toast.makeText(settingPassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Log.w(TAG, "WRONG EMAIL OR PASSWORD: ", task.getException());
                        Toast.makeText(settingPassword.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}