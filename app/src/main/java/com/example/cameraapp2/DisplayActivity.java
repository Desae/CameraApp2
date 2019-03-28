package com.example.cameraapp2;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class DisplayActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        //receiving crop name
        TextView mCropName = findViewById(R.id.ncropname);
        Intent intent = getIntent();
        mCropName.setText(intent.getStringExtra("crop_name"));

        //receiving file name

        String filePath = intent.getStringExtra("file_path");
        //File imgFile = new  File(filePath);

        ImageView imageView = findViewById(R.id.image_view);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/karaAgro/"+filePath);
        /*if(file.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

        }*/

        //imageView.setImageURI(Uri.fromFile(file));

        Picasso.with(this).load(file).into(imageView);
    }


}
