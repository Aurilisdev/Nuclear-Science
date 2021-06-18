package nuclearscience.common.recipe.categories.o2o.specificmachines;

import electrodynamics.common.recipe.categories.o2o.O2ORecipe;
import electrodynamics.common.recipe.recipeutils.CountableIngredient;
import nuclearscience.common.recipe.NuclearScienceRecipeInit;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FissionReactorRecipe extends O2ORecipe{

	public static final String RECIPE_GROUP = "fission_reactor_recipe";
	public static final String MOD_ID = nuclearscience.References.ID;
	public static final ResourceLocation RECIPE_ID = new ResourceLocation(MOD_ID,RECIPE_GROUP);
	
	public FissionReactorRecipe(ResourceLocation id, CountableIngredient input, ItemStack output) {
		super(id, input, output);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return NuclearScienceRecipeInit.FISSION_REACTOR_SERIALIZER.get();
	}

	@Override
	public IRecipeType<?> getType() {
		return Registry.RECIPE_TYPE.getOrDefault(RECIPE_ID);
	}

}
