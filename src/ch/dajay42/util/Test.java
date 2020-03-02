package ch.dajay42.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.IntStream;

import ch.dajay42.application.*;
import ch.dajay42.collections.*;
import ch.dajay42.math.TernaryUnit;
import ch.dajay42.math.Util;
import ch.dajay42.math.linAlg.Matrix;


public class Test {

	public static void main(String[] args) {
		//lazyMatrixTest();
		//testParallelSetAll();
		//testSpeed(1000);
		//testGUP();
		//testRTS();
		//testRTS2();
		//modTest();
		//arrayClearTest();
		testTernary();
	}
	
	public static void testSpeed(int n){
		double[] in = new double[n];
		double[] out1 = new double[n];
		double[] out2 = new double[n];
		
		fill(in, ThreadLocalRandom.current()::nextDouble);
		
		long tick = System.nanoTime();
		
		arrayMap((a)->1/(a*a*a), in, out1);
		
		long tock = System.nanoTime();
		
		arrayMap((a)->Math.pow(a, -3), in, out2);
		
		long tack = System.nanoTime();
		
		System.out.println(tock - tick);
		System.out.println(tack - tock);
	}
	
	/**Fills the provided {@code array} by calling {@code source} once for every element.
	 * @param array array to be filled
	 * @param source Method that generates elements.
	 */
	public static void fill(double[] array,  DoubleSupplier source){
		Arrays.setAll(array, __ -> source.getAsDouble());
	}
	/**Fills array {@code out by applying the function {@code f} to the corresponding element of {@code in}.<br/>
	 * If {@code in.length > out.length}, the additional elements are ignored.
	 * If {@code out.length > in.length}, the additional elements are filled with {@code null}.
	 * @param f
	 * @param in
	 * @param out
	 * @return
	 */
	public static void arrayMap(DoubleUnaryOperator f, double[] in, double[] out){
		Arrays.setAll(out, e -> f.applyAsDouble(in[e]));
	}
	
	public static void testSCLI(String[] args){
		SimpleCLI cli = new SimpleCLI();
		cli.registerCommmands(
				new Command("hello", "", "Hello World.") {
					@Override
					public void execute(String... args) {
						System.out.println("world");
					}
				});
		cli.parseProgramArgs(args);
		cli.run();
	}

	enum Action{INSERTION, DELETION, LOOKUP}
	
	public static void testRTS2(){
		RandomizedTreeSet<Integer> randomizedTreeSet = new RandomizedTreeSet<Integer>();
		int n = 16;
		System.out.println("Removing.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.remove(i));
		}
		System.out.println("Looking up.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.contains(i));
		}
		System.out.println("Adding.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.add(i));
		}
		System.out.println("Adding.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.add(i));
		}
		System.out.println("Looking up.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.contains(i));
		}
		System.out.println("Removing.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.remove(i));
		}
		System.out.println("Looking up.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.contains(i));
		}
		System.out.println("Removing.");
		for(int i = 0; i < n; i++){
			System.out.println(randomizedTreeSet.remove(i));
		}
	}
	
	public static void testRTS(){
		RandomizedTreeSet<Integer> randomizedTreeSet = new RandomizedTreeSet<Integer>();
		int steps = 65536;
		int n = 1024;
		int v = Action.values().length;
		
		long time = System.nanoTime();
		
		for(int k = 0; k < steps; k++){
			int i = ThreadLocalRandom.current().nextInt(v);
			Action action = Action.values()[i];
			Integer arg = ThreadLocalRandom.current().nextInt(n);
			switch(action){
				case INSERTION:
					randomizedTreeSet.add(arg);
					break;
				case DELETION:
					randomizedTreeSet.remove(arg);
					break;
				case LOOKUP:
					randomizedTreeSet.contains(arg);
					break;
			}
		}
		
		time = System.nanoTime() - time;
		for(String s : randomizedTreeSet.prettyPrint())
			System.out.println(s);
		System.out.println(time/steps + "ns per op");
	}
	
	public static void testGUP(){
		int n = 32;
		GloballyUniquePriority[] a = new GloballyUniquePriority[n];
		a[0] = GloballyUniquePriority.TOP;
		a[1] = GloballyUniquePriority.BOTTOM;
		for(int i = 2; i < n; i++){
			a[i] = new GloballyUniquePriority();
		}
		Arrays.sort(a);
		for(GloballyUniquePriority b : a){
			System.out.println(b.toString());
		}
	}
	
	public static void testParallelSetAll(){
		int z = 256;
		for(int n = 32; n <= 65536; n*=2){
			System.out.println("n: "+(n));
			long[] l0 = new long[z];
			long[] l1 = new long[z];
			long[] l2 = new long[z];
			for(int t = 0; t < z; t++){
				double[] a = ThreadLocalRandom.current().doubles(n).toArray(),
						b = ThreadLocalRandom.current().doubles(n).toArray(),
						c = new double[n],
						d = ThreadLocalRandom.current().doubles(n).toArray(),
						e = ThreadLocalRandom.current().doubles(n).toArray(),
						f = new double[n],
						g = ThreadLocalRandom.current().doubles(n).toArray(),
						h = ThreadLocalRandom.current().doubles(n).toArray(),
						j = new double[n];
				long tick0 = System.nanoTime();
				Arrays.setAll(c, i -> Math.pow(a[i], b[i]));
				long tock0 = System.nanoTime();
				
				long tick1 = System.nanoTime();
				Arrays.parallelSetAll(f, i -> Math.pow(d[i], e[i]));
				long tock1 = System.nanoTime();
				
				long tick2 = System.nanoTime();
				for(int i=0; i < n; i++){
					j[i] = Math.pow(g[i], h[i]);
				}
				long tock2 = System.nanoTime();

				l0[t] = (tock0-tick0);
				l1[t] = (tock1-tick1);
				l2[t] = (tock2-tick2);
			}
			double d0 = Arrays.stream(l0).average().getAsDouble();
			double d1 = Arrays.stream(l1).average().getAsDouble();
			double d2 = Arrays.stream(l2).average().getAsDouble();
			System.out.println("Serial time: "+d0);
			System.out.println("Parallel time: "+d1);
			System.out.println("Explicit time: "+d2);
			System.out.println("Efficient: "+(d1 < d0));
		}
	}
	
	static void charTest(){
		System.out.print((int) '\n');
		for(int i = 32; i < 128; i++){
			System.out.write(i);
			if(i % 16 == 0)
				System.out.println();
		}
		System.out.println();
	}
	
	static void lazyMatrixTest(){
		int z = 16;
		int q = 1024;
		int k;
		double p;
		
		Matrix hs, Whh, Wxh, xs, bh, ys, pWhy, by, ps, dy, dWhy, dby, dh, dhnext, dhraw, dbh, dWxh, dWhh;
		
		hs = Matrix.random(q, q, 0, 1);
		Whh = Matrix.random(q, q, 0, 1);
		Wxh = Matrix.random(q, q, 0, 1);
		xs = Matrix.random(q, q, 0, 1);
		bh = Matrix.random(q, q, 0, 1);
		ys = Matrix.random(q, q, 0, 1);
		pWhy = Matrix.random(q, q, 0, 1);
		by = Matrix.random(q, q, 0, 1);
		ps = Matrix.random(q, q, 0, 1);
		dy = Matrix.random(q, q, 0, 1);
		dWhy = Matrix.random(q, q, 0, 1);
		dby = Matrix.random(q, q, 0, 1);
		dh = Matrix.random(q, q, 0, 1);
		dhnext = Matrix.random(q, q, 0, 1);
		dhraw = Matrix.random(q, q, 0, 1);
		dbh = Matrix.random(q, q, 0, 1);
		dWxh = Matrix.random(q, q, 0, 1);
		dWhh = Matrix.random(q, q, 0, 1);
		p = ThreadLocalRandom.current().nextDouble();
		k = ThreadLocalRandom.current().nextInt(q);
		
		long tickEager = System.nanoTime();
		
		for(int i = 0; i < z; i++){
			
			dhnext = lazyMatrixTestHelper(
					hs, Whh, Wxh, xs, bh,
					ys, pWhy, by, ps, dy,
					dWhy, dby, dh, dhnext, dhraw,
					dbh, dWxh, dWhh, p, k);
		}
		long tockEager = System.nanoTime();
		long eager = tockEager - tickEager;
		
		hs = Matrix.random(q, q, 0, 1).lazy();
		Whh = Matrix.random(q, q, 0, 1).lazy();
		Wxh = Matrix.random(q, q, 0, 1).lazy();
		xs = Matrix.random(q, q, 0, 1).lazy();
		bh = Matrix.random(q, q, 0, 1).lazy();
		ys = Matrix.random(q, q, 0, 1).lazy();
		pWhy = Matrix.random(q, q, 0, 1).lazy();
		by = Matrix.random(q, q, 0, 1).lazy();
		ps = Matrix.random(q, q, 0, 1).lazy();
		dy = Matrix.random(q, q, 0, 1).lazy();
		dWhy = Matrix.random(q, q, 0, 1).lazy();
		dby = Matrix.random(q, q, 0, 1).lazy();
		dh = Matrix.random(q, q, 0, 1).lazy();
		dhnext = Matrix.random(q, q, 0, 1).lazy();
		dhraw = Matrix.random(q, q, 0, 1).lazy();
		dbh = Matrix.random(q, q, 0, 1).lazy();
		dWxh = Matrix.random(q, q, 0, 1).lazy();
		dWhh = Matrix.random(q, q, 0, 1).lazy();
		p = ThreadLocalRandom.current().nextDouble();
		k = ThreadLocalRandom.current().nextInt(q);
		
		long tickLazy = System.nanoTime();
		for(int i = 0; i < z; i++){
			
			dhnext = lazyMatrixTestHelper(
					hs, Whh, Wxh, xs, bh,
					ys, pWhy, by, ps, dy,
					dWhy, dby, dh, dhnext, dhraw,
					dbh, dWxh, dWhh, p, k);
		}
		long tockLazy = System.nanoTime();
		
		long lazy = tockLazy - tickLazy;
		
		System.out.println("Eager: "+eager/1000d/z+"\t Lazy: "+lazy/1000d/z);
	}
	
	public static Matrix lazyMatrixTestHelper(
			Matrix hs, Matrix Whh, Matrix Wxh, Matrix xs, Matrix bh,
			Matrix ys, Matrix pWhy, Matrix by, Matrix ps, Matrix dy,
			Matrix dWhy, Matrix dby, Matrix dh, Matrix dhnext, Matrix dhraw,
			Matrix dbh, Matrix dWxh, Matrix dWhh, double p, int k){

		System.out.println("Starting. Lazy = " + hs.isLazy());
		
	    hs = Whh.multiplySimple(hs.inplaceSum(Wxh.multiplySimple(xs)).inplaceSum(bh)).inplaceElementWise(Math::tanh);
	    ys = pWhy.multiplySimple(hs).scalarOp(Util::division, p).inplaceSum(by);
	    
	    Matrix expYsT = ys.elementWise(Math::exp);
	    ps = expYsT.scalarOp(Util::division, expYsT.aggregateOp(Util::sum));
	    
	    dy = ps.clone();
		
		dy.modValueAt(k, 0, -1.0);
		
		dWhy.inplaceSum(dy.multiplySimple(hs.transpose()));
		
		dby.inplaceSum(dy);
		
		dh = (pWhy.transpose().multiplySimple(dy)).inplaceSum(dhnext); // backprop into h
		
		dhraw = (hs.scalarOp(Math::pow, 2)).inplaceElementWise(a -> 1 - a).inplaceElementWise(Util::multiplication, dh); // backprop through tanh nonlinearity
		
		dbh.inplaceSum(dhraw);
		
		dWxh.inplaceSum(dhraw.multiplySimple(xs.transpose()));
		
		dWhh.inplaceSum(dhraw.multiplySimple(hs.transpose()));
		
		dhnext = Whh.transpose().multiplySimple(dhraw);
		
		return dhnext.cacheIfLazy();
	}
	
	
	public static void modTest(){
		int t = 64;
		int n = 1 << 24;
		int q = ThreadLocalRandom.current().nextInt(1 << 8, 1 << 12);
		int[] a = ThreadLocalRandom.current().ints(n, 0, q).toArray(),
				b = ThreadLocalRandom.current().ints(n, 0, q).toArray(),
				c = ThreadLocalRandom.current().ints(n, 0, q).toArray();
		
		int w = 0;
		long time1 = System.nanoTime();
		for(int k = 0; k < t; k++)
		for(int i = 0; i < n; i++){
			w = (w+a[i])%q;
			a[i] = w;
		}
		time1 = System.nanoTime() - time1;
		
		w = 0;
		long time2 = System.nanoTime();
		for(int k = 0; k < t; k++)
		for(int i = 0; i < n; i++){
			w = w+b[i];
			if(w>=q) w-=q;
			b[i] = w;
		}
		time2 = System.nanoTime() - time2;
		
		w = 0;
		long time3 = System.nanoTime();
		for(int k = 0; k < t; k++)
		for(int i = 0; i < n; i++){
			w = w+c[i];
			w = (w<q) ? w : w-q;
			c[i] = w;
		}
		time3 = System.nanoTime() - time3;
		
		for(int i = 0; i < n; i++){
			if(a[i] >= q || b[i] >= q || c[i] >= q) throw new AssertionError();
		}
		
		System.out.println(String.format("Modulo time:\t%d\r\nBranch time:\t%d\r\nTernary time:\t%d\r\nRatio MB: %f\tRatio MT: %f\tRatio BT: %f",
				time1, time2, time3,
				(double)time1/(double)time2,(double)time1/(double)time3,(double)time2/(double)time3));
	}
	
	
	public static void arrayClearTest(){
		int n = 20;
		int tmax = 1 << 4;
		long[] time1 = new long[n];
		long[] time2 = new long[n];
		long time;
		for(int w = 0; w < n; ++w){
			int q = 1 << (w+4);
			Object[] data = new Object[q];
			time = System.nanoTime();
			for(int t = 0; t < tmax; ++t){
				data = new Object[data.length];
				Objects.requireNonNull(Objects.requireNonNullElse(data[ThreadLocalRandom.current().nextInt(q)], new Object()));
			}
			time1[w] = System.nanoTime() - time;
			time = System.nanoTime();
			for(int t = 0; t < tmax; ++t){
				Arrays.fill(data, null);
				Objects.requireNonNull(Objects.requireNonNullElse(data[ThreadLocalRandom.current().nextInt(q)], new Object()));
			}
			time2[w] = System.nanoTime() - time;
		}
		IntStream.range(0, n).forEach(value -> System.out.println(String.format("%d:\ttime using new:  %d\r\n\ttime using null: %d\r\n", value+4, time1[value],time2[value])));
	}
	
	
	public static void testTernary(){
		for(TernaryUnit t : TernaryUnit.values()){
			System.out.println(String.format("%s %s = %s", "not", t, TernaryUnit.not(t)));
			for(TernaryUnit t1 : TernaryUnit.values()){
				System.out.println(String.format("%s %s %s = %s", t, "and", t1, t.and(t1)));
				System.out.println(String.format("%s %s %s = %s", t, "or", t1, t.or(t1)));
				System.out.println(String.format("%s %s %s = %s", t, "nand", t1, t.nand(t1)));
				System.out.println(String.format("%s %s %s = %s", t, "nor", t1, t.nor(t1)));
				System.out.println(String.format("%s %s %s = %s", t, "xnor", t1, t.xnor(t1)));
				System.out.println(String.format("%s %s %s = %s", t, "xor", t1, t.xor(t1)));
			}
		}
	}
}
