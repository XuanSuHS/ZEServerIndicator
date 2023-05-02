package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.*
import net.mamoe.mirai.utils.info
import top.xuansu.mirai.zeServerIndicator.Indicator.logger


fun ubAsync() {
    CoroutineScope(Dispatchers.IO).launch { UB.webForUB() }
    while (true) {
        while (UB.isWebsocketFailed) {
            Thread.sleep(7000)
            UB.isWebsocketFailed = false
            CoroutineScope(Dispatchers.IO).launch { UB.webForUB() }
        }
        Thread.sleep(2500)
    }
}

fun coroutineOnEnable() {

    //定时刷新UB，ZED服务器数据
    CoroutineScope(Dispatchers.IO).launch { ubAsync() }
    CoroutineScope(Dispatchers.IO).launch {
        Zed.webForZED()
        logger.info { "ZED数据已初始化" }
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            CoroutineScope(Dispatchers.IO).launch {
                Zed.webForZED()
            }
        }
    }
    CoroutineScope(Dispatchers.IO).launch {
        CoroutineScope(Dispatchers.IO).launch {
            TopZE.initializeMapData()
            TopZE.webForTopZE()
            logger.info { "5e地图数据已加载" }
        }
        TopZE.webForTopZE()
        logger.info { "5e数据已初始化" }
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            CoroutineScope(Dispatchers.IO).launch {
                TopZE.webForTopZE()
            }
        }
    }
    var time = 0

    //定时触发Java GC
    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            if (time >= 20) {
                System.gc()
                time = 0
            }
            delay(60000)
            time += 1
        }
    }
}