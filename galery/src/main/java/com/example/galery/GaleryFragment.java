package com.example.galery;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GaleryFragment extends Fragment {
    private static final String IMAGE_URI = "imageUri";
    private ImageView imageView;
    private Uri uriImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_galery, container, false);
        // Uri de la imagen editada
        uriImage = Uri.parse(getArguments().getString(IMAGE_URI));
        confiViews(root);
        return root;
    }

    private void confiViews(View root) {
        // Instanciar vista
        imageView = root.findViewById(R.id.imageView);
        imageView.setImageURI(uriImage);
    }
}
