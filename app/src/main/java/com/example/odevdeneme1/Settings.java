package com.example.odevdeneme1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Settings extends AppCompatActivity {
    Button btn1,btn3,btn4,btn5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        btn1=findViewById(R.id.btn_change_password);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent password = new Intent(Settings.this, settingPassword.class);
                password.putExtra("userID", userID);
                startActivity(password);
            }
        });

        btn3=findViewById(R.id.btn_change_phone);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phone = new Intent(Settings.this, settingPhone.class);
                phone.putExtra("userID", userID);
                startActivity(phone);
            }
        });

        btn4=findViewById(R.id.btn_change_email);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Settings.this, settingEmail.class);
                email.putExtra("userID", userID);
                startActivity(email);
            }
        });

        btn5=findViewById(R.id.btn_back_settings_to_menu);
        btn5.setOnClickListener(new View.OnClickListener() {//geri çıkma butonu
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Settings.this, MainMenu.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
}