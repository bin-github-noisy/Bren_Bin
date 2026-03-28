package nl.sniffiandros.bren.common.registry.custom.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WorkbenchBlock extends Block {

    public VoxelShape shape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(0, 0, 0, 1, 0.875, 1));

        return shape;
    }

    public WorkbenchBlock(Properties settings) {
        super(settings.noOcclusion());
    }


}
