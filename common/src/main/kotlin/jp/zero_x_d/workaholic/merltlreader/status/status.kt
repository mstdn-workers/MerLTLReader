package jp.zero_x_d.workaholic.merltlreader.status

import com.sys1yagi.mastodon4j.api.entity.Status
import jp.zero_x_d.workaholic.merltlreader.script.Lua
import org.luaj.vm2.lib.jse.JsePlatform


/**
 * Created by D on 17/06/24.
 */


private val lua_status_lib get() = Lua.require("status")

private val lIsSpam get() = lua_status_lib.get("isSpam")
private val lReadName get() = lua_status_lib.get("readName")
private val lReadContent get() = lua_status_lib.get("readContent")

val Status.isSpam: Boolean
    get() = lIsSpam.call(valueOf(this)).toboolean()

val Status.readName: String
    get() = lReadName.call(valueOf(this)).tojstring()

val Status.readContent: String
    get() = lReadContent.call(valueOf(this)).tojstring()