package com.example.yidnek.amguesthouse;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DatabaseReference gebiWechiReference;
    private List<History> historyRecords;

    private RecyclerView recyclerView;

    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.h_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        historyRecords = new ArrayList<>();
        adapter = new HistoryAdapter(historyRecords,getApplicationContext());
        GetGebiWechiData();


    }

    public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

        private List<History> listItems;
        private Context context;

        public HistoryAdapter(List<History> listItems, Context context) {
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_history,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(HistoryAdapter.ViewHolder holder, final int position) {

            final History listItem = listItems.get(position);

            holder.hGebi.setText("የዕለቱ ገቢ ：" + listItem.getGebi() + " ብር");
            holder.hWechi.setText("የዕለቱ ወጪ ：" +listItem.getWechi()  + " ብር");
            if (listItem.getGebi()-listItem.getWechi()<0){
                holder.hNet.setBackgroundColor(Color.parseColor("#FFF44336"));
            }
            holder.hNet.setText("የተጣራ ገቢ ：" +String.valueOf(listItem.getGebi()-listItem.getWechi())+" ብር");

            holder.hDate.setText("Date ："+listItem.getHistoryDate() );

        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView hGebi,hDate,hWechi,hNet;

            public ViewHolder(View itemView) {
                super(itemView);

                hGebi =(TextView) itemView.findViewById(R.id.h_gebi);
                hDate =(TextView) itemView.findViewById(R.id.h_date);
                hWechi =(TextView) itemView.findViewById(R.id.h_wechi);
                hNet =(TextView) itemView.findViewById(R.id.h_net);


            }
        }
    }
    private void GetGebiWechiData(){
        gebiWechiReference = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        gebiWechiReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                History data = dataSnapshot.getValue(History.class);

                historyRecords.add(data);
                recyclerView.setAdapter(adapter);
                Collections.reverse(historyRecords);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
