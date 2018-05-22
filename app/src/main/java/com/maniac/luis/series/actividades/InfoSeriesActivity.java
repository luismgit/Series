package com.maniac.luis.series.actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;

public class InfoSeriesActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_series);
        textView=findViewById(R.id.textView2);
        String serie = getIntent().getStringExtra(Common.CONTACTO);
        textView.setText(serie);
    }
}
