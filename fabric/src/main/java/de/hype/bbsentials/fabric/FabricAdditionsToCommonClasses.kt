package de.hype.bbsentials.fabric

import de.hype.bbsentials.client.common.mclibraries.interfaces.Vector3d
import de.hype.bbsentials.shared.constants.VanillaEntities
import de.hype.bbsentials.shared.objects.MinecraftEntity
import de.hype.bbsentials.shared.objects.Position
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * This File is used to add methods which would be more complicated otherwise by allowing new methods in classes that normally are not in the common package like mc libraries
 */

fun Position.toBlockPos(): BlockPos {
    return BlockPos(x.toInt(), y.toInt(), z.toInt());
}

fun BlockPos.toPos(): Position {
    return Position(x, y, z)
}

fun Entity.vanillaEntityType(): VanillaEntities {
    return VanillaRegistry.get(this)
}

fun Entity.getVanillaEntity(): MinecraftEntity {
    val entityType = vanillaEntityType();
    val customName = this.customName?.string
    val pos = this.blockPos.toPos()
    return MinecraftEntity(entityType, customName, pos)
}

fun Vec3d.toVector(): Vector3d {
    return Vector3d(this.x, this.z, this.z)
}

fun Vec3d?.toPos(): Position? {
    if (this == null) return null;
    return Position(this.x.toInt(), this.y.toInt(), this.z.toInt())
}
