require 'patch/string'

status = {}

local html = require 'html'
local nameConvert = require 'name'
local convertPhenomenon = require 'phenomenon'
local processSpoiler = require 'spoiler'

function status:readName()
    return nameConvert(self)
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

function processBreak(str) return str:gsub("<br />", "\n") end
function removeTag(str) return str:gsub("<[^>]+>", "") end
function replaceURL(status)
    -- """http(s)?://([\w-]+\.)+[\w-]+(:\w+)?(/[\w-./?%&=;]*)?"""
    status.content = status.content:gsub(
        "https?://[A-Za-z0-9_%.-]+:?%d*/?[A-Za-z0-9_%-%./%?%%&=;@]*",
        "URL"
    )
    --[[ local MapKt = luajava.bindClass("jp.zero_x_d.workaholic.merltlreader.status.MapKt")
    status.content = MapKt:remove_url(status.content) --]]
end
function processImageURL(status)
    for _, mediaURL in ipairs(status.mediaAttachments) do
        status.content = status.content:replace(
            mediaURL.textUrl,
            status.isSensitive and "不適切画像" or "画像"
        )
    end
end

function processQuote(str)
    return str:kreplace([[(?m)^>\s*]], "引用 ")
end

function preProcessContent(status)
    processSpoiler(status)
    status.content = processBreak(status.content)
    status.content = removeTag(status.content)
    processImageURL(status)
    replaceURL(status)
    status.content = html.unescapeEntities(status.content)
    status.content = processQuote(status.content)
end

function status:readContent()
    preProcessContent(self)
    return convertPhenomenon(self.content)
end

return status
