package jp.zero_x_d.workaholic.merltlreader.script

import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.*


/**
 * Created by quartz on 17/10/06.
 */

class HTMLLib : TwoArgFunction() {

    override fun call(moduleName: LuaValue, env: LuaValue?): LuaValue {
        val library = LuaValue.tableOf()
        library.set("unescapeEntities", unescapeEntities())
        env!!.set("html", library)

        val globals = env.checkglobals()
        globals.package_.setIsLoaded("html", library)

        return library
    }

    internal class unescapeEntities : OneArgFunction() {
        override fun call(str: LuaValue?): LuaValue {
            return LuaValue.valueOf(
                    org.jsoup.parser.Parser.unescapeEntities(
                            str!!.checkjstring(),
                            false))
        }
    }
}