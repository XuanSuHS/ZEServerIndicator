package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object Data : AutoSavePluginData("Data") {
    var UBMapChi: MutableMap<String, String> by value(mutableMapOf())
}