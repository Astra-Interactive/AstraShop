package ru.astrainteractive.astrashop.api.calculator

import java.io.File
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.BlastingRecipe
import org.bukkit.inventory.CampfireRecipe
import org.bukkit.inventory.ComplexRecipe
import org.bukkit.inventory.CookingRecipe
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.inventory.SmithingRecipe
import org.bukkit.inventory.SmithingTransformRecipe
import org.bukkit.inventory.SmithingTrimRecipe
import org.bukkit.inventory.SmokingRecipe
import org.bukkit.inventory.StonecuttingRecipe
import ru.astrainteractive.astralibs.serialization.StringFormatExt.writeIntoFile
import ru.astrainteractive.astralibs.serialization.YamlStringFormat

object RecipesProvider {
    private val map = Material.entries.associate { it to PriceCalculator.getCraftsFor(it) }

    val materialWithRecipes = map.filter { it.value != null }
    val materialWithoutRecipes = map.filter { it.value == null }

    fun getPrice(material: Material): Double {
        val crafts = map[material]
        if (crafts.isNullOrEmpty()) return 1.0
        return crafts.minOf {
            it.ingredients.toList().sumOf {
                getPrice(it.first) * it.second.toFloat()
            } / it.amount.toFloat()
        }
    }
}

object PriceCalculator {

    fun getCraftsFor(material: Material): List<MapRecipeResult>? {
        val itemStack = kotlin.runCatching { ItemStack(material) }
            .onFailure { println(it.message) }
            .getOrNull()
            ?: return null
        return PriceCalculator.getCraftsFor(itemStack)
    }

    private fun toIngredient(itemStack: ItemStack) = Ingredient(
        amount = itemStack.amount,
        ingredient = itemStack.type
    )

    @Serializable
    data class MapRecipeResult(
        val amount: Int,
        val result: Material,
        val ingredients: Map<Material, Int>
    )

    fun toMapRecipeResult(rr: RecipeResult): MapRecipeResult {
        return MapRecipeResult(
            amount = rr.amount,
            result = rr.result,
            ingredients = let {
                val map = mutableMapOf<Material, Int>()
                rr.ingredients.forEach {
                    map[it.ingredient] = map.getOrDefault(it.ingredient, 0) + it.amount
                }
                map
            }
        )
    }

    @Serializable
    data class RecipeResult(
        val amount: Int,
        val result: Material,
        val ingredients: List<Ingredient>
    )

    @Serializable
    data class Ingredient(
        val amount: Int,
        val ingredient: Material
    )

    fun getCraftsFor(
        itemStack: ItemStack,
        isRecursive: Boolean = true
    ): List<MapRecipeResult>? {
        val recipes = Bukkit.getRecipesFor(itemStack)
        if (recipes.isEmpty()) return null
        return recipes.mapNotNull {
            when (it) {
                is BlastingRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.inputChoice.itemStack.let(::toIngredient).let(::listOf)
                    )
                }

                is CampfireRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.inputChoice.itemStack.let(::toIngredient).let(::listOf)
                    )
                }

                is FurnaceRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.inputChoice.itemStack.let(::toIngredient).let(::listOf)
                    )
                }

                is MerchantRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.ingredients.map(::toIngredient)
                    )

                }

                is SmithingTransformRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = listOf(
                            it.template.itemStack.let(::toIngredient),
                            it.addition.itemStack.let(::toIngredient)
                        )
                    )

                }

                is SmithingTrimRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = listOf(
                            it.template.itemStack.let(::toIngredient),
                            it.addition.itemStack.let(::toIngredient)
                        )
                    )
                }

                is SmithingRecipe -> {
                    println("Unknown type: $it for item: $itemStack")
                    null
                }

                is ComplexRecipe -> {
                    println("Unknown type: $it for item: $itemStack")
                    null
                }

                is SmokingRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.inputChoice.itemStack.let(::toIngredient).let(::listOf)
                    )
                }

                is StonecuttingRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.inputChoice.itemStack.let(::toIngredient).let(::listOf)
                    )
                }

                is ShapedRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.choiceMap.values
                            .filterNotNull()
                            .mapNotNull(RecipeChoice::getItemStack)
                            .map(::toIngredient)
                    )

                }

                is ShapelessRecipe -> {
                    if (isRecursive && checkHasCrafts(it.result)) return null
                    RecipeResult(
                        amount = it.result.amount,
                        result = it.result.type,
                        ingredients = it.choiceList
                            .filterNotNull()
                            .mapNotNull(RecipeChoice::getItemStack)
                            .map(::toIngredient)
                    )
                }

                is CookingRecipe<*> -> error("Unknown type: $it")
                is CraftingRecipe -> error("Unknown type: $it")
                else -> error("Forgot to add new type: $it")
            }
        }.map(::toMapRecipeResult)
    }

    private fun checkHasCrafts(itemStack: ItemStack): Boolean {
        return getCraftsFor(itemStack, isRecursive = false)
            .orEmpty()
            .asSequence()
            .flatMap { it.ingredients.map { it.key } }
            .distinct()
            .filter { it.isItem }
            .flatMap { getCraftsFor(ItemStack(it), isRecursive = false).orEmpty() }
            .flatMap { it.ingredients.map { it.key } }
            .contains(itemStack.type)
    }
}