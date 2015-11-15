package ttftcuts.cuttingedge.treetap;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI {

	@Override
	public String getName() {
		return "Cutting Edge NEI: Treetap";
	}

	@Override
	public String getVersion() {
		return "0.1.0";
	}

	@Override
	public void loadConfig() {
		EvaporationNEIHandler evap = new EvaporationNEIHandler();
		API.registerRecipeHandler(evap);
		API.registerUsageHandler(evap);
		
		TappingNEIHandler tap = new TappingNEIHandler();
		API.registerRecipeHandler(tap);
		API.registerUsageHandler(tap);
	}

}
