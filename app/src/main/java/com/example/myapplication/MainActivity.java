package com.example.myapplication;



import static androidx.core.content.PermissionChecker.checkSelfPermission;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int PICK_GPX_FILE_REQUEST = 1;
    public String globalPath = "";
    Button btn;

    EditText input;

    TextView label;

    TextView label2;
    Handler handler;

    Button menuButton;

    Button button2;

    public static String rslts_tobeshown;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button2 = (Button) findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,MainActivity3.class);
                startActivity(intent);

            }
        });
        menuButton = (Button) findViewById(R.id.menuButton);

        menuButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                openFilePicker();
            }
        });
        Log.d("myTag", "BUTTON ");


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("r","granted already");
        } else {

            int PERMISSION_EXTERNAL_STORAGE = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_EXTERNAL_STORAGE);
            Log.d("r","granted");
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("r","granted permission");
        }

        int rslt = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d("lol",String.valueOf(rslt));
        Log.d("lol",String.valueOf(PackageManager.PERMISSION_GRANTED));

        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                String result = message.getData().getString("result");

                label.setText(result);

                return true;
            }
        });

        label = (TextView) findViewById(R.id.label);
        label.setText("Activity Tracker");
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Set the MIME type to select all file types
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_GPX_FILE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GPX_FILE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();

                File f = new File(uri.getPath());

                Log.e("actual_path",f.getPath());

                String filename = f.getName();

                ContextWrapper c = new ContextWrapper(this);

                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                String FileDirectory =  c.getFilesDir().getAbsolutePath()+"/"+filename;


                Log.e("path",FileDirectory);
                Log.e("reached","here");
                MyThread t1 = new MyThread(FileDirectory, handler,label,label2,getApplicationContext());
                t1.start();

            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void buttonOpenFile(View view){
        Log.d("myTag", "MenuButton ");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,42);
    }
}