package com.example.whatsappm.menu;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappm.R;
import com.example.whatsappm.adapters.ChatListAdapter;
import com.example.whatsappm.databinding.FragmentChatBinding;
import com.example.whatsappm.models.ChatList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {

    private static final String TAG = "ChatsFragment";

    public ChatFragment() {
        // Required empty public constructor
    }

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FirebaseFirestore firestore;
    private Handler handler = new Handler();

    private List<ChatList> list;

    private FragmentChatBinding binding;

    private ArrayList<String> allUserID;

    private ChatListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat, container, false);

        list = new ArrayList<>();
        allUserID = new ArrayList<>();

        binding.recyclerViewRecentlyChat.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatListAdapter(list,getContext());
        binding.recyclerViewRecentlyChat.setAdapter(adapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();


        if (firebaseUser!=null) {
            getChatList();
        }

        return binding.getRoot();
    }

    private void getChatList() {
        binding.chatLoadingProgress.setVisibility(View.VISIBLE);
        list.clear();
        allUserID.clear();;
        reference.child("ChatList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userID = Objects.requireNonNull(snapshot.child("chatid").getValue()).toString();
                    Log.d(TAG, "onDataChange: userid "+userID);

                    binding.chatLoadingProgress.setVisibility(View.GONE);
                    allUserID.add(userID);
                }
                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void getUserInfo(){


        handler.post(new Runnable() {
            @Override
            public void run() {
                for (String userID : allUserID){
                    firestore.collection("Users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d(TAG, "onSuccess: ddd"+documentSnapshot.getString("userName"));
                            try {
                                ChatList chat = new ChatList(
                                        documentSnapshot.getString("userId"),
                                        documentSnapshot.getString("userName"),
                                        "this is description..",
                                        "",
                                        documentSnapshot.getString("imageProfile")
                                );
                                list.add(chat);
                            }catch (Exception e){
                                Log.d(TAG, "onSuccess: "+e.getMessage());
                            }
                            if (adapter!=null){
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();

                                Log.d(TAG, "onSuccess: adapter "+adapter.getItemCount());
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Error L"+e.getMessage());
                        }
                    });
                }
            }
        });
    }

  }

