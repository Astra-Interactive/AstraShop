package ru.astrainteractive.astrashop.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.astrainteractive.astralibs.string.StringDesc
import ru.astrainteractive.astralibs.string.replace
import ru.astrainteractive.astrashop.util.RoundExt.round

/**
 * All translation stored here
 */
@Serializable
data class PluginTranslation(
    val general: General = General(),
    val menu: Menu = Menu(),
    val buttons: Buttons = Buttons(),
    val shop: Shop = Shop()

) {
    @Serializable
    data class Shop(
        @SerialName("item_not_purchasing")
        val itemNotForPurchase: StringDesc.Raw = StringDesc.Raw("Предмет не продается"),
        @SerialName("item_not_selling")
        val itemNotForSelling: StringDesc.Raw = StringDesc.Raw("Предмет не закупается"),
        @SerialName("shop_not_enough_items")
        val notEnoughItems: StringDesc.Raw = StringDesc.Raw("В магазине недостаточно предметов"),
        @SerialName("player_not_enough_money")
        val notEnoughMoney: StringDesc.Raw = StringDesc.Raw("Недостаточно денег"),
        @SerialName("you_spent_amount")
        private val youSpentAmount: StringDesc.Raw = StringDesc.Raw("Вы потратили {AMOUNT}"),
        @SerialName("you_earned_amount")
        private val youEarnedAmount: StringDesc.Raw = StringDesc.Raw("Вы получили {AMOUNT}"),
        @SerialName("not_fitted")
        val notFitted: StringDesc.Raw = StringDesc.Raw("Некоторые предметы не вместились. Они лежат на полу"),
        @SerialName("player_not_have_item")
        val playerNotHaveItem: StringDesc.Raw = StringDesc.Raw("У вас нет такого предмета"),
        @SerialName("infinite_purchase")
        val infinitePurchase: StringDesc.Raw = StringDesc.Raw("&6Бесконечная покупка")
    ) {
        fun youSpentAmount(amount: Number) = youSpentAmount.replace("{AMOUNT}", amount.round().toString())
        fun youEarnedAmount(amount: Number) = youEarnedAmount.replace("{AMOUNT}", amount.round().toString())
    }

    @Serializable
    data class General(
        @SerialName("reload")
        val reload: StringDesc.Raw = StringDesc.Raw("&#dbbb18Перезагрузка плагина"),
        @SerialName("reload_complete")
        val reloadComplete: StringDesc.Raw = StringDesc.Raw("&#42f596Перезагрузка успешно завершена"),
        @SerialName("no_permission")
        val noPermission: StringDesc.Raw = StringDesc.Raw("&#db2c18У вас нет прав!"),
        @SerialName("not_player")
        val notPlayer: StringDesc.Raw = StringDesc.Raw("&#db2c18Вы не игрок!"),
        @SerialName("wrong_usage")
        val wrongUsage: StringDesc.Raw = StringDesc.Raw("&#db2c18Неверное использование"),
        @SerialName("item_not_for_buy")
        val itemNotBuying: StringDesc.Raw = StringDesc.Raw("&#db2c18Предмет не закупается"),
    )

    @Serializable
    data class Menu(
        @SerialName("prev_page")
        val menuPrevPage: StringDesc.Raw = StringDesc.Raw("&#18dbd1Пред. страница"),
        @SerialName("next_page")
        val menuNextPage: StringDesc.Raw = StringDesc.Raw("&#18dbd1След. страница"),
        @SerialName("title")
        val menuTitle: StringDesc.Raw = StringDesc.Raw("&#18dbd1Магазины"),
        @SerialName("quick_sell")
        val quickSellTitle: StringDesc.Raw = StringDesc.Raw("&#18dbd1Быстрая продажа"),
        @SerialName("edit")
        val menuEdit: StringDesc.Raw = StringDesc.Raw("&#18dbd1Редактировать: ПКМ"),
        @SerialName("delete_item")
        val menuDeleteItem: StringDesc.Raw = StringDesc.Raw("&#18dbd1Удалить: Shift+ПКМ"),
    )

    @Serializable
    data class Buttons(
        @SerialName("edit_mode.enabled")
        val buttonEditModeEnabled: StringDesc.Raw = StringDesc.Raw("&#db2c18Режим редактирования ВКЛ"),
        @SerialName("edit_mode.disabled")
        val buttonEditModeDisabled: StringDesc.Raw = StringDesc.Raw("&#42f596Режим редактирования ВЫКЛ"),
        @SerialName("edit_mode.exit")
        val buttonEditModeExit: StringDesc.Raw = StringDesc.Raw("&#db2c18Выйти: ЛКМ"),
        @SerialName("edit_mode.enter")
        val buttonEditModeEnter: StringDesc.Raw = StringDesc.Raw("&#42f596Войти: ЛКМ"),
        @SerialName("back")
        val buttonBack: StringDesc.Raw = StringDesc.Raw("&#db2c18Назад"),
        @SerialName("information")
        val buttonInformation: StringDesc.Raw = StringDesc.Raw("&#18dbd1Подробнее"),
        @SerialName("information_stock")
        private val shopInfoStock: StringDesc.Raw = StringDesc.Raw("&#18dbd1Склад: {stock}"),
        @SerialName("information_price")
        private val shopInfoPrice: StringDesc.Raw = StringDesc.Raw("&#18dbd1Покупка: {price}"),
        @SerialName("information_price_sell")
        private val shopInfoSellPrice: StringDesc.Raw = StringDesc.Raw("&#18dbd1Продажа: {price}"),
        @SerialName("information_balance")
        private val shopInfoBalance: StringDesc.Raw = StringDesc.Raw("&#18dbd1Баланс: {balance}"),
        @SerialName("buy")
        val buttonBuy: StringDesc.Raw = StringDesc.Raw("&#18dbd1Купить"),
        @SerialName("sell")
        val buttonSell: StringDesc.Raw = StringDesc.Raw("&#db2c18Продать"),
        @SerialName("buy_amount")
        private val buttonBuyAmount: StringDesc.Raw = StringDesc.Raw("&#18dbd1Купить x{amount}"),
        @SerialName("sell_amount")
        private val buttonSellAmount: StringDesc.Raw = StringDesc.Raw("&#db2c18Продать x{amount}"),
    ) {
        fun shopInfoStock(stock: Int) = shopInfoStock.replace("{stock}", if (stock == -1) "∞" else stock.toString())

        fun shopInfoBuyPrice(price: Number) = shopInfoPrice.replace(
            "{price}",
            if (price.toDouble() <= 0) "-" else price.round().toString()
        )

        fun shopInfoSellPrice(price: Number) = shopInfoSellPrice.replace(
            "{price}",
            if (price.toDouble() <= 0) "-" else price.round().toString()
        )

        fun shopInfoBalance(balance: Int) = shopInfoBalance.replace("{balance}", balance.toString())

        fun buttonBuyAmount(amount: Int) = buttonBuyAmount.replace("{amount}", amount.round().toString())
        fun buttonSellAmount(amount: Int) = buttonSellAmount.replace("{amount}", amount.round().toString())
    }
}
