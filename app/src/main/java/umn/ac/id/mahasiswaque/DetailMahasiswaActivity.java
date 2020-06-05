package umn.ac.id.mahasiswaque;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class DetailMahasiswaActivity extends AppCompatActivity {
    private TextView nim;
    private TextView nama;
    private TextView prodi;
    private TextView angkatan;
    private TextView biografi;
    private Button delete;
    private Button edit;
    private ImageView profile;
    String string_profile;
    String nim_delete;
    DatabaseReference reference;
    Session sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mahasiswa);
        getSupportActionBar().setTitle("Detail Mahasiswa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        nim = findViewById(R.id.nim_isi);
        nim.setText(getIntent().getStringExtra("nim"));

        nama = findViewById(R.id.nama_isi);
        nama.setText(getIntent().getStringExtra("nama"));

        prodi = findViewById(R.id.prodi_isi);
        prodi.setText(getIntent().getStringExtra("prodi"));

        angkatan = findViewById(R.id.angkatan_isi);
        angkatan.setText(getIntent().getStringExtra("angkatan"));

        biografi = findViewById(R.id.biografi_isi);
        biografi.setText(getIntent().getStringExtra("biografi"));

        profile = findViewById(R.id.profile);
        string_profile = getIntent().getStringExtra("profile");
        //picasso untuk load image; load(datanya apa).into(di tempat apa)
        Picasso.get().load(string_profile).into(profile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(DetailMahasiswaActivity.this, "Could not get the image", Toast.LENGTH_SHORT).show();
            }
        });

        delete = findViewById(R.id.delete);
        nim_delete = getIntent().getStringExtra("nim");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(DetailMahasiswaActivity.this)
                        .setTitle("Are you sure want to delete this mahasiswa??")
                        .setMessage("Select action :")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteMahasiswa(nim_delete);
                                Intent intent = new Intent(DetailMahasiswaActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(DetailMahasiswaActivity.this)
                        .setTitle("Are you sure want to edit this mahasiswa??")
                        .setMessage("Select action :")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(DetailMahasiswaActivity.this, Edit_MahasiswaActivity.class);
                                intent.putExtra("nim", getIntent().getStringExtra("nim")).putExtra("nama", getIntent().getStringExtra("nama")).putExtra("prodi", getIntent().getStringExtra("prodi")).putExtra("angkatan", getIntent().getStringExtra("angkatan")).putExtra("biografi", getIntent().getStringExtra("biografi")).putExtra("profile", string_profile);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void deleteMahasiswa(String nim_delete) {
        reference = FirebaseDatabase.getInstance().getReference("mahasiswa").child(nim_delete);
        reference.removeValue();

        Toast.makeText(DetailMahasiswaActivity.this, "Mahasiswa " + nim_delete + " is deleted", Toast.LENGTH_SHORT).show();
    }
}
