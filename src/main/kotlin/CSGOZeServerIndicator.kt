package top.xuansu.mirai.zeServerIndicator

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object Indicator : KotlinPlugin(
    JvmPluginDescription(
        id = "top.xuansu.mirai.ze-server-indicator",
        name = "CSGO Ze Server Indicator",
        version = "0.1.4",
    ) {
        author("XuanSu")
    }
) {
    private lateinit var zepermission: Permission
    override fun onEnable() {
        zepermission = PermissionService.INSTANCE.register(PermissionId(id,"ze"),"命令使用权限")
        ZeCommand(zepermission).register()
        UbCommand(zepermission).register()
        TopzeCommand(zepermission).register()
        ZedCommand(zepermission).register()
        logger.info { "ZE Server Indicator 已启动" }
    }

    override fun onDisable() {
        logger.info {"ZE Server Indicator 已关闭"}
    }
}