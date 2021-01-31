package com.example.whatsappm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappm.R;
import com.example.whatsappm.models.CallList;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class CallListAdapter extends RecyclerView.Adapter<CallListAdapter.Holder>
{
    List<CallList> list;
    private Context context;

    public CallListAdapter(List<CallList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_call_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position)
    {
        CallList callList = list.get(position);


        holder.textViewName.setText(list.get(position).getUserName());
        holder.textViewDate.setText(list.get(position).getDate());

        if(callList.getCallType().equals("missed"))
        {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_downward_24));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_light));

        }else if(callList.getCallType().equals("income"))
        {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_downward_24));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_light));
        }else
            {
                holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_arrow_upward_24));
                holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_light));
            }

        Glide.with(context).load(list.get(position).getUrlProfile()).into(holder.profile);

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

    private TextView textViewName,textViewDate;
    private CircularImageView profile;
    private ImageView arrow;


    public Holder(@NonNull View itemView) {
        super(itemView);

        textViewName =itemView.findViewById(R.id.tv_name);
        textViewDate = itemView.findViewById(R.id.tv_date);
        profile = itemView.findViewById(R.id.img_profile);
        arrow = itemView.findViewById(R.id.img_arrow);
    }
}

}
