package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.*


fun ubAsync() {
    CoroutineScope(Dispatchers.IO).launch {UB.webforub()}
    while (true) {
        while (UB.wsfail) {
            Thread.sleep(7000)
            UB.wsfail = false
            CoroutineScope(Dispatchers.IO).launch {UB.webforub()}
        }
        Thread.sleep(2500)
    }
}

fun zedAsync() {
    CoroutineScope(Dispatchers.IO).launch {
        Zed.webforZED()
        Zed.findOBJ()
    }
}


fun coroutine() {
    CoroutineScope(Dispatchers.IO).launch { ubAsync() }
    CoroutineScope(Dispatchers.IO).launch {
        Zed.webforZED()
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            zedAsync()
        }
    }
    var time = 0

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