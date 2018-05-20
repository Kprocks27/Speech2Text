
package com.example.kp.speechtotext;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public TextView txtv;
    public EditText textView;
    public Button save;
    public Button reset;
    ArrayList<String> result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtv = findViewById(R.id.textView5);
        textView=findViewById(R.id.editText);
        save=findViewById(R.id.button2);
        reset=findViewById(R.id.button3);

        }
    public void OnClick(View View) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 10);


    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked=item.getItemId();
        if(itemThatWasClicked==R.id.action_path)
        {
            Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/pdf/");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");

            if (intent.resolveActivityInfo(getPackageManager(), 0) != null)
            {
                startActivity(intent);
            }
            else
            {
                // if you reach this place, it means there is no any file
                // explorer app installed on your device
                Toast.makeText(this, "No Supported app found", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                     result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtv.setText(result.get(0));




                }
                break;
        }

    }
    public void OnSave(View view)
    {
        textView.setVisibility(TextView.VISIBLE);
        save.setVisibility(Button.VISIBLE);
    }
    public void OnReset(View view)
    {
        textView.setVisibility(TextView.INVISIBLE);
        save.setVisibility(Button.INVISIBLE);
        txtv.setText(null);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void stringToPdf(View view) {

        String path= textView.getText().toString();
        String extStorageDir = Environment.getExternalStorageDirectory().toString();

        File folder=new File(extStorageDir,"pdf");
        if(!folder.exists()) {
            boolean bool = folder.mkdirs();
            Log.d("msg11",String.valueOf(bool));
        }
        try {
            final File file = new File(folder.getAbsolutePath(),path+".pdf");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);


            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(100, 100, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            String data = result.get(0);


            TextPaint mTextPaint=new TextPaint();
            StaticLayout mTextLayout = new StaticLayout(data, mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 0.6f, 0.0f, false);
               mTextPaint.setTextSize(10);
            canvas.save();
// calculate x and y position where your text will be placed

           int textX = 7;
           int textY = 4;

            canvas.translate(textX, textY);
            mTextLayout.draw(canvas);
            canvas.restore();

            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

        }catch (IOException e){
            Toast.makeText(this, "Something wrong: " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    }

