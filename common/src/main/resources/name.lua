local convertPhenomenon = require 'phenomenon'

local name_table = {
    -- example
    --[[
    example_username = "ユーザのｉｄから直接読み方を指定",
    use_func = function(account)
        return "ユーザの情報を関数に渡して戻り値を読み方に"
        -- return account.displayName:replace("置換を使う", "置換後")
    end,
    --]]
}
local function name_table_proc(a)
    local id = a.acct
    local name = name_table[id]
    if type(name) == "string" then return name end
    if type(name) == "function" then return name(a) end
    return a.displayName
end

return function(status)
    local name = name_table_proc(status.account)
    if not name or name == "" then
        return status.account.username
    end
    return convertPhenomenon(name)
end