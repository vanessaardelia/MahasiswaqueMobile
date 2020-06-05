package umn.ac.id.mahasiswaque;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Recyclerview extends RecyclerView.Adapter<Adapter_Recyclerview.MyViewHolder> {

    Context context;
    ArrayList<Mahasiswa> mahasiswa;

    public Adapter_Recyclerview(Context c , ArrayList<Mahasiswa> mahasiswa)
    {
        context = c;
        this.mahasiswa = mahasiswa;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Mahasiswa mahasiswaData = mahasiswa.get(position);
        holder.nama.setText(mahasiswa.get(position).getNama());
        holder.nim.setText(mahasiswa.get(position).getNim());
        Picasso.get().load(mahasiswa.get(position).getProfilePhoto()).into(holder.profilePic);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailMahasiswaActivity.class);
                intent.putExtra("nim",mahasiswa.get(position).getNim()).putExtra("nama",mahasiswa.get(position).getNama()).putExtra("prodi",mahasiswa.get(position).getProdi()).putExtra("angkatan", mahasiswa.get(position).getAngkatan().toString()).putExtra("biografi",mahasiswa.get(position).getBiografi()).putExtra("profile",mahasiswa.get(position).getProfilePhoto());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mahasiswa.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView nama, nim;
        ImageView profilePic;
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nama = (TextView) itemView.findViewById(R.id.nama);
            nim = (TextView) itemView.findViewById(R.id.nim);
            profilePic = (ImageView) itemView.findViewById(R.id.imageMahasiswa);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
