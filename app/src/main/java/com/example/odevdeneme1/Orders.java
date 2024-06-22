package com.example.odevdeneme1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Orders extends AppCompatActivity {

    Button btn1,btn2,btn3,btn4,btn5;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        btn1=findViewById(R.id.btn_list_orders);//sipaiş listele butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(Orders.this, orderList.class);
                list.putExtra("userID", userID);
                startActivity(list);
            }
        });

        btn2=findViewById(R.id.btn_update_order);//sipariş güncelle butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent update = new Intent(Orders.this, orderUpdate.class);
                update.putExtra("userID", userID);
                startActivity(update);
            }
        });

        btn3=findViewById(R.id.btn_add_order);//sipariş ekle butonu
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(Orders.this, orderAdd.class);
                add.putExtra("userID", userID);
                startActivity(add);
            }
        });

        btn4=findViewById(R.id.btn_cancel_order);//sipariş iptal butonu
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cancel = new Intent(Orders.this, orderCancel.class);
                cancel.putExtra("userID", userID);
                startActivity(cancel);
            }
        });

        btn5=findViewById(R.id.btn_back_orders_to_menu);//geri çıkma butonu
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Orders.this, MainMenu.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
}