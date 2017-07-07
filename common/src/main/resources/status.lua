status = {}

function string:endsWith(e)
    return self:sub(-(e:len()))==e
end

function status:isSpam()
    local spam_suffixes = {
        "_info", "_infom", "_information"
    }
    for _, s in ipairs(spam_suffixes) do
        if self.account.acct:lower():endsWith(s) then
            return true
        end
    end
    return false
end

local name_table = {
    D = "システムディー",
    lvndr = function(a) a.displayName:gsub("◆TrWgXi12CA", "") end,
    pcb = "ぱんかれ",
    --"Do" to { _ -> "Do" },
    --"rin_souma" to { _ -> "そうま りん"},
    pina_32 = function(a)
        return a.displayName:gsub("Pina", "ピナ")
    end,
    Fuji_Midoryzaka = function(a)
        return a.displayName:gsub("翠坂満月", "みどりざかみつづき")
    end,
    teddycube = function(a)
        return a.displayName:gsub("Kawasaki", "カワサキ")
    end,
    stlayer = "なぞのおとこ",
    misakayuni = function(a)
        return a.displayName:gsub("御坂優仁%(みさかゆに%)", "みさかゆに")
    end,
}
local function name_table_proc(a)
    local id = a.acct
    local name = name_table[id]
    if type(name) == "string" then return name end
    if type(name) == "function" then return name(a) end
    return a.displayName
end

local formats = {"@%s", "＠%s", "%%(%s%%)", " on%s" }
local words = {
    "社畜丼",
    "女装丼",
    "mstdn-workers.com",
    "mstdn-workers",
    "workers",
}
local function remove_instance_str(name)
    if not name then return nil end
    for _, w in ipairs(words) do
        for _, fmt in ipairs(formats) do
            name = name:gsub(fmt:format(w), "")
        end
    end
    return name
end

function status:readName()
    local name = name_table_proc(self.account)
    name = remove_instance_str(name)
    if not name or name == "" then
        return self.account.username
    end
    return name:toReadable()
end

function status:readContent()
    return self.content:toReadable()
end

function string:toReadable()
    return self:gsub("丼", "どん")
        :gsub("畜", "ちく")
        :gsub(":briefcase:", "")
        :gsub(":white_check_mark:", "")
        :gsub(":checkered_flag:", "チェッカーフラグ")
        :gsub("瞿麦", "クバク")
        :gsub("PSO2", "ぷそつ")
        :gsub("\u00e2\u009c\u0085", "")
        :gsub("mstdn-workers", "ますどんわーかーず")
        :gsub("%.jar", "どっとじゃー")
        :gsub("秋月", "あきづき")
end

return status
