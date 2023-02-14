package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand


class ZeCommands : SimpleCommand(Indicator, "ze"){
    @Handler
    suspend fun CommandSender.handle() {
        val webresponse = web()
        sendMessage(webresponse)
    }
}