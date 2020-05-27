package com.example.demophotoshop;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.example.camera.CaneraActivity;
import com.example.galery.GaleryFragment;
import com.example.image.ImageActivity;

public class MainActivity extends AppCompatActivity {

    //Id para la solicitud recibida en onActivityResult
    private static final int IMAGE_FROM_CAMERA = 666;
    private static final int IMAGE_SELECT_FROM_GALERY = 555;
    private static final String IMAGE_URI = "imageUri";

    private ImageView imageView;
    private Uri imageUri ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
    }

    public void openCmera(View v){
        startActivityForResult(new Intent(this, CaneraActivity.class),IMAGE_FROM_CAMERA);
    }

    public void openGalery(View v){
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), IMAGE_SELECT_FROM_GALERY);
    }

    public void openEditActivity(View v){
        Bundle parmetros = new Bundle();
        parmetros.putString(ImageActivity.IMAGE_URI, imageUri.toString());
        Intent i = new Intent(this, ImageActivity.class);
        i.putExtras(parmetros);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == Activity.RESULT_OK){
            if (requestCode == IMAGE_FROM_CAMERA){
                imageUri = Uri.parse(data.getStringExtra(CaneraActivity.IMAGE_URI));
                imageView.setImageURI(imageUri);
            } else if (requestCode == IMAGE_SELECT_FROM_GALERY){
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
         }
    }

}
