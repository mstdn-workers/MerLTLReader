package jp.zero_x_d.workaholic.merltlreader.script

import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.TwoArgFunction

/**
 * Created by quartz on 17/10/06.
 */
class KotlinLib : TwoArgFunction() {

    override fun call(moduleName: LuaValue, env: LuaValue?): LuaValue {
        val library = LuaValue.tableOf()
        env!!.set("kotlin", library)
        val strlib = LuaValue.tableOf()
        strlib.set("replace", replace())
        library.set("string", strlib)

        val globals = env.checkglobals()
        globals.package_.setIsLoaded("kotlin", library)

        return library
    }

    internal class replace : ThreeArgFunction() {
        override fun call(str: LuaValue, regex: LuaValue, replacement: LuaValue): LuaValue {
            if (replacement.isstring()) {
                return LuaValue.valueOf(
                        str.checkjstring().replace(
                                regex.checkjstring().toRegex(),
                                replacement.checkjstring()))
            }
/*            if (replacement.isfunction()) {
                return LuaValue.valueOf(
                        str.checkjstring().replace(
                                regex.checkjstring().toRegex(),
                                transform = { matchResult ->
                                    replacement.call(valueOf(matchResult)).checkjstring() }
                ))
            }*/
            return LuaValue.NIL
        }
    }
}