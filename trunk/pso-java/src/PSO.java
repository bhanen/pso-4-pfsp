import java.awt.Container;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;


public class PSO {
	private static PSO instance;
	public static PSO instance(){
		if (instance == null){
			instance = new PSO();
		}
		return instance;
	}
	private int is_sayisi;
	private int makine_sayisi;
	private int problem_turu;
	private int parcacik_sayisi;
	private int maksimum_iterasyon;
	private int zaman_kisiti;
	private int iterasyon_sayisi;
	public int getIterasyon_sayisi() {
		return iterasyon_sayisi;
	}
	public void setIterasyon_sayisi(int iterasyonSayisi) {
		iterasyon_sayisi = iterasyonSayisi;
	}
	private PSO(){
	}
	public int getIs_sayisi() {
		return is_sayisi;
	}
	public void setIs_sayisi(int isSayisi) {
		is_sayisi = isSayisi;
	}
	public int getMakine_sayisi() {
		return makine_sayisi;
	}
	public void setMakine_sayisi(int makineSayisi) {
		makine_sayisi = makineSayisi;
	}
	public int getProblem_turu() {
		return problem_turu;
	}
	public void setProblem_turu(int problemTuru) {
		problem_turu = problemTuru;
	}
	public int getParcacik_sayisi() {
		return parcacik_sayisi;
	}
	public void setParcacik_sayisi(int parcacikSayisi) {
		parcacik_sayisi = parcacikSayisi;
	}
	public int getMaksimum_iterasyon() {
		return maksimum_iterasyon;
	}
	public void setMaksimum_iterasyon(int maksimumIterasyon) {
		maksimum_iterasyon = maksimumIterasyon;
	}
	public int getZaman_kisiti() {
		return zaman_kisiti;
	}
	public void setZaman_kisiti(int zamanKisiti) {
		zaman_kisiti = zamanKisiti;
	}
	
	public void baslat(){
		
	}
	
	
}

class ParticleGroup{
	private int jobs[][];
	public void setJobs(int jobs[][]){
		this.jobs = jobs;
	}
	private Particle particles[];
	private double global_best[];
	private double global_best_tft;
	
	public void init(int particle_num,int dimension_num,int x_min,int x_max,int v_min,int v_max){
		particles = new Particle[particle_num];
		global_best = new double[dimension_num];
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new Particle();
			particles[i].init(dimension_num, x_min, x_max, v_min, v_max);
			if (i == 0){
				System.arraycopy(global_best, 0, particles[i].getBest(), 0, global_best.length);
				global_best_tft = particles[i].getBestTFT();
			}
		}
		calculate_global_best();
	}
	private void calculate_global_best(){
		for (int i = 0; i < particles.length; i++) {
			double p_x[] = particles[i].getBest();
			double pbest = Algorithm.TFT(p_x);
			if (global_best_tft < pbest){
				global_best_tft = pbest;
				System.arraycopy(p_x, 0, global_best, 0, p_x.length);
			}
		}
	}
}

class Particle {
	private double x[];
	private double v[];
	private int order[];
	private double best_x[];
	private double best_order[];
	private double best_x_tft;
	private static Random random = new Random();
	public void init(int dimension_num,int x_min,int x_max,int v_min,int v_max){
		x = new double[dimension_num];
		v = new double[dimension_num];
		best_x = new double[dimension_num];
		order = new int[dimension_num];
		for (int i = 0; i < x.length; i++) {
			x[i] = random.nextDouble()%(x_max - x_min) + x_min;			
			v[i] = random.nextDouble()%(v_min - v_max ) + v_min;			
		}
		System.arraycopy(best_x, 0, x, 0, best_x.length);
		Algorithm.apply_SPV(best_x, order);
		best_x_tft = Algorithm.TFT(best_x,order);
		System.arraycopy(order, 0, best_order, 0, best_order.length);
	}
	public double[] getBest(){
		return best_x;
	}
	public double getBestTFT(){
		return best_x_tft;
	}
	public void nextMove(double global_best[], double inertia,double c1, double c2){
		for (int i = 0; i < global_best.length; i++) {
			v[i] = v[i] * inertia + c1 * Math.abs(random.nextDouble()) * (best_x[i] - x[i]) + c2 * Math.abs(random.nextDouble())*(global_best[i] - x[i]);
			x[i] = x[i] + v[i];
		}
		Algorithm.apply_SPV(x,order);
		double current_tft = Algorithm.TFT(x,order);
		if (current_tft < best_x_tft){
			best_x_tft = current_tft;
			System.arraycopy(x, 0, best_x, 0, x.length);
		}
	}
}

class Algorithm {
	public static double TFT(double x[],int order[]){
		return 0;
	}
	public static void apply_SPV(double x[],int order[]){
		for (int i = 0; i < order.length; i++) {
			double min = 1000;
			int target=0;
			for (int j = 0; j < x.length; j++) {
				if (x[j] < min){
					target = j;
					min = x[j];
				}
			}
			x[target] += 1000000;
			order[i] = target;
		}
		for (int i = 0; i < x.length; i++) {
			x[i] -= 1000000;
		}
	}
}
