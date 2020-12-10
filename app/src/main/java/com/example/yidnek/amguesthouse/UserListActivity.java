package com.example.yidnek.amguesthouse;

import android.app.AlertDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<User> userRecords;
    private RecyclerView.Adapter adapter;
    private DatabaseReference records;
    private DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.recyclerView_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        userRecords = new ArrayList<>();
        adapter = new UserListAdapter(userRecords,getApplicationContext());
        GetData();
    }

    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

        private List<User> listItems;
        private Context context;

        public UserListAdapter(List<User> listItems, Context context) {
            this.listItems = listItems;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_users,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(UserListAdapter.ViewHolder holder, final int position) {

            final User listItem = listItems.get(position);
            holder.uEmail.setText("Email ："+listItem.getEmail());
            holder.uUsername.setText("User Name ：" + listItem.getName()  );
            holder.uPhone.setText("Phone ：" + listItem.getPhone());
            holder.uType.setText("User Type ：" +listItem.getUserType());

            holder.uDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserDelete(listItem.getUserKey());
                }
            });


        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView uEmail,uUsername,uPhone,uType;
            public Button uDelete;


            public ViewHolder(View itemView) {
                super(itemView);

                uEmail =(TextView) itemView.findViewById(R.id.u_email);
                uUsername =(TextView) itemView.findViewById(R.id.u_username);
                uPhone =(TextView) itemView.findViewById(R.id.u_phone);
                uType =(TextView) itemView.findViewById(R.id.u_type);
                uDelete =(Button) itemView.findViewById(R.id.u_delete);



            }
        }
    }
    private void GetData(){
        userRecords.clear();
        records = FirebaseDatabase.getInstance().getReference("Users");
        records.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                User data = dataSnapshot.getValue(User.class);
                data.setUserKey(dataSnapshot.getKey());
                userRecords.add(data);
                recyclerView.setAdapter(adapter);
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
    private void UserDelete(String id){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(id);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        if (uid.equals(id)){
            Toast.makeText(UserListActivity.this,"You can not delete the current user",Toast.LENGTH_LONG).show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);

            builder.setMessage("Do you want to delete this user");

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    databaseReference.removeValue();
                    Toast.makeText(UserListActivity.this,"Deleted Successfully",Toast.LENGTH_SHORT).show();
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
    }
}
