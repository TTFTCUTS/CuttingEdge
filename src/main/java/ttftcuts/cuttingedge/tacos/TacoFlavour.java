package ttftcuts.cuttingedge.tacos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TacoFlavour {
	public static List<TacoFlavour> values = new ArrayList<TacoFlavour>();
	
	public static final TacoFlavour sweet = new TacoFlavour("sweet", 1.2, 0.7){
		@Override
		protected void relations() {
			this.relate(salty, 0.5);
			this.relate(umami, -0.2);
			this.relate(sour, 0.2);
		}
	};
	public static final TacoFlavour salty = new TacoFlavour("salty", 1.2, 0.8){
		@Override
		protected void relations() {
			this.relate(sweet, -0.1);
		}
	};
	public static final TacoFlavour sour = new TacoFlavour("sour", 1.0, 1.0){
		@Override
		protected void relations() {
			this.relate(sweet, 0.2);
			this.relate(salty, -0.1);
		}
	};
	public static final TacoFlavour bitter = new TacoFlavour("bitter", 1.0, 1.0){
		@Override
		protected void relations() {
			this.relate(sweet, 0.5);
			this.relate(sour, -0.1);
			this.relate(salty, -0.25);
		}
	};
	public static final TacoFlavour umami = new TacoFlavour("umami", 1.2, 1.4){
		@Override
		protected void relations() {
			this.relate(sweet, 0.1);
			this.relate(heat, 0.3);
		}
	};
	public static final TacoFlavour heat = new TacoFlavour("heat", 1.2, 1.0){
		@Override
		protected void relations() {
			this.relate(umami, 0.2);
			this.relate(sweet, 0.2);
			this.relate(sour, -0.1);
			this.relate(fresh, 0.5);
		}
	};
	public static final TacoFlavour fresh = new TacoFlavour("fresh", 1.0, 0.8){
		@Override
		protected void relations() {
			this.relate(sweet, -0.25);
			this.relate(bitter, -0.3);
		}
	};
	public static final TacoFlavour breakfast = new TacoFlavour("breakfast", 1.0, 1.4){
		@Override
		protected void relations() {
			this.relate(umami, 1.0);
			this.relate(salty, 0.5);
			this.relate(sweet, 1.0);
			this.relate(heat, -1.0);
			this.relate(fresh, 0.25);
			this.relate(bitter, -2.0);
			this.relate(sour, -0.5);
		}
	};
	public static final TacoFlavour dinner = new TacoFlavour("dinner", 1.1, 1.2){
		@Override
		protected void relations() {
			this.relate(umami, 2.0);
			this.relate(salty, 0.5);
			this.relate(sweet, -1.0);
			this.relate(heat, 0.5);
			this.relate(fresh, 0.5);
		}
	};
	public static final TacoFlavour dessert = new TacoFlavour("dessert", 1.5, 0.8){
		@Override
		protected void relations() {
			this.relate(sweet, 2.0);
			this.relate(bitter, -0.5);
			this.relate(umami, -2.0);
			this.relate(heat, -1.0);
			this.relate(salty, -2.0);
		}
	};
	
	public static Map<Potion, TacoFlavour> potionFlavours = new HashMap<Potion, TacoFlavour>();
	
	private static char[] symbols = {'+','*','#',0x03A9};
	
	private Map<TacoFlavour,Double> relations;
	public final String name;
	public final double hungerMult;
	public final double saturationMult;
	public TasteCurve curve = null;
	public Potion potion = null;
	
	public TacoFlavour(String name, double hunger, double sat) {
		this.name = name;
		this.relations = new HashMap<TacoFlavour, Double>();
		this.hungerMult = hunger;
		this.saturationMult = sat;
		values.add(this);
	}
	
	protected void relations() {}
	
	protected void relate(TacoFlavour to, double amount) {
		this.relations.put(to, amount);
	}
	
	protected void setPotion(Potion potion) {
		this.potion = potion;
	}
	
	public double getRelation(TacoFlavour other) {
		if (this.relations.containsKey(other)) {
			return this.relations.get(other);
		}
		return 0.0;
	}
	
	public static void flavourInit() {
		for (Potion p : Potion.potionTypes) {
			if (p == null) { continue; }
			TacoFlavour f = new PotionFlavour("pot_"+p.getName(), 0.0, -0.5);
			f.setPotion(p);
			potionFlavours.put(p, f);
		}
		
		for (TacoFlavour f : TacoFlavour.values) {
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

	public String display() {
		return StatCollector.translateToLocal("tacos.flavtype."+this.name);
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
	
	// #######################################################################
	
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
	
	public static class PotionFlavour extends TacoFlavour {
		public PotionFlavour(String name, double hunger, double sat) {
			super(name, hunger, sat);
		}
		
		@Override
		public String display() {
			return StatCollector.translateToLocal(this.potion.getName());
		}
		
		@Override
		protected void relations() {
			this.relate(breakfast, -2.0);
			this.relate(dinner, -2.0);
			this.relate(dessert, -2.0);
		}
	}
}
