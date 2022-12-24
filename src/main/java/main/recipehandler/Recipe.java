package main.recipehandler;

import main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 커스텀 레시피 클래스
 * @author Barity_
 */
public class Recipe {
    /*
    레시피 저장 형식:
 *   - key
 *   ├ key:
 *   │ └ (레시피 키)
 *   ├ name:
 *   │ └ (레시피 이름)
 *   ├ result:
 *   │ └ (결과 아이템)
 *   ├ ingredients:
 *   └ └ (재료 아이템들)
     */
    public static FileConfiguration recipeData;
    public static final File recipe = new File("GameData/recipeData.yml");

    private String key;
    private String name;
    private ItemStack result;
    private List<ItemStack> ingredients;

    public Recipe(String key, String name, ItemStack result, List<ItemStack> ingredients) {
        setKey(key);
        setIngrediants(ingredients);
        setResult(result);
        setName(name);
        List<String> keys = recipeData.getStringList("Recipe.recipeKeys");
        if (keys.contains(key)) throw new IllegalStateException("해당 키의 레시피가 이미 존재합니다");
        keys.add(key);
        recipeData.set("Recipes.recipeKeys", keys);
        saveData();
    }

    public void remove() {
        recipeData.set("Recipes." + this.key, null);
        saveData();
    }

    public void setIngrediants(@NotNull List<ItemStack> ingredients) {
        this.ingredients = ingredients;
        List<String> itemToAdd = new ArrayList<>();
        for (ItemStack i : ingredients) {
            itemToAdd.add(Main.itemToString(i));
        } recipeData.set("Recipes." + this.key + ".ingredients", itemToAdd);
        saveData();
    }

    public boolean equals(@NotNull List<ItemStack> items) {
        return items.equals(this.ingredients);
    }

    public void setResult(ItemStack result) {
        this.result = result;
        recipeData.set("Recipes." + this.key + ".result", Main.itemToString(result));
        saveData();
    }

    public void setName(String name) {
        this.name = name;
        recipeData.set("Recipes." + this.key + ".name", name);
        saveData();
    }

    public void setKey(String key) {
        this.key = key;
        recipeData.set("Recipes." + this.key + ".key", key);
        saveData();
    }

    public List<ItemStack> getIngredients() {
        return this.ingredients;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public String getName() {
        return this.name;
    }

    public static void loadData() {
        recipeData = YamlConfiguration.loadConfiguration(recipe);
        try {
            if (!recipe.exists()) {
                recipeData.save(recipe);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveData() {
        try {
            recipeData.save(recipe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static @Nullable Recipe getRecipe(String key) {
        String resultString = recipeData.getString("Recipes." + key + ".result");
        String recipeKey = recipeData.getString("Recipes." + key + ".key");
        if (recipeKey == null || resultString == null) return null;
        String name = recipeData.getString("Recipes." + key + ".name");
        ItemStack result = Main.stringToItem(resultString);
        List<String> ingredientsString =  recipeData.getStringList("Recipes." + key + ".ingredients");
        List<ItemStack> ingredients = new ArrayList<>();
        for (String s : ingredientsString) {
            ingredients.add(Main.stringToItem(s));
        } return new Recipe(recipeKey, name, result, ingredients);
    }

}
