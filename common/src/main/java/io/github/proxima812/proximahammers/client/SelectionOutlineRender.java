package io.github.proxima812.proximahammers.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import io.github.proxima812.proximahammers.HammerItem;
import io.github.proxima812.proximahammers.HammerTags;

import java.util.Iterator;

public class SelectionOutlineRender {
    private static final int DIG_OUTLINE_COLOR = 0x88FFFFFF;
    private static final float DIG_OUTLINE_WIDTH = 2.5f;

    public static void render(ClientLevel world, Camera camera, PoseStack poseStack, MultiBufferSource consumers) {
        // Get the player
        if (world == null) {
            return;
        }

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (player.isShiftKeyDown()) {
            return;
        }

        // Get the player's held item
        var heldItem = player.getMainHandItem();
        var offHandItem = player.getOffhandItem();
        if (heldItem.isEmpty() && offHandItem.isEmpty()) {
            return;
        }

        // Is the held item a hammer?
        if (!(heldItem.getItem() instanceof HammerItem) && !(offHandItem.getItem() instanceof HammerItem)) {
            return;
        }

        // Raytrace to get the block we're looking at
        var blockHitResult = Minecraft.getInstance().hitResult;
        if (blockHitResult == null || blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        var item = heldItem.getItem() instanceof HammerItem ? heldItem.getItem() : offHandItem.getItem();
        var itemStack = heldItem.getItem() instanceof HammerItem ? heldItem : offHandItem;

        var hammer = (HammerItem) item;

        // Get the block's position
        var blockPos = ((BlockHitResult) blockHitResult).getBlockPos();
        var direction = ((BlockHitResult) blockHitResult).getDirection();

        // Get the block at the pos
        var block = world.getBlockState(blockPos);

        var toolComponent = itemStack.get(DataComponents.TOOL);
        if (toolComponent == null) {
            return;
        }

        var correctForDrops = toolComponent.isCorrectForDrops(block);
        if (!correctForDrops || block.is(HammerTags.HAMMER_NO_SMASHY)) {
            return;
        }

        var boundingBox = HammerItem.getAreaOfEffect(blockPos, direction, hammer.getEffectiveRadius(itemStack), hammer.getEffectiveDepth(itemStack));

        // Transform the pose stack to the camera's position
        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x(), -camera.getPosition().y(), -camera.getPosition().z());

        // Render the outline
        Iterator<BlockPos> blockPosStream = BlockPos.betweenClosedStream(boundingBox).iterator();
        while (blockPosStream.hasNext()) {
            BlockPos pos = blockPosStream.next();
            if (pos.equals(blockPos)) {
                continue;
            }

            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = blockState.getFluidState();
            if (blockState.isAir() || (!fluidState.isEmpty())) {
                continue;
            }

            // Get the shame of the block
            VoxelShape renderShape = blockState.getVisualShape(world, pos, CollisionContext.empty());

            poseStack.pushPose();
            // Shift the pose stack to the block's position
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

            var vertexConsumer = consumers.getBuffer(RenderType.lines());
            renderShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    LevelRenderer.renderLineBox(poseStack, vertexConsumer, minX, minY, minZ, maxX, maxY, maxZ,
                            1.0F, 1.0F, 1.0F, 0.53F));
            poseStack.popPose();
        }

        // Pop the pose stack
        poseStack.popPose();
    }
}
