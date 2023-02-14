package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object Indicator : KotlinPlugin(
    JvmPluginDescription(
        id = "top.xuansu.mirai.ze-server-indicator",
        name = "CSGO Ze Server Indicator",
        version = "0.1.0",
    ) {
        author("XuanSu")
    }
) {
    override fun onEnable() {
        ZeCommands().register()
        logger.info { "ZE Server Indicator 已启动" }
    }

    override fun onDisable() {
        logger.info {"ZE Server Indicator 已关闭"}
    }
}