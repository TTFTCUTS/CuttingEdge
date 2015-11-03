package ttftcuts.cuttingedge.portacart;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PortacartMessage implements IMessage {
	public PortacartMessage() {};
	
	private int x;
	private int y;
	private int z;
	
	public PortacartMessage(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class PortacartMessageHandler implements IMessageHandler<PortacartMessage, IMessage> {
		@Override
		public IMessage onMessage(PortacartMessage message, MessageContext ctx) {
			ItemPortacart.placeCart(ctx.getServerHandler().playerEntity, ctx.getServerHandler().playerEntity.worldObj, message.x, message.y, message.z);
			return null;
		}
	}
}
