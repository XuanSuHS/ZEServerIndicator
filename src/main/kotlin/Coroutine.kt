package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.*


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
    Zed.webforZED()
    GlobalScope.launch {
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(10000)
            }
            Zed.webforZED()
            Zed.findOBJ()
        }
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun coroutine() {
    GlobalScope.launch { UBasync() }
    GlobalScope.launch { ZEDasync() }
}