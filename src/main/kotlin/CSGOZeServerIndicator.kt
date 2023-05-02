package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionId
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.plugin.jvm.reloadPluginConfig
import net.mamoe.mirai.console.plugin.jvm.reloadPluginData
import net.mamoe.mirai.utils.info

object Indicator: KotlinPlugin(
    JvmPluginDescription(
        id = "top.xuansu.mirai.ze-server-indicator",
        name = "CSGO Ze Server Indicator",
        version = "0.1.9-B16",
    ) {
        author("XuanSu")
    }
) {
    private lateinit var ZEPermission: Permission
    private lateinit var OBJPermission: Permission
    override fun onEnable() {
        ZEPermission = PermissionService.INSTANCE.register(PermissionId(id,"ze"),"命令使用权限")
        OBJPermission = PermissionService.INSTANCE.register(PermissionId(id,"OBJ"),"OBJ检测使用权限")

        ZeCommand(ZEPermission).register()
        UbCommand(ZEPermission).register()
        FeCommand(ZEPermission).register()
        ZedCommand(ZEPermission).register()
        FysCommand(ZEPermission).register()
        FindOBJCommand(OBJPermission).register()
        ZeSetCommand().register()

        reloadPluginConfig(Config)
        reloadPluginData(Data)

        CoroutineScope(Dispatchers.IO).launch {
            TopZE.updateMapData()
        }

        logger.info { "ZE Server Indicator 已启动" }
        coroutineOnEnable()
    }

    override fun onDisable() {
        Config.save()
        Data.save()
        CommandManager.INSTANCE.unregisterAllCommands(Indicator)
        logger.info {"ZE Server Indicator 已关闭"}
    }
}