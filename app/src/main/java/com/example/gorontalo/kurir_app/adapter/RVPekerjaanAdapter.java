package com.example.gorontalo.kurir_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorontalo.kurir_app.DetailPekerjaanActivity;
import com.example.gorontalo.kurir_app.PekerjaanActivity;
import com.example.gorontalo.kurir_app.R;
import com.example.gorontalo.kurir_app.model.PekerjaanModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RVPekerjaanAdapter extends RecyclerView.Adapter<RVPekerjaanAdapter.ViewHolder>{
    private Context context;
    private List<PekerjaanModel> list;

    private int lastPosition = -1;

    public RVPekerjaanAdapter(Context context, List<PekerjaanModel> list){
        super();

        this.list = list;
        this.context = context;
    }

    @Override
    public RVPekerjaanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pekerjaan, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final PekerjaanModel pekerjaanModel = list.get(position);

        holder.txtId.setText(pekerjaanModel.getId());
        holder.txtIdPelanggan.setText(pekerjaanModel.getIdPelanggan());
        holder.txtNamaPelanggan.setText(pekerjaanModel.getNamaPelanggan());
        holder.txtWaktu.setText(pekerjaanModel.getWaktu());
        holder.txtJarak.setText(Math.floor(pekerjaanModel.getJarak())+" Km");

        if (pekerjaanModel.getStatus().equals("0")){
            holder.txtStatus.setBackgroundResource(R.color.colorAccent);
            holder.txtStatus.setText("MENUJU OUTLET");
        }else if (pekerjaanModel.getStatus().equals("1")){
            holder.txtStatus.setBackgroundResource(R.color.colorYellow);
            holder.txtStatus.setText("MULAI BELANJA");
        }else if (pekerjaanModel.getStatus().equals("2")){
            holder.txtStatus.setBackgroundResource(R.color.colorPrimaryDark);
            holder.txtStatus.setText("MENUJU PELANGGAN");
        }else if (pekerjaanModel.getStatus().equals("3")){
            holder.txtStatus.setBackgroundResource(R.color.colorPrimary);
            holder.txtStatus.setText("SELESAI");
        }else if (pekerjaanModel.getStatus().equals("4")){
            holder.txtStatus.setBackgroundResource(R.color.colorRed);
            holder.txtStatus.setText("DIBATALKAN");
        }


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pekerjaanModel.getStatus().equals("3") || pekerjaanModel.getStatus().equals("4")){
                    Intent intent = new Intent(context, DetailPekerjaanActivity.class);
                    intent.putExtra("id_pekerjaan", pekerjaanModel.getId());
                    intent.putExtra("nama_pelanggan", pekerjaanModel.getNamaPelanggan());
                    intent.putExtra("jarak", Math.floor(pekerjaanModel.getJarak()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{

                }
            }
        });
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
        }

    }
    @Override
    public int getItemCount() {
        if(list!= null) {
            return list.size();
        }else{
            return 0;
        }

    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtId, txtIdPelanggan, txtNamaPelanggan, txtWaktu, txtJarak, txtStatus;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            txtId = (TextView) itemView.findViewById(R.id.txtId);
            txtIdPelanggan = (TextView) itemView.findViewById(R.id.txtIdPelanggan);
            txtNamaPelanggan =(TextView) itemView.findViewById(R.id.txtNamaPelanggan);
            txtWaktu =(TextView) itemView.findViewById(R.id.txtWaktu);
            txtJarak =(TextView) itemView.findViewById(R.id.txtJarak);
            txtStatus =(TextView) itemView.findViewById(R.id.txtStatus);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.layoutCard);

        }

    }

}
