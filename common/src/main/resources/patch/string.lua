local kotlin = require 'kotlin'

function string:endsWith(e)
    return self:sub(-(e:len()))==e
end

local magics = "[" .. ("^$()%.[]*+-?"):gsub(".", "%%%1") .. "]"
function escapeMagic(pattern) return pattern:gsub(magics, "%%%1") end
function string:replace(old, new) return self:gsub(escapeMagic(old), new) end
string.kreplace = kotlin.string.replace