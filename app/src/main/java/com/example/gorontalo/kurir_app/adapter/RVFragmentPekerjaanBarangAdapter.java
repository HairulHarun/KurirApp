package com.example.gorontalo.kurir_app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.example.gorontalo.kurir_app.R;
import com.example.gorontalo.kurir_app.model.PekerjaanBarangModel;

import java.util.List;
import java.util.Random;

public class RVFragmentPekerjaanBarangAdapter extends RecyclerView.Adapter<RVFragmentPekerjaanBarangAdapter.ViewHolder> {
    private Context context;
    private List<PekerjaanBarangModel> list;

    private int lastPosition = -1;

    public RVFragmentPekerjaanBarangAdapter(Context context, List<PekerjaanBarangModel> list){
        super();

        this.list = list;
        this.context = context;
    }

    @Override
    public RVFragmentPekerjaanBarangAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_fragment_pekerjaan_barang, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final PekerjaanBarangModel pekerjaanBarangModel = list.get(position);

        holder.txtCardNama.setText(pekerjaanBarangModel.getNamaOutletBarang());
        holder.txtCardQty.setText(pekerjaanBarangModel.getQty()+" X");
        holder.txtCardJumlah.setText("Rp. "+pekerjaanBarangModel.getJumlah());

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
        public TextView txtCardNama, txtCardQty, txtCardJumlah;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardNama);
            txtCardQty =(TextView) itemView.findViewById(R.id.txtCardQty);
            txtCardJumlah =(TextView) itemView.findViewById(R.id.txtCardJumlah);

        }

    }
}
