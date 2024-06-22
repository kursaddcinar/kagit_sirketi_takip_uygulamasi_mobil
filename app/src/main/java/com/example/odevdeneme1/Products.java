package com.example.odevdeneme1;

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
import java.util.HashMap;
import java.util.Map;

public class Products extends AppCompatActivity {
    Button btn;

    private FirebaseFirestore db;
    private static final String TAG = "Products";
    private ListView listViewProduct;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productList;
    private Map<String, String> productTypeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        db = FirebaseFirestore.getInstance();

        listViewProduct = findViewById(R.id.list_product);
        productList     = new ArrayList<>();
        productTypeMap  = new HashMap<>();

        loadProductTypes(); //product listi listele fonksiyonu genelden özele fonksiyonları iç içe çağır



        btn=findViewById(R.id.btn_back_products_to_menu);
        btn.setOnClickListener(new View.OnClickListener() {//geri çıkma butonu
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Products.this, MainMenu.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void loadProductTypes(){////en özel bilgimiz olan ürün tipini alıp mape eklitoruz ve loadProducts() fonksiyonunu çağırıyoruz
        Log.d(TAG, "loadProductTypes() called");
        db.collection("ProductType")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String type_id = document.getString("type_id");
                                String type_name = document.getString("type_name");
                                productTypeMap.put(type_id, type_name);
                            }
                            loadProducts();
                        } else {
                            Log.d(TAG, "Error getting product types: ", task.getException());
                        }
                    }
                });
    }
    public void loadProducts(){//product_name bilgisini alıyoruz ve type name ile birleştiririyoruz
        Log.d(TAG, "loadProducts() called");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productList);
        listViewProduct.setAdapter(adapter);

        db.collection("Product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Her belgeden gerekli bilgileri al ve görüntülenecek liste elamanlarını ayarla
                                String product_id       = document.getString("product_id");
                                String stock_of_number  = document.getString("stock_of_number");
                                String product_name     = document.getString("product_name");
                                String type_id          = document.getString("type_id");
                                String type_name        = productTypeMap.get(type_id);

                                String listItem =   "product name: "        +product_name+
                                                    "\nstock of number: "   +stock_of_number+
                                                    "\ntype : "         +type_name;
                                productList.add(listItem);
                            }
                            // productList'e veri eklendiğini kontrol etmek için log mesajı ekle
                            Log.d(TAG, "loadProducts() productList size: " + productList.size());
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}