package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.*


@OptIn(DelicateCoroutinesApi::class)
fun ubAsync() {
    GlobalScope.launch {UB.webforub()}
    while (true) {
        while (UB.wsfail) {
            Thread.sleep(7000)
            UB.wsfail = false
            GlobalScope.launch {
                UB.webforub()
            }
        }
        Thread.sleep(2500)
    }

}

@OptIn(DelicateCoroutinesApi::class)
fun zedAsync() {
    GlobalScope.launch {
        Zed.webforZED()
        Zed.findOBJ()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun coroutine() {
    GlobalScope.launch { ubAsync() }
    GlobalScope.launch {
        Zed.webforZED()
        while (true) {
            withContext(Dispatchers.IO) {
                Thread.sleep(15000)
            }
            zedAsync()
        }
    }
}