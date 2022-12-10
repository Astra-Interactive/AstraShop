package ru.astrainteractive.astrashop.utils

import ru.astrainteractive.astralibs.file_manager.FileManager
import ru.astrainteractive.astralibs.utils.BaseTranslation

/**
 * All translation stored here
 */
class PluginTranslation : BaseTranslation() {
    /**
     * This is a default translation file. Don't forget to create translation.yml in resources of the plugin
     */
    protected override val translationFile: FileManager = FileManager("translations.yml")


    //General
    val reload = translationValue("general.reload", "#dbbb18Перезагрузка плагина")
    val reloadComplete = translationValue("general.reload_complete", "#42f596Перезагрузка успешно завершена")
    val noPermission = translationValue("general.no_permission", "#db2c18У вас нет прав!")

    val itemNotBuying = translationValue("general.item_not_for_buy", "#db2c18Предмет не закупается")
    //Menu
    val menuPrevPage = translationValue("menu.prev_page", "#18dbd1Пред. страница")
    val menuNextPage = translationValue("menu.next_page", "#18dbd1След. страница")
    val menuTitle = translationValue("menu.title", "#18dbd1Магазины")
    val quickSellTitle = translationValue("menu.quick_sell", "#18dbd1Быстрая продажа")
    val menuEdit = translationValue("menu.edit", "#18dbd1Редактировать: ПКМ")
    val menuDeleteItem = translationValue("menu.delete_item", "#18dbd1Удалить: Shift+ПКМ")


    val buttonEditModeEnabled = translationValue("buttons.edit_mode.enabled", "#db2c18Режим редактирования ВКЛ")
    val buttonEditModeDisabled = translationValue("buttons.edit_mode.disabled", "#42f596Режим редактирования ВЫКЛ")
    val buttonEditModeExit = translationValue("buttons.edit_mode.exit", "#db2c18Выйти: ЛКМ")
    val buttonEditModeEnter = translationValue("buttons.edit_mode.enter", "#42f596Войти: ЛКМ")

    val buttonBack = translationValue("buttons.back", "#db2c18Назад")
    val buttonInformation = translationValue("buttons.information", "#18dbd1Подробнее")

    private val shopInfoStock = translationValue("buttons.information_stock", "#18dbd1Склад: {stock}")
    fun shopInfoStock(stock: Int) = shopInfoStock.replace("{stock}", stock.toString())

    private val shopInfoPrice = translationValue("buttons.information_price", "#18dbd1Цена: {price}")
    fun shopInfoPrice(price: Number) = shopInfoPrice.replace("{price}", price.toString())

    private val shopInfoBalance = translationValue("buttons.information_balance", "#18dbd1Баланс: {balance}")
    fun shopInfoBalance(balance: Int) = shopInfoBalance.replace("{balance}", balance.toString())


    val buttonBuy = translationValue("buttons.buy", "#18dbd1Купить")
    val buttonSell = translationValue("buttons.sell", "#db2c18Продать")
    private val buttonBuyAmount = translationValue("buttons.buy_amount", "#18dbd1Купить x{amount}")
    fun buttonBuyAmount(amount: Int) = buttonBuyAmount.replace("{amount}", amount.toString())
    private val buttonSellAmount = translationValue("buttons.sell_amount", "#db2c18Продать x{amount}")
    fun buttonSellAmount(amount: Int) = buttonSellAmount.replace("{amount}", amount.toString())
}


