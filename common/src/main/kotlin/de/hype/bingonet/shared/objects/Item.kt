package de.hype.bingonet.shared.objects

import com.google.gson.JsonObject
import de.hype.bingonet.shared.objects.json.ApiJson
import java.time.Instant

class Item {
    private val attributes: ApiJson
    private val display: ApiJson
    private val itemCount: Int
    private var creationDate: Instant? = null
    private var itemID: String? = null
    private val mcItemId: Int
    val damage: Int
    private var lore: List<String>? = null

    constructor(jsonObject: JsonObject) : this(ApiJson(jsonObject))

    constructor(data: ApiJson) {
        attributes = data.get("tag→ExtraAttributes").copyApplied()
        display = data.get("tag→display")
        mcItemId = data.getInt("id")
        damage = data.getInt("Damage", 0)
        itemCount = data.getInt("Count", 1)
    }

    fun getItemID(): String {
        if (itemID == null) {
            itemID = attributes.getString("id")
        }
        return itemID!!
    }

    fun getCreationDate(): Instant {
        if (creationDate == null) {
            creationDate = Instant.ofEpochMilli(attributes.getLong("timestamp", 0L))
        }
        return creationDate!!
    }

    fun getItemCount(): Int = itemCount

    fun getDisplayName(): String = display.getString("Name", "BingoNet no name")

    fun getLore(): List<String> {
        if (lore != null) return lore!!
        val list = mutableListOf<String>()
        display.getJsonArray("Lore").toList().forEach { e -> list.add(e?.getString() ?: "") }
        lore = list
        return lore!!
    }

    fun getAttributes(): ApiJson = attributes

    enum class AttributeType(val bitId: Int) {
        ENCHANTMETS(1),
        ATTRIBUTE_MODIFIERS(2),
        UNBREAKABLE(3),
        CAN_DESTROY(4),
        CAN_PLACE_ON(5),
        VARIOUS(6),
        DYED(7),
        UPGRADES(8)
    }
}