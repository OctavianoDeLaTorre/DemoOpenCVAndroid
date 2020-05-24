package com.example.image;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;

public class ImageActivity extends Activity {

    private static boolean initOpenCV;

    static { initOpenCV = OpenCVLoader.initDebug(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        if (initOpenCV){
            Toast.makeText(this,"OpenCV iniciado",Toast.LENGTH_LONG).show();
        }
    }
}
