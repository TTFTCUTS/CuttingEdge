package ttftcuts.cuttingedge.tacos;

import net.minecraft.util.EnumChatFormatting;

public enum EnumComponentType {
	Shell(EnumChatFormatting.GOLD, false),
	Filling(EnumChatFormatting.GREEN),
	Sauce(EnumChatFormatting.RED),
	;
	
	public final EnumChatFormatting style;
	public final boolean countInTooltip;
	
	private EnumComponentType(EnumChatFormatting style, boolean countInTooltip) {
		this.style = style;
		this.countInTooltip = countInTooltip;
	}
	
	private EnumComponentType(EnumChatFormatting style) {
		this(style, true);
	}
}
