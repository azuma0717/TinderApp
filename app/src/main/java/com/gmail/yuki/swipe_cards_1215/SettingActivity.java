package com.gmail.yuki.swipe_cards_1215;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField;
    private Button mBack, mConfirm;
    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabese;

    private String userId, name, phone, profileImageUrl,userSex;
    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mNameField = findViewById(R.id.name);
        mPhoneField = findViewById(R.id.phone);
        mBack = findViewById(R.id.back);
        mConfirm = findViewById(R.id.confirm);
        mProfileImage = findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mUserDatabese = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        //このページに飛んで来た時に、既にデータがDBに格納されていたら、デフォルトで表示しておく
        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

    }

    //このページに飛んで来た時に、既にデータがDBに格納されていたら、デフォルトで表示しておく
    private void getUserInfo() {

        //ログインしているユーザー情報を取得する
        mUserDatabese.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //ユーザーが存在しているかつ、その配下にユーザーデータが１個でもある場合
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    //ユーザーIDにぶらさがっている情報をMapを使って取得する。
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mNameField.setText(name);

                    }

                    if (map.get("sex") != null) {
                        userSex = map.get("sex").toString();


                    }

                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);

                    }


                    //画像読み込みのキャンセル。ここでは初期化みたいなイメージかな。
                    Glide.clear(mProfileImage);

                    //Glideを使って、FirebaseのストレージからダウンロードURLをゲットして、表示させる。

                    //"profileImageUrl"がnullじゃなかったら発動。基レジスターの段階で必ずインサートされるから通るはず。
                    if (map.get("profileImageUrl") != null) {
                        profileImageUrl = map.get("profileImageUrl").toString();

                        switch (profileImageUrl) {

                            case "default":

                                Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(mProfileImage);
                                break;

                            default:
                                Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                                break;

                        }


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {

        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();

        //ログインしているユーザー情報をアップデートする、アップデートするときはHashMapでいく。
        //アップデートは「put」
        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        mUserDatabese.updateChildren(userInfo);

        if (resultUri != null) {

            //ファイルのアップロード、書き方はほぼこれでOK///

            //ファイルパスの書き方は自由。今回は"profileImages"の配下にuserIdをつけてる
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            //bitmapの書き方では、try,catchが必要らしい。
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //firebaseのファイルアップロードではこれがベーシック
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //ダウンロードURLをゲットしたら、DBに格納する
                    Map userInfo = new HashMap();
                    userInfo.put("profileImageUrl", downloadUrl.toString());
                    mUserDatabese.updateChildren(userInfo);

                    finish();
                    return;

                }
            });

        } else {
            finish();
        }

    }

    //写真のフォルダにアクセスした時のリザルトを記載。Uriはここでゲットして格納する。さらにImageもここで表示させる。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);

        }
    }
}
