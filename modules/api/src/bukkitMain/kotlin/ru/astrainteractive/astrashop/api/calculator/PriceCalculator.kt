package ru.astrainteractive.astrashop.api.calculator

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

object PriceCalculator {
    sealed interface Prices {
        data class Multiple(val values: List<Double>) : Prices
        data class Single(val price: Double) : Prices
        companion object {
            fun List<Prices>.flatten() = buildList<Double> {
                this@flatten.forEach {
                    when (it) {
                        is Prices.Multiple -> addAll(it.values)
                        is Prices.Single -> add(it.price)
                    }
                }
            }.let(Prices::Multiple)
        }
    }

    fun getCraftsFor(material: Material): List<RecipeResult>? {
        val itemStack = kotlin.runCatching { ItemStack(material) }
            .onFailure { println(it.message) }
            .getOrNull()
            ?: return null
        return PriceCalculator.getCraftsFor(itemStack)
    }

    fun ItemStack.flatten() = List(this.amount) { this.type }

    @Serializable
    data class RecipeResult(
        val amount: Int,
        val recipes: List<Material>
    )

    fun getCraftsFor(itemStack: ItemStack): List<RecipeResult>? {
        val recipes = Bukkit.getRecipesFor(itemStack)
        if (recipes.isEmpty()) return null
        return recipes.mapNotNull {
            when (it) {
                is BlastingRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.inputChoice.itemStack.flatten()
                    )
                }

                is CampfireRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.inputChoice.itemStack.flatten()
                    )
                }

                is FurnaceRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.inputChoice.itemStack.flatten()
                    )
                }

                is MerchantRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.ingredients.flatMap { it.flatten() }
                    )

                }

                is SmithingTransformRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.template.itemStack.flatten() + it.addition.itemStack.flatten()
                    )

                }

                is SmithingTrimRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.template.itemStack.flatten() + it.addition.itemStack.flatten()
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
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.inputChoice.itemStack.flatten()
                    )
                }

                is StonecuttingRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.inputChoice.itemStack.flatten()
                    )
                }

                is ShapedRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.choiceMap.values
                            .filterNotNull()
                            .mapNotNull(RecipeChoice::getItemStack)
                            .flatMap { it.flatten() }
                    )

                }

                is ShapelessRecipe -> {
                    RecipeResult(
                        amount = it.result.amount,
                        recipes = it.choiceList
                            .filterNotNull()
                            .mapNotNull(RecipeChoice::getItemStack)
                            .flatMap { it.flatten() }
                    )
                }

                is CookingRecipe<*> -> error("Unknown type: $it")
                is CraftingRecipe -> error("Unknown type: $it")
                else -> error("Forgot to add new type: $it")
            }
        }
    }
}