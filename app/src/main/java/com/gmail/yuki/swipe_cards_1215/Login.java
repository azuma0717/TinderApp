package com.gmail.yuki.swipe_cards_1215;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText ed1,ed2;
    Button bt1,bt2;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed1 = findViewById(R.id.email);
        ed2 = findViewById(R.id.pass);
        bt1 = findViewById(R.id.login);
        bt2 = findViewById(R.id.back);

        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

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

            case R.id.login:

                userLogin();
                break;

            case R.id.back:
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);


        }

    }

    ////User login//
    private void userLogin() {

        final String email = ed1.getText().toString();
        final String pass = ed2.getText().toString();

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
        ///////////////////////////////////

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getApplicationContext(),"Welcome to Swipe Card App", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);

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

}
