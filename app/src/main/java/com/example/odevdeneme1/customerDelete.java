package com.example.odevdeneme1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class customerDelete extends AppCompatActivity {
    Button btn1,btn2;
    private EditText editCustomerId;
    private FirebaseFirestore db;
    private static final String TAG = "customerDelete";
    private ListView listViewCustomer;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> customerList,customerIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_delete);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        db = FirebaseFirestore.getInstance();

        customerList   = new ArrayList<>();
        customerIdList = new ArrayList<>();

        listViewCustomer   = findViewById(R.id.listView_customer_customerdelete);

        listCustomer();     //customer listeleme fonksiyonu
        getCustomerId();    //custumer ıd'sini tutma fonksiyonu(listeden seçme)

        btn1=findViewById(R.id.btn_delete_deletecustomer);//delete butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCustomer();
                Intent replace = new Intent(customerDelete.this, customerDelete.class);
                replace.putExtra("userID", userID);
                startActivity(replace);
            }
        });

        btn2=findViewById(R.id.btn_back_customerdelete);//geri çıkma butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(customerDelete.this, Customers.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void listCustomer(){
        Log.d(TAG, "listCustomer() called");
        //listViewleri doldurmak için adapter kullanıyoruz
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerList);
        listViewCustomer.setAdapter(adapter);

        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // bütün bilgileri bu kısımda topluyoruz
                                String customer_id               = document.getString("customer_id");
                                String customer_name_surname     = document.getString("customer_name_surname");
                                String phone_number_of_customer  = document.getString("phone_number_of_customer");

                                String listItem =   "customer id"               +customer_id+
                                                    "\nname surname: "          +customer_name_surname+
                                                    "\nphone number: "          +phone_number_of_customer;
                                customerList.add(listItem);         //görüntülenecek list
                                customerIdList.add(customer_id);    //listeden id bilgisini çekmemiz için gerekli id list
                            }
                            // customersList'e veri eklendiğini kontrol etmek için log mesajı
                            Log.d(TAG, "listCustomer() customerList size: " + customerList.size());
                            adapter.notifyDataSetChanged();//listview içeriğini güncelle
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void getCustomerId(){//customer ListView'inden seçilen kısmın ıd bilgisini alıyoruz(edittexte yazdır)
        Log.d(TAG, "getCustomerId() called");
        editCustomerId = findViewById(R.id.txt_customer_id_customerdelete);
        listViewCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCustomerId    = customerIdList.get(position);
                editCustomerId.setText(selectedCustomerId);
            }
        });
    }
    public void deleteCustomer(){//alınan order id ile seçilen orderin silinmesi
        Log.d(TAG, "deleteCustomer() called");
        db.collection("Customers").document(editCustomerId.getText().toString())
                .delete()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(customerDelete.this,"CUSTOMER DELETED SUCCESSFULLY",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(customerDelete.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}