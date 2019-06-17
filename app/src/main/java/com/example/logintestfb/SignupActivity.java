package com.example.logintestfb;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {

    EditText etName,etPhone,etAddress;
    ListView lv;
    DatabaseReference friends_table;
    List<String> ids,data;
    ArrayAdapter<String> adapter;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            etName = findViewById(R.id.etName);
            etPhone = findViewById(R.id.etPhone);
            etAddress = findViewById(R.id.etAddress);
            lv=findViewById(R.id.lv);

            friends_table = FirebaseDatabase.getInstance().getReference()
                    .child("MyFriends");

            friends_table.addValueEventListener(new ValueEventListener(){

                @Override
                public void onDataChange(DataSnapshot p1)
                {

                    ids=new ArrayList<>();
                    data=new ArrayList<>();
                    if (p1 != null)
                    {
                        for (DataSnapshot row:p1.getChildren())
                        {
                            String output = "";
                            FriendModel friend=row.getValue(
                                    FriendModel.class);
                            ids.add(row.getKey());
                            output += friend.name + "\n";
                            output += friend.phone + "\n";
                            output += friend.address;
                            data.add(output);
                        }
                        adapter=new ArrayAdapter<String>(SignupActivity.this,android.R.layout.simple_list_item_1,data);
                        lv.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError p1)
                {
// TODO: Implement this method
                }

            });

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    Toast.makeText(getApplicationContext(),ids.get(p3),0).show();
                }


            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

                @Override
                public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
                {
                    showDeleteDialog(ids.get(p3));
                    return true;
                }
            });
        }

    public void addClick(View v)
    {
        String name=etName.getText().toString();
        String phone=etPhone.getText().toString();
        String address=etAddress.getText().toString();
        if (name.length() > 0 && phone.length() > 0 &&
                address.length() > 0)
        {
            FriendModel friend=new FriendModel(name, phone, address);
            friends_table.push().setValue(friend,
                    new DatabaseReference.CompletionListener(){

                        @Override
                        public void onComplete(DatabaseError p1,
                                               DatabaseReference p2)
                        {
                            if (p1==null)
                            {
                                etName.setText("");
                                etPhone.setText("");
                                etAddress.setText("");
                                Toast.makeText(getApplicationContext(),
                                        "Data inserted.", 1).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),
                                        "Cannot insert data.", 1).show();
                            }
                        }

                    });
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Please fill in all fields.", 1).show();
        }

        }

    public void showDeleteDialog(final String id){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Delete record");
        builder.setMessage("Are you sure you want to delete this record?");
        final AlertDialog ad=builder.create();
        ad.setButton(AlertDialog.BUTTON_POSITIVE,"Yes",
                new AlertDialog.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        friends_table.child(id).setValue(null);
                    }
                });

        ad.setButton(AlertDialog.BUTTON_NEGATIVE,"No",
                new AlertDialog.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface p1, int p2)
                    {
                        ad.dismiss();
                    }
                });
        ad.show();

        }
    }
