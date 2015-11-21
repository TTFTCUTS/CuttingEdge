package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ttftcuts.cuttingedge.util.TextUtil;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TacoTipHandler {
	
	public static final int displaymult = 1;
	
	public static int dmult(double val) {
		return (int)Math.floor(val * displaymult);
	}
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (event.itemStack.getItem() instanceof ItemTaco) {
			TacoData data = TacoData.getData(event.itemStack);
			
			if (data.container == null) {
				return;
			}
			
			Map<TacoComponent, Integer> parts = new HashMap<TacoComponent, Integer>();
			
			for (TacoComponent comp : data.components) {
				if (!parts.containsKey(comp)) {
					parts.put(comp, 1);
				} else {
					parts.put(comp, parts.get(comp)+1);
				}
			}

			Map<EnumComponentType, String> partlists = new HashMap<EnumComponentType, String>();

			for (Entry<EnumComponentType,Double> ct : data.container.capacities.entrySet()) {
				EnumComponentType t = ct.getKey();
				if (!partlists.containsKey(t)) {
					partlists.put(t, "");
				}
				
				String list = partlists.get(t);
				double fill = data.filled.containsKey(t) ? data.filled.get(t) : 0;
				double capacity = data.container.capacities.containsKey(t) ? data.container.capacities.get(t) : 0;
				if (capacity > 0 && t.countInTooltip) {
					list += TextUtil.fraction(fill/capacity).toString()+dmult(fill)+"/"+dmult(capacity)+t.style.toString()+EnumChatFormatting.GRAY +" ";
				}
				list += t.name()+":";
				partlists.put(t, list);
			}

			for (TacoComponent comp : parts.keySet()) {
				String tip = comp.stack.getItem().getRarity(comp.stack).rarityColor + comp.stack.getDisplayName() + EnumChatFormatting.GRAY;
				int amount = parts.get(comp);
				if (amount > 1) {
					tip += " x"+amount;
				}
				
				String list = partlists.get(comp.type);
				list += " " + tip + ",";
				partlists.put(comp.type, list);
			}
			
			for (EnumComponentType ct : EnumComponentType.values()) {
				if (partlists.containsKey(ct)) {
					String out = partlists.get(ct);
					if (out.endsWith(",")) {
						out = out.substring(0, out.length()-1);
					}
					event.toolTip.addAll(TextUtil.wrapText(out,130));
				}
			}
			//event.toolTip.add("------");
		}
	}
}
