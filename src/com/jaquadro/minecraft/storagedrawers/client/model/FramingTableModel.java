package com.jaquadro.minecraft.storagedrawers.client.model;

import com.jaquadro.minecraft.chameleon.Chameleon;
import com.jaquadro.minecraft.chameleon.model.EnumQuadGroup;
import com.jaquadro.minecraft.chameleon.render.ChamRender;
import com.jaquadro.minecraft.chameleon.resources.IModelRegister;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.model.dynamic.CommonFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IFlexibleBakedModel;

import java.util.ArrayList;
import java.util.List;

public class FramingTableModel implements IFlexibleBakedModel
{
    public static class Register implements IModelRegister
    {
        @Override
        public List<IBlockState> getBlockStates () {
            List<IBlockState> states = new ArrayList<IBlockState>();

            for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                for (Boolean side : new Boolean[] { false, true })
                    states.add(ModBlocks.framingTable.getDefaultState().withProperty(BlockFramingTable.FACING, dir).withProperty(BlockFramingTable.RIGHT_SIDE, side));
            }

            //String key = StorageDrawers.MOD_ID + ":framingTable#inventory";
            //ModelResourceLocation location = new ModelResourceLocation(key);

            //itemResourceLocations.add(location);

            return states;
        }

        @Override
        public List<ResourceLocation> getTextureResources () {
            List<ResourceLocation> locs = new ArrayList<ResourceLocation>();

            locs.add(new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/base/base_oak"));
            locs.add(new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/base/trim_oak"));
            locs.add(new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_worktable_left"));
            locs.add(new ResourceLocation(StorageDrawers.MOD_ID + ":blocks/overlay/shading_worktable_right"));

            return locs;
        }

        @Override
        public IBakedModel getModel (IBlockState state) {
            return new FramingTableModel(state);
        }
    }

    private static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>(0);

    private final CommonFramingRenderer renderer;
    private final IBlockState blockState;

    private final TextureAtlasSprite iconBase;
    private final TextureAtlasSprite iconTrim;
    private final TextureAtlasSprite iconOverlayLeft;
    private final TextureAtlasSprite iconOverlayRight;

    public FramingTableModel (IBlockState state) {
        renderer = new CommonFramingRenderer(ChamRender.instance);
        blockState = state;

        iconBase = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconBaseOak);
        iconTrim = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconTrimOak);
        iconOverlayLeft = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconOverlayFramingTableLeft);
        iconOverlayRight = Chameleon.instance.iconRegistry.getIcon(StorageDrawers.proxy.iconOverlayFramingTableRight);
    }

    @Override
    public VertexFormat getFormat () {
        return DefaultVertexFormats.BLOCK;
    }

    @Override
    public List<BakedQuad> getFaceQuads (EnumFacing facing) {
        if (MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.SOLID && MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.TRANSLUCENT)
            return EMPTY;

        ChamRender.instance.startBaking(getFormat());
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, blockState.getValue(BlockFramingTable.FACING).getIndex());

        if (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.SOLID) {
            if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
                renderer.renderRight(null, blockState, iconBase, iconTrim, EnumQuadGroup.FACE);
            else
                renderer.renderLeft(null, blockState, iconBase, iconTrim, EnumQuadGroup.FACE);
        }
        else if (MinecraftForgeClient.getRenderLayer() == EnumWorldBlockLayer.TRANSLUCENT) {
            if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
                renderer.renderOverlayRight(null, blockState, iconOverlayRight, EnumQuadGroup.FACE);
            else
                renderer.renderOverlayLeft(null, blockState, iconOverlayLeft, EnumQuadGroup.FACE);
        }

        ChamRender.instance.state.clearRotateTransform();
        return ChamRender.instance.stopBaking();
    }

    @Override
    public List<BakedQuad> getGeneralQuads () {
        if (MinecraftForgeClient.getRenderLayer() != EnumWorldBlockLayer.SOLID)
            return EMPTY;

        ChamRender.instance.startBaking(getFormat());
        ChamRender.instance.state.setRotateTransform(ChamRender.ZPOS, blockState.getValue(BlockFramingTable.FACING).getIndex());

        if (blockState.getValue(BlockFramingTable.RIGHT_SIDE))
            renderer.renderRight(null, blockState, iconBase, iconTrim, EnumQuadGroup.GENERAL);
        else
            renderer.renderLeft(null, blockState, iconBase, iconTrim, EnumQuadGroup.GENERAL);

        ChamRender.instance.state.clearRotateTransform();
        return ChamRender.instance.stopBaking();
    }

    @Override
    public boolean isAmbientOcclusion () {
        return true;
    }

    @Override
    public boolean isGui3d () {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer () {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture () {
        return iconBase;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms () {
        return null;
    }
}