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

    //Menu
    val menuPrevPage = translationValue("menu.prev_page", "#18dbd1Пред. страница")
    val menuNextPage = translationValue("menu.next_page", "#18dbd1След. страница")
    val menuTitle = translationValue("menu.title", "#18dbd1Магазины")
    val menuEdit = translationValue("menu.edit", "#18dbd1Редактировать: ПКМ")



    val buttonEditMode = translationValue("buttons.edit_mode", "#db2c18Режим редактирования")
    val buttonEditModeExit = translationValue("buttons.edit_mode_exit", "#db2c18Выйти: ЛКМ")
    val buttonBack = translationValue("buttons.back", "#db2c18Назад")
    val buttonInformation = translationValue("buttons.information", "#18dbd1Подробнее")

    val shopInfoStock = translationValue("buttons.information_stock", "#18dbd1Склад: {stock}")
    val shopInfoPrice = translationValue("buttons.information_price", "#18dbd1Цена: {price}")
    val shopInfoBalance = translationValue("buttons.information_balance", "#18dbd1Баланс: {balance}")


    val buttonBuy = translationValue("buttons.buy", "#18dbd1Купить")
    val buttonSell = translationValue("buttons.sell", "#db2c18Продать")
    val buttonBuyAmount = translationValue("buttons.buy_amount", "#18dbd1Купить x{amount}")
    val buttonSellAmount = translationValue("buttons.sell_amount", "#db2c18Продать x{amount}")
}


