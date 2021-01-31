package com.example.whatsappm.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappm.R;
import com.example.whatsappm.models.user.Users;
import com.example.whatsappm.view.activities.chats.ChatsActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.Holder>
{
    private List<Users> list;
    private Context context;

    public ContactsAdapter(List<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_contact_item,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final Users users=list.get(position);

        holder.tvUserNameUser.setText(users.getUserName());
        holder.tvDescriptionUser.setText(users.getUserPhone());
        Glide.with(context).load(users.getImageProfile()).into(holder.imgUserProfile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userId",users.getUserId())
                .putExtra("userName",users.getUserName())
                .putExtra("userProfile",users.getImageProfile()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

   public class Holder extends RecyclerView.ViewHolder{

        private CircularImageView imgUserProfile;
        private TextView tvUserNameUser,tvDescriptionUser;

        public Holder(@NonNull View itemView) {
            super(itemView);

            imgUserProfile = itemView.findViewById(R.id.img_profile_contact);
            tvUserNameUser = itemView.findViewById(R.id.tv_userName_contact);
            tvDescriptionUser = itemView.findViewById(R.id.tv_description_contact);

        }
    }

}
