package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import ttftcuts.cuttingedge.util.GUIUtil;
import ttftcuts.cuttingedge.util.TextUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TacoTipHandler {
	
	public static final int displaymult = 1;
	
	public static int dmult(double val) {
		return (int)Math.floor(val * displaymult);
	}
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		KeyBinding sneak = Minecraft.getMinecraft().gameSettings.keyBindSneak;
		boolean sneakpressed = GUIUtil.isKeyDown(sneak.getKeyCode());
		
		if (event.itemStack.getItem() instanceof ItemTaco) {
			TacoData data = TacoData.getData(event.itemStack);
			
			event.toolTip.add(EnumChatFormatting.ITALIC.toString() + StatCollector.translateToLocal("tacos.tacolicious"));
			
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
 				list += t.style + t.display()+EnumChatFormatting.GRAY+":";
				partlists.put(t, list);
			}

			for (TacoComponent comp : parts.keySet()) {
				EnumRarity rarity = comp.displayStack == null ? EnumRarity.common : comp.displayStack.getItem().getRarity(comp.displayStack);
				String itemname = comp.displayStack == null ? "[NULL ITEM]" : comp.displayStack.getDisplayName();
				String tip = rarity.rarityColor + itemname + EnumChatFormatting.GRAY;
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
					double fill = data.filled.containsKey(ct) ? data.filled.get(ct) : 0;
					double capacity = data.container.capacities.containsKey(ct) ? data.container.capacities.get(ct) : 0;
					if (capacity > 0 && ct.countInTooltip) {
						out += EnumChatFormatting.DARK_GRAY + " ("+dmult(fill)+"/"+dmult(capacity)+")";
					}
					
					event.toolTip.addAll(TextUtil.wrapText(out,130));
				}
			}

			if (sneakpressed) {
				event.toolTip.add(EnumChatFormatting.DARK_GRAY+"-----");
				
				Map<TacoFlavour, Double> flavours = new HashMap<TacoFlavour, Double>();
				
				for (TacoComponent c : data.components) {
					for (TacoFlavour f : c.flavours.keySet()) {
						if (!flavours.containsKey(f)) {
							flavours.put(f, 0.0);
						}
						flavours.put(f, flavours.get(f) + c.flavours.get(f));
					}
				}
				
				for (TacoFlavour f : TacoFlavour.values) {
					if (flavours.containsKey(f)) {
						double level = flavours.get(f);// * data.container.size;
						
						event.toolTip.add("- "+f.flavourLevel(level));
					}
				}
				
				event.toolTip.add(EnumChatFormatting.DARK_GRAY+"-----");
				
				String hunger = StatCollector.translateToLocal("tacos.hunger") + ": " + EnumChatFormatting.WHITE;
				double hhams = Math.min(10,data.hunger * 0.5);
				String sat = StatCollector.translateToLocal("tacos.saturation") + ": " + EnumChatFormatting.WHITE;
				double shams = Math.min(10,data.saturation * 0.5);
				for (int i=0; i<Math.floor(hhams); i++) {
					hunger += (char)0x25A0;
				}
				if (hhams % 1 >= 0.5) {
					hunger += EnumChatFormatting.DARK_GRAY.toString() + (char)0x25A0;
				}
				if (data.hunger > 20) {
					hunger += EnumChatFormatting.GRAY + "+ ("+((int)Math.floor(data.hunger*0.5)) + ")";
				}
				if (hhams < 1) {
					hunger += "-";
				}
				
				
				for (int i=0; i<Math.floor(shams); i++) {
					sat += (char)0x25A0;
				}
				if (shams % 1 >= 0.5) {
					sat += EnumChatFormatting.DARK_GRAY.toString() + (char)0x25A0;
				}
				if (data.saturation > 20) {
					sat += EnumChatFormatting.GRAY + "+ ("+((int)Math.floor(data.saturation*0.5)) + ")";
				}
				if (shams < 1) {
					sat += "-";
				}
				
				event.toolTip.add(hunger);
				event.toolTip.add(sat);
				
				if (data.container.size > 1) {
					event.toolTip.add(String.format(StatCollector.translateToLocal("tacos.servings"), data.servings, data.container.size));
					if (event.itemStack.stackSize > 1) {
						event.toolTip.add(EnumChatFormatting.RED+StatCollector.translateToLocal("tacos.stackwarning"));
					}
				}
			} else {
				event.toolTip.add(EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + String.format(StatCollector.translateToLocal("tacos.moreinfo"), Keyboard.getKeyName(sneak.getKeyCode())));
			}
		}
		else {
			boolean heldContainer = (event.entityPlayer != null && event.entityPlayer.getHeldItem() != null && (event.entityPlayer.getHeldItem().getItem() instanceof ItemTaco || TacoContainer.isContainer(event.entityPlayer.getHeldItem())));
			TacoContainer container = TacoContainer.getContainer(event.itemStack);
			TacoComponent component = TacoComponent.getComponent(event.itemStack);
			boolean showstats = (heldContainer || container != null);
			
			if (container != null || (component != null && showstats)) {
				String tip = EnumChatFormatting.ITALIC.toString() + StatCollector.translateToLocal("tacos.tacolicious");
				if (!sneakpressed) {
					tip += " "+EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + String.format(StatCollector.translateToLocal("tacos.moreinfoshort"), Keyboard.getKeyName(sneak.getKeyCode()));
				}
				event.toolTip.add(tip);
			}
			if (sneakpressed) {
				if (container != null) {
					event.toolTip.add(StatCollector.translateToLocal("tacos.canhold"));
					for (EnumComponentType ct : container.capacities.keySet()) {
						if (ct.countInTooltip) {
							event.toolTip.add(ct.style + ct.display() + EnumChatFormatting.GRAY + " x"+dmult(container.capacities.get(ct)));
						}
					}
					
					if (container.size > 1) {
						event.toolTip.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("tacos.servingsshell"));
					}
				}
				
				if (component != null && showstats) {
					if (container != null) {
						event.toolTip.add(EnumChatFormatting.DARK_GRAY+"-----");
					}
					
					double size = component.size * component.getSize(event.itemStack);
					event.toolTip.add(component.type.style + component.type.display() + EnumChatFormatting.GRAY + " "+StatCollector.translateToLocal("tacos.size") + " " + dmult(size)+":");
					
					for (TacoFlavour f : TacoFlavour.values) {
						if (component.flavours.containsKey(f)) {
							double level = component.flavours.get(f);
							
							event.toolTip.add("- "+f.flavourLevel(level));
						}
					}
				}
			}
		}
	}
}
