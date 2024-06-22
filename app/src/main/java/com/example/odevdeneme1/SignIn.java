package com.example.odevdeneme1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignIn extends AppCompatActivity {
    Button btnBack,btnComplateSign;
    CheckBox checkBoxData;
    private EditText editEmail  ,editSifre  ,editName   ,editSurname    ,editPhone;
    private String   txtEmail   ,txtSifre   ,txtName    ,txtSurname     ,txtPhone ;
    private boolean  Data;
    private FirebaseAuth mAuth;
    private FirebaseUser mEmployee;
    private FirebaseFirestore mFirestore;
    private HashMap<String, Object> mData;
    private HashMap<String, Object> mDataBonus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editName=   (EditText)findViewById      (R.id.txt_name_sign);       //kullanıcı girdilerini alıyoruz(edittext objesi şeklinde)
        editSurname=(EditText)findViewById      (R.id.txt_surname_sign);
        editPhone=  (EditText)findViewById      (R.id.txt_phone_sign);
        editEmail=  (EditText)findViewById      (R.id.txt_email_sign);
        editSifre=  (EditText) findViewById     (R.id.txt_password_sign);

        mAuth       = FirebaseAuth.getInstance();           //doğrulama sistemi
        mFirestore  = FirebaseFirestore.getInstance();      //veritabanı sistemi

        btnBack=findViewById(R.id.btn_sign_to_log);
        btnBack.setOnClickListener(new View.OnClickListener() {//geri çıkma butonu
            @Override
            public void onClick(View v) {
                Intent back = new Intent(SignIn.this, MainActivity.class);
                startActivity(back);
            }
        });

        btnComplateSign=findViewById(R.id.btn_complate_sign);//kayıt etme butonu
        btnComplateSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complateSign(v);
            }
        });
    }
    public void complateSign(View v){
        txtEmail    =editEmail.getText().toString();        //kullanıcı girdilerini string değerlere dönüştürüyoruz
        txtSifre    =editSifre.getText().toString();
        txtName     =editName.getText().toString();
        txtSurname  =editSurname.getText().toString();
        txtPhone    =editPhone.getText().toString();

        checkBoxData =findViewById(R.id.chxbox_data);
        checkBoxData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {  //kvk iznini verip vermediğini kontrol et
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    Data = true;
                }
                else {
                    Data = false;
                }
            }
        });
        if(Data == true){       // kvk izni verildiyse kayıt işlemine başla
            if(!TextUtils.isEmpty(txtEmail) && !TextUtils.isEmpty(txtSifre) ){// email şifre boş mu kontrol et
                mAuth.createUserWithEmailAndPassword(txtEmail,txtSifre)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){//kayıt başarılıysa
                                    //Toast.makeText(SignIn.this, "REGISTRATION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                    mEmployee = mAuth.getCurrentUser();

                                    mDataBonus = new HashMap<>();   //hashmap oluşturup bonusu veritabanına kaydediyoruz
                                    mDataBonus.put("employee_id",mEmployee.getUid());//hatalı mı kontrol et
                                    mDataBonus.put("number_of_bonus","0");           //bonusu 0 şekilde kullanıcı olustur
                                    mFirestore.collection("Bonus").document(mEmployee.getUid())
                                            .set(mDataBonus)
                                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        System.out.println("bonus basarili sekilde girildi");
                                                    }
                                                    else
                                                        Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    mData = new HashMap<>();        //hashmap oluşturup ad soyad ve telefonu veritabanına kaydediyoruz
                                    mData.put("employee_id",mEmployee.getUid());
                                    mData.put("employee_name_and_surname",txtName+" "+txtSurname);
                                    mData.put("phone_number_of_employee",txtPhone);
                                    mFirestore.collection("Employees").document(mEmployee.getUid())
                                            .set(mData)
                                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(SignIn.this, "REGISTRATION SUCCESSFUL", Toast.LENGTH_LONG).show();
                                                        Intent back = new Intent(SignIn.this, MainActivity.class);
                                                        startActivity(back);
                                                    }
                                                    else
                                                        Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else {
                                    Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else Toast.makeText(this, "EMAIL AND PASSWORD DON'T BE EMPTY", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(SignIn.this,"you must consest to data usage!",Toast.LENGTH_SHORT).show();
        }
    }

}
