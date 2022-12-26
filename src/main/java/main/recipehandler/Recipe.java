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

    private final String key;
    private String name;
    private ItemStack result;
    private List<ItemStack> ingredients;

    /**
     * 레시피 인스턴스,
     * 이미 존재하는 레시피의 키를 사용할 경우 해당 레시피의 구조물을 변경함
     * @param key 레시피 키 (시스템에 등록될 이름)
     * @param name 레시피 이름 (구별용 이름)
     * @param result 결과물 (아이템)
     * @param ingredients 재료 아이템들 (반드시 9개여야 함, 없더라도 AIR 사용)
     * @throws IllegalArgumentException 재료 아이템이 9개가 아닐 경우
     */
    public Recipe(String key, String name, ItemStack result, List<ItemStack> ingredients) {
        this.key = key;
        setIngrediants(ingredients);
        setResult(result);
        setName(name);
        if (ingredients.size() != 9) throw new IllegalArgumentException("재료 아이템은 9개여야 합니다");
        List<String> keys = recipeData.getStringList("Recipes.recipeKeys");
        if (!keys.contains(key)) {
            keys.add(key);
            recipeData.set("Recipes." + key + ".key", key);
            recipeData.set("Recipes.recipeKeys", keys);
            saveData();
        }
    }

    /**
     * 레시피를 제거하는 메소드. 해당 레시피를 파일에서 제거함
     */
    public void remove() {
        recipeData.set("Recipes." + this.key, null);
        List<String> keys = recipeData.getStringList("Recipes.recipeKeys");
        keys.remove(this.key);
        recipeData.set("Recipes.recipeKeys", keys);
        saveData();
    }

    /**
     * 레시피의 재료를 설정함
     * @param ingredients 새로 설정할 재료
     * @throws IllegalArgumentException ingredients의 원소가 9개가 아닐 경우
     */
    public void setIngrediants(@NotNull List<ItemStack> ingredients) {
        if (ingredients.size() != 9) throw new IllegalArgumentException("재료 아이템은 9개여야 합니다");
        this.ingredients = ingredients;
        List<String> itemToAdd = new ArrayList<>();
        for (ItemStack i : ingredients) {
            itemToAdd.add(Main.itemToString(i));
        } recipeData.set("Recipes." + this.key + ".ingredients", itemToAdd);
        saveData();
    }

    /**
     * 결과 아이템을 설정함
     * @param result 설정할 결과 아이템
     */
    public void setResult(ItemStack result) {
        this.result = result;
        recipeData.set("Recipes." + this.key + ".result", Main.itemToString(result));
        saveData();
    }

    /**
     * 레시피의 이름을 설정함
     * @param name 설정할 이름
     */
    public void setName(String name) {
        this.name = name;
        recipeData.set("Recipes." + this.key + ".name", name);
        saveData();
    }

    /**
     * 레시피의 재료를 알아냄
     * @return 레시피의 재료 아이템 (9개)
     */
    public List<ItemStack> getIngredients() {
        return this.ingredients;
    }

    /**
     * 레시피의 결과물을 알아냄
     * @return 레시피의 결과물 아이템
     */
    public ItemStack getResult() {
        return this.result;
    }

    /**
     * 레시피의 아름을 알아냄
     * @return 레시피의 이름
     */
    public String getName() {
        return this.name;
    }

    /**
     * 레시피 파일을 로드함
     */
    public static void loadData() {
        recipeData = YamlConfiguration.loadConfiguration(recipe);
        try {
            if (!recipe.exists()) {
                recipeData.save(recipe);
            }
        } catch (IOException e) {
            Main.printException(e);
        }
    }

    /**
     * 레시피의 변경 사항을 파일에 저장함
     */
    public static void saveData() {
        try {
            recipeData.save(recipe);
        } catch (IOException e) {
            Main.printException(e);
        }
    }

    /**
     * 키에 맞는 레시피를 반환함
     * @param key 레시피의 키
     * @return key의 키를 가진 레시피 반환, 없을 경우 null 반환
     */
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
