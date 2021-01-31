package com.example.whatsappm.menu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappm.R;
import com.example.whatsappm.adapters.CallListAdapter;
import com.example.whatsappm.models.CallList;

import java.util.ArrayList;
import java.util.List;


public class CallsFragment extends Fragment {
    private List<CallList> list = new ArrayList<>();
    private RecyclerView recyclerView;

    public CallsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addFakeData();
        return view;
    }

    private void addFakeData()
    {
       // list.add(new CallList("123","laila","25/12/2020 01.00 AM","https://media.dhliz.com/e0b8/f596/%D9%84%D9%8A%D9%84%D9%89_%D8%A3%D8%AD%D9%85%D8%AF_%D8%B2%D8%A7%D9%87%D8%B1-e0b88089-b672-4781-806b-48dfe566f596..jpg","missed"));
       // list.add(new CallList("456","emalia","25/12/2020 02.00 AM","https://s.wsj.net/public/resources/images/BN-BI095_mag031_FR_20140131160207.jpg","missed"));
      //  list.add(new CallList("789","sarah","25/12/2020 03.00 AM","https://upload.wikimedia.org/wikipedia/commons/7/72/Sarah_Wayne_Callies_January_2015.jpg","income"));
       // recyclerView.setAdapter(new CallListAdapter(list,getContext()));
    }

}