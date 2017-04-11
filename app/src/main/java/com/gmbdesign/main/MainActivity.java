package com.gmbdesign.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String rutaFoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pedirPermisos();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.d("TAG", "Permiso para usar la camara y lectura/escritura concedido");
            tomarFoto();
        } else {
            Log.d("TAG", "Permiso para usar la camara y lectura/escritura denegado");
            Log.d("TAG", "Abandonamos la ejecución");
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case RESULT_OK:
                Log.d("TAG", "El usuario guardó la foto");
                Bitmap foto = null;

                if(data == null){
                    //significa que la foto se almaceno en un fichero.
                    File fichero = new File(rutaFoto);
                    foto = BitmapFactory.decodeFile(fichero.getAbsolutePath());
                } else {
                    //la foto viene dentro del intent de devolucion
                    foto = (Bitmap) data.getExtras().get("data");
                }

                ImageView myImagen = (ImageView) findViewById(R.id.imagen);
                myImagen.setImageBitmap(foto);

                break;
            case RESULT_CANCELED:
                Log.d("TAG", "El usuario canceló la foto");
                break;
            default:
                Log.d("TAG", "Algo no fue bien...");

        }
    }

    private void pedirPermisos(){
        String[] permisos = new String[2];

        permisos[0] = Manifest.permission.CAMERA;
        permisos[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        ActivityCompat.requestPermissions(this, permisos, 999);
    }

    private void tomarFoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //dos opciones, usar una URI que identifica al fichero o no usar nada y que el intent de vuelta
        //nos traiga directamente el bitmap

        Uri uriFoto = crearFicheroImagen();

        intent.putExtra("Uri", uriFoto);

        startActivityForResult(intent, 8);

    }

    private Uri crearFicheroImagen(){

        Uri uriDev = null;

        String nombreFichero = null;
        String momentoActual = null;
        String rutaDirectorioPublico = null;

        File fichero = null;

        momentoActual = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        nombreFichero = "CICE_"+momentoActual+".jpg";
        rutaDirectorioPublico = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        rutaFoto = rutaDirectorioPublico+"/"+nombreFichero;

        fichero = new File(rutaFoto);

        try {
            if(fichero.createNewFile()){
                Log.d("TAG", "El fichero se ha creado correctamente");
                uriDev = Uri.fromFile(fichero);
            }
        }catch (Throwable ex){
            Log.e("TAG", "Error al crear el fichero", ex);
        }

        return uriDev;

    }
}
