package ir.itnsa.takepic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    File photoFile = null;
    String currentPhotoPath = "";
    Uri photoURI = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }
        else {
            Intent takepictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //startActivityForResult(takepictureIntent, CAPTURE_IMAGE_REQUEST);
            if (takepictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile();
                    Toast.makeText(this, photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(this,
                                "ir.itnsa.takepic.fileprovider",
                                photoFile);
                        takepictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takepictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                }catch (IOException ex){
                    Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Bundle exteras = data.getExtras();
        //Bitmap imageBitmap = (Bitmap) exteras.get("data");
        //imageView.setImageBitmap(imageBitmap);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK)
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            myBitmap = rotateImage(myBitmap,90);
            imageView.setImageBitmap(myBitmap);
        }else {
            Toast.makeText(this, "Request cancell or Wrong", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){

        if (requestCode == 0)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                captureImage();
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
