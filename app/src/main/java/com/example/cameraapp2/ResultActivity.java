package com.example.cameraapp2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class ResultActivity extends AppCompatActivity {
    Context context;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        TextView mCropName = findViewById(R.id.crop_name);
        mCropName.setText(intent.getStringExtra("crop_name"));

        TextView diseaseName = findViewById(R.id.disease_name_tag);
        diseaseName.setText(intent.getStringExtra("result"));

        TextView predConf = findViewById(R.id.pred_confidence);
        predConf.setText(intent.getStringExtra("confi"));

        // Setting crop name into Activity

        String filePath = intent.getStringExtra("file_path");
        //File imgFile = new  File(filePath);

        ImageView imageView = findViewById(R.id.crop_img);
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/karaAgro/" + filePath);

        if(file.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }


    }


    public void redoTest(View view) {
        context = view.getContext();
        Intent intent = new Intent(context, MainActivity.class);
        //intent.putExtra("crop_name", cropName.getText().toString());
        //intent.putExtra("file_path", file.getName());
        context.startActivity(intent);
    }
}
