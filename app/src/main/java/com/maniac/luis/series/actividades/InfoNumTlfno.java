package com.maniac.luis.series.actividades;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;

public class InfoNumTlfno extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_num_tlfno);
    }

    public void verPolitica(View view) {
        Uri uri = Uri.parse(Common.POL_PRIV_IUBENDA);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        this.startActivity(intent);
    }


    public void volver(View view) {
        finish();
    }
}
