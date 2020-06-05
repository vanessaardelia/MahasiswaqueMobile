package umn.ac.id.mahasiswaque;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    ArrayList<Mahasiswa> suggestList;
    Adapter_Recyclerview searchAdapter;
    private Button btnSearch;
    private CardView cardSearch;
    private RecyclerView recyclerView;
    private SearchView searchBar;
    Session sharedpref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Search Mahasiswa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference("mahasiswa");
        suggestList = new ArrayList<Mahasiswa>();

        searchBar = findViewById(R.id.search_bar);
        recyclerView = findViewById(R.id.recyclerSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        cardSearch = findViewById(R.id.cardSearch);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()){
                    Mahasiswa item = dataSnap.getValue(Mahasiswa.class);
                    suggestList.add(item);
                }
                searchAdapter = new Adapter_Recyclerview(SearchActivity.this, suggestList);
                recyclerView.setAdapter(searchAdapter);
                recyclerView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(searchBar!= null){
            searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return false;
                }
            });
        }

    }
    private void search(String str){
        Log.i("searchData",str);
        ArrayList<Mahasiswa> myList = new ArrayList<>();
        for(Mahasiswa searchMenu : suggestList){
            if(searchMenu.getNama().toLowerCase().contains(str.toLowerCase())){
                myList.add(searchMenu);
            }
        }
        searchAdapter = new Adapter_Recyclerview(SearchActivity.this, myList);
        recyclerView.setAdapter(searchAdapter);
        if(str != null){
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }
}
