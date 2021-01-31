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
import com.example.whatsappm.models.ChatList;
import com.example.whatsappm.view.activities.chats.ChatsActivity;
import com.example.whatsappm.view.activities.dialog.DialogViewUser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder>
{
    List<ChatList> list;
    Context context;

    public ChatListAdapter(List<ChatList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_list,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position)
    {

        final ChatList chatList = list.get(position);

        holder.textViewName.setText(list.get(position).getUserName());
        holder.textViewDescription.setText(list.get(position).getDescription());
        holder.textViewDate.setText(list.get(position).getDate());


        // for image we need library ...
        if (chatList.getUrlProfile().equals("")){
            holder.profile.setImageResource(R.drawable.icon_male_photo);  // set  default image when profile user is null
        } else {
            Glide.with(context).load(chatList.getUrlProfile()).into(holder.profile);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatsActivity.class)
                        .putExtra("userId",chatList.getUserId())
                        .putExtra("userName",chatList.getUserName())
                        .putExtra("userProfile",chatList.getUrlProfile()));
            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogViewUser(context,chatList);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        if(list==null)
            return 0;
        else
            return list.size();
    }

    class Holder extends RecyclerView.ViewHolder{

    private TextView textViewName,textViewDescription,textViewDate;
    private CircularImageView profile;
    Context context;

    public Holder(@NonNull View itemView) {
        super(itemView);

        context=itemView.getContext();
        textViewName =itemView.findViewById(R.id.tv_name);
        textViewDescription = itemView.findViewById(R.id.tv_desc);
        textViewDate = itemView.findViewById(R.id.tv_date);
        profile = itemView.findViewById(R.id.img_profile);


    }
}

}
