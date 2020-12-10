package com.example.yidnek.amguesthouse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TodayRecordsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<TodayRecord> todayRecords;
    private RecyclerView.Adapter adapter;
    private DatabaseReference records;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    private long lastCreatedDate = 0;
    private long currentDbValue;
    private String NAME_RECIEVED = "";
    private String USER_TYPE_RECIEVED = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_records);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        todayRecords = new ArrayList<>();
        adapter = new TodayRecordAdapter(todayRecords,getApplicationContext());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        NAME_RECIEVED = getIntent().getStringExtra("USER_NAME");
        USER_TYPE_RECIEVED = getIntent().getStringExtra("USER_TYPE");

        GetData();

        DatabaseReference currDate = FirebaseDatabase.getInstance().getReference("CurrentDate");
        currDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastCreatedDate = (long) dataSnapshot.child("lastDate").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final DatabaseReference currGebi = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        currGebi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(ConvForFirebaseDate(lastCreatedDate)).child("TotalGebi").exists()){
                    currentDbValue = (long) dataSnapshot.child(ConvForFirebaseDate(lastCreatedDate)).child("TotalGebi").getValue();
                    progressDialog.dismiss();
                }
                else {
                    progressDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class TodayRecordAdapter extends RecyclerView.Adapter<TodayRecordAdapter.ViewHolder>{

        private List<TodayRecord> listItems;
        private Context context;

        public TodayRecordAdapter(List<TodayRecord> listItems, Context context) {
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_today,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TodayRecordAdapter.ViewHolder holder, final int position) {

            final TodayRecord listItem = listItems.get(position);
            holder.roomNo.setText("አልጋ ቁ ："+listItem.getRoomNo());
//            holder.rDate.setText("ቀን ：" + String.valueOf(listItem.getReserveDate())   );
            holder.rShort.setText("ጊዜያዊ ：" + listItem.getReserveShort() + " ብር");
            holder.rNight.setText("አዳር ：" +listItem.getReserveLong()  + " ብር");
            holder.rName.setText("ስም ：" +listItem.getReserveName());
            holder.rUser.setText("የመዘገበው ：" + listItem.getReserveUser());

            if (ConvForFirebaseDate(lastCreatedDate).equals(ConvForFirebaseDate(listItem.getReserveDate()))){
                holder.rDate.setText("ቀን : ዛሬ  ሰዓት:- "+ConvTodayDate(listItem.getReserveDate()));
            }
            else {
                holder.rDate.setText(ConvDate(listItem.getReserveDate()));
            }

            holder.TodayDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((listItem.getReserveUser().equals(NAME_RECIEVED)) || USER_TYPE_RECIEVED.equals("Admin"))){
                        if (ConvForFirebaseDate(lastCreatedDate).equals(ConvForFirebaseDate(listItem.getReserveDate()))){
                            DeleteHistory(listItem.getReserveShort(),listItem.getReserveLong());
                        }
                        TodayRecordDelete(listItem.getTodayRecordKey());
                    }
                    else{
                        Toast.makeText(TodayRecordsActivity.this,"Cannot delete this item",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView roomNo,rDate,rShort,rNight,rName,rUser;
            public Button TodayDelete;

            public ViewHolder(View itemView) {
                super(itemView);

                roomNo =(TextView) itemView.findViewById(R.id.roomNo);
                rDate =(TextView) itemView.findViewById(R.id.r_date);
                rShort =(TextView) itemView.findViewById(R.id.rShort);
                rNight =(TextView) itemView.findViewById(R.id.rNight);
                rName =(TextView) itemView.findViewById(R.id.rName);
                rUser =(TextView) itemView.findViewById(R.id.rUser);
                TodayDelete =(Button) itemView.findViewById(R.id.today_delete);


            }
        }
    }

    private void GetData(){
        todayRecords.clear();
        records = FirebaseDatabase.getInstance().getReference("Reserves");
        records.orderByChild("reserveDate").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                TodayRecord data = dataSnapshot.getValue(TodayRecord.class);
                data.setTodayRecordKey(dataSnapshot.getKey());
                todayRecords.add(data);
                recyclerView.setAdapter(adapter);
                Collections.reverse(todayRecords);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
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
    private void TodayRecordDelete(String id){
        databaseReference = FirebaseDatabase.getInstance().getReference("Reserves")
                .child(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(TodayRecordsActivity.this);

        builder.setMessage("Do you want to delete this item");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseReference.removeValue();
                Toast.makeText(TodayRecordsActivity.this,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                progressDialog.show();
                GetData();
                progressDialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }
    private String ConvDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE-MMM dd,yyyy h:mm a");
        String date = simpleDateFormat.format(time);
        return date;
    }
    private String ConvForFirebaseDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(time);
        return date;
    }
    private void DeleteHistory(final long shortValue, final long longValue){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        long tempo = currentDbValue-(shortValue+longValue);
        ref.child((ConvForFirebaseDate(lastCreatedDate))).child("TotalGebi").setValue(tempo);
        progressDialog.dismiss();
    }
    private String ConvTodayDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" h:mm a");
        String date = simpleDateFormat.format(time);
        return date;
    }
}
