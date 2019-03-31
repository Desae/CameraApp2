package com.example.cameraapp2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;

public class DisplayActivity extends AppCompatActivity {
    private TextView mCropName;
    private ImageView imageView;
    String filePath;
    Context context;
    public static final String EXTRA_ABSOLUTE_FILE_PATH = "extra_absolute_path";
    private static final String TAG = "Camera2Activity";


    //Load the tensorflow inference library//////
    static {
        System.loadLibrary("tensorflow_inference");
    }

    //PATH TO OUR MODEL FILE AND NAMES OF THE INPUT AND OUTPUT NODES
    private String MODEL_PATH = "file:///android_asset/squeezenet.pb";
    private String INPUT_NAME = "input_1";
    private String OUTPUT_NAME = "output_1";
    private TensorFlowInferenceInterface tf;

    //ARRAY TO HOLD THE PREDICTIONS AND FLOAT VALUES TO HOLD THE IMAGE DATA
    float[] PREDICTIONS = new float[1000];
    private float[] floatValues;
    private int[] INPUT_SIZE = {224,224,3};
    Button diagnoseBtn;
    //String result, confi;

    //TextView resultView;
    Snackbar progressBar;
    File file;
    String crop_name;
    String result, confi;


    ////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display2);

        //receiving crop name
        TextView mCropName = findViewById(R.id.ncropname);
        Intent intent = getIntent();
        crop_name = intent.getStringExtra("crop_name");
        mCropName.setText(crop_name);


        //receiving file name

        String filePath = intent.getStringExtra("file_path");
        //File imgFile = new  File(filePath);

        imageView = findViewById(R.id.image_view);
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/karaAgro/" + filePath);
        if(file.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

        }

        boolean exists = file.exists();

        Toast.makeText(this, "File exists: " + String.valueOf(exists), Toast.LENGTH_SHORT).show();
        //imageView.setImageURI(Uri.fromFile(file));

        if (!exists) {
            //file = new File(intent.getStringExtra(EXTRA_ABSOLUTE_FILE_PATH));
            Picasso.get().load(file).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DisplayActivity.this, "Image loaded successfully",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            });

        }


        diagnoseBtn = findViewById(R.id.diagnose_btn);
        diagnoseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{


                    progressBar.show();


                    //InputStream imageStream = getAssets().open("cherry.jpg");

                   // Bitmap bitmap = BitmapFactory.decodeStream(imageStream);


                    //READ THE IMAGE FROM FILE SYSTEM FOLDER
                    //imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));

                    Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    //imageView.setImageBitmap(myBitmap);

                    predict(myBitmap);

                }
                catch (Exception e){

                }
            }
        });


        progressBar = Snackbar.make(imageView,"PROCESSING IMAGE",Snackbar.LENGTH_INDEFINITE);
    }


    //FUNCTION TO COMPUTE THE MAXIMUM PREDICTION AND ITS CONFIDENCE
    public Object[] argmax(float[] array){
        int best = -1;
        float best_confidence = 0.0f;

        for(int i = 0;i < array.length;i++){

            float value = array[i];

            if (value > best_confidence){

                best_confidence = value;
                best = i;
            }
        }
        return new Object[]{best,best_confidence};
    }

    @SuppressLint("StaticFieldLeak")
    public void predict(final Bitmap bitmap){


        //Runs inference in background thread
        new AsyncTask<Integer,Integer,Integer>(){

            @Override

            protected Integer doInBackground(Integer ...params){

                //Resize the image into 224 x 224
                Bitmap resized_image = ImageUtils.processBitmap(bitmap,224);

                //Normalize the pixels
                floatValues = ImageUtils.normalizeBitmap(resized_image,224,127.5f,1.0f);

                //Pass input into the tensorflow
                tf.feed(INPUT_NAME,floatValues,1,224,224,3);

                //compute predictions
                tf.run(new String[]{OUTPUT_NAME});

                //copy the output into the PREDICTIONS array
                tf.fetch(OUTPUT_NAME,PREDICTIONS);

                //Obtained highest prediction
                Object[] results = argmax(PREDICTIONS);


                int class_index = (Integer) results[0];
                float confidence = (Float) results[1];


                try{

                    final String conf = String.valueOf(confidence * 100).substring(0,5);

                    //Convert predicted class index into actual label name
                    final String label = ImageUtils.getLabel(getAssets().open("labels.json"),class_index);



                    //Display result on UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.dismiss();
                            result = label;
                            confi = conf;

                            displayResult(result,confi);

                        }
                    });

                }

                catch (Exception e){


                }


                return 0;
            }



        }.execute(0);

    }

    private void displayResult(String result, String confi){
        Intent intent = new Intent(this,ResultActivity.class);
        intent.putExtra("crop_name", crop_name);
        intent.putExtra("file_path", file.getName());
        intent.putExtra(DisplayActivity.EXTRA_ABSOLUTE_FILE_PATH, file.getPath());
        intent.putExtra("result",result);
        intent.putExtra("confi",confi);
        startActivity(intent);

    }

    public void openCaptureActivity(View view) {
        finish();
    }
}
