package umn.ac.id.mahasiswaque;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Edit_MahasiswaActivity extends AppCompatActivity {
    Session sharedpref;
    private TextView nim_isi, nama_isi, biografi_isi;
    private Spinner spinner_prodi;
    private Spinner spinner_angkatan;
    ImageView profile;
    String profile_mhs;
    Button edit_nama, edit_prodi, edit_angkatan, edit_profile, edit_biografi, edit_profile_ok;
    DatabaseReference databaseReference;
    String nama_mhs, nim_mhs;
    String biografi_mhs;
    String prodi_mhs;
    String angkatan_mhs;
    Mahasiswa mahasiswa;
    DatabaseReference reference;
    private Uri filePath;
    private StorageReference mStorageRef;
    private StorageTask uploadTask;
    private static final int CAMERA_PERM_CODE = 101;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dark mode
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mahasiswa);

        //nama toolbar
        getSupportActionBar().setTitle("Edit Mahasiswa");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //database ref
        databaseReference = FirebaseDatabase.getInstance().getReference().child("mahasiswa");

        //nim
        nim_mhs = getIntent().getStringExtra("nim");
        nim_isi = findViewById(R.id.nim_isi);
        nim_isi.setText(nim_mhs);

        //nama
        nama_mhs = getIntent().getStringExtra("nama");
        nama_isi = findViewById(R.id.nama_isi);
        nama_isi.setText(nama_mhs);

        //biografi
        biografi_mhs = getIntent().getStringExtra("biografi");
        biografi_isi = findViewById(R.id.biografi_isi);
        biografi_isi.setText(biografi_mhs);

        //spinner prodi
        prodi_mhs = getIntent().getStringExtra("prodi");
        spinner_prodi = findViewById(R.id.prodi_isi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.prodi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_prodi.setAdapter(adapter);
        if (prodi_mhs != null) {
            int spinnerPosition = adapter.getPosition(prodi_mhs);
            spinner_prodi.setSelection(spinnerPosition);
        }

        //spinner angkatan
        angkatan_mhs = getIntent().getStringExtra("angkatan");
        spinner_angkatan = findViewById(R.id.angkatan_isi);
        ArrayAdapter<CharSequence> adapter_angkatan = ArrayAdapter.createFromResource(this, R.array.angkatan, android.R.layout.simple_spinner_item);
        adapter_angkatan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_angkatan.setAdapter(adapter_angkatan);
        if (angkatan_mhs != null) {
            int spinnerPosition = adapter_angkatan.getPosition(angkatan_mhs);
            spinner_angkatan.setSelection(spinnerPosition);
        }

        //profile
        profile_mhs = getIntent().getStringExtra("profile");
        reference = FirebaseDatabase.getInstance().getReference().child("mahasiswa");
        profile = findViewById(R.id.profile);
        Picasso.get().load(profile_mhs).into(profile, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(Edit_MahasiswaActivity.this, "Could not get the image", Toast.LENGTH_SHORT).show();
            }
        });

        //edit_nama
        edit_nama = findViewById(R.id.edit_nama);
        edit_nama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_nama_mhs();
            }
        });

        //edit_prodi
        edit_prodi = findViewById(R.id.edit_prodi);
        edit_prodi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_prodi_mhs();
            }
        });

        //edit_angkatan
        edit_angkatan = findViewById(R.id.edit_angkatan);
        edit_angkatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_angkatan_mhs();
            }
        });

        //edit_biografi
        edit_biografi = findViewById(R.id.edit_biografi);
        edit_biografi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_biografi_mhs();
            }
        });

        //edit_profile
        //storage foto
        mStorageRef = FirebaseStorage.getInstance().getReference("Images/");
        edit_profile = findViewById(R.id.edit_profile);
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(Edit_MahasiswaActivity.this)
                        .setTitle("Select action")
                        .setMessage("Select action :")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (ContextCompat.checkSelfPermission(Edit_MahasiswaActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Edit_MahasiswaActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
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

        edit_profile_ok = findViewById(R.id.edit_profile_ok);
        edit_profile_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(Edit_MahasiswaActivity.this, "Data upload is in progress", Toast.LENGTH_LONG).show();
                } else {
                    fileUpload();
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Edit_MahasiswaActivity.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Edit_MahasiswaActivity.this,
                        "id.ac.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Edit_MahasiswaActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void fileUpload() {
        Query queryCat =  FirebaseDatabase.getInstance().getReference().child("mahasiswa").orderByChild("nim").equalTo(nim_mhs);
        queryCat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final String nim_query = getIntent().getStringExtra("nim");
                    final StorageReference ref = mStorageRef.child(nim_query + ".jpg");
                    if(filePath == null){
                        Toast.makeText(Edit_MahasiswaActivity.this, "All Field Must Not Be Empty", Toast.LENGTH_LONG).show();
                    }else {
                        uploadTask = ref.putFile(filePath);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        reference.child(nim_query).child("profilePhoto").setValue(uri.toString());
                                        Toast.makeText(Edit_MahasiswaActivity.this, "Data edited successfully", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Edit_MahasiswaActivity.this, "Image upload failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Edit_MahasiswaActivity.this, "Database Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(Edit_MahasiswaActivity.this, "CAMERA PERMESSION IS REQUIRED TO USE CAMERA", Toast.LENGTH_SHORT).show();
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

    private void edit_nama_mhs() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.fragment_edit_nama, null);
        final EditText nama = alertLayout.findViewById(R.id.nama);
        AlertDialog.Builder alert = new AlertDialog.Builder(Edit_MahasiswaActivity.this);
        alert.setTitle("Edit Nama");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nama_mhs_baru = nama.getText().toString();
                if(TextUtils.isEmpty(nama.getText().toString())){
                    Toast.makeText(Edit_MahasiswaActivity.this, "All Field Must Not Be Empty", Toast.LENGTH_LONG).show();
                }else {
                    nama_isi.setText(nama_mhs_baru);
                    databaseReference.child(nim_mhs).child("nama").setValue(nama_mhs_baru);
                    nama.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void edit_prodi_mhs() {
        String prodi = spinner_prodi.getSelectedItem().toString();
        databaseReference.child(nim_mhs).child("prodi").setValue(prodi);
    }

    private void edit_angkatan_mhs(){
        databaseReference.child(nim_mhs).child("angkatan").setValue(Long.valueOf(spinner_angkatan.getSelectedItem().toString()));
    }

    private void edit_biografi_mhs(){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.fragment_edit_biografi, null);
        final EditText biografi = alertLayout.findViewById(R.id.biografi);
        AlertDialog.Builder alert = new AlertDialog.Builder(Edit_MahasiswaActivity.this);
        alert.setTitle("Edit Biografi");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String biografi_mhs_baru = biografi.getText().toString();
                if(TextUtils.isEmpty(biografi.getText().toString())){
                    Toast.makeText(Edit_MahasiswaActivity.this, "All Field Must Not Be Empty", Toast.LENGTH_LONG).show();
                }else {
                    biografi_isi.setText(biografi_mhs_baru);
                    databaseReference.child(nim_mhs).child("biografi").setValue(biografi_mhs_baru);
                    biografi.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
