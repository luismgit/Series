package com.maniac.luis.series.actividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.FirebaseDatabase;
import com.maniac.luis.series.AdaptadorColorSolido;
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;

public class ListaFondosColorSolido extends AppCompatActivity {

    RecyclerView rv;
    private AdaptadorColorSolido adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_fondos_color_solido);
        FirebaseDatabase.getInstance().goOnline();
        rv=findViewById(R.id.listaFondosColorSolido);
        adapter=new AdaptadorColorSolido(this);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int posicion=rv.getChildLayoutPosition(view);
                devolverNumeroFondo(posicion);
            }
        });
        layoutManager=new GridLayoutManager(this,2);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
    }

    private void devolverNumeroFondo(int posicion) {
        Intent intent = new Intent();
        intent.putExtra(Common.FONDO_SELECCIONADO,posicion);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        devolverNumeroFondo(-1);
        super.onBackPressed();
    }
}
