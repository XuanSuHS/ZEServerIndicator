package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    var topzeToken: String by value("fec9d45f7416256443b5e051c200fc1d")
    var objGroups : MutableSet<Long> by value(mutableSetOf())
}