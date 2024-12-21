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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class User extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("");

    public static ArrayList<Recipe> arrMyRecipes = new ArrayList<>();
    public static ArrayList<Recipe> arrPublish = Home.arrPublish;
    public static ArrayList<Recipe> filteredArrMyRecipes = new ArrayList<>();
    public static ArrayList<Recipe> arrRecipesFavourite = Home.arrRecipesFavourite;

    ListView lvMyRecipes;
    Spinner spMySort;
    TextView tvMy;
    Button btFault;
    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        lvMyRecipes = findViewById(R.id.lvMyRecipes);
        spMySort = findViewById(R.id.spMySort);
        tvMy = findViewById(R.id.tvMyRecipes);
        btFault = findViewById(R.id.btFault);

        getAllItemR(arrMyRecipes);


        String[] optionsSp = {"All types","Main dishes ","Starters ","Desserts"};
        //Inserting spinner options
        ArrayAdapter<String> adapterP = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsSp);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMySort.setAdapter(adapterP);

        spMySort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = (String) parentView.getItemAtPosition(position);
                filterByType(selectedType);
                refresh_lv(filteredArrMyRecipes);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        lvMyRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedItem = arrMyRecipes.get(position);

                Dialog dialog = new Dialog(User.this);
                dialog.setContentView(R.layout.mydialog);

                TextView tvTitleDialog = dialog.findViewById(R.id.tvTitleDialog);
                TextView tvIngredients = dialog.findViewById(R.id.tvIngredients);
                TextView tvrecipes = dialog.findViewById(R.id.tvrecipes);

                Button btExitDialog = dialog.findViewById(R.id.btExitDialog);
                Button btFvDialog = dialog.findViewById(R.id.btFvDialog);
                Button btSaveDialog = dialog.findViewById(R.id.btSave);
                Button btPublish = dialog.findViewById(R.id.btPublish);
                btSaveDialog.setVisibility(View.GONE);

                EditText etIngredientsInput = null;
                EditText etRecipeInput = null;

                tvTitleDialog.setText("recipe " + selectedItem.name);
                tvIngredients.setText("ingredients: " + selectedItem.ingredients);
                tvrecipes.setText("recipes: " + selectedItem.recipes);

                if (selectedItem.ingredients.isEmpty()) {
                    tvIngredients.setVisibility(View.GONE);
                    etIngredientsInput = new EditText(User.this);
                    etIngredientsInput.setHint("Add Ingredients");
                    dialog.addContentView(etIngredientsInput, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // Show the Save button if ingredients are missing
                    btSaveDialog.setVisibility(View.VISIBLE);
                }

                if (selectedItem.recipes.isEmpty()) {
                    tvrecipes.setVisibility(View.GONE);
                    etRecipeInput = new EditText(User.this);
                    etRecipeInput.setHint("Add Recipe");
                    dialog.addContentView(etRecipeInput, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // Show the Save button if recipe is missing
                    btSaveDialog.setVisibility(View.VISIBLE);
                }
                // בדיקה אם המתכון נמצא במסד נתונים של Firebase
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
                        Toast.makeText(User.this, "Error checking favorite status", Toast.LENGTH_SHORT).show();
                    }
                });
                btFvDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (flag) {
                            myRef.child("My Favorite").child(selectedItem.name).removeValue();
                            btFvDialog.setBackgroundResource(R.drawable.favorite);
                            flag = false;
                            Toast.makeText(User.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            myRef.child("My Favorite").child(selectedItem.name).setValue(selectedItem);
                            btFvDialog.setBackgroundResource(R.drawable.favorite_full);
                            flag = true;
                            Toast.makeText(User.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                EditText finalEtIngredientsInput = etIngredientsInput;
                EditText finalEtRecipeInput = etRecipeInput;
                btSaveDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Save the data entered by the user
                        if (finalEtIngredientsInput != null) {
                            selectedItem.ingredients = finalEtIngredientsInput.getText().toString();
                        }
                        if (finalEtRecipeInput != null) {
                            selectedItem.recipes = finalEtRecipeInput.getText().toString();
                        }

                        // Update the list view with the new data
                        refresh_lv(arrMyRecipes);

                        Toast.makeText(User.this, "Recipe updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                btPublish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean recipeExists = false;
                        for (Recipe recipe : arrPublish) {
                            if (recipe.name.equals(selectedItem.name)) {
                                recipeExists = true;
                                break;
                            }
                        }
                        if (!recipeExists) {
                            arrPublish.add(selectedItem);
                            Toast.makeText(User.this, "Will be published successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(User.this, "The recipe has already been published", Toast.LENGTH_SHORT).show();
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
        btFault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "My fault ";
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");

                sendIntent.putExtra("jid", "972506862163@s.whatsapp.net");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(User.this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.itm_home) {
            Intent intent = new Intent(User.this, Home.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.itm_add_recipe) {
            Intent intent = new Intent(User.this, AddedRecipe.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.item_Recipes_liked) {
            Intent intent = new Intent(User.this, RecipesLiked.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
////Sort by type
    private ArrayList<Recipe> filterByType(String selectedType) {
        //Clears the array
        filteredArrMyRecipes.clear();
        if ("All types".equals(selectedType)) {
            //Enter all the details for me
            filteredArrMyRecipes.addAll(arrMyRecipes);
        } else {
            for (Recipe recipe : arrMyRecipes) {
                if (recipe.type.equals(selectedType)) {
                    filteredArrMyRecipes.add(recipe);
                }
            }
        }
        return filteredArrMyRecipes;
    }
    private void getAllItemR(ArrayList<Recipe> arr) {
        Query q = myRef.child("My Recipes");
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
    private void getAllItemF(ArrayList<Recipe> arr) {
        Query q = myRef.child("My Favorite");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ArrayList<Recipe> arr = new ArrayList<>();
                arr.clear();
                for (DataSnapshot dsitem : snapshot.getChildren()) {
                    arr.add(dsitem.getValue(Recipe.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void refresh_lv(ArrayList<Recipe> arr) {
        MyArrAdpter adp = new MyArrAdpter(User.this, android.R.layout.simple_list_item_1, arr);
        lvMyRecipes.setAdapter(adp);
    }
}