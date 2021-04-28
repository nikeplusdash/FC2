package com.foodmgmt.dontstarve.onboarding;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.foodmgmt.dontstarve.DailyMenu;
import com.foodmgmt.dontstarve.MainActivity;
import com.foodmgmt.dontstarve.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class Verify extends Fragment {

    private String name, email, regno;
    Boolean isVerified = false, isOnboardingDone = false;

    private ImageButton camera_button,button;

    //private ImageView imageView;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int requestCode = 101;

    private StorageReference mStorage;

    private ProgressDialog mProgress;

    Bundle bundle = new Bundle();

    Context context = getActivity();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify, container, false);
        name = getArguments().getString("name");
        email = getArguments().getString("email");
        regno = getArguments().getString("regno");
        isVerified = false;
        isOnboardingDone = false;

        Intent myIntent = new Intent(getContext(), DailyMenu.class);

        myIntent.putExtra("name", name);
        myIntent.putExtra("email", email);
        myIntent.putExtra("regno", regno);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
        }

        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://don-t-starve-c1f9c.appspot.com");
        mProgress = new ProgressDialog(getContext());

        camera_button = v.findViewById(R.id.camera_button);
        button = v.findViewById(R.id.button);

        //imageView = v.findViewById(R.id.image);

        camera_button.setOnClickListener(view -> {

            dispatchTakePictureIntent(v);

        });

        button.setOnClickListener(view -> {

            skipVerification(v);

            if(isOnboardingDone)
            {
                myIntent.putExtra("verification", isVerified);
                myIntent.putExtra("onboarding", isOnboardingDone);

                getContext().startActivity(myIntent);

                getActivity().finish();
            }
        });

         /*
         * Open camera to take a picture
         * then send the input to a url to verify and move to verification screen
         * give option to take new image or wait to be approved by admin
         *
         * or
         *
         * We can do is approve and limit features as we decided before, thus no need for verification screen,
         * but needs to regularly fetch from a "Database/FS" and see if the user has been approved
         */

        return v;
    }


    public void dispatchTakePictureIntent(View v) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                return;

            } catch (ActivityNotFoundException e) {
                // display error state to the user
                Toast.makeText(getContext().getApplicationContext(), "Camera Launch Failed", Toast.LENGTH_SHORT).show();
            }
        }
        else
            skipVerification(v);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            mProgress.setMessage("Uploading Image...");
            mProgress.show();

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //imageView.setImageBitmap(imageBitmap);

            //Uri uri = data.getData();

            StorageReference filepath = mStorage.child("Photos").child(regno);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data2 = baos.toByteArray();

            UploadTask uploadTask = filepath.putBytes(data2);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(getContext(), "File upload to firebase failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                    isOnboardingDone =  true;
                    isVerified = true;

                    mProgress.dismiss();

                    Toast.makeText(getContext(), "Upload Successful", Toast.LENGTH_SHORT).show();

                    if(isOnboardingDone)
                    {
                        Intent myIntent = new Intent(getContext(), DailyMenu.class);

                        myIntent.putExtra("name", name);
                        myIntent.putExtra("email", email);
                        myIntent.putExtra("regno", regno);

                        myIntent.putExtra("verification", isVerified);
                        myIntent.putExtra("onboarding", isOnboardingDone);

                        getContext().startActivity(myIntent);

                        getActivity().finish();
                    }
                    
                }
            });

        }
    }

    public void skipVerification(View v){

        isOnboardingDone = true;

        Toast.makeText(getContext(), "You can use Verification tab to verify at a later date", Toast.LENGTH_LONG).show();
    }
}