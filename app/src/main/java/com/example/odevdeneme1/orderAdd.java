package com.example.odevdeneme1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;



public class orderAdd extends AppCompatActivity {
    Button btn1,btn2,btn3;
    private EditText editCostumerId,editProductId,editNumber,editDate;
    String txtCostumerId,txtProductId,txtNumber,txtDate;
    private FirebaseFirestore db;
    private static final String TAG = "orderAdd";
    private ListView listViewCustomers,listViewProduct;
    private TextView selectedCustomerIdTextView,selectedProductIdTextView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> customersList,productList;
    private Map<String, String> customerMap,productMap,productTypeMap;
    private HashMap<String, Object> newOrderDetailMap,newOrderMap,newBonusMap,newStockMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        db = FirebaseFirestore.getInstance();
        listCostumer();
        getCostumerId();

        newOrderDetailMap = new HashMap<>();                        //detail mapinin oluşturulası

        listViewProduct = findViewById(R.id.listView_Products_OrderAdd);
        productList     = new ArrayList<>();
        productMap      = new HashMap<>();
        productTypeMap  = new HashMap<>();
        getProductId();     //seçilen productu yazdır

        btn1=findViewById(R.id.btn_add_order);//ekle butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrder(userID);
            }
        });

        btn2=findViewById(R.id.btn_addorderToList);//orderlist'e git butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent(orderAdd.this, orderList.class);
                startActivity(list);
            }
        });

        btn3=findViewById(R.id.btn_addOrderToOrders);//geri çıkma butonu
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(orderAdd.this, Orders.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void listCostumer(){//costumer bilgilerini çekiyoruz ve listView'de listeliyoruz
        Log.d(TAG, "listCostumer() called");
        listViewCustomers = findViewById(R.id.listView_Costumers_OrderAdd);
        customersList   = new ArrayList<>();        //list ve mapleri tanımladık
        customerMap     = new HashMap<>();

        adapter         = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customersList);
        listViewCustomers.setAdapter(adapter);      //listView'e verileri adapter ile eklemek için adapter tanımla
        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String customer_id = document.getString("customer_id");
                                String customer_name_surname = document.getString("customer_name_surname");

                                // Liste şeklinde görüntüleme için listeye ekle
                                customersList.add(customer_name_surname);
                                customerMap.put(customer_name_surname,customer_id);
                            }
                            // Değişiklikleri güncelle
                            adapter.notifyDataSetChanged();
                            loadProductTypes();//böyle yaparak veri çekme işlemini sırasıyla yapmış oluyoruz
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void getCostumerId(){//costumer listView'inden seçilen ıd bilgisini alıyoruz
        Log.d(TAG, "getCostumerId() called");
        selectedCustomerIdTextView = findViewById(R.id.txt_customer_id_addOrder);
        listViewCustomers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Tıklanan öğenin metnini al
                String selectedCostumer = customersList.get(position);
                // İlgili customer_id'yi al
                String selectedCustomerId = customerMap.get(selectedCostumer);
                selectedCustomerIdTextView.setText(selectedCustomerId);
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
    public void loadProducts(){//product_name bilgisini alıyoruz ve önceki bilgiler ile birleştiririyoruz
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
                                // Her belgeden gerekli bilgileri al
                                String product_id   = document.getString("product_id");
                                String product_name = document.getString("product_name");
                                String type_id      = document.getString("type_id");
                                String type_name    = productTypeMap.get(type_id);

                                String listItem = product_name + "\n" + (type_name != null ? type_name : "Unknown Type");
                                productList.add(listItem);
                                productMap.put(listItem,product_id);
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
    public void getProductId(){//product listView'inden seçilen ıd bilgisini alıyoruz
        Log.d(TAG, "getProductId() called");
        selectedProductIdTextView = findViewById(R.id.txt_product_id_addOrder);
        listViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Tıklanan öğenin metnini al
                String selectedProduct = productList.get(position);
                // İlgili product_id'yi al
                String selectedProductId = productMap.get(selectedProduct);
                // customer_id'yi editTexte'e yazdır
                selectedProductIdTextView.setText(selectedProductId);
            }
        });
    }
    public void addOrder(String uId){   //siparişi ekleyen kişi pazarlamacı olacağı için girişteki id bilgisini fonksiyona girdi olarak veriyoruz
        Log.d(TAG, "addOrder() called");
        editCostumerId  =   findViewById(R.id.txt_customer_id_addOrder);
        txtCostumerId   =   editCostumerId.getText().toString();

        newOrderMap = new HashMap<>();      //bilgileri tamamlamak için yeni sipariş mapini oluşturuyoruz
        newOrderMap.put("customer_id",txtCostumerId);
        newOrderMap.put("employee_id",uId);

        db.collection("Orders")
                .add(newOrderMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        newOrderMap.put("order_id",documentReference.getId());//hashmape id bilgisini ekleyip, databse'ye yenıden yukluyoruz

                        //id bilgisini tabloya eklemek için tabloyu oluşturduktan sonra id bilgisini eklemek için update ediyoruz
                        db.collection("Orders").document(documentReference.getId())
                                .update(newOrderMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.w(TAG, "ORDER ADDING COMPLETE SUCCESFULLY");
                                        addOrderDetail(documentReference.getId(),uId);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        Toast.makeText(orderAdd.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        Log.d(TAG, "Order add succsfully");
                    }
                });
    }
    public void addOrderDetail(String newOrderId,String uId){  //order bilgilerimizle devam edeceğimiz için id bilgisini girdi olarak aldık
        Log.d(TAG, "addOrderDetail() called");
        editProductId   =   findViewById(R.id.txt_product_id_addOrder); //verilerin editTextten alınması
        editNumber      =   findViewById(R.id.txt_number_addOrder);
        editDate        =   findViewById(R.id.txt_date_addOrder);

        txtProductId    =   editProductId.getText().toString();
        txtNumber       =   editNumber.getText().toString();
        txtDate         =   editDate.getText().toString();


        newOrderDetailMap.put("number_of_product",txtNumber);
        newOrderDetailMap.put("order_date",txtDate);
        newOrderDetailMap.put("product_id",txtProductId);
        newOrderDetailMap.put("order_id",newOrderId);

        db.collection("Order Detail")
                .add(newOrderDetailMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {    //id bilgisininde tabloya eklenmesi
                        newOrderDetailMap.put("order_detail_id",documentReference.getId());//hashmape id bilgisini ekleyip, databse'ye yenıden yukluyoruz
                        db.collection("Order Detail").document(documentReference.getId())
                                .update(newOrderDetailMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.w(TAG, "ORDER and ORDER DETAİL ADDING COMPLETE SUCCESFULLY");
                                        Toast.makeText(orderAdd.this,"ORDER and ORDER DETAİL ADDING COMPLETE SUCCESFULLY",Toast.LENGTH_SHORT).show();
                                        updateBonus(txtNumber,uId);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(orderAdd.this,"Order can't add",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateBonus(String bonus,String uId){  //satış sayısına göre bonusu düzenliyoruz
        Log.d(TAG, "updateBonus() called");
        newBonusMap = new HashMap<>();

        db.collection("Bonus").document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // bonus değerini al
                                String lastBonus = document.getString("number_of_bonus");
                                int newBonusInteger     =   Integer.valueOf(lastBonus)+Integer.valueOf(bonus);
                                String newBonusString   =   String.valueOf(newBonusInteger);
                                newBonusMap.put("employee_id",uId);
                                newBonusMap.put("number_of_bonus",newBonusString);

                                db.collection("Bonus").document(uId)
                                        .update(newBonusMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "BONUS WAS UPDATED SUCCESSFULLY");
                                                } else {
                                                    Log.d(TAG, "Update failed with ", task.getException());
                                                }
                                            }
                                        });

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