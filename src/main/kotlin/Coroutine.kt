package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.*
import top.xuansu.mirai.zeServerIndicator.Indicator.logger


@OptIn(DelicateCoroutinesApi::class)
fun UBasync() {
    UB.webforub()
    GlobalScope.launch {
        while (true) {
            while (UB.wsfail) {
                withContext(Dispatchers.IO) {
                    Thread.sleep(7000)
                }
                UB.wsfail = false
                UB.webforub()
            }
            withContext(Dispatchers.IO) {
                Thread.sleep(2500)
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun ZEDasync() {
    GlobalScope.launch {
        Zed.webforZED()
        logger.info("Web For ZED Triggered")
        Zed.findOBJ()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun coroutine() {
    GlobalScope.launch { UBasync() }
    GlobalScope.launch {
        Zed.webforZED()
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            ZEDasync()
        }
    }
}