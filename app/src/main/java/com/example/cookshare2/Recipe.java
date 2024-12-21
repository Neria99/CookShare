package com.example.cookshare2;
public class Recipe {

        public String name;
        public String type;
        public int difficulty;
        public String ingredients;
        public String recipes;

        public String pic;

        public Recipe(String name,String type, int difficulty, String ingredients, String recipes,String pic){
            this.name = name;
            this.type = type;
            this.difficulty = difficulty;
            this.ingredients = ingredients;
            this.recipes = recipes;
            this.pic = pic;
        }

        public Recipe() {
            this.name = "";
            this.type = "";
            this.difficulty = 0;
            this.ingredients = "";
            this.recipes = "";
            this.pic = "";
        }

        @Override
        public String toString() {
            return "Recipe: " +
                    "Name='" + name + '\''+
                    "Type='" + type + '\''+
                    ", Difficulty='" + difficulty + '\'' +
                    ",Ingredients ='" + ingredients + '\'' +
                    ", Recipes='" + recipes + '\''
                    ;
        }

        public void setName(String name) {
            this.name = name;
        }
        public void setType(String type) {
        this.type = type;
    }

        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }

        public void setIngredients(String ingredients) {
            this.ingredients = ingredients;
        }

        public void setRecipes(String recipes) {
            this.recipes = recipes;
        }
        public void setPic(String pic) {this.pic = pic;}

        public String getName() {
            return name;
        }
        public String getType() {
        return type;
    }

        public int getDifficulty() {return difficulty;}

        public String getIngredients() {return ingredients;}

        public String getRecipes() {return recipes;}

        public String getPic() {return pic;}
}

