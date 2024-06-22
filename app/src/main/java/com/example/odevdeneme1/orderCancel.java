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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class orderCancel extends AppCompatActivity {
    Button btn1,btn2;
    private EditText editOrderId;
    private FirebaseFirestore db;
    private FirebaseUser mUser;
    private static final String TAG = "orderCancel";
    private ListView listViewOrders;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> ordersList,ordersIdList,orderDetailIdList;
    private Map<String, String> ordersMap,orderDetailMap,productMap,customerMap,orderDetailBridgeMap,numberProductMap,orderDetailIdMap;
    private Map<String, Object> newBonusMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cancel);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        db = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        listViewOrders   = findViewById(R.id.listView_Orders_cancelorders);
        editOrderId      = findViewById(R.id.txt_Order_id_cancelorder);

        ordersList       = new ArrayList<>();   //list ve maplarin tanımlanması
        ordersIdList     = new ArrayList<>();
        orderDetailIdList= new ArrayList<>();

        productMap       = new HashMap<>();
        orderDetailBridgeMap = new HashMap<>();
        numberProductMap = new HashMap<>();
        ordersMap        = new HashMap<>();
        orderDetailMap   = new HashMap<>();
        orderDetailIdMap = new HashMap<>();
        customerMap      = new HashMap<>();
        loadProduct();//seçilen productu yazdır
        getOrderId();

        btn1=findViewById(R.id.btn_calcelOrder);//cancel butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelOrder();
                Intent replace = new Intent(orderCancel.this, orderCancel.class);
                replace.putExtra("userID", userID);
                startActivity(replace);
            }
        });

        btn2=findViewById(R.id.btn_cancelToOrders);//geri çıkma butonu
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(orderCancel.this, Orders.class);
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
                                orderDetailMap. put(order_id,listItemForOrders);
                                orderDetailBridgeMap.put(order_id,order_detail_id);
                                numberProductMap.put(order_id,number_of_product);
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
        listViewOrders.setAdapter(adapter);

        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //System.out.println("orders ıcındekıler");
                            for (DocumentSnapshot document : task.getResult()) {
                                // Her belgeden gerekli bilgileri al
                                String order_id     = document.getString("order_id");
                                String customer_id  = document.getString("customer_id");
                                String employee_id  = document.getString("employee_id");
                                String customer_name_surname  = customerMap.get(customer_id);

                                String listItem =   "order id: "                    +order_id+
                                                    "\ncustomer name surname: "     +customer_name_surname+
                                                    "\n"                            +orderDetailMap.get(order_id);
                                
                                //bu kısım ile sadece kullanıcının yapmış olduğu satışlar listeleniyor
                                if(employee_id.equals(mUser.getUid())){
                                    ordersMap.put(order_id,listItem);
                                    ordersList.add(listItem);
                                    ordersIdList.add(order_id);
                                    orderDetailIdList.add(orderDetailBridgeMap.get(order_id));
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
    public void getOrderId(){//order ListView'inden seçilen kısmın ıd bilgisini alıyoruz(edittexte yazdır)
        Log.d(TAG, "getOrderId() called");
        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editOrderId.setText(ordersIdList.get(position));
            }
        });
    }
    public String getOrderDetailId(){//order ListView'inden alınan ıd bilgisi ile order_detail_id bilgisini döndürme fonksiyonu
        String OrderId= String.valueOf(editOrderId.getText());
        String orderDetailId = orderDetailBridgeMap.get(OrderId);
        return orderDetailId;
    }
    public String getNumberProduct(){//order ListView'inden alınan ıd bilgisi ile satılan ürün bilgisini döndürme fonksiyonu
        String OrderId= String.valueOf(editOrderId.getText());
        String NumberProduct = numberProductMap.get(OrderId);
        return NumberProduct;
    }
    public void cancelOrder(){//alınan order id ile seçilen orderin silinmesi
        Log.d(TAG, "cancelOrder() called");
        db.collection("Orders").document(editOrderId.getText().toString())
                .delete()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(orderCancel.this,"Order cancalled succesfully",Toast.LENGTH_SHORT).show();
                            cancelOrderDetail();
                        }
                        else {
                            Toast.makeText(orderCancel.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void cancelOrderDetail(){//alınan order_detail_id ile seçilen orderin detail'in silinmesi
        Log.d(TAG, "cancelOrderDetail() called");
        db.collection("Order Detail").document(getOrderDetailId())
                .delete()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(orderCancel.this,"Order Detail cancalled succesfully",Toast.LENGTH_SHORT).show();
                            updateBonus(mUser.getUid());
                        }
                        else {
                            Toast.makeText(orderCancel.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void updateBonus(String uId){  //satış sayısına göre bonusu düzenliyoruz
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
                                int newBonusInteger     =   Integer.valueOf(lastBonus)  - Integer.valueOf(getNumberProduct());
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
                                                    System.out.println("maptaki bonus " + newBonusMap.get("number_of_bonus").toString());
                                                    System.out.println(uId);
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