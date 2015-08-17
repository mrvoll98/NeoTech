package com.dyonovan.neotech.common.tiles.storage

import com.dyonovan.neotech.common.blocks.storage.BlockTank
import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids._
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 16, 2015
 */
class TileTank(tier: Int) extends TileEntity with IFluidHandler with UpdatingTile {

    def this() = this(1)

    val tank = new FluidTank(getTierInfo(tier)._2)

    override def onServerTick() : Unit = {
        if(tank.getFluid != null && worldObj.getWorldTime % 20 == 0) {
            worldObj.getTileEntity(pos.offset(EnumFacing.DOWN)) match {
                case otherTank: IFluidHandler =>
                    if (otherTank.canFill(EnumFacing.UP, tank.getFluid.getFluid)) {
                        tank.drain(otherTank.fill(EnumFacing.UP, new FluidStack(tank.getFluid.getFluid,
                            if (tank.getFluidAmount > 1000) 1000 else tank.getFluidAmount), true), true)
                    }
                case _ =>
            }
        }
    }

    def getFluidLevelScaled: Float = {
        Math.min(15.99F, 16 * tank.getFluidAmount / tank.getCapacity)
    }

    def getCurrentFluid: Fluid = {
        if (tank.getFluid != null)
            tank.getFluid.getFluid
        else null
    }

    @SideOnly(Side.CLIENT)
    def getTierIcon: TextureAtlasSprite = {
        tier match {
            case 1 => Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/iron_block")
            case 2 => Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/gold_block")
            case 3 => Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/diamond_block")
            case 4 => Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/emerald_block")
            case _ => Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/iron_block")
        }
    }

    def getBrightness: Int = {
        if (tank.getFluid != null) {
            return (tank.getFluid.getFluid.getLuminosity * tank.getFluidAmount) / tank.getCapacity
        }
        0
    }

    def getTierInfo(tier: Int): (Int, Int) = {
        tier match {
            case 1 => (1, FluidContainerRegistry.BUCKET_VOLUME * 8)
            case 2 => (2, FluidContainerRegistry.BUCKET_VOLUME * 16)
            case 3 => (3, FluidContainerRegistry.BUCKET_VOLUME * 64)
            case 4 => (4, FluidContainerRegistry.BUCKET_VOLUME * 8)
            case _ => (1, FluidContainerRegistry.BUCKET_VOLUME * 8)
        }
    }

    def getTier: Int = {
        tier
    }

    override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean): FluidStack = drain(from, resource, doDrain)

    override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean): FluidStack = {
        val fluidAmount = tank.drain(maxDrain, false)
        if (fluidAmount != null && doDrain) {
            tank.drain(maxDrain, true)
            worldObj.markBlockForUpdate(pos)
        }
        fluidAmount
    }

    override def canFill(from: EnumFacing, fluid: Fluid): Boolean = {
        tank.getFluid == null || tank.getFluid.getFluid == fluid
    }

    override def canDrain(from: EnumFacing, fluid: Fluid): Boolean = {
        tank.getFluid != null || tank.getFluid.getFluid == fluid
    }

    override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
        if (canFill(from, resource.getFluid)) {
            if (tank.fill(resource, false) > 0) {
                val actual = tank.fill(resource, doFill)
                worldObj.markBlockForUpdate(pos)
                return actual
            } else return fillAbove(from, resource, doFill)
        }
        0
    }

    def fillAbove(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
        val newPos = pos.offset(EnumFacing.UP)
        while(!worldObj.isAirBlock(newPos) && worldObj.getBlockState(newPos).getBlock.isInstanceOf[BlockTank])
            return worldObj.getTileEntity(newPos).asInstanceOf[TileTank].fill(from, resource, doFill)
        0
    }

    override def getTankInfo(from: EnumFacing): Array[FluidTankInfo] = Array(tank.getInfo)

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super.writeToNBT(tag)
        tank.writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super.readFromNBT(tag)
        tank.readFromNBT(tag)
        if (worldObj != null)
            worldObj.markBlockRangeForRenderUpdate(pos, pos)
    }
}