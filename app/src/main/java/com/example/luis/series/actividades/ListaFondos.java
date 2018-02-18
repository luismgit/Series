package com.example.luis.series.actividades;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.luis.series.Adapters.AdaptadorFondos;
import com.example.luis.series.Adapters.AdaptadorIconos;
import com.example.luis.series.R;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.Imagenes;

public class ListaFondos extends AppCompatActivity{

    private int [] fondos;
    private ListView listaFondos;
    AdaptadorFondos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_fondos);
        fondos= Imagenes.getFondosPantalla();
        //COGEMOS NUESTRA LISTVIEW DEL XML Y LA GUARDAMOS
        listaFondos=findViewById(R.id.listaFondos);
        //NOS CREAMOS UN ADAPTADOR DE NUESTRA CLASE AdaptadorFondos Y LE PASAMOS EL LISTADO DE FONDOS A MOSTRAR EN CADA VISTA
        adapter=new AdaptadorFondos(this,fondos);
        listaFondos.setAdapter(adapter);

        //LE AÑADIMOS LA INTERFAZ setOnItemClickListener QUE NOS DEVOLVERÁ LA VISTA (FONDO) SELECCIONADO
        listaFondos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView fondo = view.findViewById(R.id.fondoItem);
                final Animation myRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotator);
                myRotation.setRepeatCount(0);
                //INICIAMOS LA ANIMACIÓN DEL ICONO
                fondo.startAnimation(myRotation);
                //EJECUTAMOS UN HILO DE 1SEG PARA QUE LE DE TIEMPO A LA ANIMACIÓN A VERSE
                new HiloIcono(i).start();

            }
        });
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
