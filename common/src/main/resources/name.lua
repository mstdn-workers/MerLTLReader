local convertPhenomenon = require 'phenomenon'

local name_table = {
    D = "システムディー",
    lvndr = function(a) a.displayName:replace("◆TrWgXi12CA", "") end,
    pcb = "ぱんかれ",
    Do = "どぅー",
    rin_souma = "そうま りん",
    pina_32 = function(a)
        return a.displayName:replace("Pina", "ピナ")
    end,
    Fuji_Midoryzaka = function(a)
        return a.displayName:replace("翠坂満月", "みどりざかみつづき")
    end,
    teddycube = function(a)
        return a.displayName:replace("Kawasaki", "カワサキ")
    end,
    stlayer = "なぞのおとこ",
    misakayuni = function(a)
        return a.displayName:replace("御坂優仁(みさかゆに)", "みさかゆに")
    end,
    TyamEpp = function(a)
        return a.displayName:replace("TyamEpp", "ちゃめっぷ")
    end,
    Erica_Hartmann = function(a)
        return a.displayName
            :replace("Neutralität", "中立")
            :replace("Löwe", "レーヴェ")
            :replace("GmbH", "ゲーエムベーハー")
    end
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
            name = name:gsub(fmt:format(w)..'$', "")
        end
    end
    return name
end

return function(status)
    local name = name_table_proc(status.account)
    name = remove_instance_str(name)
    if not name or name == "" then
        return status.account.username
    end
    return convertPhenomenon(name)
end