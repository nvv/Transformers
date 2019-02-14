package com.vnamashko.transformers.network

import com.google.gson.*
import com.vnamashko.transformers.network.model.Transformer
import java.lang.reflect.Type

/**
 * @author Vlad Namashko
 */
class TransformerDeserializer : JsonDeserializer<Transformer> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Transformer {
        val obj = json.asJsonObject

        val id = obj.get("id")?.asString
        val name = obj.get("name").asString
        val team = obj.get("team").asString
        val icon = obj.get("team_icon")?.asString

        val skills = Transformer.Skills(obj.get("strength").asInt,
                obj.get("intelligence").asInt, obj.get("speed").asInt, obj.get("endurance").asInt,
                obj.get("rank").asInt, obj.get("courage").asInt, obj.get("firepower").asInt, obj.get("skill").asInt)

        return Transformer(id, name, skills, team, icon)
    }
}

class TransformerSerializer : JsonSerializer<Transformer> {

    override fun serialize(src: Transformer, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val json = JsonObject()

        json.addProperty("id", src.id)
        json.addProperty("name", src.name)

        json.addProperty("strength", src[Transformer.STRENGTH])
        json.addProperty("intelligence", src[Transformer.INTELLIGENCE])
        json.addProperty("speed", src[Transformer.SPEED])
        json.addProperty("endurance", src[Transformer.ENDURANCE])
        json.addProperty("rank", src[Transformer.RANK])
        json.addProperty("courage", src[Transformer.COURAGE])
        json.addProperty("firepower", src[Transformer.FIREPOWER])
        json.addProperty("skill", src[Transformer.SKILL])

        json.addProperty("team", src.team)
        json.addProperty("icon", src.icon)

        return json
    }
}