package com.example.luis.series.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.luis.series.Adapters.AdaptadorIconos;
import com.example.luis.series.R;
import com.example.luis.series.utilidades.Common;
import com.example.luis.series.utilidades.Imagenes;

public class ListaIconos extends AppCompatActivity {

    private int [] iconos;
    private ListView listaIconos;
    AdaptadorIconos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_iconos);
        iconos= Imagenes.getAvataresUsuarios();
        //COGEMOS NUESTRA LISTVIEW DEL XML Y LA GUARDAMOS
        listaIconos=findViewById(R.id.listaIconos);
        //NOS CREAMOS UN ADAPTADOR DE NUESTRA CLASE AdaptadorIconos Y LE PASAMOS EL LISTADO DE AVATARES A MOSTRAR EN CADA VISTA
        adapter=new AdaptadorIconos(this,iconos);
        listaIconos.setAdapter(adapter);

        //LE AÑADIMOS LA INTERFAZ setOnItemClickListener QUE NOS DEVOLVERÁ LA VISTA (AVATAR) SELECCIONADA
        listaIconos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView icono = view.findViewById(R.id.iconoItem);
                final Animation myRotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotator);
                myRotation.setRepeatCount(0);
                //INICIAMOS LA ANIMACIÓN DEL ICONO
                icono.startAnimation(myRotation);
                //EJECUTAMOS UN HILO DE 1SEG PARA QUE LE DE TIEMPO A LA ANIMACIÓN A VERSE
                    new HiloIcono(i).start();

            }
        });
    }

    /*MÉTODO PARA DEVOLVER A LA CLASE REGISTRO EL AVATAR SELECCIONADO POR EL USUARIO A TRAVÉS DE UN INTENT, DESPUES CERRAMOS LA VENTANA
     , TAMBIÉN SE EJECUTARÁ SI EL USUARIO CIERRA LA VENTANA SIN SELECCIONAR NINGÚN AVATAR EN EL onBackPressed()
     */
    private void devolverNumeroIcono(int numeroIcono){
        Intent intent = new Intent();
        intent.putExtra(Common.ICONOSELECCIONADO,numeroIcono);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        devolverNumeroIcono(-1);
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
            devolverNumeroIcono(i);
        }
    }


}
