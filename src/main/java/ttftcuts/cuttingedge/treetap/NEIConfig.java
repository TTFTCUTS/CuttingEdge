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
		EvaporationHandler evap = new EvaporationHandler();
		API.registerRecipeHandler(evap);
		API.registerUsageHandler(evap);
	}

}
