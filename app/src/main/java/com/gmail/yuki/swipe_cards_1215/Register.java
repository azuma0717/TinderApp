package com.gmail.yuki.swipe_cards_1215;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText ed1,ed2,ed3;
    Button bt1,bt2;
    RadioGroup rg1;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ed1 = findViewById(R.id.email);
        ed2 = findViewById(R.id.pass);
        ed3 = findViewById(R.id.name);
        bt1 = findViewById(R.id.register);
        bt2 = findViewById(R.id.login);
        rg1 = findViewById(R.id.radiogroup);

        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);



        mAuth = FirebaseAuth.getInstance();

        //ユーザーがレジスターされた後に、onStartで呼び出されてるっぽい。
        //レジスター後、currentUserが作成されるので、強制Intentされる。
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;


                }


            }
        };


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.register:

                registerUser();


                break;

            case R.id.login:

                Intent intent =  new Intent(getApplicationContext(),Login.class);
                startActivity(intent);

                break;



        }

    }

    ////User Register//
    private void registerUser() {

        //ラジオボタンでセレクトされているテキストをゲットする。
        int selectId = rg1.getCheckedRadioButtonId();
        final RadioButton radioButton = findViewById(selectId);

        final String email = ed1.getText().toString();
        final String pass = ed2.getText().toString();
        final String name = ed3.getText().toString();

        /////////////varidation check/////////////////
        if (email.isEmpty()) {
            ed1.setError("phone required");
            ed1.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            ed2.setError("pass required");
            ed2.requestFocus();
            return;
        }

        if (name.isEmpty()) {
            ed3.setError("pass required");
            ed3.requestFocus();
            return;
        }

        if(radioButton.getText() == null){
            return;
        }
        ///////////////////////////////////

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    String userId = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                    //Mapを使って、名前とプロファイルイメージのDBのコラムをアップデートする。アップデートの時はput.
                    Map userInfo = new HashMap<>();
                    userInfo.put("name",name);
                    userInfo.put("sex",radioButton.getText().toString());
                    userInfo.put("profileImageUrl","default");
                    currentUserDb.updateChildren(userInfo);

                    Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT).show();




                }else if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                    Toast.makeText(getApplicationContext(), "Already Registered...", Toast.LENGTH_SHORT).show();

                } else {

                    //それ以外の例外をここで検知して弾く。（アドレスとパスワードのルール）
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(firebaseAuthStateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
//////////////////////////



}
