package com.example.odevdeneme1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class customerList extends AppCompatActivity {
    Button btn1;
    private FirebaseFirestore db;
    private static final String TAG = "customerList";
    private ListView listViewCustomer;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> customersList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        listCustomer();
        btn1=findViewById(R.id.btn_back_customerlist);//geri çıkma butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(customerList.this, Customers.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void listCustomer(){
        Log.d(TAG, "listCustomer() called");
        db = FirebaseFirestore.getInstance();
        listViewCustomer   = findViewById(R.id.listView_customer_list);

        customersList   = new ArrayList<>();

        //listViewleri doldurmak için adapter kullanıyoruz
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customersList);
        listViewCustomer.setAdapter(adapter);

        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // bütün bilgileri bu kısımda topluyoruz
                                String customer_name_surname     = document.getString("customer_name_surname");
                                String phone_number_of_customer  = document.getString("phone_number_of_customer");

                                String listItem =   "name surname: "        +customer_name_surname+
                                                    "\nphone number: "      +phone_number_of_customer;
                                customersList.add(listItem);
                            }
                            // customersList'e veri eklendiğini kontrol etmek için log mesajı
                            Log.d(TAG, "loadProducts() productList size: " + customersList.size());
                            adapter.notifyDataSetChanged();//listview içeriğini güncelle
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}