package com.mobileapp.wyd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobileapp.wyd.model.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends BaseActivity {

    Uri fileUri;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageView imgBack=findViewById(R.id.img_back);
        EditText etUsername=findViewById(R.id.et_username);
        EditText etDesc=findViewById(R.id.et_details);
        EditText etEmail=findViewById(R.id.et_email);
        TextView tvSave=findViewById(R.id.button_save);
        userImage=findViewById(R.id.user_img);
        userImage.setClipToOutline(true);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ImagePicker.Companion.with(ProfileActivity.this).cropSquare().compress(200).start();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name=etUsername.getText().toString().trim();
                String desc=etDesc.getText().toString().trim();
                String email=etEmail.getText().toString().trim();
                String phone=getIntent().getStringExtra("number");
                User user=new User();
                user.setName(name);
                user.setDescription(desc);
                user.setEmail(email);
                user.setPhone(phone);
                if(name.isEmpty()||desc.isEmpty()||email.isEmpty())
                {
                    showToast("Input field missing");
                }
                else if(fileUri!=null)
                {
                    uploadData(user);
                }
                else
                {
                    showLoading();
                    updateFirebaseData(user);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

    }

    public void uploadData(User user)
    {
        showLoading();
        String id= FirebaseAuth.getInstance().getUid();
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("users" + "/" +id);
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();

        try {
            byte[] thumb_byte_data;
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
            Bitmap thumbBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            thumb_byte_data = baos.toByteArray();
            UploadTask uploadTask = mStorageRef.putBytes(thumb_byte_data, metadata);
            final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        hideLoading();
                        throw task.getException();
                    }

                    return mStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        user.setImage(task.getResult().toString());
                        updateFirebaseData(user);
                    }
                    else
                    {
                        hideLoading();
                        Toast.makeText(getBaseContext(), "Failed to upload details.! Try again" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();//error saving photo
                    }
                }
            });

        } catch (Exception e)
        {
            hideLoading();
        }
    }

    public void updateFirebaseData(User user)
    {
        String id=FirebaseAuth.getInstance().getUid();
        user.setId(id);
        DocumentReference mUserRef= FirebaseFirestore.getInstance().collection("users").document(id);
        mUserRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused)
            {
                hideLoading();
                Toast.makeText(ProfileActivity.this, "Profile settings updated", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
        {
            fileUri = data.getData();
            Picasso.get().load(fileUri).into(userImage);
        }
    }

}