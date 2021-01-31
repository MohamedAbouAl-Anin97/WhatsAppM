package com.example.whatsappm.view.activities.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.whatsappm.R;
import com.example.whatsappm.adapters.ContactsAdapter;
import com.example.whatsappm.models.user.Users;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    //to read data from phone
    public static final int REQUEST_READ_CONTACTS = 79;
    ListView contactList;
    ArrayList mobileArray;

    private List<Users>list = new ArrayList<>();
    private ContactsAdapter adapter;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private static final String TAG="ContactsActivity";
    private ImageButton back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (firebaseUser !=null) {
             getContactFromPhone();  //if they using this app
            //getContactList();
        }

        if(mobileArray != null){

            getContactList();

        }



    }
 private void initView()
 {
     recyclerView = findViewById(R.id.recycle_contact_list);
     recyclerView.setLayoutManager(new LinearLayoutManager(this));
     firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
     firestore = FirebaseFirestore.getInstance();
     back = findViewById(R.id.btnBackk);

 }

 private void getContactFromPhone(){

     if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
             == PackageManager.PERMISSION_GRANTED) {
         mobileArray = getAllPhoneContacts();
     } else {
         requestPermission();
     }
 }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllPhoneContacts();
                } else {
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList getAllPhoneContacts() {
        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
               // String name = cur.getString(cur.getColumnIndex(
                       // ContactsContract.Contacts.DISPLAY_NAME));
               // nameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneList;
    }



 private void getContactList(){

        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                {
                    String userID = snapshot.getString("userId");
                    String userName = snapshot.getString("userName");
                    String imageUrl = snapshot.getString("imageProfile");
                    String desc = snapshot.getString("bio");
                    String phone = snapshot.getString("userPhone");


                    Users user = new Users();
                    user.setUserId(userID);
                    user.setUserName(userName);
                    user.setImageProfile(imageUrl);
                    user.setBio(desc);
                    user.setUserPhone(phone);


                    if (userID != null && !userID.equals(firebaseUser.getUid()))
                    {
                        list.add(user);
                    }

                }
                for (Users user : list){
                    if(mobileArray.contains(user.getUserPhone())){
                        Log.d(TAG, "getContactList: true"+user.getUserPhone());
                    }else {
                        Log.d(TAG, "getContactList: false"+user.getUserPhone());
                    }
                }

                adapter=new ContactsAdapter(list,ContactsActivity.this);
                recyclerView.setAdapter(adapter);

            }




        });


 }

}