package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.permission.Permission


class ZeCommand(perm: Permission) : CompositeCommand(
    owner = Indicator,
    primaryName = "ze",
    parentPermission = perm
) {
    private var webresponse = ""

    @SubCommand("topze")
    @Description("显示 5e TOPZE社区 服务器列表")
    suspend fun CommandSender.topze() {
        webresponse = webfortopze()
        sendMessage(webresponse)
    }

    @SubCommand("ub")
    @Description("显示 UB社区 服务器列表")
    suspend fun CommandSender.ub() {
        sendMessage("获取服务器信息中，请稍等几秒")
        webresponse = webforub()
        sendMessage(webresponse)
    }

    @SubCommand("zed")
    @Description("显示 僵尸乐园社区 服务器列表")
    suspend fun CommandSender.zed() {
        webresponse = webforzed()
        sendMessage(webresponse)
    }
}

class UbCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "ub",
    parentPermission = perm
) {

    @Handler
    suspend fun CommandSender.handle() {
        sendMessage("获取服务器信息中，请稍等几秒")
        val webresponse = webforub()
        sendMessage(webresponse)
    }
}

class TopzeCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "topze",
    parentPermission = perm
) {
    @Handler
    suspend fun CommandSender.handle() {
        val webresponse = webfortopze()
        sendMessage(webresponse)
    }
}

class ZedCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "zed",
    parentPermission = perm
) {
    @Handler
    suspend fun CommandSender.handle() {
        val webresponse = webforzed()
        sendMessage(webresponse)
    }
}