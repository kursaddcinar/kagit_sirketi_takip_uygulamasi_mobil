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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class orderUpdate extends AppCompatActivity {

    Button btn1,btn2;
    private EditText editNewNumber,editNewProductId,editOrderId;
    private FirebaseFirestore db;
    private FirebaseUser mUser;
    private static final String TAG = "orderUpdate";
    private ListView listViewOrderDetail, listViewProduct;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> productList,productIdList,ordersList,ordersIdList ;
    private Map<String, String> customerMap,orderInformationMap,orderDateMap,productMap,productTypeMap,
            OldNumberProductMap,orderDetailIdBridgeMap;
    private HashMap<String, Object> newOrderDetailMap,newBonusMap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_update);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();


        listViewProduct = findViewById(R.id.listViewProductOrderupdate);
        listViewOrderDetail = findViewById(R.id.listView_order_detail_id_orderupdate);
        productList     = new ArrayList<>();    //listView için gerekli listede toparlıyoruz
        ordersList      = new ArrayList<>();

        productIdList   = new ArrayList<>();    //seçili ögeleri seçmek için gerekli listeler
        ordersIdList    = new ArrayList<>();

        productMap              = new HashMap<>();          //bilgileri kaybetmemek için kullanılacak mapler
        customerMap             = new HashMap<>();
        orderInformationMap     = new HashMap<>();
        orderDetailIdBridgeMap  = new HashMap<>();
        orderDateMap            = new HashMap<>();
        OldNumberProductMap     = new HashMap<>();
        productTypeMap          = new HashMap<>();

        newOrderDetailMap       = new HashMap<>();         //veritabanı için gerekli mapler
        newBonusMap             = new HashMap<>();

        editNewNumber       = findViewById(R.id.txt_new_product_number);
        editNewProductId    = findViewById(R.id.txt_selected_product_ıd);
        editOrderId         = findViewById(R.id.txt_selected_order_ıd);

        loadProduct();
        loadProductTypes(); //product listi listele
        setOrderId();       //seçilen orderId'yi yazdır
        setProductId();     //seçilen productId'yi yazdır


        btn1=findViewById(R.id.btn_updateOrder);//update butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderDetail();
                Intent replace = new Intent(orderUpdate.this, orderUpdate.class);
                replace.putExtra("userID", userID);
                startActivity(replace);
            }
        });

        btn2=findViewById(R.id.btn_updateOrderToOrders);//geri çıkma butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(orderUpdate.this, Orders.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }
    public void loadProduct(){//en özel bilgimiz olan product bilgilerini alıp mape eklitoruz ve loadCustomer() fonksiyonunu çağırıyoruz
        Log.d(TAG, "loadProduct() called");
        db.collection("Product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String product_id   = document.getString("product_id");
                                String product_name = document.getString("product_name");
                                productMap.put(product_id,product_name);
                            }
                            loadCustomer();
                        } else {
                            Log.d(TAG, "Error getting product id: ", task.getException());
                        }
                    }
                });
    }
    public void loadCustomer(){//en özel bilgimiz olan product bilgilerini alıp mape eklitoruz ve loadOrderDetail() fonksiyonunu çağırıyoruz
        Log.d(TAG, "loadCustomer() called");
        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String customer_id   = document.getString("customer_id");
                                String customer_name_surname = document.getString("customer_name_surname");
                                customerMap.put(customer_id,customer_name_surname);//
                            }
                            loadOrderDetail();
                        } else {
                            Log.d(TAG, "Error getting customer id: ", task.getException());
                        }
                    }
                });
    }
    public void loadOrderDetail(){//order detail bilgilerini alıyoruz ve önceki bilgiler ile birlikte map ve listeye ekliyoruz
        Log.d(TAG, "loadOrderDetail() called");
        db.collection("Order Detail")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String order_id             = document.getString("order_id");
                                String order_date           = document.getString("order_date");
                                String number_of_product    = document.getString("number_of_product");
                                String product_id           = document.getString("product_id");
                                String order_detail_id      = document.getString("order_detail_id");

                                String listItemForOrders =      "product name: "        +productMap.get(product_id)+
                                        "\nnumber of product: " +number_of_product+
                                        "\norder date: "        +order_date;
                                orderInformationMap. put(order_id,listItemForOrders);//

                                orderDateMap.put(order_id,order_date);//newOrderDetailMap için gerekli dateler
                                orderDetailIdBridgeMap.put(order_id,order_detail_id);//newOrderDetailMap için gerekli idler
                                OldNumberProductMap.put(order_id,number_of_product);//bonus sayısını güncellemek için gerekli sayı
                            }
                            loadOrders();
                        } else {
                            Log.d(TAG, "Error getting order detail id: ", task.getException());
                        }
                    }
                });
    }
    public void loadOrders(){//orders bilgilerini de alıp bütün map ve listeleri birleştirip Orders listView'e eklıyoruz
        Log.d(TAG, "loadOrders() called");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ordersList);
        listViewOrderDetail.setAdapter(adapter);

        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // Her belgeden gerekli bilgileri al
                                String order_id     = document.getString("order_id");
                                String customer_id  = document.getString("customer_id");
                                String employee_id  = document.getString("employee_id");

                                String listItem =   "order id: "        +order_id+
                                        "\ncustomer name : "            +customerMap.get(customer_id)+
                                        "\n"                            +orderInformationMap.get(order_id);

                                //bu kısım ile sadece kullanıcının yapmış olduğu satışlar listeleniyor
                                if(employee_id.equals(mUser.getUid())){
                                    ordersList.add(listItem);
                                    ordersIdList.add(order_id);
                                }
                            }
                            // productList'e veri eklendiğini kontrol etmek için log mesajı ekle
                            Log.d(TAG, "loadOrders() ordersList size: " + ordersList.size());
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void setOrderId(){//order ListView'inden seçilen kısmın ıd bilgisini alıyoruz(edittexte yazdır)
        Log.d(TAG, "getOrderId() called");
        listViewOrderDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editOrderId.setText(ordersIdList.get(position));
            }
        });
    }
    public void loadProductTypes(){//en özel bilgimiz olan ürün tipini alıp mape eklitoruz ve loadProducts() fonksiyonunu çağırıyoruz
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
                                productIdList.add(product_id);
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
    public void setProductId(){
        Log.d(TAG, "getProductId() called");
        listViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editNewProductId.setText(productIdList.get(position));
            }
        });
    }

    public String getOrderId(){
        String selectedOrderId = String.valueOf(editOrderId.getText());
        return selectedOrderId;
    }
    public String getNewProductId(){
        String newProductId =   String.valueOf(editNewProductId.getText());
        return newProductId;
    }
    public String getNewNumberProduct(){//order ListView'inden alınan ıd bilgisi ile satılan ürün bilgisini döndürme fonksiyonu
        String newNumberProduct= String.valueOf(editNewNumber.getText());
        return newNumberProduct;
    }

    //veri toplamada özelden genele gidiyoruz
    public void updateOrderDetail(){      //itemlerin seçilip seçilmediğini kontrol et!!!!!!
        //veritabanını güncelleyeceğimiz mapı burada dolduruyoruz
        newOrderDetailMap.put("order_detail_id",orderDetailIdBridgeMap.get(getOrderId()));
        newOrderDetailMap.put("order_date",orderDateMap.get(getOrderId()));
        newOrderDetailMap.put("order_id",getOrderId());
        newOrderDetailMap.put("number_of_product",getNewNumberProduct());
        newOrderDetailMap.put("product_id",getNewProductId());

        db.collection("Order Detail").document(orderDetailIdBridgeMap.get(getOrderId()))
                .update(newOrderDetailMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(orderUpdate.this,"Order detail update succesfully.",Toast.LENGTH_SHORT).show();
                            updateBonus();
                        }
                    }
                });
    }
    public void updateBonus(){  //satış sayısına göre bonusu düzenliyoruz
        Log.d(TAG, "updateBonus() called");

        db.collection("Bonus").document(mUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // bonus değerini al
                                String lastTotalBonus   = document.getString("number_of_bonus");

                                int newBonusInteger     =   Integer.valueOf(lastTotalBonus)
                                                            - Integer.valueOf(OldNumberProductMap.get(getOrderId()))
                                                            + Integer.valueOf(getNewNumberProduct());
                                String newBonusString   =   String.valueOf(newBonusInteger);

                                newBonusMap.put("employee_id",mUser.getUid());
                                newBonusMap.put("number_of_bonus",newBonusString);

                                db.collection("Bonus").document(mUser.getUid())
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