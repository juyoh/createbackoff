package net.juyoh.backoff.block;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.CubePointRange;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UseableVoxelShape extends VoxelShape {
    float minx;
    float miny;
    float minz;

    float maxx;
    float maxy;
    float maxz;

    public UseableVoxelShape(DiscreteVoxelShape shape, float minx, float miny, float minz, float maxx, float maxy, float maxz) {
        super(shape);
        this.minx = minx;
        this.miny = miny;
        this.minz = minz;
        this.maxx = minx;
        this.maxy = miny;
        this.maxz = minz;
    }

    public DoubleList getCoords(Direction.Axis axis) {
        return new CubePointRange(this.shape.getSize(axis));
    }

    public int findIndex(Direction.Axis axis, double position) {
        int i = this.shape.getSize(axis);
        return Mth.floor(Mth.clamp(position * (double)i, (double)-1.0F, (double)i));
    }

    @Override
    public AABB bounds() {
        return new AABB(minx, miny, minz, maxx, maxy, maxz);
    }
}
