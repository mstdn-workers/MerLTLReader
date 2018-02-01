local function spoilerAllOpen(status)
    if status.spoilerText ~= "" then
        status.content = status.spoilerText .. " もっと見る " .. status.content
    end                                        -- 隠す
end

local function spoilerAllClose(status)
    if status.spoilerText ~= "" then
        status.content = status.spoilerText .. " もっと見る " ..
                #status.content .. "文字以内の隠されたテキスト"
    end
end

local function spoilerOpenByBlackList(status)
    local bl = {
        -- blacklist_username_here = true
    }
    if bl[status.account.acct] then
        spoilerAllClose(status)
    else
        spoilerAllOpen(status)
    end
end

return spoilerAllOpen
-- return spoilerAllClose
-- return spoilerOpenByBlackList