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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class orderList extends AppCompatActivity {
    Button btn1;
    private static final String TAG = "orderList";
    private FirebaseFirestore db;
    private ListView listViewOrders;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> ordersList;
    private Map<String, String> orderDetailMap,productMap,customerMap;
    FirebaseUser mUser;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        db = FirebaseFirestore.getInstance();
        mUser= FirebaseAuth.getInstance().getCurrentUser();

        listViewOrders   = findViewById(R.id.listView_order_listorder);

        ordersList       = new ArrayList<>();   //listView için gerekli listede toparlıyoruz

        productMap       = new HashMap<>();     //mapler her yeden erişilebilir olması için tanımladık
        customerMap      = new HashMap<>();
        orderDetailMap   = new HashMap<>();
        loadProduct();                          //önce product bilgisi sonra orderDetail bilgisi en son ise order bilgilerini
                                                //iç içe foksiyon ile maplere yerleştirdik ve
                                                //maplerden arrayliste alıp ordan listviewe çektik

        btn1=findViewById(R.id.btn_listToOrders);//geri çıkma butonu
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(orderList.this, Orders.class);
                back.putExtra("userID", userID);
                startActivity(back);
            }
        });
    }

    public void loadProduct(){//product bilgilerini mape alıyoruz ve loadOrderDetail() fonksiyonunu çağırıyoruz
        Log.d(TAG, "loadProduct() called");
        db.collection("Product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {//product bilgilerini mape doldur
                                String product_id   = document.getString("product_id");
                                String product_name = document.getString("product_name");
                                productMap.put(product_id,product_name);//
                            }
                            loadCustomer();//asenkron işlem yaptığımız için fonksiyonları iç içe tanımladık
                        } else {
                            Log.d(TAG, "Error getting product id: ", task.getException());
                        }
                    }
                });
    }
    public void loadCustomer(){
        Log.d(TAG, "loadCustomer() called");
        db.collection("Customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String customer_id             = document.getString("customer_id");
                                String customer_name_surname           = document.getString("customer_name_surname");
                                String phone_number_of_customer    = document.getString("phone_number_of_customer");

                                customerMap.put(customer_id,customer_name_surname);
                            }
                            loadOrderDetail();
                        } else {
                            Log.d(TAG, "Error getting customer id: ", task.getException());
                        }
                    }
                });
    }
    public void loadOrderDetail(){//orderDetail bilgilerini mape alıyoruz ve loadOrders() fonksiyonunu çağırıyoruz
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

                                String listItemForOrders =  "product name: "        +productMap.get(product_id)+
                                                            "\nnumber of product: " +number_of_product+
                                                            "\norder date: "        +order_date;
                                orderDetailMap.put(order_id,listItemForOrders);
                                //tablolar arası bilgileri mapler ile aktarıyoruz

                            }
                            loadOrders();
                        } else {
                            Log.d(TAG, "Error getting orderDetail: ", task.getException());
                        }
                    }
                });
    }
    public void loadOrders(){//orders bilgileni ve maplerdeki bilgileri birşleştirip listViewe'e yazdırırıyoruz
        Log.d(TAG, "loadOrderDetail() called");
        //listViewleri doldurmak için adapter kullanıyoruz
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ordersList);
        listViewOrders.setAdapter(adapter);

        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // bütün bilgileri bu kısımda topluyoruz
                                String order_id     = document.getString("order_id");
                                String customer_id  = document.getString("customer_id");
                                String employee_id  = document.getString("employee_id");

                                String listItem =   "order id: "        +order_id+
                                                    "customer name: "   +customerMap.get(customer_id)+
                                                    "\n"                +orderDetailMap.get(order_id);

                                if (employee_id.equals(mUser.getUid())){
                                    ordersList.add(listItem);//bu kısım ile sadece kullanıcının yapmış olduğu satışlar listeleniyor
                                }
                            }
                            // ordersList'e veri eklendiğini kontrol etmek için log mesajı
                            Log.d(TAG, "loadProducts() productList size: " + ordersList.size());
                            adapter.notifyDataSetChanged();//listview içeriğini güncelle
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}