package ttftcuts.cuttingedge.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TextUtil {
	public static EnumChatFormatting fraction(double n) {
		if (n < 0.1) {
			return EnumChatFormatting.DARK_RED;
		} else if (n < 0.33) {
			return EnumChatFormatting.RED;
		} else if (n < 0.66) {
			return EnumChatFormatting.GOLD;
		} else if (n < 0.9) {
			return EnumChatFormatting.YELLOW;
		}
		return EnumChatFormatting.GREEN;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> wrapText(String input, int width) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		if (input == null) {
			return new ArrayList<String>();
		}
		List<String> output = fr.listFormattedStringToWidth(input, width);

		return output;
	}
	
	public static List<String> translateAndWrap(String input, int width) {
		return wrapText(StatCollector.translateToLocal(input), width);
	}
}
