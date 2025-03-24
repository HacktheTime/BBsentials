package de.hype.bingonet.fabric
// Credits go to nea89 for this (Firmanent)! Just slightly adapted by me

import com.mojang.blaze3d.systems.RenderSystem
import de.hype.bingonet.fabric.objects.WorldRenderLastEvent
import de.hype.bingonet.shared.objects.RenderInformation
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gl.Defines
import net.minecraft.client.gl.ShaderProgramKey
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
import java.awt.Color
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
    object RenderLayers {
        val TRANSLUCENT_TRIS = RenderLayer.of(
            "bingonet_translucent_tris",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.TRIANGLES,
            RenderLayer.CUTOUT_BUFFER_SIZE,
            false, true,
            RenderLayer.MultiPhaseParameters.builder()
                .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .program(RenderPhase.POSITION_COLOR_PROGRAM)
                .build(false)
        )
        val LINES = RenderLayer.of(
            "bingonet_rendertype_lines",
            VertexFormats.LINES,
            VertexFormat.DrawMode.LINES,
            RenderLayer.CUTOUT_BUFFER_SIZE,
            false, false, // do we need translucent? i dont think so
            RenderLayer.MultiPhaseParameters.builder()
                .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                .program(FirmamentShaders.LINES)
                .build(false)
        )
        val COLORED_QUADS = RenderLayer.of(
            "bingonet_quads",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.QUADS,
            RenderLayer.CUTOUT_BUFFER_SIZE,
            false, true,
            RenderLayer.MultiPhaseParameters.builder()
                .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
                .program(RenderPhase.POSITION_COLOR_PROGRAM)
                .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
                .build(false)
        )
    }

    fun block(blockPos: BlockPos, color: Int) {
        matrixStack.push()
        matrixStack.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
        buildCube(matrixStack.peek().positionMatrix, vertexConsumers.getBuffer(RenderLayers.COLORED_QUADS), color)
        matrixStack.pop()
    }

    fun block(blockPos: BlockPos, color: Color) {
        block(blockPos, color.rgb)
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

    fun waypoint(position: BlockPos, vararg label: Text) {
        text(
            position.toCenterPos(),
            *label,
            Text.literal("§e${formatDistance(MinecraftClient.getInstance().player?.pos?.distanceTo(position.toCenterPos()) ?: 42069.0)}"),
            background = 0xAA202020.toInt()
        )
    }

    fun waypoint(position: BlockPos, color: Color, vararg label: Text) {
        text(
            position.toCenterPos(),
            *label,
            Text.literal("§e${formatDistance(MinecraftClient.getInstance().player?.pos?.distanceTo(position.toCenterPos()) ?: 42069.0)}"),
            background = color.rgb
        )
    }


    fun doWaypointIcon(position: Vec3d, textures: List<RenderInformation>, width: Int, height: Int) {
        renderTextures(
            position,
            textures.map { it.texturePath?.let { path -> Identifier.of(path) } },
            width,
            height,
            0.1f
        )
    }

    fun renderTextures(position: Vec3d, textures: List<Identifier?>, width: Int, height: Int, padding: Float) {
        if (textures.isEmpty()) return
        val totalWidth = width * textures.size + padding * (textures.size - 1)
        val left = -totalWidth / 2
        for ((index, texture) in textures.withIndex()) {
            if (texture == null || texture.path.isEmpty()) continue
            texture(
                position.add(left + index * (width + padding).toDouble(), 0.0, 0.0),
                texture, width, height
            )
        }
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
        matrixStack.scale(0.025F, -0.025F, 1F)

        FacingThePlayerContext(this).run(block)

        matrixStack.pop()
        vertexConsumers.drawCurrentLayer()
    }

    fun sprite(position: Vec3d, sprite: Sprite, width: Int, height: Int) {
        texture(
            position, sprite.atlasId, width, height, sprite.minU, sprite.minV, sprite.maxU, sprite.maxV
        )
    }

    fun texture(position: Vec3d, texture: Identifier, width: Int, height: Int) {
        texture(position, texture, width, height, 0.0f, 0.0f, 1.0f, 1.0f)
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


    fun text(
        position: Vec3d,
        vararg texts: Text,
        verticalAlign: VerticalAlign = VerticalAlign.CENTER,
        background: Int = 0x70808080
    ) {
        withFacingThePlayer(position) {
            text(*texts, verticalAlign = verticalAlign)
        }
    }

    fun tinyBlock(vec3d: Vec3d, size: Float, color: Int) {
        matrixStack.push()
        matrixStack.translate(vec3d.x, vec3d.y, vec3d.z)
        matrixStack.scale(size, size, size)
        matrixStack.translate(-.5, -.5, -.5)
        buildCube(matrixStack.peek().positionMatrix, vertexConsumers.getBuffer(RenderLayers.COLORED_QUADS), color)
        matrixStack.pop()
        vertexConsumers.draw()
    }

    fun wireframeCube(blockPos: BlockPos, lineWidth: Float = 10F) {
        val buf = vertexConsumers.getBuffer(RenderLayer.LINES)
        matrixStack.push()
        // TODO: this does not render through blocks (or water layers) anymore
        RenderSystem.lineWidth(lineWidth / pow(camera.pos.squaredDistanceTo(blockPos.toCenterPos()), 0.25).toFloat())
        matrixStack.translate(blockPos.x.toFloat(), blockPos.y.toFloat(), blockPos.z.toFloat())
        buildWireFrameCube(matrixStack.peek(), buf)
        matrixStack.pop()
        vertexConsumers.draw()
    }

    fun line(vararg points: Vec3d, lineWidth: Float = 10F) {
        line(points.toList(), lineWidth)
    }

    fun tracer(toWhere: Vec3d, lineWidth: Float = 3f) {
        val cameraForward = Vector3f(0f, 0f, -1f).rotate(camera.rotation)
        line(camera.pos.add(Vec3d(cameraForward)), toWhere, lineWidth = lineWidth)
    }

    fun line(points: List<Vec3d>, lineWidth: Float = 10F) {
        RenderSystem.lineWidth(lineWidth)
        // TODO: replace with renderlayers
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

        RenderLayers.LINES.draw(buffer.end())
    }
    // TODO: put the favourite icons in front of items again

    companion object {
        private fun doLine(
            matrix: MatrixStack.Entry,
            buf: VertexConsumer,
            i: Float,
            j: Float,
            k: Float,
            x: Float,
            y: Float,
            z: Float
        ) {
            val normal = Vector3f(x, y, z).sub(i, j, k).normalize()
            buf.vertex(matrix.positionMatrix, i, j, k).normal(matrix, normal.x, normal.y, normal.z).color(-1).next()
            buf.vertex(matrix.positionMatrix, x, y, z).normal(matrix, normal.x, normal.y, normal.z).color(-1).next()
        }


        private fun buildWireFrameCube(matrix: MatrixStack.Entry, buf: VertexConsumer) {
            for (i in 0..1) {
                for (j in 0..1) {
                    val i = i.toFloat()
                    val j = j.toFloat()
                    doLine(matrix, buf, 0F, i, j, 1F, i, j)
                    doLine(matrix, buf, i, 0F, j, i, 1F, j)
                    doLine(matrix, buf, i, j, 0F, i, j, 1F)
                }
            }
        }

        private fun buildCube(matrix: Matrix4f, buf: VertexConsumer, color: Int) {
            // Y-
            buf.vertex(matrix, 0F, 0F, 0F).color(color)
            buf.vertex(matrix, 0F, 0F, 1F).color(color)
            buf.vertex(matrix, 1F, 0F, 1F).color(color)
            buf.vertex(matrix, 1F, 0F, 0F).color(color)
            // Y+
            buf.vertex(matrix, 0F, 1F, 0F).color(color)
            buf.vertex(matrix, 1F, 1F, 0F).color(color)
            buf.vertex(matrix, 1F, 1F, 1F).color(color)
            buf.vertex(matrix, 0F, 1F, 1F).color(color)
            // X-
            buf.vertex(matrix, 0F, 0F, 0F).color(color)
            buf.vertex(matrix, 0F, 0F, 1F).color(color)
            buf.vertex(matrix, 0F, 1F, 1F).color(color)
            buf.vertex(matrix, 0F, 1F, 0F).color(color)
            // X+
            buf.vertex(matrix, 1F, 0F, 0F).color(color)
            buf.vertex(matrix, 1F, 1F, 0F).color(color)
            buf.vertex(matrix, 1F, 1F, 1F).color(color)
            buf.vertex(matrix, 1F, 0F, 1F).color(color)
            // Z-
            buf.vertex(matrix, 0F, 0F, 0F).color(color)
            buf.vertex(matrix, 1F, 0F, 0F).color(color)
            buf.vertex(matrix, 1F, 1F, 0F).color(color)
            buf.vertex(matrix, 0F, 1F, 0F).color(color)
            // Z+
            buf.vertex(matrix, 0F, 0F, 1F).color(color)
            buf.vertex(matrix, 0F, 1F, 1F).color(color)
            buf.vertex(matrix, 1F, 1F, 1F).color(color)
            buf.vertex(matrix, 1F, 0F, 1F).color(color)
        }


        fun renderInWorld(event: WorldRenderLastEvent, block: RenderInWorldContext. () -> Unit) {
            // TODO: there should be *no more global state*. the only thing we should be doing is render layers. that includes settings like culling, blending, shader color, and depth testing
            // For now i will let these functions remain, but this needs to go before i do a full (non-beta) release
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
            event.vertexConsumers.draw()
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F)
            VertexBuffer.unbind()
            RenderSystem.enableDepthTest()
            RenderSystem.enableCull()
            RenderSystem.disableBlend()
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
        val buf = worldContext.vertexConsumers.getBuffer(RenderLayer.getGuiTexturedOverlay(texture))
        val hw = width / 2F
        val hh = height / 2F
        val matrix4f: Matrix4f = worldContext.matrixStack.peek().positionMatrix
        buf.vertex(matrix4f, -hw, -hh, 0F)
            .color(-1)
            .texture(u1, v1).next()
        buf.vertex(matrix4f, -hw, +hh, 0F)
            .color(-1)
            .texture(u1, v2).next()
        buf.vertex(matrix4f, +hw, +hh, 0F)
            .color(-1)
            .texture(u2, v2).next()
        buf.vertex(matrix4f, +hw, -hh, 0F)
            .color(-1)
            .texture(u2, v1).next()
        worldContext.vertexConsumers.draw()
    }

}

fun VertexConsumer.next() = this


fun getFontHeight(): Float {
    return MinecraftClient.getInstance().textRenderer.fontHeight.toFloat()
}


object FirmamentShaders {
    val shaders = mutableListOf<ShaderProgramKey>()

    private fun shader(name: String, format: VertexFormat, defines: Defines): ShaderProgramKey {
        val key = ShaderProgramKey(Identifier.of("bingonet", name), format, defines)
        shaders.add(key)
        return key
    }

    val LINES = RenderPhase.ShaderProgram(shader("core/rendertype_lines", VertexFormats.LINES, Defines.EMPTY))
}