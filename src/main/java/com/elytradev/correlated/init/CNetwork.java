package com.elytradev.correlated.init;

import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.correlated.Correlated;
import com.elytradev.correlated.network.CorrelatedGuiHandler;
import com.elytradev.correlated.network.automaton.AutomatonSpeakMessage;
import com.elytradev.correlated.network.automaton.SaveProgramMessage;
import com.elytradev.correlated.network.automaton.SetAutomatonNameMessage;
import com.elytradev.correlated.network.automaton.SetEditorStatusMessage;
import com.elytradev.correlated.network.documentation.AnimationSeenMessage;
import com.elytradev.correlated.network.documentation.OpenDocumentationMessage;
import com.elytradev.correlated.network.dungeon.EnterDungeonMessage;
import com.elytradev.correlated.network.dungeon.LeaveDungeonMessage;
import com.elytradev.correlated.network.fx.AddCaltropMessage;
import com.elytradev.correlated.network.fx.AddGlobeMessage;
import com.elytradev.correlated.network.fx.AddLineMessage;
import com.elytradev.correlated.network.fx.DungeonTransitionMessage;
import com.elytradev.correlated.network.fx.ShowTerminalErrorMessage;
import com.elytradev.correlated.network.fx.StartWeldthrowingMessage;
import com.elytradev.correlated.network.inventory.AddStatusLineMessage;
import com.elytradev.correlated.network.inventory.InsertAllMessage;
import com.elytradev.correlated.network.inventory.RecipeTransferMessage;
import com.elytradev.correlated.network.inventory.SetSearchQueryClientMessage;
import com.elytradev.correlated.network.inventory.SetSearchQueryServerMessage;
import com.elytradev.correlated.network.inventory.SetSlotExtendedMessage;
import com.elytradev.correlated.network.wireless.APNRequestMessage;
import com.elytradev.correlated.network.wireless.APNResponseMessage;
import com.elytradev.correlated.network.wireless.ChangeAPNMessage;
import com.elytradev.correlated.network.wireless.SignalStrengthMessage;
import com.google.common.base.Predicates;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CNetwork {
	public static NetworkContext CONTEXT;
	
	public static void register() {
		CONTEXT = NetworkContext.forChannel("Correlated");
		
		CONTEXT.register(SetSearchQueryClientMessage.class);
		CONTEXT.register(SetSearchQueryServerMessage.class);
		CONTEXT.register(SetSlotExtendedMessage.class);
		CONTEXT.register(StartWeldthrowingMessage.class);
		CONTEXT.register(DungeonTransitionMessage.class);
		CONTEXT.register(EnterDungeonMessage.class);
		CONTEXT.register(SetAutomatonNameMessage.class);
		CONTEXT.register(LeaveDungeonMessage.class);
		CONTEXT.register(AddStatusLineMessage.class);
		CONTEXT.register(AutomatonSpeakMessage.class);
		CONTEXT.register(SetEditorStatusMessage.class);
		CONTEXT.register(SaveProgramMessage.class);
		CONTEXT.register(RecipeTransferMessage.class);
		CONTEXT.register(ShowTerminalErrorMessage.class);
		CONTEXT.register(InsertAllMessage.class);
		CONTEXT.register(ChangeAPNMessage.class);
		CONTEXT.register(SignalStrengthMessage.class);
		CONTEXT.register(APNRequestMessage.class);
		CONTEXT.register(APNResponseMessage.class);
		CONTEXT.register(OpenDocumentationMessage.class);
		CONTEXT.register(AnimationSeenMessage.class);
		
		CONTEXT.register(AddCaltropMessage.class);
		CONTEXT.register(AddGlobeMessage.class);
		CONTEXT.register(AddLineMessage.class);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(Correlated.inst, new CorrelatedGuiHandler());
	}

	public static void sendUpdatePacket(TileEntity te) {
		sendUpdatePacket(te, te.getUpdateTag());
	}

	public static void sendUpdatePacket(TileEntity te, NBTTagCompound nbt) {
		WorldServer ws = (WorldServer)te.getWorld();
		Chunk c = te.getWorld().getChunkFromBlockCoords(te.getPos());
		SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(te.getPos(), te.getBlockMetadata(), nbt);
		for (EntityPlayerMP player : te.getWorld().getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
			if (ws.getPlayerChunkMap().isPlayerWatchingChunk(player, c.xPosition, c.zPosition)) {
				player.connection.sendPacket(packet);
			}
		}
	}
}
