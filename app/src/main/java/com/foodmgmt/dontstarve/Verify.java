package com.foodmgmt.dontstarve;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.Navigation;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.foodmgmt.dontstarve.MainMenu;
import com.foodmgmt.dontstarve.R;
import com.foodmgmt.dontstarve.onboarding.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;

public class Verify extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int requestCode = 101;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private String name, email, regno;
    private Thread thread;
    private int cameraId, rotation;
    private static int failedAttempts;
    private boolean isVerified = false;
    private ImageButton camera_button, button;
    private ProgressDialog mProgress;
    private StorageReference mStorage, filepath;
    private DatabaseReference mDatabase;
    private View v;
    private byte[] imgData;
    private Bundle rebundle;
    private Context ctx;

    static {
        failedAttempts = 0;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_DontStarve);
        setContentView(R.layout.activity_verify);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        rebundle = new Bundle();
        ctx = this;

        camera_button = findViewById(R.id.camera_button);
        button = findViewById(R.id.button);

        Bundle b = getIntent().getExtras();
        name = b.getString("name");
        email = b.getString("email");
        regno = b.getString("regno");
        isVerified = false;

        rebundle.putString("name", name);
        rebundle.putString("regno", regno);
        rebundle.putString("email", email);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
        }

        camera_button.setOnClickListener(view -> dispatchTakePictureIntent(v));

        button.setOnClickListener(view -> {
            Toast.makeText(this, "You can use Verification tab to verify at a later date", Toast.LENGTH_LONG).show();
            thread.start();
            endOnboarding();
        });

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Toast.LENGTH_SHORT); // As I am using LENGTH_LONG in Toast
                    thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            mProgress.setMessage("Verifying Image...");
            mProgress.show();
            filepath = mStorage.child("Photos").child(regno);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imgData = baos.toByteArray();
            performVerification(imageBitmap);
        }
    }

    private void endOnboarding() {
        SharedPreferences sp = getSharedPreferences("dontstarve", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("regno", regno);
        editor.putBoolean("verification", isVerified);
        editor.commit();

        Users u = new Users(email, regno, name, isVerified);
        mDatabase.child("users").child(regno).setValue(u);

        finish();
    }
    private void performVerification(Bitmap imageCapture) {
        InputImage image = InputImage.fromBitmap(imageCapture, rotation);
        TextRecognizer recognizer = TextRecognition.getClient();
        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text result) {
                        for (Text.TextBlock block : result.getTextBlocks()) {
                            for (Text.Line line : block.getLines()) {
                                for (Text.Element element : line.getElements()) {
                                    String elementText = element.getText();
                                    System.out.println(elementText+" --- " + regno);
                                    if (elementText.contains(regno)) {
                                        isVerified = true;
                                        mProgress.dismiss();
                                        Toast.makeText(getApplicationContext(), "You have been verified", Toast.LENGTH_SHORT).show();
                                        thread.start();
                                        endOnboarding();
                                    }
                                }
                            }
                        }
                        if (!isVerified) {
                            if (failedAttempts < 3)
                                new AlertDialog.Builder(ctx)
                                        .setTitle("Verification Failed")
                                        .setMessage("There was an issue. Try again Later")
                                        .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgress.dismiss();
                                                failedAttempts += 1;
                                            }
                                        })
                                        .setNegativeButton("Upload", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgress.dismiss();
                                                performUpload(filepath, imgData);
                                            }
                                        }).show();
                            else {
                                mProgress.dismiss();
                                performUpload(filepath, imgData);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.dismiss();
                        performUpload(filepath, imgData);
                    }
                });
    }

    private void performUpload(StorageReference filepath, byte[] imageData) {
        mProgress.setMessage("Uploading Image...");
        mProgress.show();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/png")
                .setCustomMetadata("Name", name)
                .setCustomMetadata("RegNo", regno)
                .setCustomMetadata("Email", email)
                .build();
        UploadTask uploadTask = filepath.putBytes(imageData, metadata);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                new AlertDialog.Builder(ctx)
                        .setTitle("Uploading Failed")
                        .setMessage("There was an issue. Try again Later")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProgress.dismiss();
                                finish();
                            }
                        }).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), "You will be verified soon", Toast.LENGTH_SHORT).show();
                thread.start();
                endOnboarding();
            }
        });
    }

    public void dispatchTakePictureIntent(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                int numberOfCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numberOfCameras; i++) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, info);
                    if (info.facing != Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        cameraId = i;
                        rotation = getRotationCompensation(cameraId + "", this, false);
                        break;
                    }
                }
            } catch (ActivityNotFoundException | CameraAccessException e) {
                Toast.makeText(this, "Camera Launch Failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            new AlertDialog.Builder(ctx)
                    .setTitle("Allow access to Camera")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
                            }
                        }
                    }).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, boolean isFrontFacing)
            throws CameraAccessException {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int rotationCompensation = ORIENTATIONS.get(deviceRotation);

        // Get the device's sensor orientation.
        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
        int sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION);

        if (isFrontFacing) {
            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
        } else { // back-facing
            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
        }
        return rotationCompensation;
    }

}