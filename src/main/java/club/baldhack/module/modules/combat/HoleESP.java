package club.baldhack.module.modules.combat;

import club.baldhack.setting.Setting;
import club.baldhack.setting.Settings;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import club.baldhack.util.MathUtil;
import club.baldhack.util.KamiTessellator;
import club.baldhack.event.events.RenderEvent;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import club.baldhack.module.Module;

@Module.Info(name = "HoleESP", category = Module.Category.COMBAT)
public class HoleESP extends Module
{
    private ArrayList<BlockPos> holes;
    BlockPos pos;
    private Setting<HoleESPMode> mode;
    private Setting<Integer> red;
    private Setting<Integer> green;
    private Setting<Integer> blue;
    private Setting<Integer> alpha;
    private Setting<Integer> range;

    public HoleESP() {
        holes = new ArrayList<BlockPos>();
        mode = register(Settings.e("Mode", HoleESPMode.SOLID));
        red = register((Setting<Integer>)Settings.integerBuilder("Red").withRange(0, 255).withValue(255).build());
        green = register((Setting<Integer>)Settings.integerBuilder("Green").withRange(0, 255).withValue(0).build());
        blue = register((Setting<Integer>)Settings.integerBuilder("Blue").withRange(0, 255).withValue(0).build());
        alpha = register((Setting<Integer>)Settings.integerBuilder("Transparency").withRange(0, 255).withValue(70).build());
        range = register((Setting<Integer>)Settings.integerBuilder("Range").withRange(1, 16).withValue(8).build());
        holes = new ArrayList<BlockPos>();
    }

    @Override
    public void onUpdate() {
        holes = new ArrayList<BlockPos>();
        final Iterable<BlockPos> blocks = (Iterable<BlockPos>)BlockPos.getAllInBox(HoleESP.mc.player.getPosition().add(-range.getValue(), -range.getValue(), -range.getValue()), HoleESP.mc.player.getPosition().add((int)range.getValue(), (int)range.getValue(), (int)range.getValue()));
        for (final BlockPos pos : blocks) {
            if (!HoleESP.mc.world.getBlockState(pos).getMaterial().blocksMovement() && !HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().blocksMovement()) {
                final boolean solidNeighbours = (HoleESP.mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK | HoleESP.mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN) && (HoleESP.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleESP.mc.world.getBlockState(pos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN) && (HoleESP.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK | HoleESP.mc.world.getBlockState(pos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN) && (HoleESP.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK | HoleESP.mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN) && (HoleESP.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK | HoleESP.mc.world.getBlockState(pos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN) && HoleESP.mc.world.getBlockState(pos.add(0, 0, 0)).getMaterial() == Material.AIR && HoleESP.mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial() == Material.AIR && HoleESP.mc.world.getBlockState(pos.add(0, 2, 0)).getMaterial() == Material.AIR;
                if (!solidNeighbours) {
                    continue;
                }
                holes.add(pos);
            }
        }
    }

    @Override
    public void onWorldRender(final RenderEvent event) {
        final IBlockState[] iBlockState3 = new IBlockState[1];
        final Vec3d[] interp3 = new Vec3d[1];
        final IBlockState[] iBlockState4 = new IBlockState[1];
        final Vec3d[] interp4 = new Vec3d[1];
        final IBlockState[] iBlockState5 = new IBlockState[1];
        final Vec3d[] interp5 = new Vec3d[1];
        holes.forEach(blockPos -> {
            switch (mode.getValue()) {
                case SOLID: {
                    KamiTessellator.prepare(7);
                    KamiTessellator.drawBox(blockPos, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue(), 63);
                    KamiTessellator.release();
                    break;
                }
                case SOLIDFLAT: {
                    KamiTessellator.prepare(7);
                    KamiTessellator.drawFace(blockPos, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue(), 63);
                    KamiTessellator.release();
                    break;
                }
                case FULL: {
                    iBlockState3[0] = HoleESP.mc.world.getBlockState(blockPos);
                    interp3[0] = MathUtil.interpolateEntity((Entity)HoleESP.mc.player, HoleESP.mc.getRenderPartialTicks());
                    KamiTessellator.drawFullBox(iBlockState3[0].getSelectedBoundingBox((World)HoleESP.mc.world, blockPos).grow(0.0020000000949949026).offset(-interp3[0].x, -interp3[0].y, -interp3[0].z), blockPos, 1.5f, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
                    break;
                }
                case OUTLINE2: {
                    iBlockState4[0] = HoleESP.mc.world.getBlockState(blockPos);
                    interp4[0] = MathUtil.interpolateEntity((Entity)HoleESP.mc.player, HoleESP.mc.getRenderPartialTicks());
                    KamiTessellator.drawBoundingBox(iBlockState4[0].getSelectedBoundingBox((World)HoleESP.mc.world, blockPos).grow(0.0020000000949949026).offset(-interp4[0].x, -interp4[0].y, -interp4[0].z), 1.5f, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
                    break;
                }
                case OUTLINEFLAT2: {
                    iBlockState5[0] = HoleESP.mc.world.getBlockState(blockPos);
                    interp5[0] = MathUtil.interpolateEntity((Entity)HoleESP.mc.player, HoleESP.mc.getRenderPartialTicks());
                    KamiTessellator.drawBoundingBoxFace(iBlockState5[0].getSelectedBoundingBox((World)HoleESP.mc.world, blockPos).grow(0.0020000000949949026).offset(-interp5[0].x, -interp5[0].y, -interp5[0].z), 1.5f, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
                    break;
                }
                default: {
                    KamiTessellator.prepare(7);
                    KamiTessellator.drawBox(blockPos, red.getValue(), green.getValue(), blue.getValue(), alpha.getValue(), 63);
                    KamiTessellator.release();
                    break;
                }
            }
        });
    }

    private enum HoleESPMode
    {
        SOLID,
        SOLIDFLAT,
        FULL,
        OUTLINE2,
        OUTLINEFLAT2;
    }
}
