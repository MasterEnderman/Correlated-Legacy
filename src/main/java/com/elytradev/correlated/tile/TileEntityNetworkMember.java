package com.elytradev.correlated.tile;

import com.elytradev.correlated.Correlated;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class TileEntityNetworkMember extends TileEntity {
	private TileEntityController controller;
	private Vec3i controllerPos;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (controllerPos != null) {
			compound.setInteger("ControllerOffsetX", controllerPos.getX());
			compound.setInteger("ControllerOffsetY", controllerPos.getY());
			compound.setInteger("ControllerOffsetZ", controllerPos.getZ());
		}
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("ControllerOffsetX")) {
			controllerPos = new Vec3i(
					compound.getInteger("ControllerOffsetX"),
					compound.getInteger("ControllerOffsetY"),
					compound.getInteger("ControllerOffsetZ"));
		}
	}

	public boolean hasStorage() {
		return getStorage() != null;
	}

	public TileEntityController getStorage() {
		if (!hasWorld()) return null;
		if (controller != null && controller.isInvalid()) controller = null;
		if (controller == null && controllerPos != null) {
			BlockPos pos = getPos().add(controllerPos);
			TileEntity te = getWorld().getTileEntity(pos);
			if (te instanceof TileEntityController) {
				controller = (TileEntityController)te;
			} else {
				controllerPos = null;
				if (!(te instanceof TileEntityNetworkImporter)) {
					Correlated.log.debug("The network member at {}, {}, {} failed to find its controller", getPos().getX(), getPos().getY(), getPos().getZ());
				}
			}
		}
		return controller;
	}
	public void setController(TileEntityController controller) {
		if (!hasWorld()) return;
		if (controller == null) {
			controllerPos = null;
		} else {
			controllerPos = controller.getPos().subtract(getPos());
		}
		this.controller = controller;
		if (controller != null) {
			for (EnumFacing ef : EnumFacing.VALUES) {
				TileEntity neighbor = world.getTileEntity(getPos().offset(ef));
				if (neighbor instanceof TileEntityNetworkMember) {
					TileEntityNetworkMember tenm = (TileEntityNetworkMember)neighbor;
					if (!tenm.hasStorage() && this.hasStorage()) {
						tenm.setController(controller);
						controller.updateConsumptionRate(tenm.getEnergyConsumedPerTick());
						controller.onNetworkPatched(tenm);
					}
				}
			}
		}
	}


	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	public abstract long getEnergyConsumedPerTick();

	public void handleNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity te = world.getTileEntity(neighbor);
		if (te instanceof TileEntityNetworkMember) {
			TileEntityNetworkMember tenm = (TileEntityNetworkMember)te;
			if (!tenm.hasStorage() && this.hasStorage()) {
				tenm.setController(this.getStorage());
				getStorage().updateConsumptionRate(tenm.getEnergyConsumedPerTick());
				getStorage().onNetworkPatched(tenm);
			}
		} else {
			if (hasStorage()) {
				if (getStorage().knowsOfMemberAt(neighbor)) {
					getStorage().scanNetwork();
				}
			}
		}
	}

}
