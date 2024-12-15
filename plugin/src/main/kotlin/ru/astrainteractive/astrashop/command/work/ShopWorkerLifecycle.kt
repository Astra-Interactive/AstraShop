package ru.astrainteractive.astrashop.command.work

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import ru.astrainteractive.astralibs.async.CoroutineFeature
import ru.astrainteractive.astralibs.lifecycle.Lifecycle
import ru.astrainteractive.astralibs.logging.JUtiltLogger
import ru.astrainteractive.astralibs.logging.Logger
import ru.astrainteractive.astrashop.api.ShopApi
import ru.astrainteractive.astrashop.core.Worker

class ShopWorkerLifecycle(
    private val shopApi: ShopApi
) : Lifecycle, Logger by JUtiltLogger("AstraShop-ShopWorker") {
    private val scope: CoroutineScope = CoroutineFeature.Default(Dispatchers.IO)

    private val workers = mutableListOf<Worker>()

    override fun onEnable() {
        scope.launch {
            shopApi.fetchShopList().forEach { shopConfig ->
                if (shopConfig.options.stabilizing.enabled) {
                    StabilizingWorker(
                        shopApi = shopApi,
                        configName = shopConfig.configName,
                        options = shopConfig.options.stabilizing,
                        scope = scope
                    ).run(workers::add)
                }
                if (shopConfig.options.fluctuations.enabled) {
                    FluctuationsWorker(
                        shopApi = shopApi,
                        configName = shopConfig.configName,
                        options = shopConfig.options.fluctuations,
                        scope = scope
                    ).run(workers::add)
                }
            }
            workers.forEach(Worker::start)
        }
    }

    override fun onDisable() {
        workers.forEach(Worker::stop)
        workers.clear()
        scope.coroutineContext.job.cancelChildren()
    }

    override fun onReload() {
        onDisable()
        onEnable()
    }
}
