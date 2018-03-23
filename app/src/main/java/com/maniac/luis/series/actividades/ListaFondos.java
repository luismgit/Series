package com.maniac.luis.series.actividades;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.maniac.luis.series.Adapters.AdapatadorRecyclerFondos;
import com.maniac.luis.series.R;
import com.maniac.luis.series.utilidades.Common;
import com.google.firebase.database.FirebaseDatabase;

public class ListaFondos extends AppCompatActivity {

    private AdapatadorRecyclerFondos adaptador;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_fondos);
        FirebaseDatabase.getInstance().goOnline();
        recyclerView=findViewById(R.id.listaFondosRecycler);

        adaptador=new AdapatadorRecyclerFondos(this);
        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("DemoRecView", "Pulsado el elemento " + recyclerView.getChildPosition(view));
                int posicion=recyclerView.getChildLayoutPosition(view);
                ImageView fondo = view.findViewById(R.id.fondoImagenSerie);
                final Animation myRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
               // myRotation.setRepeatCount(0);
                myRotation.setDuration(1000);
                //INICIAMOS LA ANIMACIÓN DEL ICONO
                fondo.startAnimation(myRotation);
                //EJECUTAMOS UN HILO DE 1SEG PARA QUE LE DE TIEMPO A LA ANIMACIÓN A VERSE
                new HiloIcono(posicion).start();
            }
        });
        recyclerView.setAdapter(adaptador);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));




    }


    /*MÉTODO PARA DEVOLVER A LA CLASE TABACTIVITY EL FONDO SELECCIONADO POR EL USUARIO A TRAVÉS DE UN INTENT, DESPUES CERRAMOS LA VENTANA
  , TAMBIÉN SE EJECUTARÁ SI EL USUARIO CIERRA LA VENTANA SIN SELECCIONAR NINGÚN FONDO EN EL onBackPressed()
  */
    private void devolverNumeroFondo(int numeroFondo){
        Intent intent = new Intent();
        intent.putExtra(Common.FONDO_SELECCIONADO,numeroFondo);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        devolverNumeroFondo(-1);
        super.onBackPressed();
    }




    private class HiloIcono extends Thread{
        int i;
        public HiloIcono(int i){
            this.i=i;
        }
        public void run(){
            try {
                sleep(1300);

            } catch (InterruptedException e) {

            }
            devolverNumeroFondo(i);
        }
    }
}
