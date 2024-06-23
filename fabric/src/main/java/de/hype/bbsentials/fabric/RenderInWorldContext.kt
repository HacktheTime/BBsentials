package de.hype.bbsentials.fabric

import com.mojang.blaze3d.systems.RenderSystem
import de.hype.bbsentials.fabric.objects.WorldRenderLastEvent
import de.hype.bbsentials.shared.objects.RenderInformation
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.*
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.MatrixStack.Entry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import java.lang.Math.pow

// Credits go to nea89 for this (Firmanent)! Just slightly adapted by me

class RenderInWorldContext private constructor(
    private val tesselator: Tessellator,
    private val matrixStack: MatrixStack,
    private val camera: Camera,
    private val tickDelta: Float,
    private val vertexConsumers: VertexConsumerProvider.Immediate,
) {
    private val buffer = tesselator.buffer
//    val effectiveFov = (MinecraftClient.getInstance().gameRenderer as AccessorGameRenderer).getFov_firmament(camera, tickDelta, true)
//    val effectiveFovScaleFactor = 1 / tan(toRadians(effectiveFov) / 2)

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
        buildCube(matrixStack.peek().positionMatrix, buffer)
        tesselator.draw()
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
            position.toCenterPos(), label, Text.literal(
                "§e${
                    formatDistance(
                        MinecraftClient.getInstance().player?.pos?.distanceTo(
                            position.toCenterPos()
                        ) ?: 42069.0
                    )
                }"
            )
        )
    }

    fun formatDistance(distance: Double): String {
        if (distance < 10) return "%.1fm".format(distance)
        return "%dm".format(distance.toInt())
    }

    fun text(position: Vec3d, vararg texts: Text, verticalAlign: VerticalAlign = VerticalAlign.CENTER) {
        if (texts.isEmpty()) {
            return
        }
        matrixStack.push()
        matrixStack.translate(position.x, position.y, position.z)
        val actualCameraDistance = position.distanceTo(camera.pos)
        val distanceToMoveTowardsCamera = if (actualCameraDistance < 10) 0.0 else -(actualCameraDistance - 10.0)
        val vec = position.subtract(camera.pos).multiply(distanceToMoveTowardsCamera / actualCameraDistance)
        matrixStack.translate(vec.x, vec.y, vec.z)
        matrixStack.multiply(camera.rotation)
        matrixStack.scale(-0.025F, -0.025F, -1F)

        for ((index, text) in texts.withIndex()) {
            matrixStack.push()
            val width = MinecraftClient.getInstance().textRenderer.getWidth(text)
            matrixStack.translate(-width / 2F, verticalAlign.align(index, texts.size), 0F)
            val vertexConsumer: VertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackgroundSeeThrough())
            val matrix4f = matrixStack.peek().positionMatrix
            vertexConsumer.vertex(matrix4f, -1.0f, -1.0f, 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            vertexConsumer.vertex(matrix4f, -1.0f, getFontHeight(), 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            vertexConsumer.vertex(matrix4f, width.toFloat(), getFontHeight(), 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            vertexConsumer.vertex(matrix4f, width.toFloat(), -1.0f, 0.0f).color(0x70808080)
                .light(LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE).next()
            matrixStack.translate(0F, 0F, 0.01F)

            MinecraftClient.getInstance().textRenderer.draw(
                text,
                0F,
                0F,
                -1,
                false,
                matrixStack.peek().positionMatrix,
                vertexConsumers,
                TextRenderer.TextLayerType.SEE_THROUGH,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
            )
            matrixStack.pop()
        }
        matrixStack.pop()
        vertexConsumers.drawCurrentLayer()
    }

    fun tinyBlock(vec3d: Vec3d, size: Float) {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram)
        matrixStack.push()
        matrixStack.translate(vec3d.x, vec3d.y, vec3d.z)
        matrixStack.scale(size, size, size)
        matrixStack.translate(-.5, -.5, -.5)
        buildCube(matrixStack.peek().positionMatrix, buffer)
        tesselator.draw()
        matrixStack.pop()
    }

    fun wireframeCube(blockPos: BlockPos, lineWidth: Float = 10F) {
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)
        matrixStack.push()
        RenderSystem.lineWidth(lineWidth / pow(camera.pos.squaredDistanceTo(blockPos.toCenterPos()), 0.25).toFloat())
        matrixStack.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
        buildWireFrameCube(matrixStack.peek(), buffer)
        tesselator.draw()
        matrixStack.pop()
    }

    fun line(vararg points: Vec3d, lineWidth: Float = 10F) {
        line(points.toList(), lineWidth)
    }

    fun line(points: List<Vec3d>, lineWidth: Float = 10F) {
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)
        RenderSystem.lineWidth(lineWidth / pow(camera.pos.squaredDistanceTo(points.first()), 0.25).toFloat())
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)
        buffer.fixedColor(255, 255, 255, 255)

        points.zipWithNext().forEach { (a, b) ->
            doLine(matrixStack.peek(), buffer, a.x, a.y, a.z, b.x, b.y, b.z)
        }
        buffer.unfixColor()

        tesselator.draw()
    }

    companion object {
        private fun doLine(
            matrix: Entry,
            buf: BufferBuilder,
            i: Number,
            j: Number,
            k: Number,
            x: Number,
            y: Number,
            z: Number
        ) {
            val normal =
                Vector3f(x.toFloat(), y.toFloat(), z.toFloat()).sub(i.toFloat(), j.toFloat(), k.toFloat()).normalize()
            buf.vertex(matrix.positionMatrix, i.toFloat(), j.toFloat(), k.toFloat())
                .normal(matrix, normal.x, normal.y, normal.z).next()
            buf.vertex(matrix.positionMatrix, x.toFloat(), y.toFloat(), z.toFloat())
                .normal(matrix, normal.x, normal.y, normal.z).next()
        }

        private fun doTracer(
            matrix: Entry, buf: BufferBuilder, i: Number, j: Number, k: Number, x: Number, y: Number, z: Number
        ) {
            doLine(matrix, buf, i, j, k, x, y, z)
        }

        private fun buildWireFrameCube(matrix: MatrixStack.Entry, buf: BufferBuilder) {
            buf.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES)
            buf.fixedColor(255, 255, 255, 255)

            for (i in 0..1) {
                for (j in 0..1) {
                    doLine(matrix, buf, 0, i, j, 1, i, j)
                    doLine(matrix, buf, i, 0, j, i, 1, j)
                    doLine(matrix, buf, i, j, 0, i, j, 1)
                }
            }
            buf.unfixColor()
        }

        private fun buildCube(matrix: Matrix4f, buf: BufferBuilder) {
            buf.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR)
            buf.fixedColor(255, 255, 255, 255)
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
            buf.unfixColor()
        }

        @JvmStatic
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
                event.tickDelta,
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

    fun sprite(position: Vec3d, sprite: Sprite, width: Int, height: Int) {
        texture(
            position, sprite.atlasId, width, height, sprite.minU, sprite.minV, sprite.maxU, sprite.maxV
        )
    }
    fun withFacingThePlayer(position: Vec3d, block: () -> Unit) {
        matrixStack.push()
        matrixStack.translate(position.x, position.y, position.z)
        val actualCameraDistance = position.distanceTo(camera.pos)
        val distanceToMoveTowardsCamera = if (actualCameraDistance < 10) 0.0 else -(actualCameraDistance - 10.0)
        val vec = position.subtract(camera.pos).multiply(distanceToMoveTowardsCamera / actualCameraDistance)
        matrixStack.translate(vec.x, vec.y, vec.z)
        matrixStack.multiply(camera.rotation)
        matrixStack.scale(-0.025F, -0.025F, -1F)

        block()

        matrixStack.pop()
        vertexConsumers.drawCurrentLayer()
    }

    fun texture(
        position: Vec3d, texture: Identifier, width: Int, height: Int,
        u1: Float, v1: Float,
        u2: Float, v2: Float,
    ) {
        val backupColor = RenderSystem.getShaderColor()
        color(1f, 1f, 1f, 1f)
        withFacingThePlayer(position) {
            RenderSystem.setShaderTexture(0, texture)
            RenderSystem.setShader(GameRenderer::getPositionColorTexProgram)
            val hw = width / 2F
            val hh = height / 2F
            val matrix4f: Matrix4f = matrixStack.peek().positionMatrix
            val buf = Tessellator.getInstance().buffer
            buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE)
            buf.fixedColor(255, 255, 255, 255)
            buf.vertex(matrix4f, -hw, -hh, 0F)
                .texture(u1, v1).next()
            buf.vertex(matrix4f, -hw, +hh, 0F)
                .texture(u1, v2).next()
            buf.vertex(matrix4f, +hw, +hh, 0F)
                .texture(u2, v2).next()
            buf.vertex(matrix4f, +hw, -hh, 0F)
                .texture(u2, v1).next()
            buf.unfixColor()
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
    fun waypointIcon(
        position: Vec3d, textures: RenderInformation, width: Int, height: Int, xmodifier: Float
    ) {
        val backupColor = RenderSystem.getShaderColor()
        color(1f, 1f, 1f, 1f)
        withFacingThePlayer(position) {
            matrixStack.push()
            matrixStack.translate(xmodifier, -25f, 0f)
            RenderSystem.setShaderTexture(0, Identifier(textures.namespace, textures.pathToFile))
            RenderSystem.setShader(GameRenderer::getPositionColorTexProgram)
            val hw = width / 2F
            val hh = height / 2F
            val matrix4f: Matrix4f = matrixStack.peek().positionMatrix
            val buf = Tessellator.getInstance().buffer
            buf.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE)
            buf.fixedColor(255, 255, 255, 255)
            buf.vertex(matrix4f, -hw, -hh, 0F)
                .texture(0f, 0f).next()
            buf.vertex(matrix4f, -hw, +hh, 0F)
                .texture(0f, 1f).next()
            buf.vertex(matrix4f, +hw, +hh, 0F)
                .texture(1f, 1f).next()
            buf.vertex(matrix4f, +hw, -hh, 0F)
                .texture(1f, 0f).next()
            buf.unfixColor()
            BufferRenderer.drawWithGlobalProgram(buf.end())
            matrixStack.pop()
        }
        RenderSystem.setShaderColor(backupColor[0], backupColor[1], backupColor[2], backupColor[3])
    }
}


fun getFontHeight(): Float {
    return MinecraftClient.getInstance().textRenderer.fontHeight.toFloat();
}