package com.example.cookshare2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipesLiked extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("");
    public static ArrayList<Recipe> arrRecipesFavourite = Home.arrRecipesFavourite;
    public static ArrayList<Recipe> filteredArrRecipesFv = new ArrayList<>();
    ListView lvFv;
    Spinner spFv;
    boolean flag =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_liked);
        lvFv = findViewById(R.id.lvRecipesFv);
        spFv = findViewById(R.id.spSortFv);
        getAllItem(arrRecipesFavourite);
        String[] optionsSp = {"All types", "Main dishes", "Starters", "Desserts"};

        ArrayAdapter<String> adapterP = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsSp);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFv.setAdapter(adapterP);

        spFv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = (String) parentView.getItemAtPosition(position);
                filterByType(selectedType);
                refresh_lv(filteredArrRecipesFv);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        lvFv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedItem = arrRecipesFavourite.get(position);

                Dialog dialog = new Dialog(RecipesLiked.this);
                dialog.setContentView(R.layout.mydialog);

                TextView tvTitleDialog = dialog.findViewById(R.id.tvTitleDialog);
                TextView tvIngredients = dialog.findViewById(R.id.tvIngredients);
                TextView tvrecipes = dialog.findViewById(R.id.tvrecipes);

                Button btExitDialog = dialog.findViewById(R.id.btExitDialog);
                Button btFvDialog = dialog.findViewById(R.id.btFvDialog);
                Button btSaveDialog = dialog.findViewById(R.id.btSave);
                Button btPublish = dialog.findViewById(R.id.btPublish);
                btSaveDialog.setVisibility(View.GONE);
                btPublish.setVisibility(View.GONE);

                tvTitleDialog.setText("recipe " + selectedItem.name);
                tvIngredients.setText("ingredients: " + selectedItem.ingredients);
                tvrecipes.setText("recipes: " + selectedItem.recipes);


                myRef.child("My Favorite").child(selectedItem.name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            flag = true;
                            btFvDialog.setBackgroundResource(R.drawable.favorite_full); // Change button image
                        } else {
                            flag = false;
                            btFvDialog.setBackgroundResource(R.drawable.favorite); // Default button image
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RecipesLiked.this, "Error checking favorite status", Toast.LENGTH_SHORT).show();
                    }
                });
                btFvDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (flag) {
                            myRef.child("My Favorite").child(selectedItem.name).removeValue();
                            btFvDialog.setBackgroundResource(R.drawable.favorite);
                            flag = false;
                            Toast.makeText(RecipesLiked.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            myRef.child("My Favorite").child(selectedItem.name).setValue(selectedItem);
                            btFvDialog.setBackgroundResource(R.drawable.favorite_full);
                            flag = true;
                            Toast.makeText(RecipesLiked.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                btExitDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_like, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itm_home) {
            Intent intent = new Intent(RecipesLiked.this, Home.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.item_user) {
            Intent intent = new Intent(RecipesLiked.this, User.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    private void getAllItem(ArrayList<Recipe> arr) {
        Query q = myRef.child("My Favorite");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ArrayList<Recipe> arr = new ArrayList<>();
                arr.clear();
                for (DataSnapshot dsitem : snapshot.getChildren()) {
                    arr.add(dsitem.getValue(Recipe.class));
                }
                refresh_lv(arr);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private ArrayList<Recipe> filterByType(String selectedType) {
        // מנקה את המערך
        filteredArrRecipesFv.clear();
        if ("All types".equals(selectedType)) {
            // מוסיף את כל המתכונים
            filteredArrRecipesFv.addAll(arrRecipesFavourite);
        } else {
            for (Recipe recipe : arrRecipesFavourite) {
                if (recipe.type.equals(selectedType)) {
                    filteredArrRecipesFv.add(recipe);
                }
            }
        }
        return filteredArrRecipesFv;
    }

    private void refresh_lv(ArrayList<Recipe> arr) {
        MyArrAdpter adp = new MyArrAdpter(RecipesLiked.this, android.R.layout.simple_list_item_1, arr);
        lvFv.setAdapter(adp);
    }
}
