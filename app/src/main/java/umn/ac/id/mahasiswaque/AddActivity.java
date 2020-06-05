package umn.ac.id.mahasiswaque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import android.text.TextUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    EditText nim, nama, biografi;
    Button add;
    DatabaseReference reference;
    Mahasiswa mahasiswa;
    ImageView profile;
    private Spinner spinner_prodi;
    private Spinner spinner_angkatan;

    private static final int CAMERA_PERM_CODE = 101;
    private Uri filePath;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private String currentPhotoPath;

    Session sharedpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().setTitle("Add Mahasiswa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //storage foto
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/");
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(AddActivity.this)
                        .setTitle("Select action")
                        .setMessage("Select action :")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
                                } else {
                                    dispatchTakePictureIntent();
                                }
                            }
                        })
                        .setNegativeButton("Storage", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, 1);
                            }
                        })
                        .show();
            }
        });

        //spinner prodi
        spinner_prodi = findViewById(R.id.prodi_isi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.prodi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prodi.setAdapter(adapter);
        spinner_prodi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String prodi = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), prodi, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //spinner angkatan
        spinner_angkatan = findViewById(R.id.angkatan_isi);
        ArrayAdapter<CharSequence> adapter_angkatan = ArrayAdapter.createFromResource(this, R.array.angkatan, android.R.layout.simple_spinner_item);
        adapter_angkatan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_angkatan.setAdapter(adapter_angkatan);
        spinner_angkatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String angkatan = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), angkatan, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //edit text
        nim = findViewById(R.id.nim_isi);
        nama = findViewById(R.id.nama_isi);
        biografi = findViewById(R.id.biografi_isi);

        //button
        add = findViewById(R.id.add);

        //insert to database
        mahasiswa = new Mahasiswa();
        reference = FirebaseDatabase.getInstance().getReference().child("mahasiswa");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(AddActivity.this, "Data upload is in progress", Toast.LENGTH_LONG).show();
                } else {
                    if(TextUtils.isEmpty(nim.getText().toString()) || TextUtils.isEmpty(nama.getText().toString()) || TextUtils.isEmpty(biografi.getText().toString())){
                        Toast.makeText(AddActivity.this, "All Field Must Not Be Empty", Toast.LENGTH_LONG).show();
                    }else {
                        fileUpload();
                        Intent intent = new Intent(AddActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void fileUpload() {
        String nim_mhs = nim.getText().toString();
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("mahasiswa").orderByChild("nim").equalTo(nim_mhs);
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    Toast.makeText(AddActivity.this, "Choose a different NIM",Toast.LENGTH_SHORT).show();
                }else{
                    final StorageReference ref = mStorageRef.child(nim.getText().toString() + ".jpg");
                    if(filePath == null){
                        Toast.makeText(AddActivity.this, "All Field Must Not Be Empty", Toast.LENGTH_LONG).show();
                    }else {
                        uploadTask = ref.putFile(filePath);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        mahasiswa = new Mahasiswa(
                                                nama.getText().toString(),
                                                nim.getText().toString(),
                                                spinner_prodi.getSelectedItem().toString(),
                                                Long.valueOf(spinner_angkatan.getSelectedItem().toString()),
                                                biografi.getText().toString(),
                                                "" + uri.toString());
                                        reference.child(nim.getText().toString()).setValue(mahasiswa);
                                        Toast.makeText(AddActivity.this, "Data inserted successfully", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(AddActivity.this, "Image upload failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddActivity.this, "Database Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(AddActivity.this, "CAMERA PERMESSION IS REQUIRED TO USE CAMERA", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            filePath = data.getData();
            Picasso.get().load(filePath).into(profile);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            File f = new File(currentPhotoPath);
            filePath = Uri.fromFile(f);
            profile.setImageURI(filePath);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = AddActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(AddActivity.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(AddActivity.this,
                        "id.ac.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }
}
