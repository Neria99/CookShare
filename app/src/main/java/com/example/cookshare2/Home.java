package com.example.cookshare2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Home extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("");

    public static ArrayList<Recipe> arrRecipes = new ArrayList<>();
    public static ArrayList<Recipe> arrPublish = new ArrayList<>();
    public static ArrayList<Recipe> arrRecipesFavourite = new ArrayList<>();
    public static ArrayList<Recipe> filteredArrRecipes = new ArrayList<>();
    SearchView svRecipesActivity;
    ListView lvRecipes;
    Spinner spRecipes;
    String search;
    TextView tv ;
    boolean flag = false;
    Random random = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        lvRecipes = findViewById(R.id.lvRecipes);
        spRecipes = findViewById(R.id.spRecipes);
        svRecipesActivity = findViewById(R.id.svRecipesActivity);
        tv = findViewById(R.id.tvRecipes);
        transferPublishedRecipes();

        svRecipesActivity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                arrRecipes.clear();
                getRecipeSearch(search);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        String[] optionsSp = {"All types","Main dishes","Desserts", "Starters","Side","Vegetarian"};
        //Inserting spinner options
        ArrayAdapter<String> adapterP = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsSp);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRecipes.setAdapter(adapterP);

        spRecipes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = (String) parentView.getItemAtPosition(position);
                filterByType(selectedType);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        lvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedItem = arrRecipes.get(position);

                Dialog dialog = new Dialog(Home.this);
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
                tvrecipes.setMovementMethod(new ScrollingMovementMethod());

                EditText etIngredientsInput = null;
                EditText etRecipeInput = null;

                // Hide the Save button by default
                btSaveDialog.setVisibility(View.GONE);
                if (selectedItem.ingredients.isEmpty()) {
                    tvIngredients.setVisibility(View.GONE);
                    etIngredientsInput = new EditText(Home.this);
                    etIngredientsInput.setHint("Add Ingredients");
                    dialog.addContentView(etIngredientsInput, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    // Show the Save button if ingredients are missing
                    btSaveDialog.setVisibility(View.VISIBLE);
                }

                if (selectedItem.recipes.isEmpty()) {
                    tvrecipes.setVisibility(View.GONE);
                    etRecipeInput = new EditText(Home.this);
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
                        Toast.makeText(Home.this, "Error checking favorite status", Toast.LENGTH_SHORT).show();
                    }
                });

                btFvDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (flag) {
                            myRef.child("My Favorite").child(selectedItem.name).removeValue();
                            btFvDialog.setBackgroundResource(R.drawable.favorite);
                            flag = false;
                            Toast.makeText(Home.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            myRef.child("My Favorite").child(selectedItem.name).setValue(selectedItem);
                            btFvDialog.setBackgroundResource(R.drawable.favorite_full);
                            flag = true;
                            Toast.makeText(Home.this, "Added to favorites", Toast.LENGTH_SHORT).show();
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
                        refresh_lv(arrRecipes);

                        Toast.makeText(Home.this, "Recipe updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_user) {
            Intent intent = new Intent(Home.this, User.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.item_Recipes_liked) {
            Intent intent = new Intent(Home.this, RecipesLiked.class);
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //Sort by type
    private void filterByType(String selectedType) {
        //Clears the array
        filteredArrRecipes.clear();
        if ("All types".equals(selectedType)) {
            arrRecipes.clear();
           getAllRecipe();
        } else if("Main dishes".equals(selectedType)) {
            arrRecipes.clear();
            getRecipeMainDishes();
        }else if("Desserts".equals(selectedType)) {
            arrRecipes.clear();
            getRecipeDesserts();
        }else if("Starters".equals(selectedType)) {
            arrRecipes.clear();
            getRecipeStarters();
        }else if("Side".equals(selectedType)) {
            arrRecipes.clear();
            getRecipeSide();
        }else if("Vegetarian".equals(selectedType)) {
            arrRecipes.clear();
            getRecipeSide();
        }
    }
    private void refresh_lv(ArrayList<Recipe> arr) {
        MyArrAdpter adp = new MyArrAdpter(Home.this, android.R.layout.simple_list_item_1, arr);
        lvRecipes.setAdapter(adp);
    }
    private void transferPublishedRecipes() {
        for (Recipe recipe : arrPublish) {
                arrRecipes.add(recipe);
        }
        refresh_lv(arrRecipes);
    }

    private void getRecipeSearch(String search) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // code to run in background thread
                String prefix = "https://www.themealdb.com/api/json/v1/1/search.php?s=";
                ArrayList<Recipe> searchResults = new ArrayList<>();
                try {

                    String line, newjson = "";
                    URL urls = new URL(prefix + search);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream(), "UTF-8"))) {
                        while ((line = reader.readLine()) != null) {
                            newjson += line;
                        }

                        String json = newjson.toString();
                        JSONObject jObj = new JSONObject(json);
                        int numberofrecipes = 0;
                        JSONArray recipesjson = jObj.getJSONArray("meals");

                        //בדיקה כמה מתכונים יש...
                        for (int i = 0; i < recipesjson.length(); i++) {
                            JSONObject recipeobject = recipesjson.getJSONObject(i);
                            if (recipeobject.has("idMeal"))
                                numberofrecipes++;
                            }

                        for (int i = 0; i < numberofrecipes; i++) {
                            JSONObject recipeobject = recipesjson.getJSONObject(i);
                            Recipe recipe = new Recipe();
                            recipe.name = recipeobject.get("strMeal").toString();
                            recipe.pic = recipeobject.get("strMealThumb").toString();
                            recipe.difficulty = 6 + random.nextInt(4);;
                            recipe.ingredients = "";
                            recipe.recipes = recipeobject.get("strInstructions").toString();

                            arrRecipes.add(recipe);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // בדיקת מתכונים במערך arrPublish והוספתם לתוצאות החיפוש
                for (Recipe recipe : arrPublish) {
                    if (recipe.name.toLowerCase().contains(search.toLowerCase())) {
                        if (!searchResults.contains(recipe)) {
                            searchResults.add(recipe);
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arrRecipes.addAll(searchResults);
                        refresh_lv(arrRecipes);
                    }
                });
            }
        });
        thread.start();
    }
    private void getAllRecipe() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // code to run in background thread
                String prefix = "https://www.themealdb.com/api/json/v1/1/search.php?f=";
                String alphabet = "abcdefghijklmnopqrstuvwxyz";
                try {
                    for (int j = 0; j < alphabet.length(); j++) {
                        char searchChar = alphabet.charAt(j);
                        String search = String.valueOf(searchChar);

                        String line, newjson = "";
                        URL urls = new URL(prefix + search);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream(), "UTF-8"))) {
                            while ((line = reader.readLine()) != null) {
                                newjson += line;
                            }

                            String json = newjson.toString();
                            JSONObject jObj = new JSONObject(json);
                            int numberofrecipes = 0;
                            JSONArray recipesjson = jObj.getJSONArray("meals");

                            //בדיקה כמה מתכונים יש...
                            for (int i = 0; i < recipesjson.length(); i++) {
                                JSONObject recipeobject = recipesjson.getJSONObject(i);
                                if (recipeobject.has("idMeal"))
                                    numberofrecipes++;
                            }

                            for (int i = 0; i < numberofrecipes; i++) {
                                JSONObject recipeobject = recipesjson.getJSONObject(i);
                                Recipe recipe = new Recipe();
                                recipe.name = recipeobject.get("strMeal").toString();
                                recipe.pic = recipeobject.get("strMealThumb").toString();
                                recipe.difficulty = 6 + random.nextInt(4);;
                                recipe.ingredients = "";
                                recipe.recipes = recipeobject.get("strInstructions").toString();

                                arrRecipes.add(recipe);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_lv(arrRecipes);
                    }
                });
            }
        });
        thread.start();
    }
    private void getRecipeMainDishes() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // code to run in background thread
                String arrMain[] = {"beef", "chicken", "lamb"};
                String prefix = "https://www.themealdb.com/api/json/v1/1/filter.php?c=";

                try {
                    for (String search : arrMain) {
                        String line, newjson = "";
                        URL urls = new URL(prefix + search);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream(), "UTF-8"))) {
                            while ((line = reader.readLine()) != null) {
                                newjson += line;
                            }

                            String json = newjson;
                            JSONObject jObj = new JSONObject(json);
                            JSONArray recipesjson = jObj.getJSONArray("meals");

                            for (int i = 0; i < recipesjson.length(); i++) {
                                JSONObject recipeobject = recipesjson.getJSONObject(i);
                                Recipe recipe = new Recipe();
                                recipe.name = recipeobject.get("strMeal").toString();
                                recipe.pic = recipeobject.get("strMealThumb").toString();
                                recipe.difficulty = 6 + random.nextInt(4);;
                                recipe.ingredients = "";
                                recipe.recipes = "";

                                arrRecipes.add(recipe);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_lv(arrRecipes);
                    }
                });
            }
        });
        thread.start();
    }
    private void getRecipeDesserts() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // code to run in background thread
                String arrMain[] = {"Dessert"};
                String prefix = "https://www.themealdb.com/api/json/v1/1/filter.php?c=";

                try {
                    for (String search : arrMain) {
                        String line, newjson = "";
                        URL urls = new URL(prefix + search);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream(), "UTF-8"))) {
                            while ((line = reader.readLine()) != null) {
                                newjson += line;
                            }

                            String json = newjson;
                            JSONObject jObj = new JSONObject(json);
                            JSONArray recipesjson = jObj.getJSONArray("meals");

                            for (int i = 0; i < recipesjson.length(); i++) {
                                JSONObject recipeobject = recipesjson.getJSONObject(i);
                                Recipe recipe = new Recipe();
                                recipe.name = recipeobject.get("strMeal").toString();
                                recipe.pic = recipeobject.get("strMealThumb").toString();
                                recipe.difficulty = 6 + random.nextInt(4);;
                                recipe.ingredients = "";
                                recipe.recipes = "";

                                arrRecipes.add(recipe);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_lv(arrRecipes);
                    }
                });
            }
        });
        thread.start();
    }
    private void getRecipeStarters() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // code to run in background thread
                String arrMain[] = {"Starter", "Pasta"};
                String prefix = "https://www.themealdb.com/api/json/v1/1/filter.php?c=";

                try {
                    for (String search : arrMain) {
                        String line, newjson = "";
                        URL urls = new URL(prefix + search);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream(), "UTF-8"))) {
                            while ((line = reader.readLine()) != null) {
                                newjson += line;
                            }

                            String json = newjson;
                            JSONObject jObj = new JSONObject(json);
                            JSONArray recipesjson = jObj.getJSONArray("meals");

                            for (int i = 0; i < recipesjson.length(); i++) {
                                JSONObject recipeobject = recipesjson.getJSONObject(i);
                                Recipe recipe = new Recipe();
                                recipe.name = recipeobject.get("strMeal").toString();
                                recipe.pic = recipeobject.get("strMealThumb").toString();
                                recipe.difficulty = 6 + random.nextInt(4);;
                                recipe.ingredients = "";
                                recipe.recipes = "";
                                arrRecipes.add(recipe);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_lv(arrRecipes);
                    }
                });
            }
        });
        thread.start();
    }
    private void getRecipeSide() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // code to run in background thread
                String arrMain[] = {"Side", "Pasta"};
                String prefix = "https://www.themealdb.com/api/json/v1/1/filter.php?c=";

                try {
                    for (String search : arrMain) {
                        String line, newjson = "";
                        URL urls = new URL(prefix + search);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream(), "UTF-8"))) {
                            while ((line = reader.readLine()) != null) {
                                newjson += line;
                            }

                            String json = newjson;
                            JSONObject jObj = new JSONObject(json);
                            JSONArray recipesjson = jObj.getJSONArray("meals");

                            for (int i = 0; i < recipesjson.length(); i++) {
                                JSONObject recipeobject = recipesjson.getJSONObject(i);
                                Recipe recipe = new Recipe();
                                recipe.name = recipeobject.get("strMeal").toString();
                                recipe.pic = recipeobject.get("strMealThumb").toString();
                                recipe.difficulty =6 + random.nextInt(4);;
                                recipe.ingredients = "";
                                recipe.recipes = "";

                                arrRecipes.add(recipe);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_lv(arrRecipes);
                    }
                });
            }
        });
        thread.start();
    }
    private void getRecipeVegetarian() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // code to run in background thread
                String arrMain[] = {"Vegetarian"};
                String prefix = "https://www.themealdb.com/api/json/v1/1/filter.php?c=";

                try {
                    for (String search : arrMain) {
                        String line, newjson = "";
                        URL urls = new URL(prefix + search);
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.openStream(), "UTF-8"))) {
                            while ((line = reader.readLine()) != null) {
                                newjson += line;
                            }

                            String json = newjson;
                            JSONObject jObj = new JSONObject(json);
                            JSONArray recipesjson = jObj.getJSONArray("meals");

                            for (int i = 0; i < recipesjson.length(); i++) {
                                JSONObject recipeobject = recipesjson.getJSONObject(i);
                                Recipe recipe = new Recipe();
                                recipe.name = recipeobject.get("strMeal").toString();
                                recipe.pic = recipeobject.get("strMealThumb").toString();
                                recipe.difficulty = 6 + random.nextInt(4);;
                                recipe.ingredients = "";
                                recipe.recipes = "";
                                arrRecipes.add(recipe);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refresh_lv(arrRecipes);
                    }
                });
            }
        });
        thread.start();
    }
}