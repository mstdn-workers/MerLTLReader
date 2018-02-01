package jp.zero_x_d.workaholic.merltlreader.script

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.JsePlatform

/**
 * Created by D on 17/10/06.
 */

private val defaultPackagePath = listOf(
        "script/?.lua",
        "lua/?.lua",
        "?.lua"
).joinToString(";")

private var g = newAppDefaultGlobals()

private fun newAppDefaultGlobals() =
    JsePlatform.debugGlobals().apply {
        setPackagePath(defaultPackagePath)
        load(HTMLLib())
        println("load kotlin lib")
        load(KotlinLib())
    }

private fun Globals.setPackagePath(path: String) = load("package.path = '$path'").call()
private fun Globals.getPackagePath() = load("return package.path").call()
private fun Globals.require(lib: String) = load("return require '$lib'").call()

object Lua {
    fun require(lib: String): LuaValue {
        val returnLuaValue = g.require(lib)
        return returnLuaValue
    }

    fun reload() {
        g = newAppDefaultGlobals()
        TODO()
    }

    var packagePath: String
        get() = g.getPackagePath().tojstring()
        set(path) { g.setPackagePath(path) }

    val globals: Globals
        get() = g
}