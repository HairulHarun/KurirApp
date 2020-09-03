package com.example.gorontalo.kurir_app.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorontalo.kurir_app.ProfileActivity;
import com.example.gorontalo.kurir_app.R;
import com.example.gorontalo.kurir_app.adapter.SessionPekerjaanAdapter;
import com.example.gorontalo.kurir_app.adapter.URLAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentProfilePelanggan extends BottomSheetDialogFragment {
    private SessionPekerjaanAdapter sessionPekerjaanAdapter;
    private CircleImageView circleImageView;
    private TextView txtNama, txtHp, txtAlamat;
    private String NAMA, HP, ALAMAT;
    private Button btnChat, btnPanggil;

    public FragmentProfilePelanggan() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_pelanggan, container, false);

        sessionPekerjaanAdapter = new SessionPekerjaanAdapter(getActivity().getApplicationContext());
        NAMA = sessionPekerjaanAdapter.getPelanggan();
        HP = sessionPekerjaanAdapter.getHpPelanggan();
        ALAMAT = sessionPekerjaanAdapter.getAlamatPelanggan();

        circleImageView = view.findViewById(R.id.img_fragment_pelanggan);
        txtNama = view.findViewById(R.id.txtFragmentNamaPelanggan);
        txtHp = view.findViewById(R.id.txtFragmentHpPelanggan);
        txtAlamat = view.findViewById(R.id.txtFragmentAlamatPelanggan);
        btnChat = view.findViewById(R.id.btnFragmentChat);
        btnPanggil = view.findViewById(R.id.btnFragmentPanggil);

        Picasso.with(getActivity())
                .load(new URLAdapter().getPhotoProfilePelanggan()+sessionPekerjaanAdapter.getPhotoPelanggan())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(circleImageView);

        txtNama.setText(NAMA);
        txtHp.setText(HP);
        txtAlamat.setText(ALAMAT);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp(HP);
            }
        });

        btnPanggil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", HP, null)));
            }
        });

        return view;
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void openWhatsApp(String number){
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            String text = "Ping !";
            String finalNumber = "+62"+number.substring(1);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+finalNumber +"&text="+text));
            startActivity(intent);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
        }
    }
}
