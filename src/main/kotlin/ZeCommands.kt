package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand


object ZeCommands : CompositeCommand(Indicator, "ze") {
    var webresponse = ""

    @SubCommand("5e")
    @Description("显示 5e TOPZE社区 服务器列表")
    suspend fun CommandSender.topze() {
        webresponse = webfor5e()
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