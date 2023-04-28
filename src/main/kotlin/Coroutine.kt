package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.*


fun ubAsync() {
    CoroutineScope(Dispatchers.IO).launch {UB.webForUB()}
    while (true) {
        while (UB.isWebsocketFailed) {
            Thread.sleep(7000)
            UB.isWebsocketFailed = false
            CoroutineScope(Dispatchers.IO).launch {UB.webForUB()}
        }
        Thread.sleep(2500)
    }
}

fun zedAsync() {
    CoroutineScope(Dispatchers.IO).launch {
        Zed.webForZED()
        Zed.findOBJ()
    }
}


fun coroutineOnEnable() {

    //定时刷新UB，ZED服务器数据
    CoroutineScope(Dispatchers.IO).launch { ubAsync() }
    CoroutineScope(Dispatchers.IO).launch {
        Zed.webForZED()
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            zedAsync()
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