package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public enum EnumFlavour {
		Sweet(1.2, 0.7){
			@Override
			protected void relations() {
				this.relate(Salty, 0.5);
				this.relate(Umami, -0.2);
				this.relate(Sour, 0.2);
			}
		},
		Salty(1.2, 0.8){
			@Override
			protected void relations() {
				this.relate(Sweet, -0.1);
			}
		},
		Sour(1.0, 1.0){
			@Override
			protected void relations() {
				this.relate(Sweet, 0.2);
				this.relate(Salty, -0.1);
			}
		},
		Bitter(1.0, 1.0){
			@Override
			protected void relations() {
				this.relate(Sweet, 0.5);
				this.relate(Sour, -0.1);
				this.relate(Salty, -0.25);
			}
		},
		Umami(1.2, 1.4){
			@Override
			protected void relations() {
				this.relate(Sweet, 0.1);
				this.relate(Heat, 0.3);
			}
		},
		Heat(1.2, 1.0){
			@Override
			protected void relations() {
				this.relate(Umami, 0.2);
				this.relate(Sweet, 0.2);
				this.relate(Sour, -0.1);
				this.relate(Fresh, 0.5);
			}
		},
		Fresh(1.0, 0.8){
			@Override
			protected void relations() {
				this.relate(Sweet, -0.25);
				this.relate(Bitter, -0.3);
			}
		},
		Breakfast(1.0, 1.4){
			@Override
			protected void relations() {
				this.relate(Umami, 1.0);
				this.relate(Salty, 0.5);
				this.relate(Sweet, 1.0);
				this.relate(Heat, -1.0);
				this.relate(Fresh, 0.25);
				this.relate(Bitter, -2.0);
				this.relate(Sour, -0.5);
			}
		},
		Dinner(1.1, 1.2){
			@Override
			protected void relations() {
				this.relate(Umami, 2.0);
				this.relate(Salty, 0.5);
				this.relate(Sweet, -1.0);
				this.relate(Heat, 0.5);
				this.relate(Fresh, 0.5);
			}
		},
		Dessert(1.5, 0.8){
			@Override
			protected void relations() {
				this.relate(Sweet, 2.0);
				this.relate(Bitter, -0.5);
				this.relate(Umami, -2.0);
				this.relate(Heat, -1.0);
				this.relate(Salty, -2.0);
			}
		},
	;
	
	private static char[] symbols = {'+','*','#',0x03A9};
	
	private Map<EnumFlavour,Double> relations;
	public final double hungerMult;
	public final double saturationMult;
	public TasteCurve curve = null;
	
	private EnumFlavour(double hunger, double sat) {
		this.relations = new HashMap<EnumFlavour, Double>();
		this.hungerMult = hunger;
		this.saturationMult = sat;
	}
	
	protected void relations() {}
	
	protected void relate(EnumFlavour to, double amount) {
		this.relations.put(to, amount);
	}
	
	public double getRelation(EnumFlavour other) {
		if (this.relations.containsKey(other)) {
			return this.relations.get(other);
		}
		return 0.0;
	}
	
	public static void doRelations() {
		for (EnumFlavour f : EnumFlavour.values()) {
			f.relations();
		}
	}
	
	public double getCurve(double level) {
		TasteCurve c = TasteCurve.basic;
		if (this.curve != null) {
			c = this.curve;
		}
		return c.calc(level);
	}
	
	public static class TasteCurve {
		public final double gain;
		public final double breakpoint;
		public final double decay;
		
		public static final TasteCurve basic = new TasteCurve(1.0,-1,0.0);
		
		public TasteCurve(double gain, double breakpoint, double decay) {
			this.gain = gain;
			this.breakpoint = breakpoint;
			this.decay = decay;
		}
		
		public double calc(double level) {
			if (breakpoint != -1) {
				double pos = Math.max(level, breakpoint);
				double neg = Math.max(0, level - breakpoint);
				
				return pos * gain + neg * decay;
			}
			return level * gain;
		}
	}
	
	public String display() {
		return StatCollector.translateToLocal("tacos.flavtype."+this.name().toLowerCase());
	}
	
	public String flavourLevel(double level) {
		String out = this.display();
		
		EnumChatFormatting colour = EnumChatFormatting.WHITE;
		if (level < 0.3) {
			colour = EnumChatFormatting.DARK_GRAY;
		}
		else if (level < 1.0) {
			colour = EnumChatFormatting.GRAY;
		}
		
		double n = level;
		int symbol = 0;
		while (n / 5.0 > 1) {
			symbol++;
			n /= 5.0;
		}
		symbol = Math.min(symbols.length-1, symbol);
		
		int count = (int)Math.ceil((level / Math.pow(5.0,symbol+1))*5)-1;
		
		for (int i=0; i<count; i++) {
			out += symbols[symbol];
		}
		
		return colour + out;
	}
}
