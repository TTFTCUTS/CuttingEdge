package ttftcuts.cuttingedge.util;

import ttftcuts.cuttingedge.CuttingEdge;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class NetworkUtil {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(CuttingEdge.MOD_ID);
	
	private static int id = 0;
	public static int getNextPacketId() {
		return id++;
	}
}
