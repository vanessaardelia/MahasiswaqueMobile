package umn.ac.id.mahasiswaque;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private Session session;
    DatabaseReference reference;
    RecyclerView recyclerView;
    ArrayList<Mahasiswa> list;
    ArrayList<Mahasiswa> nim_asc_list;
    ArrayList<Mahasiswa> nama_asc_list;
    Adapter_Recyclerview adapter;
    FloatingActionButton btnAdd;
    private TextView searchBar;

    Session sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new Session(this);
        if (!session.loggedin()) {
            logout();
        }

        //recylcer view
        recyclerView = findViewById(R.id.recyclerview_mahasiswa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        reference = FirebaseDatabase.getInstance().getReference().child("mahasiswa");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<Mahasiswa>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                    list.add(mahasiswa);
                }
                adapter = new Adapter_Recyclerview(MainActivity.this,list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
            }
        });

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchScreen  = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchScreen);
            }
        });

        //add mahasiswa FAB
        btnAdd = findViewById(R.id.add_mahasiswa);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_nim:
                Toast.makeText(this, "Sort NIM", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nim_asc:
                nim_asc();
                return true;
            case R.id.nim_desc:
                nim_desc();
                return true;
            case R.id.sort_name:
                Toast.makeText(this, "Sort Name", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nama_asc:
                nama_asc();
                return true;
            case R.id.nama_desc:
                nama_desc();
                return true;
            case R.id.aboutme:
                Intent intent = new Intent(MainActivity.this, AboutmeActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void nama_desc() {
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("mahasiswa").orderByChild("nama");
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nama_asc_list = new ArrayList<Mahasiswa>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        String menuKey = dataSnapshot1.getKey();
                        Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                        nama_asc_list.add(mahasiswa);
                    }
                    Collections.reverse(nama_asc_list);
                    Adapter_Recyclerview adapter_nama_asc = new Adapter_Recyclerview(MainActivity.this,nama_asc_list);
                    recyclerView.setAdapter(adapter_nama_asc);
                    adapter_nama_asc.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MainActivity.this, "Data Not Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nama_asc() {
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("mahasiswa").orderByChild("nama");
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nama_asc_list = new ArrayList<Mahasiswa>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        String menuKey = dataSnapshot1.getKey();
                        Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                        nama_asc_list.add(mahasiswa);
                    }

                    Adapter_Recyclerview adapter_nama_asc = new Adapter_Recyclerview(MainActivity.this,nama_asc_list);
                    recyclerView.setAdapter(adapter_nama_asc);
                    adapter_nama_asc.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MainActivity.this, "Data Not Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nim_desc() {
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("mahasiswa").orderByChild("nim");
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nim_asc_list = new ArrayList<Mahasiswa>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        String menuKey = dataSnapshot1.getKey();
                        Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                        nim_asc_list.add(mahasiswa);
                    }
                    Collections.reverse(nim_asc_list);
                    Adapter_Recyclerview adapterCat = new Adapter_Recyclerview(MainActivity.this,nim_asc_list);
                    recyclerView.setAdapter(adapterCat);
                    adapterCat.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MainActivity.this, "Data Not Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void nim_asc() {
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("mahasiswa").orderByChild("nim");
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nim_asc_list = new ArrayList<Mahasiswa>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        String menuKey = dataSnapshot1.getKey();
                        Mahasiswa mahasiswa = dataSnapshot1.getValue(Mahasiswa.class);
                        nim_asc_list.add(mahasiswa);
                    }

                    Adapter_Recyclerview adapterCat = new Adapter_Recyclerview(MainActivity.this,nim_asc_list);
                    recyclerView.setAdapter(adapterCat);
                    adapterCat.notifyDataSetChanged();
                }
                else{
                    Toast.makeText(MainActivity.this, "Data Not Found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }
}
