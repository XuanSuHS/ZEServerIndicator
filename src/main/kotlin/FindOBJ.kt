package top.xuansu.mirai.zeServerIndicator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact

object FindOBJ {
    lateinit var group: Contact
    var FindON = false

    fun sendUBOBJtoGroup(
        serverName: String,
        serverMap: String, serverPlayer: Int,
        serverAddress: String
    ) {
        val message = "有OBJ!\n$serverName\n$serverMap\n人数：$serverPlayer\n地址：$serverAddress"
        CoroutineScope(Dispatchers.Default).launch { group.sendMessage(message) }
    }
}