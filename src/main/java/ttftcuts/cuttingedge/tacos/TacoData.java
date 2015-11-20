package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

public class TacoData {
	public TacoContainer container;
	public List<TacoComponent> components;
	
	public TacoData() {
		this.components = new ArrayList<TacoComponent>();
	}
	
	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		
		return tag;
	}
	
	public static TacoData readFromNBT(NBTTagCompound tag) {
		TacoData data = new TacoData();
		
		return data;
	}
}
