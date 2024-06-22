package com.example.odevdeneme1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Customers extends AppCompatActivity {
    Button btn1,btn2,btn3,btn4,btn5;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        btn1=findViewById(R.id.btn_list_customer);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(Customers.this, customerList.class);
                list.putExtra("userID", userID);
                startActivity(list);
            }
        });

        btn2=findViewById(R.id.btn_update_customer);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent update = new Intent(Customers.this, customerUpdate.class);
                update.putExtra("userID", userID);
                startActivity(update);
            }
        });

        btn3=findViewById(R.id.btn_add_customer);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(Customers.this, customerAdd.class);
                add.putExtra("userID", userID);
                startActivity(add);
            }
        });

        btn4=findViewById(R.id.btn_delete_customer);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent delete = new Intent(Customers.this, customerDelete.class);
                delete.putExtra("userID", userID);
                startActivity(delete);
            }
        });

        btn5=findViewById(R.id.btn_back_customer_to_menu);
        btn5.setOnClickListener(new View.OnClickListener() {//geri çıkma butonu
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Customers.this, MainMenu.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
}