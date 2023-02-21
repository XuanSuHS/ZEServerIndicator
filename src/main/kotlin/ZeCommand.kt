package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
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
    @Description("5e TOPZE社区 服务器列表\n短命令/topze\n")
    suspend fun CommandSender.topze() {
        webresponse = webfortopze()
        sendMessage(webresponse)
    }

    @SubCommand("5e")
    @Description("5e TOPZE社区 服务器列表\n短命令/5e\n")
    suspend fun CommandSender.fe() {
        webresponse = webfortopze()
        sendMessage(webresponse)
    }

    @SubCommand("UB")
    @Description("UB社区 服务器列表\n短命令/ub\n")
    suspend fun CommandSender.ub() {
        sendMessage("获取服务器信息中，请稍等几秒")
        webresponse = UB.dataOutput()
        sendMessage(webresponse)
    }

    @SubCommand("Zed")
    @Description("僵尸乐园社区 服务器列表\n短命令/zed\n")
    suspend fun CommandSender.zed() {
        webresponse = Zed.dataOutput()
        sendMessage(webresponse)
    }
}

class UbCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "UB",
    parentPermission = perm
) {

    @Handler
    suspend fun CommandSender.handle() {
        val webresponse = UB.dataOutput()
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

class FeCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "5e",
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
    primaryName = "Zed",
    parentPermission = perm
) {
    @Handler
    suspend fun CommandSender.handle() {
        val webresponse = Zed.dataOutput()
        sendMessage(webresponse)
    }
}

class FysCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "Fy\$",
    parentPermission = perm
) {
    @Handler
    suspend fun CommandSender.handle() {
        val webresponse = webforfys()
        sendMessage(webresponse)
    }
}

class OpenOBJCommand(perm: Permission) : CompositeCommand(
    owner = Indicator,
    primaryName = "FindOBJ",
    parentPermission = perm
) {
    @SubCommand("on")
    @Description("开启本群OBJ提醒")
    suspend fun CommandSenderOnMessage<*>.on() {
        FindOBJ.group = fromEvent.subject
        FindOBJ.FindON = true
        sendMessage("本群OBJ提醒已开启")
        UB.firstTimeFindOBJ()
        Zed.findOBJ()
    }

    @SubCommand("status")
    @Description("查看提醒OBJ群列表")
    suspend fun CommandSender.status() {
        sendMessage(FindOBJ.group.toString())
    }

    @SubCommand("off")
    @Description("关闭本群OBJ提醒")
    suspend fun CommandSender.off() {
        FindOBJ.FindON = false
        sendMessage("本群OBJ提醒已关闭")
    }
}