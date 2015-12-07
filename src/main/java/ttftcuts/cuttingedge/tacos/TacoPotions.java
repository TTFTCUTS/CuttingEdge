package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TacoPotions {

	public static Map<Potion, PotionCurve> potionCurves = new HashMap<Potion, PotionCurve>();
	
	public static PotionCurve getCurve(Potion potion) {
		if (potionCurves.containsKey(potion)) {
			return potionCurves.get(potion);
		}
		return PotionCurve.basic;
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void potionTips(TacoData data, List tooltip)
    {
        List<PotionEffect> effects = data.potionEffects;
        HashMultimap hashmultimap = HashMultimap.create();
        Iterator iterator1;

        if (effects != null && !effects.isEmpty())
        {
            iterator1 = effects.iterator();

            while (iterator1.hasNext())
            {
                PotionEffect potioneffect = (PotionEffect)iterator1.next();
                String s1 = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                Map map = potion.func_111186_k();

                if (map != null && map.size() > 0)
                {
                    Iterator iterator = map.entrySet().iterator();

                    while (iterator.hasNext())
                    {
                        Entry entry = (Entry)iterator.next();
                        AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), potion.func_111183_a(potioneffect.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        hashmultimap.put(((IAttribute)entry.getKey()).getAttributeUnlocalizedName(), attributemodifier1);
                    }
                }

                if (potioneffect.getAmplifier() > 0)
                {
                    s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                }

                if (potioneffect.getDuration() > 20)
                {
                    s1 = s1 + " (" + Potion.getDurationString(potioneffect) + ")";
                }

                if (potion.isBadEffect())
                {
                    tooltip.add(EnumChatFormatting.RED + s1);
                }
                else
                {
                    tooltip.add(EnumChatFormatting.GRAY + s1);
                }
            }
        }
        else
        {
            String s = StatCollector.translateToLocal("potion.empty").trim();
            tooltip.add(EnumChatFormatting.GRAY + s);
        }

        if (!hashmultimap.isEmpty())
        {
            tooltip.add("");
            tooltip.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
            iterator1 = hashmultimap.entries().iterator();

            while (iterator1.hasNext())
            {
                Entry entry1 = (Entry)iterator1.next();
                AttributeModifier attributemodifier2 = (AttributeModifier)entry1.getValue();
                double d0 = attributemodifier2.getAmount();
                double d1;

                if (attributemodifier2.getOperation() != 1 && attributemodifier2.getOperation() != 2)
                {
                    d1 = attributemodifier2.getAmount();
                }
                else
                {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D)
                {
                    tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted("attribute.modifier.plus." + attributemodifier2.getOperation(), new Object[] {ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry1.getKey())}));
                }
                else if (d0 < 0.0D)
                {
                    d1 *= -1.0D;
                    tooltip.add(EnumChatFormatting.RED + StatCollector.translateToLocalFormatted("attribute.modifier.take." + attributemodifier2.getOperation(), new Object[] {ItemStack.field_111284_a.format(d1), StatCollector.translateToLocal("attribute.name." + (String)entry1.getKey())}));
                }
            }
        }
    }

	public static class PotionCurve {
		public final double durGain;
		public final double levelCost;
		public final double levelAdd;
		public final double levelMult;
		
		public static final PotionCurve basic = new PotionCurve(0, 18 * 200.0, 3.0, -1600.0, 1.0);
		
		public PotionCurve(double threshold, double durationPerPoint, double pointsPerLevel, double durationPerLevel, double durationMult) {
			this.durGain = durationPerPoint;
			this.levelCost = pointsPerLevel;
			this.levelAdd = durationPerLevel;
			this.levelMult = durationMult;
		}
		
		public int getPotionLevel(Potion potion, double level) {
			return (int)Math.max(0, Math.ceil(level / this.levelCost) - 1);
		}
		
		public int getPotionDuration(Potion potion, double level) {
			if (potion.isInstant()) { return 0; }
			int pl = this.getPotionLevel(potion, level);
			return (int)Math.floor((this.durGain * level + pl * this.levelAdd) * this.levelMult * potion.getEffectiveness());
		}
	}
}
