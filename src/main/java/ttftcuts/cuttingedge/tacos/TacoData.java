package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class TacoData {
	public TacoContainer container;
	public List<TacoComponent> components;
	public static final String TACOTAG = "taco";
	public Map<EnumComponentType, Double> filled;
	
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
		
		return tag;
	}
	
	public static TacoData readFromNBT(NBTTagCompound tag) {
		TacoData data = new TacoData();
		
		String cont = tag.getString("s");
		if (ModuleTacos.containers.containsKey(cont)) {
			data.container = ModuleTacos.containers.get(cont);
		}
		
		NBTTagList list = tag.getTagList("p", 10);
		for (int i=0; i<list.tagCount(); i++) {
			NBTTagCompound part = list.getCompoundTagAt(i);
			String id = part.getString("id");
			if (ModuleTacos.components.containsKey(id)) {
				data.addComponent(ModuleTacos.components.get(id));
			}
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
}
