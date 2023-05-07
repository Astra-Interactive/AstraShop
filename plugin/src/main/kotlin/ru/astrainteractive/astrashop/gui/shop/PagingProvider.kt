package ru.astrainteractive.astrashop.gui.shop

interface PagingProvider {
    val page: Int
    val maxItemsPerPage: Int
    fun index(i: Int) = i + maxItemsPerPage * page
    fun index(i: Int, page: Int) = i + maxItemsPerPage * page
}
