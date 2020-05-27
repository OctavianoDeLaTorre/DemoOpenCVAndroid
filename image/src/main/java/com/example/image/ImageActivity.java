package com.example.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.galery.GaleryFragment;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ImageActivity extends AppCompatActivity {
    // Id para la imagen recibida como parametro
    public static final String IMAGE_URI = "imageUri";

    // Vista para mostrar la imagen
    private ImageView imageView;

    private String imageUri;

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

        //Obtener uri de imagen recibida
        Bundle parametros = getIntent().getExtras();
        imageUri = parametros.getString(IMAGE_URI);

        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(Uri.parse(imageUri));
    }

    public void escalaGrises(View v){
        //Convertir bitmat a mat
        Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap bmtGray = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        // Crear copia de bitmat
        Mat tmp = new Mat (bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, tmp);
        //Convertir a escala de grises
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);
        //Convertir a bitmat en escala degrises
        Utils.matToBitmap(tmp, bmtGray);
        // Actualizar la vista para mostrar la imagen
        openFragment();
        imageView.setImageBitmap(bmtGray);
    }

    public void openFragment(){
        Bundle args = new Bundle();
        args.putString(IMAGE_URI,imageUri);
        // Crear fragmento de tu clase
        Fragment fragment = new GaleryFragment();
        fragment.setArguments(args);
        // Obtener el administrador de fragmentos a través de la actividad
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Definir una transacción
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Remplazar el contenido principal por el fragmento
        fragmentTransaction.replace(R.id.ctlFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        // Cambiar
        fragmentTransaction.commit();
    }

    /**
     * Guarda imagen en galeria.
     */
    public void guardarImagen(View v){
        //Obtener imagen del imageview
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        // Crear la carpeta para guardar la imagen
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/DemoFhotoShop";
        File carpeta = new File(file_path);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        // Crear archivo de imagen
        File foto = new File(carpeta, getImageName() + currentDateAndTime() + ".jpg");
        try {
            FileOutputStream fOut = new FileOutputStream(foto);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            // Actualiazar galeria para mmostrar la imagen guardada
            MakeSureFileWasCreatedThenMakeAvabile(foto);
        } catch (FileNotFoundException e) {
            Log.i("Error DPS:",e.getMessage());
        } catch (IOException e1) {
            Log.i("Error DPS:",e1.getMessage());
        }
        Toast.makeText(this,"Imagen guardada!!",Toast.LENGTH_SHORT).show();
    }

    /**
     * Asegúrese de que el archivo haya sido creado y
     * ponerlo a disposición.
     * @param file
     */
    private void MakeSureFileWasCreatedThenMakeAvabile(File file){
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() } , null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    /**
     * Genera nombre de la imagen qe se
     * guardara en galeria.
     */
    private String getImageName(){
        return "Imagen_editada_"+currentDateAndTime();
    }

    /**
     * Obtiene la fecha y hora del sistema.
     */
    private String currentDateAndTime() {
        Calendar c = Calendar.getInstance();
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-­ss").format(c.getTime());
    }

}
