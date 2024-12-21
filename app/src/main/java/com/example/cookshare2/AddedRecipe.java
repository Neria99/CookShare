package com.example.cookshare2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddedRecipe extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("");
    EditText edName;
    EditText edType;
    EditText eddifficulty;
    EditText edIngredients;
    EditText edRecipe;
    EditText edPic;
    Button btAddR;
    Button btEXit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_recipe);
        getSupportActionBar().hide();
        edName = findViewById(R.id.edName);
        edType = findViewById(R.id.edType);
        eddifficulty = findViewById(R.id.eddifficulty);
        edIngredients = findViewById(R.id.edIngredients);
        edRecipe = findViewById(R.id.edRecipe);
        edPic = findViewById(R.id.edPic);
        btAddR = findViewById(R.id.btAddR);
        btEXit = findViewById(R.id.btBack);

        btAddR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recipe newRecipe = new Recipe();
                newRecipe.name = edName.getText().toString();
                newRecipe.type = edType.getText().toString();
                newRecipe.difficulty = Integer.parseInt(eddifficulty.getText().toString());
                newRecipe.ingredients = edIngredients.getText().toString();
                newRecipe.recipes = edRecipe.getText().toString();
                newRecipe.pic = edPic.getText().toString();

                myRef.child("My Recipes").child(newRecipe.name).setValue(newRecipe);
                Toast.makeText(AddedRecipe.this, newRecipe.name + "Will be addesd successfully", Toast.LENGTH_SHORT).show();
                edName.setText("Enter Name recipe:");
                edType.setText("Enter type recipe:");
                eddifficulty.setText("Enter Difficulty:");
                edIngredients.setText("Enter Ingredients:");
                edRecipe.setText("Enter Recipe:");
                edPic.setText("link");
            }
        });
        btEXit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddedRecipe.this, User.class);
                startActivity(intent);
            }
        });
    }
}