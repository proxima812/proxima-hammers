package io.github.proxima812.proximahammers;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.proxima812.proximahammers.HammerTags.HAMMER_NO_SMASHY;

public class HammerItem extends Item {
    private static final float ATTACK_DAMAGE = 1.0F;
    private static final float ATTACK_SPEED = -2.8F;

    private final int depth;
    private final int radius;
    private final float miningSpeed;
    private final float attackDamage;
    private final float attackSpeed;
    private final int enchantmentValue;

    public HammerItem(Item.Properties rootProperties, Tier tier, int radius, int depth, int level) {
        this(rootProperties, tier, radius, depth, level, 1.0F, 1.0F, 1.0F);
    }

    public HammerItem(Item.Properties rootProperties, Tier tier, int radius, int depth, int level, float statMultiplier, float speedMultiplier) {
        this(rootProperties, tier, radius, depth, level, statMultiplier, speedMultiplier, statMultiplier);
    }

    public HammerItem(Item.Properties rootProperties, Tier tier, int radius, int depth, int level, float statMultiplier, float speedMultiplier, float durabilityMultiplier) {
        super(computeProperties(tier, rootProperties, level, statMultiplier, speedMultiplier, durabilityMultiplier));

        this.depth = depth;
        this.radius = radius;
        this.miningSpeed = tier.getSpeed() * speedMultiplier;
        this.attackDamage = (ATTACK_DAMAGE + tier.getAttackDamageBonus()) * statMultiplier;
        this.attackSpeed = ATTACK_SPEED;
        this.enchantmentValue = Math.round(tier.getEnchantmentValue() * statMultiplier);
    }

    private static Item.Properties computeProperties(Tier tier, Item.Properties properties, int level) {
        return computeProperties(tier, properties, level, 1.0F, 1.0F);
    }

    private static Item.Properties computeProperties(Tier tier, Item.Properties properties, int level, float statMultiplier, float speedMultiplier) {
        return computeProperties(tier, properties, level, statMultiplier, speedMultiplier, statMultiplier);
    }

    private static Item.Properties computeProperties(Tier tier, Item.Properties properties, int level, float statMultiplier, float speedMultiplier, float durabilityMultiplier) {
        Tier effectiveTier = wrapMaterial(tier, computeDurability(tier, level, durabilityMultiplier), statMultiplier, speedMultiplier);
        properties.durability(effectiveTier.getUses())
                .component(DataComponents.TOOL, effectiveTier.createToolProperties(BlockTags.MINEABLE_WITH_PICKAXE))
                .attributes(DiggerItem.createAttributes(effectiveTier, ATTACK_DAMAGE * statMultiplier, ATTACK_SPEED));

        if (tier == Tiers.NETHERITE) {
            properties.fireResistant();
        }

        return properties;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag) {
        int damage = Math.max(0, itemStack.getDamageValue());
        int maxDamage = itemStack.getMaxDamage();
        int remainingDurability = Math.max(0, maxDamage - damage);
        int durabilityPercentage = (int) (((float) (maxDamage - damage) / (float) maxDamage) * 100);

        var color = ChatFormatting.GREEN;
        if (durabilityPercentage <= 50) {
            if (durabilityPercentage <= 25) {
                color = ChatFormatting.RED;
            } else {
                color = ChatFormatting.YELLOW;
            }
        }

        components.add(Component.translatable("proximahammers.tooltip.durability", prettyDurability(maxDamage), prettyDurability(remainingDurability))
                .withStyle(color));
        components.add(Component.translatable("proximahammers.tooltip.mining_speed", formatNumber(getEffectiveMiningSpeed(itemStack))).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("proximahammers.tooltip.attack_damage", formatNumber(getEffectiveAttackDamage(itemStack))).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("proximahammers.tooltip.attack_speed", formatNumber(this.attackSpeed)).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("proximahammers.tooltip.area", getEffectiveRadius(itemStack), getEffectiveRadius(itemStack), getEffectiveDepth(itemStack)).withStyle(ChatFormatting.GRAY));
        components.add(Component.translatable("proximahammers.tooltip.enchantment_value", this.enchantmentValue).withStyle(ChatFormatting.DARK_GRAY));

        var modules = HammerModuleData.getModules(itemStack);
        if (!modules.isEmpty()) {
            components.add(Component.translatable("proximahammers.tooltip.modules").withStyle(ChatFormatting.GOLD));
            modules.forEach(module -> components.add(Component.translatable(module.langKey()).withStyle(ChatFormatting.DARK_AQUA)));
        }
    }

    private static String prettyDurability(int durability) {
        String[] units = {"", "k", "m"};
        double displayDurability = durability;

        int unitIndex = durability > 0 ? (int) (Math.log10(durability) / 3) : 0;
        if (unitIndex >= units.length) {
            unitIndex = units.length - 1;
        }

        displayDurability /= Math.pow(1000, unitIndex);

        var output = String.format("%.2f", displayDurability);
        // Remove trailing .00
        if (output.endsWith(".00")) {
            output = output.substring(0, output.length() - 3);
        }

        return output + units[unitIndex];
    }

    public static String formatNumber(float value) {
        if (value == (int) value) {
            return Integer.toString((int) value);
        }

        return String.format("%.1f", value);
    }

    private static int computeDurability(Tier tier, int level) {
        return computeDurability(tier, level, 1.0F);
    }

    private static int computeDurability(Tier tier, int level, float statMultiplier) {
        return Math.round(((int) (tier.getUses() * 2.5F) + (200 * level)) * level * statMultiplier);
    }

    public void causeAoe(Level level, BlockPos pos, BlockState state, ItemStack hammer, LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayer)) return;

        if (level.isClientSide() || state.getDestroySpeed(level, pos) == 0.0F) {
            return;
        }

        if (livingEntity.isShiftKeyDown()) {
            return;
        }

        HitResult pick = livingEntity.pick(20D, 0.0F, false);

        // Not possible?
        if (!(pick instanceof BlockHitResult blockHitResult)) {
            return;
        }

        this.findAndBreakNearBlocks(blockHitResult, pos, hammer, level, livingEntity);
    }

    public void findAndBreakNearBlocks(BlockHitResult pick, BlockPos blockPos, ItemStack hammerStack, Level level, LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayer player)) return;

        Direction direction = pick.getDirection();
        var boundingBox = getAreaOfEffect(blockPos, direction, getEffectiveRadius(hammerStack), getEffectiveDepth(hammerStack));

        int damage = 0;
        Iterator<BlockPos> iterator = BlockPos.betweenClosedStream(boundingBox).iterator();
        Set<BlockPos> removedPos = new HashSet<>();
        while (iterator.hasNext()) {
            var pos = iterator.next();

            // Don't mess with the originally broken block
            if (pick.getBlockPos().equals(pos)) {
                continue;
            }

            BlockState targetState = level.getBlockState(pos);
            if (pos == blockPos || removedPos.contains(pos) || !canDestroy(targetState, level, pos, hammerStack)) {
                continue;
            }

            var toolComponent = hammerStack.get(DataComponents.TOOL);
            if (toolComponent == null) {
                continue;
            }

            var correctForDrops = toolComponent.isCorrectForDrops(targetState);
            if (!correctForDrops || targetState.is(HammerTags.HAMMER_NO_SMASHY)) {
                continue;
            }

            // Throw event out there and let mods block us breaking this block
            var xp = Hammers.XPLAT.getBlockXpAmount(pos, targetState, level, livingEntity, hammerStack);
            var canContinue = Hammers.XPLAT.fireBlockBrokenEvent((ServerLevel) level, pos, targetState, player);
            if (!canContinue) {
                continue;
            }

            if (!player.isCreative()) {
                boolean correctToolForDrops = player.hasCorrectToolForDrops(targetState);
                if (correctToolForDrops) {
                    targetState.spawnAfterBreak((ServerLevel) level, pos, hammerStack, true);
                    List<ItemStack> drops;
                    if (HammerModuleData.has(hammerStack, HammerModule.SILK) && targetState.getBlock().asItem() != Items.AIR) {
                        drops = new java.util.ArrayList<>(List.of(new ItemStack(targetState.getBlock())));
                    } else {
                        drops = Block.getDrops(targetState, (ServerLevel) level, pos, level.getBlockEntity(pos), livingEntity, hammerStack).stream()
                                .map(drop -> transformDrop(drop, hammerStack))
                                .filter(drop -> !shouldVoidDrop(drop, hammerStack))
                                .collect(Collectors.toList());
                    }

                    if (HammerModuleData.has(hammerStack, HammerModule.FORTUNE) && level.getRandom().nextFloat() < 0.22F) {
                        drops.addAll(drops.stream().map(ItemStack::copy).toList());
                    }

                    List<ItemEntity> dropEntities = drops.stream().map(e -> new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), e))
                            .collect(Collectors.toList()); // Ensure it's a mutable list

                    var result = Hammers.XPLAT.fireBlockDropsEvent((ServerLevel) level, pos, targetState, level.getBlockEntity(pos), dropEntities, livingEntity, hammerStack);
                    if (!result) {
                        dropEntities.stream().forEach(entity -> {
                            if (HammerModuleData.has(hammerStack, HammerModule.MAGNET)) {
                                Block.popResource(level, player.blockPosition(), entity.getItem());
                            } else {
                                Block.popResourceFromFace(level, pos, pick.getDirection(), entity.getItem());
                            }
                        });
                    }

                    if (xp != -1 && ((ServerLevel) level).getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
                        ExperienceOrb.award((ServerLevel) level, Vec3.atCenterOf(blockPos), getEffectiveExperience(hammerStack, xp));
                    }
                }
            }

            removedPos.add(pos);
            targetState.getBlock().destroy(level, pos, targetState);
            BlockState newState = Blocks.AIR.defaultBlockState();
            var setResult = level.setBlock(pos, newState, 3);
            if (setResult) {
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(livingEntity, newState));

                // We definitely mined the block. Add the stat and cause food exhaustion
                player.awardStat(Stats.BLOCK_MINED.get(targetState.getBlock()));
                player.causeFoodExhaustion(0.005F);
            }

            damage ++;

            if (HammerModuleData.has(hammerStack, HammerModule.LIGHT)) {
                tryPlaceTorch(player, level, pos);
            }
        }

        if (damage != 0 && !player.isCreative()) {
            int finalDamage = damage;
            if (HammerModuleData.has(hammerStack, HammerModule.DURABILITY)) {
                finalDamage = Math.max(1, Math.round(damage * 0.65F));
            }
            if (HammerModuleData.has(hammerStack, HammerModule.REPAIR) && level.getRandom().nextFloat() < 0.12F) {
                hammerStack.setDamageValue(Math.max(0, hammerStack.getDamageValue() - 1));
            }
            if (!HammerModuleData.has(hammerStack, HammerModule.THOR)) {
                hammerStack.hurtAndBreak(finalDamage, livingEntity, EquipmentSlot.MAINHAND);
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = true;
        if (!HammerModuleData.has(stack, HammerModule.THOR)) {
            result = super.hurtEnemy(stack, target, attacker);
        }
        if (HammerModuleData.has(stack, HammerModule.POWER) && attacker instanceof ServerPlayer player && target.level() instanceof ServerLevel serverLevel) {
            target.hurt(player.damageSources().playerAttack(player), 12.0F);
        }
        return result;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (HammerModuleData.has(stack, HammerModule.THOR)) {
            return true;
        }

        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        float speed = super.getDestroySpeed(itemStack, blockState);
        return speed + HammerModuleData.speedBonus(itemStack);
    }

    private float getEffectiveMiningSpeed(ItemStack stack) {
        return this.miningSpeed + HammerModuleData.speedBonus(stack);
    }

    private float getEffectiveAttackDamage(ItemStack stack) {
        return this.attackDamage + (HammerModuleData.has(stack, HammerModule.POWER) ? 12.0F : 0.0F);
    }

    private static int getEffectiveExperience(ItemStack hammerStack, int experience) {
        if (HammerModuleData.has(hammerStack, HammerModule.KNOWLEDGE)) {
            return experience * 2;
        }

        if (HammerModuleData.has(hammerStack, HammerModule.EXPERIENCE)) {
            return experience + Math.max(1, experience / 2);
        }

        return experience;
    }

    public static BoundingBox getAreaOfEffect(BlockPos blockPos, Direction direction, int radius, int depth) {
        var size = (radius / 2);
        var offset = size - 1;

        return switch (direction) {
            case DOWN, UP -> new BoundingBox(blockPos.getX() - size, blockPos.getY() - (direction == Direction.UP ? depth - 1 : 0), blockPos.getZ() - size, blockPos.getX() + size, blockPos.getY() + (direction == Direction.DOWN ? depth - 1 : 0), blockPos.getZ() + size);
            case NORTH, SOUTH -> new BoundingBox(blockPos.getX() - size, blockPos.getY() - size + offset, blockPos.getZ() - (direction == Direction.SOUTH ? depth - 1 : 0), blockPos.getX() + size, blockPos.getY() + size + offset, blockPos.getZ() + (direction == Direction.NORTH ? depth - 1 : 0));
            case WEST, EAST -> new BoundingBox(blockPos.getX() - (direction == Direction.EAST ? depth - 1 : 0), blockPos.getY() - size + offset, blockPos.getZ() - size, blockPos.getX() + (direction == Direction.WEST ? depth - 1 : 0), blockPos.getY() + size + offset, blockPos.getZ() + size);
        };
    }

    private boolean canDestroy(BlockState targetState, Level level, BlockPos pos, ItemStack hammerStack) {
        if (targetState.getDestroySpeed(level, pos) <= 0) {
            return false;
        }

        if (targetState.is(HAMMER_NO_SMASHY)) {
            return false;
        }

        if (level.getBlockEntity(pos) != null) {
            return false;
        }

        if (HammerModuleData.has(hammerStack, HammerModule.SAFETY)) {
            for (Direction direction : Direction.values()) {
                if (level.getBlockState(pos.relative(direction)).is(Blocks.LAVA)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getDepth() {
        return depth;
    }

    public int getRadius() {
        return radius;
    }

    public int getEffectiveDepth(ItemStack stack) {
        return HammerModuleData.has(stack, HammerModule.EXPANSION) ? depth + 1 : depth;
    }

    public int getEffectiveRadius(ItemStack stack) {
        return HammerModuleData.has(stack, HammerModule.EXPANSION) ? radius + 2 : radius;
    }

    private static ItemStack transformDrop(ItemStack drop, ItemStack hammerStack) {
        if (HammerModuleData.has(hammerStack, HammerModule.SILK)) {
            return drop;
        }

        if (!HammerModuleData.has(hammerStack, HammerModule.SMELTING)) {
            return drop;
        }

        if (drop.is(Items.RAW_IRON)) return new ItemStack(Items.IRON_INGOT, drop.getCount());
        if (drop.is(Items.RAW_GOLD)) return new ItemStack(Items.GOLD_INGOT, drop.getCount());
        if (drop.is(Items.RAW_COPPER)) return new ItemStack(Items.COPPER_INGOT, drop.getCount());
        if (drop.is(Items.COBBLESTONE)) return new ItemStack(Items.STONE, drop.getCount());
        if (drop.is(Items.SAND)) return new ItemStack(Items.GLASS, drop.getCount());
        return drop;
    }

    private static boolean shouldVoidDrop(ItemStack drop, ItemStack hammerStack) {
        return HammerModuleData.has(hammerStack, HammerModule.VOIDING)
                && (drop.is(Items.COBBLESTONE)
                || drop.is(Items.COBBLED_DEEPSLATE)
                || drop.is(Items.DIRT)
                || drop.is(Items.GRAVEL)
                || drop.is(Items.NETHERRACK));
    }

    private static void tryPlaceTorch(ServerPlayer player, Level level, BlockPos minedPos) {
        if (!level.getBlockState(minedPos).isAir() || level.getMaxLocalRawBrightness(minedPos) > 7 || level.getRandom().nextFloat() > 0.12F) {
            return;
        }

        var inventory = player.getInventory();
        for (int index = 0; index < inventory.getContainerSize(); index++) {
            ItemStack stack = inventory.getItem(index);
            if (stack.is(Items.TORCH)) {
                level.setBlock(minedPos, Blocks.TORCH.defaultBlockState(), 3);
                stack.shrink(1);
                return;
            }
        }
    }

    // Fabric only
    public boolean allowContinuingBlockBreaking(Player player, ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    private static Tier wrapMaterial(Tier toolMaterial, int durability, float statMultiplier, float speedMultiplier) {
        return new Tier() {
            @Override
            public int getUses() {
                return durability;
            }

            @Override
            public float getSpeed() {
                return toolMaterial.getSpeed() * speedMultiplier;
            }

            @Override
            public float getAttackDamageBonus() {
                return toolMaterial.getAttackDamageBonus() * statMultiplier;
            }

            @Override
            public net.minecraft.tags.TagKey<Block> getIncorrectBlocksForDrops() {
                return toolMaterial.getIncorrectBlocksForDrops();
            }

            @Override
            public int getEnchantmentValue() {
                return Math.round(toolMaterial.getEnchantmentValue() * statMultiplier);
            }

            @Override
            public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
                return toolMaterial.getRepairIngredient();
            }
        };
    }
}
