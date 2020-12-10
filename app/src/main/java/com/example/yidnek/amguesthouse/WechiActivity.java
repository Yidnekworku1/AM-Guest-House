package com.example.yidnek.amguesthouse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WechiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Wechi> wechiRecords;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference;
    private EditText itemType, price;
    private Button add;
    private ProgressBar progressBar;
    DatabaseReference records;
    private String NAME_RECIEVED = "";
    private String USER_TYPE_RECIEVED = "";

    private long lastCreatedDate = 0;
    private long total = 0;

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentWechi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechi);

        progressBar = findViewById(R.id.wechi_progress);

        recyclerView = findViewById(R.id.recyclerView_wechi);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        wechiRecords = new ArrayList<>();

        adapter = new WechiAdapter(wechiRecords,getApplicationContext());
        GetData();


        itemType = findViewById(R.id.item_type);
        price = findViewById(R.id.price);
        add = findViewById(R.id.add);

        NAME_RECIEVED = getIntent().getStringExtra("USER_NAME");
        USER_TYPE_RECIEVED = getIntent().getStringExtra("USER_TYPE");
        records = FirebaseDatabase.getInstance().getReference("Wechi");


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



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String EnteredItem = itemType.getText().toString().trim();
                final String EnterdrPrice = price.getText().toString().trim();

                Validate(EnteredItem,EnterdrPrice);
                if (Validate(EnteredItem,EnterdrPrice)){
                    AddWechi(EnteredItem, Long.parseLong(EnterdrPrice));
                }

            }
        });
    }


    public Boolean Validate(String ei, String ep){
        if (ei.isEmpty()){
            itemType.setError("Please Fill this");
        }
        else if (ep.isEmpty()){
            price.setError("Please Fill this");
        }
        else {
            return true;
        }
        return false;
    }
    public void AddWechi(String ei, long ep){

        Wechi wechi = new Wechi(ei,ep,NAME_RECIEVED);
        String id = records.push().getKey();
        records.child(id).setValue(wechi);

        total = total + wechi.getPrice();
        Totalwechi(ConvForFirebaseDate(lastCreatedDate));
        Toast.makeText(this,"Successfully Added",Toast.LENGTH_SHORT).show();
        itemType.setText("");
        price.setText("");
        Collections.reverse(wechiRecords);

    }

    public class WechiAdapter extends RecyclerView.Adapter<WechiAdapter.ViewHolder>{

        private List<Wechi> listItems;
        private Context context;

        public WechiAdapter(List<Wechi> listItems, Context context) {
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_wechi,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(WechiAdapter.ViewHolder holder, final int position) {

            final Wechi listItem = listItems.get(position);
            holder.wType.setText("የዕቃው ዓይነት ："+listItem.getItem());
            holder.wAmount.setText("ዋጋ ：" +listItem.getPrice()  + " ብር");
            holder.wName.setText("ስም ：" +listItem.getName());
//            holder.wDate.setText("ቀን : "+listItem.getGreeting());


            if (ConvForFirebaseDate(lastCreatedDate).equals(ConvForFirebaseDate(listItem.getDate()))){
                holder.wDate.setText("ቀን : ዛሬ   ሰዓት :-"+ConvTodayDate(listItem.getDate()));

            }

            else{
                holder.wDate.setText("ቀን ："+ConvDate(listItem.getDate()) );
            }

            holder.wDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if ((listItem.getName().equals(NAME_RECIEVED)) || USER_TYPE_RECIEVED.equals("Admin")) {
                       WechiDelete(listItem.getKey(),listItem.getDate(),listItem.getPrice());
                   }
                   else {
                       Toast.makeText(WechiActivity.this,"You can not Delete this item",Toast.LENGTH_SHORT).show();
                   }

                }
            });
            holder.wUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((listItem.getName().equals(NAME_RECIEVED)) || USER_TYPE_RECIEVED.equals("Admin")){
                        final long temprice = listItem.getPrice();
                        final long temdate = listItem.getDate();
                        itemType.setText(listItem.getItem());
                        price.setText(String.valueOf(listItem.getPrice()));
                        add.setText("Update");
                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                WechiUpdate(itemType.getText().toString(), Long.parseLong(price.getText().toString()),listItem.getKey(),temprice,temdate);
                            }
                        });
                    }
                    else {
                        Toast.makeText(WechiActivity.this,"You can not update this item",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView wAmount,wDate,wType,wName;
            public Button wDelete,wUpdate;


            public ViewHolder(View itemView) {
                super(itemView);

                wAmount =(TextView) itemView.findViewById(R.id.w_amount);
                wDate =(TextView) itemView.findViewById(R.id.w_date);
                wType =(TextView) itemView.findViewById(R.id.w_type);
                wName =(TextView) itemView.findViewById(R.id.w_name);

                wDelete =(Button) itemView.findViewById(R.id.w_delete);
                wUpdate =(Button) itemView.findViewById(R.id.w_update);


            }
        }
    }
    private void GetData(){
        wechiRecords.clear();
        records = (DatabaseReference) FirebaseDatabase.getInstance().getReference("Wechi");
        records.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Wechi data = dataSnapshot.getValue(Wechi.class);
                data.setKey(dataSnapshot.getKey());
                wechiRecords.add(data);
                recyclerView.setAdapter(adapter);
                Collections.reverse(wechiRecords);
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
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
    public String ConvertToString(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:MM");
        Calendar calendar = Calendar.getInstance();
        Date ss = calendar.getTime();
        String cd = simpleDateFormat.format(time);
        return cd;
    }
    private void WechiDelete(String id, final long date, final long price){
        databaseReference = FirebaseDatabase.getInstance().getReference("Wechi")
                .child(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(WechiActivity.this);

        builder.setMessage("Do you want to delete this item");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (ConvForFirebaseDate(lastCreatedDate).equals(ConvForFirebaseDate(date))){
                    DeleteHistory(price);
                }
                databaseReference.removeValue();
                Toast.makeText(WechiActivity.this,"Deleted Successfully",Toast.LENGTH_SHORT).show();
                GetData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }
    private void WechiUpdate(String ei ,long ep,String key,long tempvalue,long tempdadte){

        Wechi wechi = new Wechi(ei,ep,NAME_RECIEVED);
        if (ConvForFirebaseDate(tempdadte).equals(ConvForFirebaseDate(lastCreatedDate))){
            long changedDiff = ep-tempvalue;
            UpdateHistory(changedDiff);
        }
        records.child(key).setValue(wechi);
        Toast.makeText(WechiActivity.this,"Successfully Updated",Toast.LENGTH_SHORT).show();
        GetData();
    }
    private String ConvDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE-MMM dd,yyyy h:mm a");
        String date = simpleDateFormat.format(time);
        return date;
    }
    private void Totalwechi(String date){
        DatabaseReference totalWechi = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        totalWechi.child(date).child("TotalWechi").setValue(total);
        totalWechi.child(date).child("date").setValue(date);
    }
    private String ConvForFirebaseDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(time);
        return date;
    }
    private void getCurrentWechi(){
        DatabaseReference currWechi = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        currWechi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(ConvForFirebaseDate(lastCreatedDate)).child("TotalWechi").exists()){
                    total = (long) dataSnapshot.child(ConvForFirebaseDate(lastCreatedDate)).child("TotalWechi").getValue();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void DeleteHistory(final long Value){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        long tempo = total-Value;
        ref.child((ConvForFirebaseDate(lastCreatedDate))).child("TotalWechi").setValue(tempo);
    }
    private void UpdateHistory(long value){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        long tempo;
        Toast.makeText(WechiActivity.this, String.valueOf(value),Toast.LENGTH_SHORT).show();
        tempo = total+value;
        ref.child((ConvForFirebaseDate(lastCreatedDate))).child("TotalWechi").setValue(tempo);
    }
    private String ConvTodayDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" h:mm a");
        String date = simpleDateFormat.format(time);
        return date;
    }

//    class RetrieveDate extends AsyncTask<String,Void,Long> {
//
//        @Override
//        protected Long doInBackground(String... strings) {
//            DatabaseReference currDate = FirebaseDatabase.getInstance().getReference("CurrentDate");
//            currDate.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    lastCreatedDate = (long) dataSnapshot.child("lastDate").getValue();
//                    System.out.println("......"+dataSnapshot.child("lastDate").getValue());
//                    String toast = String.valueOf(lastCreatedDate);
//                    Toast.makeText(WechiActivity.this, toast,Toast.LENGTH_SHORT).show();
//
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            return lastCreatedDate;
//        }
//
//        @Override
//        protected void onPostExecute(Long aLong) {
//            super.onPostExecute(aLong);
//            lastCreatedDate = aLong;
//        }
//    }
}
