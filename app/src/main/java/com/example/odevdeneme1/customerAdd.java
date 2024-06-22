package com.example.odevdeneme1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class customerAdd extends AppCompatActivity {
    Button btn1,btn2,btn3;
    private EditText editNameSurname  ,editPhoneNumber;
    private String   txtNameSurname   ,txtPhoneNumber;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_add);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        editNameSurname =   (EditText)findViewById      (R.id.txt_name_surname_customer);//edittext şeklinde isim ve numarayı alıyoruz
        editPhoneNumber =   (EditText)findViewById      (R.id.txt_phone_number_customer);

        db = FirebaseFirestore.getInstance();

        btn1=findViewById(R.id.btn_add_costumer_addcustomer);//ekle butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomer(v);
            }
        });
        btn2=findViewById(R.id.btn_list_costumer_addcustomer);//listeye git butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(customerAdd.this, customerList.class);
                list.putExtra("userID", userID);
                startActivity(list);
            }
        });
        btn3=findViewById(R.id.btn_back_addcustomer);//geri çıkma butonu
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(customerAdd.this, Customers.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void addCustomer(View v){
        txtNameSurname  =   editNameSurname.getText().toString();//stringe dönüştürüyoruz
        txtPhoneNumber  =   editPhoneNumber.getText().toString();

        if(!TextUtils.isEmpty(txtNameSurname) && !TextUtils.isEmpty(txtPhoneNumber) ){// adsoyad ve  telefon boş mu kontrol et
            Map<String, Object> mData = new HashMap<>();
            mData.put("customer_name_surname",      txtNameSurname);//hashmape isim soyısım ve telefonu eklıyoruz
            mData.put("phone_number_of_customer",   txtPhoneNumber);

            db.collection("Customers")
                    .add(mData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            mData.put("customer_id",documentReference.getId());//hashmape id bilgisini ekleyip, databse'ye yenıden yukluyoruz
                            db.collection("Customers").document(documentReference.getId())
                                    .set(mData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            Toast.makeText(customerAdd.this,"ADDING SUCCESFULLY",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            Toast.makeText(customerAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(customerAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else Toast.makeText(this, "NAME_SURNAME AND PHONE DON'T BE EMPTY", Toast.LENGTH_SHORT).show();
    }
}