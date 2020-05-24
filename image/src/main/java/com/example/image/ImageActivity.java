package com.example.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ImageActivity extends Activity {

    public static final String IMAGE_URI = "imageUri";
    private ImageView imageView;

    //Iniciar openCV
    private static boolean initOpenCV;
    static { initOpenCV = OpenCVLoader.initDebug(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Verificar si openCV inicio correctamente
        if (initOpenCV){
            Toast.makeText(this,"OpenCV iniciado",Toast.LENGTH_LONG).show();
        }

        //Obtener uri de imagen
        Bundle parametros = this.getIntent().getExtras();
        String imageUri = parametros.getString(IMAGE_URI);

        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(imageUri));
    }

    public void escalaGrises(View v){
        //Convertir bitmat a mat
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Mat tmp = new Mat (bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, tmp);
        //Convertir a escala de grises
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
        //Convertir a bitmat
        Utils.matToBitmap(tmp, bmp);
        imageView.setImageBitmap(bmp);
    }

    public void guardarImagen(View v){
        //Obtener imagen del imageview
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();

        OutputStream fileOutStream = null;
        Uri uri;
        try {
            //Crear instancia del directorio
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Imagenes_Editadas" + File.separator);
            //Crear directorio
            file.mkdirs();

            //Crear archivo de imagen
            File directorioImagenes = new File(file, "esalade_grises.jpg");
            uri = Uri.fromFile(directorioImagenes);
            fileOutStream = new FileOutputStream(directorioImagenes);
        } catch (Exception e) {
            Log.e("ERROR!", e.getMessage());
        }

        try {
            //Guardar imagen
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream);
            fileOutStream.flush();
            fileOutStream.close();
        } catch (Exception e) {
            Log.e("ERROR!", e.getMessage());
        }

        Toast.makeText(this,"Imagen guardada!!",Toast.LENGTH_SHORT).show();
    }
}
