package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ttftcuts.cuttingedge.CuttingEdge;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TacoData {
	public TacoContainer container;
	public List<TacoComponent> components;
	public static final String TACOTAG = "taco";
	public Map<EnumComponentType, Double> filled;
	public double hunger = 0;
	public double saturation = 0;
	public int servings = 0;
	public List<TacoComponent> iconComponents = new ArrayList<TacoComponent>();
	
	public TacoData() {
		this.components = new ArrayList<TacoComponent>();
		this.filled = new HashMap<EnumComponentType, Double>();
	}
	
	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		
		if (this.container != null) {
			tag.setString("s", container.name);
		}
		
		NBTTagList list = new NBTTagList();
		for (TacoComponent comp : components) {
			NBTTagCompound part = new NBTTagCompound();
			part.setString("id", comp.name);
			list.appendTag(part);
		}
		tag.setTag("p", list);
		
		tag.setDouble("hng", hunger);
		tag.setDouble("sat", saturation);
		tag.setInteger("srv", servings);
		
		NBTTagList rlist = new NBTTagList();
		for (TacoComponent comp : iconComponents) {
			NBTTagCompound part = new NBTTagCompound();
			part.setString("id", comp.name);
			rlist.appendTag(part);
		}
		tag.setTag("r", rlist);
		
		return tag;
	}
	
	public static TacoData readFromNBT(NBTTagCompound tag) {
		TacoData data = new TacoData();
		
		String cont = tag.getString("s");
		if (ModuleTacos.containers.containsKey(cont)) {
			data.setContainer(ModuleTacos.containers.get(cont));
		}
		
		NBTTagList list = tag.getTagList("p", 10);
		for (int i=0; i<list.tagCount(); i++) {
			NBTTagCompound part = list.getCompoundTagAt(i);
			String id = part.getString("id");
			if (ModuleTacos.components.containsKey(id)) {
				data.addComponent(ModuleTacos.components.get(id));
			}
		}
		
		data.hunger = tag.getDouble("hng");
		data.saturation = tag.getDouble("sat");
		data.servings = tag.getInteger("srv");
		
		data.iconComponents = new ArrayList<TacoComponent>();
		NBTTagList rlist = tag.getTagList("r", 10);
		for (int i=0; i<rlist.tagCount(); i++) {
			NBTTagCompound part = rlist.getCompoundTagAt(i);
			String id = part.getString("id");
			data.iconComponents.add(ModuleTacos.components.get(id));
		}
		
		return data;
	}
	
	public boolean addComponent(TacoComponent comp) {
		if (!this.filled.containsKey(comp.type)) {
			this.filled.put(comp.type, 0.0);
		}
		double fill = this.filled.get(comp.type); 
		double capacity = this.container != null ? this.container.capacities.get(comp.type) : 0;
		if (fill + comp.size > capacity) {
			return false;
		}
		this.components.add(comp);
		this.filled.put(comp.type, fill + comp.size);
		return true;
	}
	
	public void setContainer(TacoContainer container) {
		this.container = container;
		this.servings = container.size;
	}
	
	public static TacoData getData(ItemStack stack) {
		if (!(stack.getItem() instanceof ItemTaco)) {
			return null;
		}
		
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if (!stack.getTagCompound().hasKey(TACOTAG)) {
			TacoData data = new TacoData();
			stack.getTagCompound().setTag(TACOTAG, data.writeToNBT());
			return data;
		} else {
			return readFromNBT(stack.getTagCompound().getCompoundTag(TACOTAG));
		}
	}
	
	public static void setData(ItemStack stack, TacoData data) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		stack.getTagCompound().setTag(TACOTAG, data.writeToNBT());
	}
	
	public void calculateDataValues() {
		this.hunger = 0;
		this.saturation = 0;
		
		if (this.container == null) { return; }
		
		Collections.sort(this.components, TacoComponent.renderSorter);
		
		Map<TacoFlavour, Double> flavours = new HashMap<TacoFlavour,Double>();
		Set<TacoComponent> doublecheck = new HashSet<TacoComponent>();
		Map<EnumComponentType, List<TacoComponent>> renderparts = new HashMap<EnumComponentType, List<TacoComponent>>();
		for (EnumComponentType t : EnumComponentType.values()) {
			renderparts.put(t, new ArrayList<TacoComponent>());
		}
		
		for (TacoComponent c : this.components) {
			for (TacoFlavour f : c.flavours.keySet()) {
				if (!flavours.containsKey(f)) {
					flavours.put(f, 0.0);
				}
				flavours.put(f, flavours.get(f) + (c.flavours.get(f) / (double)this.container.size));
			}
			
			if (!doublecheck.contains(c)) {
				doublecheck.add(c);
				renderparts.get(c.type).add(c);
			}
		}
		
		// calc hunger
		for (TacoFlavour f : flavours.keySet()) {
			double af = flavours.get(f);
			this.hunger += f.getCurve(af) * f.hungerMult;
			this.saturation += f.getCurve(af) * f.saturationMult;
			for (TacoFlavour r : flavours.keySet()) {
				if (f == r) { continue; }
				double rel = f.getRelation(r);
				if (rel > 0) {
					double n = af * rel;
					double h = n * f.hungerMult;
					double s = n * f.saturationMult;
					this.hunger += h;
					this.saturation += s;
				}
			}
		}
		
		// calc draw list
		int rc = EnumComponentType.rendercount;
		int spare = 0;
		for (EnumComponentType t : renderparts.keySet()) {
			int l = renderparts.get(t).size();
			spare += Math.max(0, l-rc);
		}
		
		this.iconComponents = new ArrayList<TacoComponent>();
		
		for (EnumComponentType t : EnumComponentType.values()) {
			List<TacoComponent> parts = renderparts.get(t);
			if (parts.size() > rc) {
				int extra = Math.min(spare, parts.size() - rc);
				for (int i=0; i<rc + extra; i++) {
					this.iconComponents.add(parts.get(i));
				}
				spare -= extra;
			} else {
				this.iconComponents.addAll(parts);
			}
		}
	}
}
