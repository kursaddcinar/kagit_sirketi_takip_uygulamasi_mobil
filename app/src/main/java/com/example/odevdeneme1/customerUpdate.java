package com.example.odevdeneme1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class customerUpdate extends AppCompatActivity {
    Button btn1,btn2;
    private EditText editNewNumber,editCustomerId,editNameSurname;
    String txtNewNumber,txtCustomerId,txtNameSurname;
    private FirebaseFirestore db;
    private static final String TAG = "customerUpdate";
    private ListView listViewCustomer;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> customerList ;
    private Map<String, String> customerMap;
    private HashMap<String, Object> newCustomerMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custumer_update);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        db = FirebaseFirestore.getInstance();
        listCustomer();//customer listi listele //prodcut list bu fonksiyonunu içerisinde tanımlı senkron olması için

        editCustomerId  = findViewById(R.id.txt_customer_id_customerupdate);
        editNameSurname = findViewById(R.id.txt_customer_name_surname_customerupdate);
        editNewNumber   = findViewById(R.id.txt_customer_phone_customerupdate);

        getValues();//seçilen değerleri edittextlere yazdır

        btn1=findViewById(R.id.btn_update_customerupdate);//update butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCustomer();
                Intent replace = new Intent(customerUpdate.this, customerUpdate.class);
                replace.putExtra("userID", userID);
                startActivity(replace);
            }
        });

        btn2=findViewById(R.id.btn_back_customerupdate);//geri çıkma butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(customerUpdate.this, Customers.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void listCustomer(){//Customers bilgilerini listView'e çekiyoruz
        Log.d(TAG, "listCustomer() called");
        listViewCustomer = findViewById(R.id.listView_customer_customerupdate);
        customerList     = new ArrayList<>();        //listView için gerekli listede toparlıyoruz
        customerMap      = new HashMap<>();          //mapler her yeden erişilebilir olması için tanımladık

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerList);
        listViewCustomer.setAdapter(adapter);    //listView'i doldurmak için adapter
        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String customer_id  = document.getString("customer_id");
                                String customer_name_surname         = document.getString("customer_name_surname");
                                String phone_number_of_customer       = document.getString("phone_number_of_customer");

                                String listItem =
                                                "id: " + customer_id +
                                                "\nname surname: " + customer_name_surname +
                                                "\nphone number: " + phone_number_of_customer ;
                                // Liste şeklinde görüntüleme için listeye ekle
                                customerList.add(listItem);
                                customerMap.put(listItem,customer_id);
                            }
                            // Değişiklikleri güncelle
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void getValues(){//seçilen bilgilerini alıyoruz ve edittext'lere yazdırıyoruz
        Log.d(TAG, "getValues() called");

        listViewCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCustomer = customerList.get(position);
                // İlgili müşteri bilgilerini al
                String selectedCustomerId = customerMap.get(selectedCustomer);
                // Verileri EditText bileşenlerine yazdır
                editCustomerId.setText(selectedCustomerId);

                // Seçilen müşterinin diğer bilgilerini ayırarak EditText bileşenlerine yazdırın
                String[] customerDetails = selectedCustomer.split("\n");
                if (customerDetails.length == 3) {
                    editNameSurname.setText(customerDetails[1].split(": ")[1]);
                    editNewNumber.setText(customerDetails[2].split(": ")[1]);
                }
            }
        });
    }
    public void updateCustomer(){
        Log.d(TAG, "updateCustomer() called");
        txtCustomerId       = editCustomerId.getText().toString();
        txtNameSurname      = editNameSurname.getText().toString();
        txtNewNumber        = editNewNumber.getText().toString();

        newCustomerMap = new HashMap<>();
        newCustomerMap.put("customer_id",txtCustomerId);
        newCustomerMap.put("customer_name_surname",txtNameSurname);
        newCustomerMap.put("phone_number_of_customer",txtNewNumber);

        db.collection("Customers").document(txtCustomerId)
                .update(newCustomerMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(customerUpdate.this,"CUSTOMER UPDATE SUCCESSFULLY.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}