package com.example.gorontalo.kurir_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.gorontalo.kurir_app.R;
import com.example.gorontalo.kurir_app.model.PekerjaanBarangModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RVPekerjaanBarangAdapter extends RecyclerView.Adapter<RVPekerjaanBarangAdapter.ViewHolder>{
    private Context context;
    private List<PekerjaanBarangModel> list;

    private int lastPosition = -1;

    public RVPekerjaanBarangAdapter(Context context, List<PekerjaanBarangModel> list){
        super();

        this.list = list;
        this.context = context;
    }

    @Override
    public RVPekerjaanBarangAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pekerjaan_barang, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final PekerjaanBarangModel pekerjaanBarangModel = list.get(position);

        holder.txtCardNama.setText(pekerjaanBarangModel.getNamaOutletBarang());
        holder.txtCardHarga.setText(konversiRupiah(Integer.parseInt(pekerjaanBarangModel.getHarga())));
        holder.txtCardQty.setText("Qty : "+pekerjaanBarangModel.getQty());
        holder.txtCardJumlah.setText(konversiRupiah(Integer.parseInt(pekerjaanBarangModel.getJumlah())));

        Picasso.with(context)
                .load(new URLAdapter().getPhotoBarang()+pekerjaanBarangModel.getPhoto())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.imgBarang);


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
        public ImageView imgBarang;
        public TextView txtCardNama, txtCardHarga, txtCardQty, txtCardJumlah;

        public ViewHolder(View itemView) {
            super(itemView);
            imgBarang = (ImageView) itemView.findViewById(R.id.imgBarang);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardNama);
            txtCardHarga = (TextView) itemView.findViewById(R.id.txtCardHarga);
            txtCardQty =(TextView) itemView.findViewById(R.id.txtCardQty);
            txtCardJumlah =(TextView) itemView.findViewById(R.id.txtCardJumlah);

        }

    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }

}
