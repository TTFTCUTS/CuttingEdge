package ttftcuts.cuttingedge.tacos;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

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
	
	public String display() {
		return StatCollector.translateToLocal("tacos.comptype."+this.name().toLowerCase());
	}
}
