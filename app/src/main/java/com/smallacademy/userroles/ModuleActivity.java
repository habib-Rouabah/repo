package com.smallacademy.userroles;



import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ModuleActivity extends AppCompatActivity {

    private RecyclerView cat_recycler_view;
    private Button addCatB;
    public static ArrayList<ModuleModel> catList = new ArrayList<>();
    public static int selected_cat_index = 0;

    private FirebaseFirestore firestore;
    private Dialog loadingDialog, addCatDialog;
    private EditText dialogCatName;
    private Button dialogAddB;
    private ModulesAdapter adapter;
    ModulesAdapter myadapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);





            Toolbar tool = findViewById(R.id.toolbar);
            setActionBar(tool);
            getSupportActionBar().setTitle("Modules");


            cat_recycler_view = (RecyclerView) findViewById(R.id.cat_recycler);

            addCatB = findViewById(R.id.addCatB);


            loadingDialog = new Dialog(ModuleActivity.this);
            loadingDialog.setContentView(R.layout.loading_progressbar);
            loadingDialog.setCancelable(false);
            loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            addCatDialog = new Dialog(ModuleActivity.this);
            addCatDialog.setContentView(R.layout.add_module_dialog);
            addCatDialog.setCancelable(true);
            addCatDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            dialogCatName = addCatDialog.findViewById(R.id.ac_cat_name);
            dialogAddB = addCatDialog.findViewById(R.id.ac_add_btn);
            // firebaseAuth = FirebaseAuth.getInstance();
            firestore = FirebaseFirestore.getInstance();


            //  List<String> catList =new ArrayList<>();
            //   catList.add("MOD 1");
            // catList.add("MOD 2");
            // catList.add("MOD 3");
            // catList.add("MOD 4");


            //firestore = FirebaseFirestore.getInstance();

            addCatB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogCatName.getText().clear();
                    addCatDialog.show();
                }
            });

            dialogAddB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogCatName.getText().toString().isEmpty()) {
                        dialogCatName.setError("Enter Module Name");
                        return;
                    }

                    addNewModules(dialogCatName.getText().toString());
                }
            });


            // LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            cat_recycler_view.setLayoutManager(new LinearLayoutManager(this));

adapter=new ModulesAdapter(catList);
cat_recycler_view.setAdapter(adapter);
// Write a message to the database
            // FirebaseDatabase database = FirebaseDatabase.getInstance();
            // DatabaseReference myRef = database.getReference("message");

            //  myRef.setValue("Hello, World!");

            loadData();


        }

        private void loadData ()
        {
            loadingDialog.show();

            catList.clear();

            firestore.collection("QUIZ").document("QlTeDpaoCo5GXgt3DdQf")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();


                        if (doc.exists()) {
                            int count =Integer.parseInt( doc.get("COUNTER").toString() );

                            for (int i = 1; i <= count; i++) {
                                //String catName = doc.getString("MOD" + String.valueOf(i) + "_NAME");
                                //String catid = doc.getString("MOD" + String.valueOf(i) + "_ID");
                                String catName = doc.getString("NAME");
                                String catid = doc.getId();

                                catList.add(new ModuleModel(catid, catName, "0", "1"));

                            }

                            adapter = new ModulesAdapter(catList);
                            cat_recycler_view.setAdapter(adapter);

                        } else {
                            Toast.makeText(ModuleActivity.this, "No Module Document Exists!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } else {

                        Toast.makeText(ModuleActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    loadingDialog.dismiss();
                }
            });

        }

        private void addNewModules (String title)
        {
            addCatDialog.dismiss();
            loadingDialog.show();

            final Map<String, Object> catData = new ArrayMap<>();
            catData.put("NAME", title);
            catData.put("SETS", 0);
            catData.put("COUNTER", "1");

            final String doc_id = firestore.collection("QUIZ").document().getId();

            firestore.collection("QUIZ").document(doc_id)
                    .set(catData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String, Object> catDoc = new ArrayMap<>();
                            catDoc.put("MOD" + String.valueOf(catList.size() + 1) + "_NAME", title);
                            catDoc.put("MOD" + String.valueOf(catList.size() + 1) + "_ID", doc_id);
                            catDoc.put("COUNT", catList.size() + 1);

                            firestore.collection("QUIZ").document().update(catDoc)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(ModuleActivity.this, "Module added successfully", Toast.LENGTH_SHORT).show();

                                            catList.add(new ModuleModel(doc_id, title, "0", "1"));

                                            adapter.notifyItemInserted(catList.size());

                                            loadingDialog.dismiss();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ModuleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();
                                        }
                                    });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ModuleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });


        }

    }

