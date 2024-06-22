package com.example.odevdeneme1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class settingPhone extends AppCompatActivity {
    Button btn1,btn2;
    EditText    editCurrentPhone,editNewPhone,editNewPhoneAgain;
    String      txtNewPhone,txtNewPhoneAgain;
    String TAG = "settingPhone";
    FirebaseFirestore db;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_phone);


        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        db  = FirebaseFirestore.getInstance();

        editCurrentPhone    =findViewById(R.id.txt_current_phone);
        editNewPhone        =findViewById(R.id.txt_new_phone);
        editNewPhoneAgain   =findViewById(R.id.txt_new_phone_again);

        getPhone(userID);   //mevcut telefonun veritabanın çekme fonkssiyonu

        btn1=findViewById(R.id.btn_phone);//change butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhone(userID);
            }
        });

        btn2=findViewById(R.id.btn_back_settings_phone);//geri çıkma butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(settingPhone.this, Settings.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void getPhone(String uId){
        Log.d(TAG, "getPhone() called");
        db.collection("Employees").document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                editCurrentPhone.setText(document.getString("phone_number_of_employee"));
                            } else {
                                Log.w(TAG, "No such document");
                            }
                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void changePhone(String uId){
        txtNewPhone         = editNewPhone.getText().toString();
        txtNewPhoneAgain    = editNewPhoneAgain.getText().toString();

        Log.d(TAG, "changePhone() called");
        if(txtNewPhone.equals(txtNewPhoneAgain)){
            db.collection("Employees").document(uId)
                    .update("phone_number_of_employee",txtNewPhone)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                            Toast.makeText(settingPhone.this,"PHONE NUMBER UPDATED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                            editNewPhone.setText("");
                            editNewPhoneAgain.setText("");
                            getPhone(uId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
        else {
            Log.d(TAG, "PHONE NUMBERS ARE NOT SAME");
            Toast.makeText(settingPhone.this,"PHONE NUMBERS ARE NOT SAME",Toast.LENGTH_SHORT).show();
        }
    }
}