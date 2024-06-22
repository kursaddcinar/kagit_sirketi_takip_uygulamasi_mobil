package com.example.odevdeneme1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainMenu extends AppCompatActivity {
    Button btn1,btn2,btn3,btn4,btn5;
    private TextView    txtBonus;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        txtBonus=(TextView) findViewById     (R.id.txt_bonus);
        getBonus(userID);

        btn1=findViewById(R.id.btn_order);//siparişlere gitme butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent order = new Intent(MainMenu.this, Orders.class);
                order.putExtra("userID", userID);
                startActivity(order);
            }
        });

        btn2=findViewById(R.id.btn_list_product);//ürünlere gitme butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(MainMenu.this, Products.class);
                list.putExtra("userID", userID);
                startActivity(list);
            }
        });

        btn3=findViewById(R.id.btn_costumer);//müşterilere gitme butonu
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent costumer = new Intent(MainMenu.this, Customers.class);
                costumer.putExtra("userID", userID);
                startActivity(costumer);
            }
        });

        btn4=findViewById(R.id.btn_setting);//ayarlara gitme butonu
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setting = new Intent(MainMenu.this, Settings.class);
                setting.putExtra("userID", userID);
                startActivity(setting);
            }
        });

        btn5=findViewById(R.id.btn_exit);//çıkış butonu
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //çıkış yaparken AlertDialog oluşturma
                new AlertDialog.Builder(MainMenu.this)  //çıkış yaparken eminmisiniz diyalogu
                        .setTitle("Çıkış")
                        .setMessage("Çıkmak istediğinize emin misiniz?")
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Kullanıcı "Evet"e tıklarsa çıkış işlemleri yapılacak
                                FirebaseAuth.getInstance().signOut();
                                Intent cikis = new Intent(MainMenu.this, MainActivity.class);
                                cikis.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(cikis);
                                finish();
                            }
                        })
                        .setNegativeButton("İptal", null)
                        .show();
            }
        });
    }
    public void getBonus(String uId){   //bonusun vertabanından çekilmesi
        db = FirebaseFirestore.getInstance();
        db.collection("Bonus").document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // bonus değerini al
                                String bonus = document.getString("number_of_bonus");
                                // TextView'e yazdır
                                txtBonus.setText("   TOTAL BONUS\n    "+bonus);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }
}