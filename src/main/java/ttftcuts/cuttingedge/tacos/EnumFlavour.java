package ttftcuts.cuttingedge.tacos;

import java.util.HashMap;
import java.util.Map;

public enum EnumFlavour {
		Sweet{
			@Override
			protected void relations() {
				this.relate(Salty, 0.5);
				this.relate(Umami, -0.2);
				this.relate(Sour, 0.2);
			}
		},
		Salty{
			@Override
			protected void relations() {
				this.relate(Sweet, -0.1);
			}
		},
		Sour{
			@Override
			protected void relations() {
				this.relate(Sweet, 0.2);
				this.relate(Salty, -0.1);
			}
		},
		Bitter{
			@Override
			protected void relations() {
				this.relate(Sweet, 0.5);
				this.relate(Sour, -0.1);
				this.relate(Salty, -0.25);
			}
		},
		Umami{
			@Override
			protected void relations() {
				this.relate(Sweet, 0.1);
				this.relate(Heat, 0.3);
			}
		},
		Heat{
			@Override
			protected void relations() {
				this.relate(Umami, 0.2);
				this.relate(Sweet, 0.2);
				this.relate(Sour, -0.1);
				this.relate(Fresh, 0.5);
			}
		},
		Fresh{
			@Override
			protected void relations() {
				this.relate(Sweet, -0.25);
				this.relate(Bitter, -0.3);
			}
		},
		Breakfast{
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
		Dinner{
			@Override
			protected void relations() {
				this.relate(Umami, 2.0);
				this.relate(Salty, 0.5);
				this.relate(Sweet, -1.0);
				this.relate(Heat, 0.5);
				this.relate(Fresh, 0.5);
			}
		},
		Dessert{
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
	
	private Map<EnumFlavour,Double> relations;
	
	private EnumFlavour() {
		this.relations = new HashMap<EnumFlavour, Double>();
	}
	
	protected void relations() {}
	
	protected void relate(EnumFlavour to, double amount) {
		this.relations.put(to, amount);
	}
	
	public static void doRelations() {
		for (EnumFlavour f : EnumFlavour.values()) {
			f.relations();
		}
	}
	
	public static class TasteCurve {
		public final double gain;
		public final int breakpoint;
		public final double decay;
		
		public TasteCurve(double gain, int breakpoint, double decay) {
			this.gain = gain;
			this.breakpoint = breakpoint;
			this.decay = decay;
		}
		
		public double calc(int level) {
			int pos = Math.max(level, breakpoint);
			int neg = Math.max(0, level - breakpoint);
			
			return pos * gain + neg * decay;
		}
	}
}
