package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.permission.Permission
import top.xuansu.mirai.zeServerIndicator.TopZE.webForTopZE


class ZeCommand(perm: Permission) : CompositeCommand(
    owner = Indicator,
    primaryName = "ze",
    parentPermission = perm
) {
    private var webResponse = ""

    @SubCommand("5e")
    @Description("5e TOPZE社区 服务器列表\n短命令/5e\n")
    suspend fun CommandSender.fe() {
        webResponse = webForTopZE()
        sendMessage(webResponse)
    }

    @SubCommand("UB")
    @Description("UB社区 服务器列表\n短命令/ub\n")
    suspend fun CommandSender.ub() {
        webResponse = UB.dataOutput()
        sendMessage(webResponse)
    }

    @SubCommand("Zed")
    @Description("僵尸乐园社区 服务器列表\n短命令/zed\n")
    suspend fun CommandSender.zed() {
        webResponse = Zed.dataOutput()
        sendMessage(webResponse)
    }
}

class UbCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "UB",
    parentPermission = perm
) {

    @Handler
    suspend fun CommandSender.handle() {
        val webResponse = UB.dataOutput()
        sendMessage(webResponse)
    }
}

class FeCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "5e",
    parentPermission = perm
) {
    @Handler
    suspend fun CommandSender.handle() {
        val webResponse = webForTopZE()
        sendMessage(webResponse)
    }
}

class ZedCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "Zed",
    parentPermission = perm
) {
    @Handler
    suspend fun CommandSender.handle() {
        val webResponse = Zed.dataOutput()
        sendMessage(webResponse)
    }
}

class FysCommand(perm: Permission) : SimpleCommand(
    owner = Indicator,
    primaryName = "Fy\$",
    parentPermission = perm
) {
    @Handler
    suspend fun CommandSender.handle() {
        val webResponse = webForFys()
        sendMessage(webResponse)
    }
}

class FindOBJCommand(perm: Permission) : CompositeCommand(
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
    }

    @SubCommand("status")
    @Description("查看提醒OBJ群列表")
    suspend fun CommandSender.status() {
        val message = FindOBJ.FindON.toString() + "\n" + FindOBJ.group.id
        sendMessage(message)
    }

    @SubCommand("off")
    @Description("关闭本群OBJ提醒")
    suspend fun CommandSender.off() {
        FindOBJ.FindON = false
        sendMessage("本群OBJ提醒已关闭")
    }
}

class ZeSetCommand(): CompositeCommand(
    owner = Indicator,
    primaryName = "ze-set"
) {
    @SubCommand("updateMapData")
    @Description("更新地图翻译数据，目前仅适用于topZE社区")
    suspend fun CommandSender.updateMapData() {
        TopZE.updateMapData()
        sendMessage("地图数据更新完毕")
    }
}