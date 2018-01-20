package com.example.luis.series.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.luis.series.Adapters.AdaptadorIconos;
import com.example.luis.series.R;

public class ListaIconos extends AppCompatActivity {

    private int [] iconos = new int[]
            {       R.drawable.icono0,
                    R.drawable.icono5,
                    R.drawable.icono6,
                    R.drawable.icono7,
                    R.drawable.icono8,
                    R.drawable.icono9,
                    R.drawable.icono10,
                    R.drawable.icono11,
                    R.drawable.icono12,
                    R.drawable.icono13,
                    R.drawable.icono14,
                    R.drawable.icono15,
                    R.drawable.icono16,
                    R.drawable.icono17};
    private ListView listaIconos;
    AdaptadorIconos adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_iconos);
        //COGEMOS NUESTRA LISTVIEW DEL XML Y LA GUARDAMOS
        listaIconos=findViewById(R.id.listaIconos);
        //NOS CREAMOS UN ADAPTADOR DE NUESTRA CLASE AdaptadorIconos Y LE PASAMOS EL LISTADO DE AVATARES A MOSTRAR EN CADA VISTA
        adapter=new AdaptadorIconos(this,iconos);
        listaIconos.setAdapter(adapter);

        //LE AÑADIMOS LA INTERFAZ setOnItemClickListener QUE NOS DEVOLVERÁ LA VISTA (AVATAR) SELECCIONADA
        listaIconos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                devolverNumeroIcono(i);
            }
        });
    }

    /*MÉTODO PARA DEVOLVER A LA CLASE ADMINISTRACIÓN EL AVATAR SELECCIONADO POR EL USUARIO A TRAVÉS DE UN INTENT, DESPUES CERRAMOS LA VENTANA
     , TAMBIÉN SE EJECUTARÁ SI EL USUARIO CIERRA LA VENTANA SIN SELECCIONAR NINGÚN AVATAR EN EL onBackPressed()
     */
    private void devolverNumeroIcono(int numeroIcono){
        Intent intent = new Intent();
        intent.putExtra("ICONOSELECCIONADO",numeroIcono);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        devolverNumeroIcono(-1);
        super.onBackPressed();
    }
}
