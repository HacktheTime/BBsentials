package de.hype.bbsentials.fabric
// Credits go to nea89 for this (Firmanent)! Just slightly adapted by me

import com.mojang.blaze3d.systems.RenderSystem
import de.hype.bbsentials.fabric.objects.WorldRenderLastEvent
import de.hype.bbsentials.shared.objects.RenderInformation
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.*
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import java.lang.Math.pow

/**
 * @author nea89o in Firmanent
 */
class RenderInWorldContext(
    private val tesselator: Tessellator,
    val matrixStack: MatrixStack,
    private val camera: Camera,
    private val tickCounter: RenderTickCounter,
    val vertexConsumers: VertexConsumerProvider.Immediate,
) {

    fun color(color: me.shedaniel.math.Color) {
        color(color.red / 255F, color.green / 255f, color.blue / 255f, color.alpha / 255f)
    }

    fun color(red: Float, green: Float, blue: Float, alpha: Float) {
        RenderSystem.setShaderColor(red, green, blue, alpha)
    }

    fun block(blockPos: BlockPos) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)
        matrixStack.push()
        matrixStack.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
        buildCube(matrixStack.peek().positionMatrix, tesselator)
        matrixStack.pop()
    }

    enum class VerticalAlign {
        TOP, BOTTOM, CENTER;

        fun align(index: Int, count: Int): Float {
            return when (this) {
                CENTER -> (index - count / 2F) * (1 + getFontHeight())
                BOTTOM -> (index - count) * (1 + getFontHeight())
                TOP -> (index) * (1 + getFontHeight())
            }
        }
    }

    fun waypoint(position: BlockPos, label: Text) {
        text(
            position.toCenterPos(),
            label,
            Text.literal("§e${formatDistance(MinecraftClient.getInstance().player?.pos?.distanceTo(position.toCenterPos()) ?: 42069.0)}")
        )
    }

    fun formatDistance(distance: Double): String {
        if (distance < 10) return "%.1fm".format(distance)
        return "%dm".format(distance.toInt())
    }

    fun withFacingThePlayer(position: Vec3d, block: FacingThePlayerContext.() -> Unit) {
        matrixStack.push()
        matrixStack.translate(position.x, position.y, position.z)
        val actualCameraDistance = position.distanceTo(camera.pos)
        val distanceToMoveTowardsCamera = if (actualCameraDistance < 10) 0.0 else -(actualCameraDistance - 10.0)
        val vec = position.subtract(camera.pos).multiply(distanceToMoveTowardsCamera / actualCameraDistance)
        matrixStack.translate(vec.x, vec.y, vec.z)
        matrixStack.multiply(camera.rotation)
        matrixStack.scale(-0.025F, -0.025F, -1F)

        FacingThePlayerContext(this).run(block)

        matrixStack.pop()
        vertexConsumers.drawCurrentLayer()
    }

    fun sprite(position: Vec3d, sprite: Sprite, width: Int, height: Int) {
        texture(
            position, sprite.atlasId, width, height, sprite.minU, sprite.minV, sprite.maxU, sprite.maxV
        )
    }

    fun texture(
        position: Vec3d, texture: Identifier, width: Int, height: Int,
        u1: Float, v1: Float,
        u2: Float, v2: Float,
    ) {
        withFacingThePlayer(position) {
            texture(texture, width, height, u1, v1, u2, v2)
        }
    }

    fun text(position: Vec3d, vararg texts: Text, verticalAlign: VerticalAlign = VerticalAlign.CENTER) {
        withFacingThePlayer(position) {
            text(*texts, verticalAlign = verticalAlign)
        }
    }

    fun tinyBlock(vec3d: Vec3d, size: Float) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)
        matrixStack.push()
        matrixStack.translate(vec3d.x, vec3d.y, vec3d.z)
        matrixStack.scale(size, size, size)
        matrixStack.translate(-.5, -.5, -.5)
        buildCube(matrixStack.peek().positionMatrix, tesselator)
        matrixStack.pop()
    }

    fun wireframeCube(blockPos: BlockPos, lineWidth: Float = 10F) {
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)
        matrixStack.push()
        RenderSystem.lineWidth(lineWidth / pow(camera.pos.squaredDistanceTo(blockPos.toCenterPos()), 0.25).toFloat())
        matrixStack.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
        buildWireFrameCube(matrixStack.peek(), tesselator)
        matrixStack.pop()
    }

    fun line(vararg points: Vec3d, lineWidth: Float = 10F) {
        line(points.toList(), lineWidth)
    }

    fun tracer(toWhere: Vec3d, lineWidth: Float = 3f) {
        val cameraForward = Vector3f(0f, 0f, 1f).rotate(camera.rotation)
        line(camera.pos.add(Vec3d(cameraForward)), toWhere, lineWidth = lineWidth)
    }

    fun line(points: List<Vec3d>, lineWidth: Float = 10F) {
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)
        RenderSystem.lineWidth(lineWidth)
        val buffer = tesselator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)

        val matrix = matrixStack.peek()
        var lastNormal: Vector3f? = null
        points.zipWithNext().forEach { (a, b) ->
            val normal =
                Vector3f(b.x.toFloat(), b.y.toFloat(), b.z.toFloat()).sub(a.x.toFloat(), a.y.toFloat(), a.z.toFloat())
                    .normalize()
            val lastNormal0 = lastNormal ?: normal
            lastNormal = normal
            buffer.vertex(matrix.positionMatrix, a.x.toFloat(), a.y.toFloat(), a.z.toFloat()).color(-1)
                .normal(matrix, lastNormal0.x, lastNormal0.y, lastNormal0.z).next()
            buffer.vertex(matrix.positionMatrix, b.x.toFloat(), b.y.toFloat(), b.z.toFloat()).color(-1)
                .normal(matrix, normal.x, normal.y, normal.z).next()
        }

        BufferRenderer.drawWithGlobalProgram(buffer.end())
    }

    companion object {
        private fun doLine(
            matrix: MatrixStack.Entry, buf: BufferBuilder, i: Float, j: Float, k: Float, x: Float, y: Float, z: Float
        ) {
            val normal = Vector3f(x, y, z).sub(i, j, k).normalize()
            buf.vertex(matrix.positionMatrix, i, j, k).normal(matrix, normal.x, normal.y, normal.z).color(-1).next()
            buf.vertex(matrix.positionMatrix, x, y, z).normal(matrix, normal.x, normal.y, normal.z).color(-1).next()
        }


        private fun buildWireFrameCube(matrix: MatrixStack.Entry, tessellator: Tessellator) {
            val buf = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)

            for (i in 0..1) {
                for (j in 0..1) {
                    val i = i.toFloat()
                    val j = j.toFloat()
                    doLine(matrix, buf, 0F, i, j, 1F, i, j)
                    doLine(matrix, buf, i, 0F, j, i, 1F, j)
                    doLine(matrix, buf, i, j, 0F, i, j, 1F)
                }
            }
            BufferRenderer.drawWithGlobalProgram(buf.end())
        }

        private fun buildCube(matrix: Matrix4f, tessellator: Tessellator) {
            val buf = tessellator.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION)
            buf.vertex(matrix, 0.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 0.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 0.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 0.0F, 1.0F, 1.0F).next()
            buf.vertex(matrix, 1.0F, 0.0F, 1.0F).next()
            BufferRenderer.drawWithGlobalProgram(buf.end())
        }


        fun renderInWorld(event: WorldRenderLastEvent, block: RenderInWorldContext. () -> Unit) {
            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.disableCull()

            event.matrices.push()
            event.matrices.translate(-event.camera.pos.x, -event.camera.pos.y, -event.camera.pos.z)

            val ctx = RenderInWorldContext(
                RenderSystem.renderThreadTesselator(),
                event.matrices,
                event.camera,
                event.tickCounter,
                event.vertexConsumers
            )

            block(ctx)

            event.matrices.pop()

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F)
            VertexBuffer.unbind()
            RenderSystem.enableDepthTest()
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
        }
    }

    fun waypointIcon(
        position: Vec3d, textures: RenderInformation, width: Int, height: Int, xmodifier: Float
    ) {
        val backupColor = RenderSystem.getShaderColor()
        color(1f, 1f, 1f, 1f)
        withFacingThePlayer(position) {
            matrixStack.push()
            matrixStack.translate(xmodifier, -25f, 0f)
            RenderSystem.setShaderTexture(
                0, Identifier.of(textures.namespace, textures.pathToFile)
            )
            RenderSystem.setShader(net.minecraft.client.render.GameRenderer::getPositionTexColorProgram)
            val hw = width / 2F
            val hh = height / 2F
            val matrix4f: Matrix4f = matrixStack.peek().positionMatrix
            val buf = tesselator.begin(
                net.minecraft.client.render.VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION_TEXTURE_COLOR
            )
            buf.color(255, 255, 255, 255)
            buf.vertex(matrix4f, -hw, -hh, 0F).texture(0f, 0f).next()
            buf.vertex(matrix4f, -hw, +hh, 0F).texture(0f, 1f).next()
            buf.vertex(matrix4f, +hw, +hh, 0F).texture(1f, 1f).next()
            buf.vertex(matrix4f, +hw, -hh, 0F).texture(1f, 0f).next()
            BufferRenderer.drawWithGlobalProgram(buf.end())
        }
        RenderSystem.setShaderColor(backupColor[0], backupColor[1], backupColor[2], backupColor[3])
    }

    fun doWaypointIcon(position: Vec3d, textures: List<RenderInformation>, width: Int, height: Int) {
        val xStartPosition = -((textures.dropLast(1).sumOf { it.spaceToNext }) / 2).toFloat()
        var xmodifer = xStartPosition
        for ((index, value) in textures.withIndex()) {
            if (value.pathToFile.isEmpty()) continue
            waypointIcon(position, value, width, height, xmodifer)
            xmodifer += width
        }

    }
}

/**
 * @author nea89o in Firmanent
 */
class FacingThePlayerContext(val worldContext: RenderInWorldContext) {
    val matrixStack by worldContext::matrixStack
    fun waypoint(position: BlockPos, label: Text) {
        text(
            label,
            Text.literal("§e${formatDistance(MinecraftClient.getInstance().player?.pos?.distanceTo(position.toCenterPos()) ?: 42069.0)}")
        )
    }

    fun formatDistance(distance: Double): String {
        if (distance < 10) return "%.1fm".format(distance)
        return "%dm".format(distance.toInt())
    }

    fun text(
        vararg texts: Text,
        verticalAlign: RenderInWorldContext.VerticalAlign = RenderInWorldContext.VerticalAlign.CENTER
    ) {
        for ((index, text) in texts.withIndex()) {
            worldContext.matrixStack.push()
            val width = MinecraftClient.getInstance().textRenderer.getWidth(text)
            worldContext.matrixStack.translate(-width / 2F, verticalAlign.align(index, texts.size), 0F)
            val vertexConsumer: VertexConsumer =
                worldContext.vertexConsumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough())
            val matrix4f = worldContext.matrixStack.peek().positionMatrix
            vertexConsumer.vertex(matrix4f, -1.0f, -1.0f, 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            vertexConsumer.vertex(matrix4f, -1.0f, getFontHeight(), 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            vertexConsumer.vertex(matrix4f, width.toFloat(), getFontHeight(), 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            vertexConsumer.vertex(matrix4f, width.toFloat(), -1.0f, 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            worldContext.matrixStack.translate(0F, 0F, 0.01F)

            MinecraftClient.getInstance().textRenderer.draw(
                text,
                0F,
                0F,
                -1,
                false,
                worldContext.matrixStack.peek().positionMatrix,
                worldContext.vertexConsumers,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
            )
            worldContext.matrixStack.pop()
        }
    }


    fun texture(
        texture: Identifier, width: Int, height: Int,
        u1: Float, v1: Float,
        u2: Float, v2: Float,
    ) {
        RenderSystem.setShaderTexture(0, texture)
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram)
        val hw = width / 2F
        val hh = height / 2F
        val matrix4f: Matrix4f = worldContext.matrixStack.peek().positionMatrix
        val buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR)
        buf.vertex(matrix4f, -hw, -hh, 0F).color(-1).texture(u1, v1).next()
        buf.vertex(matrix4f, -hw, +hh, 0F).color(-1).texture(u1, v2).next()
        buf.vertex(matrix4f, +hw, +hh, 0F).color(-1).texture(u2, v2).next()
        buf.vertex(matrix4f, +hw, -hh, 0F).color(-1).texture(u2, v1).next()
        BufferRenderer.drawWithGlobalProgram(buf.end())
    }

}

fun VertexConsumer.next() = this


fun getFontHeight(): Float {
    return MinecraftClient.getInstance().textRenderer.fontHeight.toFloat()
}