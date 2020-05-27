package com.example.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaneraActivity extends AppCompatActivity {

    private static String TAG = "CameraXBasic";
    private static String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static int REQUEST_CODE_PERMISSIONS = 10;
    private static String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String IMAGE_URI = "imageUri";

    private Preview preview = null;
    private ImageCapture imageCapture = null;
    private ImageAnalysis imageAnalyzer = null;
    private Camera camera = null;

    private File outputDirectory;
    private ExecutorService cameraExecutor;

    private PreviewView viewFinder ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewFinder = findViewById(R.id.viewFinder);

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        outputDirectory = getOutputDirectory();

        cameraExecutor = Executors.newSingleThreadExecutor();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    preview = new Preview.Builder()
                            .build();

                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                    cameraProvider.unbindAll();

                    camera = cameraProvider.bindToLifecycle( CaneraActivity.this, cameraSelector, preview);

                    preview.setSurfaceProvider(viewFinder.createSurfaceProvider(camera.getCameraInfo()));

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Use case binding failed", e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Use case binding failed", e);
                }
            }
        },ContextCompat.getMainExecutor(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void takePhoto() {
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        File photoFile = new File(
                outputDirectory,
                new SimpleDateFormat(FILENAME_FORMAT, Locale.US
                ).format(System.currentTimeMillis()) + ".jpg");

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        ProcessCameraProvider cameraProvider = null;
        try {
            cameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle( CaneraActivity.this, cameraSelector, imageCapture);

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(outputOptions, getMainExecutor(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Toast.makeText(CaneraActivity.this, "Foto guardada!!", Toast.LENGTH_SHORT).show();
                Uri imageUri = Uri.fromFile(photoFile);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(IMAGE_URI,imageUri.toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
            }
        });
    }


    private boolean allPermissionsGranted() {
        if (ContextCompat.checkSelfPermission(getBaseContext(),REQUIRED_PERMISSIONS[0]  ) !=  PackageManager.PERMISSION_GRANTED)
            return false;

        if (ContextCompat.checkSelfPermission(getBaseContext(),REQUIRED_PERMISSIONS[1]  ) !=  PackageManager.PERMISSION_GRANTED)
            return false;

        if (ContextCompat.checkSelfPermission(getBaseContext(),REQUIRED_PERMISSIONS[2]  ) !=  PackageManager.PERMISSION_GRANTED)
            return false;

        return true;
    }

    public File getOutputDirectory() {
        File mediaDir = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES),"DemoPhotoShop");
        if (!mediaDir.exists()){
            mediaDir.mkdirs();
        }
        return (mediaDir != null && mediaDir.exists()) ? mediaDir : getFilesDir();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (allPermissionsGranted()){
                startCamera();
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
