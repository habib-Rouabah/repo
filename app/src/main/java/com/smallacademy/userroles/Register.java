package com.smallacademy.userroles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName,email,password,phone;
    Button registerBtn,goToLogin;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CheckBox isTeacher,isStudent;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);
        isTeacher=findViewById(R.id.isTeacher);
        isStudent=findViewById(R.id.isStudent);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);




        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();

        checkField(fullName);
        checkField(email);
        checkField(password);
        checkField(phone);

registerBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        loadingDialog.show();
        if (true) {
            fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser user = fAuth.getCurrentUser();
                    Toast.makeText(Register.this, "Account created", Toast.LENGTH_SHORT).show();
                    DocumentReference df = fStore.collection("Users").document(user.getUid());
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("FullName", fullName.getText().toString());
                    userInfo.put("userEmail", email.getText().toString());
                    userInfo.put("PhoneNumber", phone.getText().toString());
                    if(isStudent.isChecked()){
                        userInfo.put("isUser", "1");
                        df.set(userInfo);
                        loadingDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    if(isTeacher.isChecked()){
                        userInfo.put("isAdmin", "1");
                        df.set(userInfo);
                        loadingDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),ModuleActivity.class));
                        finish();


                    }



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Register.this, "Failed to creat account", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(Register.this, "not valid", Toast.LENGTH_SHORT).show();
        }
    }
});
goToLogin.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(),Login.class));
    }
});


    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}