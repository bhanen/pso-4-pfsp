import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class PSO {
	
	private static PSO instance;
	public static PSO instance(){
		if (instance == null){
			instance = new PSO();
		}
		return instance;
	}
	private boolean dosyaya_yaz;
	private int parcacik_sayisi;
	private int problem_index;
	private int maksimum_iterasyon;
	private int zaman_kisiti;
	private ParticleGroup pg;
	private PSO(){
		pg = new ParticleGroup();
	}
	public void setDosyayaYaz(boolean d){
		dosyaya_yaz = d;
	}
	public void setProblemIndex(int index){
		problem_index = index;
	}
	public boolean dosyaya_yaz(){
		return dosyaya_yaz;
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
	
	public void durdur(){
		pg.stop();
	}
	public void baslat(){
		String name = Test.names[problem_index];
		PSOScreen.instance().pso_set_name(name);
		JobParser j = new JobParser();
		//PermutationFlowShopProblem problem = j.getProblem("/flowshop/test.txt");
		PermutationFlowShopProblem problem = j.getProblem("/flowshop/"+name);
		//PermutationFlowShopProblem problem = j.getProblem("/flowshop/50_20_07_ta057.txt");
		//PermutationFlowShopProblem problem = j.getProblem("/flowshop/500_20_10_ta120.txt");
		pg.init(problem, parcacik_sayisi, 0, 4, -4, 4);
		Thread t = new Thread(){
			public void run() {
				pg.solve(maksimum_iterasyon,zaman_kisiti);
			};
		};
		t.start();
	}
	public JComponent getCozumEkrani(){
		return pg.getScreen();
	}
}

class ParticleGroup{
	private PermutationFlowShopProblem problem;
	private Particle particles[];
	private double global_best[];
	private int global_best_order[];
	private int global_best_tft;
	private int temp[];
	private JComponent screen;
	private boolean init;
	private boolean stop;
	private FileOutputStream fileOutput;
	private boolean dosyaya_yaz;
	private long baslangic_zamani;
	public ParticleGroup(){
		init = false;
		screen =  new JComponent(){
			Font font = Font.getFont("Arial");
			Color colors[]={
				Color.black,	
				Color.red,	
				Color.yellow,	
				Color.blue,	
				Color.pink,	
				Color.CYAN,	
				Color.green,
			};
			public void paint(Graphics g) {
				try {
					_paint(g);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			int showing_order[];
			int table[][];
			int total_makespan;
			public void _paint(Graphics g) {
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());
				if (!init){
					return;
				}
				boolean make_table = false;
				if (table == null || showing_order == null){
					showing_order = new int[global_best_order.length];
					make_table = true;
				} else {
					if (showing_order.length != global_best_order.length){
						showing_order = new int[global_best_order.length];
						make_table = true;
					} else {
						for (int i = 0; i < showing_order.length; i++) {
							if (showing_order[i] != global_best_order[i]){
								make_table = true;
								break;
							}
						}
					}
				}
				if (make_table){
					System.arraycopy(global_best_order,0,showing_order,0,showing_order.length);
					table = Algorithm.TFT_table(global_best_order, problem);
					total_makespan = Algorithm.TFT("paint", global_best_order, problem, temp);
				}
				int cur_y = 0;
				int cell_height = getHeight()/table.length;
				double cell_width = ((double)getWidth())/((double)total_makespan);
				for (int i=0;i<table.length;i++){
					for (int k = 0; k < table[i].length; k++) {
						int x = (int)(table[i][k] * cell_width);
						int width = (int)(problem.getMachineTimes(i)[k] * cell_width);
						x -= width;
						fillRect(g, k, x, cur_y, width, cell_height);
					}
					cur_y += cell_height;
				}
			}
			private Polygon p1 = new Polygon(new int[]{0,0,0,0},new int[]{0,0,0,0},4);
			private Polygon p2 = new Polygon(new int[]{0,0,0,0},new int[]{0,0,0,0},4);
			private void fillRect(Graphics g,int num,int x,int y,int w,int h){
				g.setClip(x, y, w, h);	
				if (num < colors.length){
					g.setColor(colors[num]);
					g.fillRect(x, y, w, h);
				} else {
					Color c1 = colors[num%colors.length];
					Color c2 = colors[(num-1)%colors.length];
					int step_size = h/6;
					if (step_size <= 0){
						step_size = 1;
					}
					int target = w;
					if (h > w){
						target = h;
					}
					int cur = 0;
					for (int i = 0; cur < target ; i++) {
						if (i%2==0){
							g.setColor(c1);
						} else {
							g.setColor(c2);
						}
						if (num % 2 == 0){
							p1.xpoints[0] = x + cur;
							p1.ypoints[0] = y;
							
							p1.xpoints[1] = x + cur + step_size;
							p1.ypoints[1] = y;
							
							p1.xpoints[2] = x;
							p1.ypoints[2] = y+cur+step_size;
							
							p1.xpoints[3] = x;
							p1.ypoints[3] = y+cur;
							
							p2.xpoints[0] = x+target;
							p2.ypoints[0] = y+cur;
							
							p2.xpoints[1] = x+target;
							p2.ypoints[1] = y+cur+step_size;
							
							p2.xpoints[2] = x+cur+step_size;
							p2.ypoints[2] = y+target;
							
							p2.xpoints[3] = x+cur;
							p2.ypoints[3] = y+target;
							
							
						} else {
							
							p1.xpoints[0] = x + cur;
							p1.ypoints[0] = y;
							
							p1.xpoints[1] = x + cur + step_size;
							p1.ypoints[1] = y;
							
							p1.xpoints[2] = x+target;
							p1.ypoints[2] = y+target - cur - step_size;
							
							p1.xpoints[3] = x+target;
							p1.ypoints[3] = y+target - cur;
							
							
							p2.xpoints[0] = x;
							p2.ypoints[0] = y+cur;
							
							p2.xpoints[1] = x;
							p2.ypoints[1] = y+cur+step_size;
							
							p2.xpoints[2] = x + target - cur - step_size;
							p2.ypoints[2] = y+target;
							
							p2.xpoints[3] = x + target - cur;
							p2.ypoints[3] = y+target;
							
						}
						g.fillPolygon(p1);
						g.fillPolygon(p2);
						cur += step_size;
					}
					
					
				}
			}
		};
	}
	public void stop(){
		stop = true;
	}
	public JComponent getScreen(){
		return screen;
	}
	
	public void init(PermutationFlowShopProblem problem,int particle_num,int x_min,int x_max,int v_min,int v_max){
		this.problem = problem;
		stop = false;
		dosyaya_yaz = PSO.instance().dosyaya_yaz();
		if (dosyaya_yaz){
			File f = new File(problem.getName()+"_"+System.currentTimeMillis()+".txt");
			try {
				f.createNewFile();
				if (fileOutput != null){
					try {
						fileOutput.close();
					} catch (Exception e) {
					}
				}
				fileOutput = new FileOutputStream(f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		particles = new Particle[particle_num];
		global_best = new double[problem.getJobNum()];
		global_best_order = new int[problem.getJobNum()];
		temp = new int[problem.getMachineNum()];
		if (dosyaya_yaz){
			writeToFile("Başlangıç parametreleri");
			writeToFile("İş Sayısı:"+problem.getJobNum());
			writeToFile("Makine Sayısı:"+problem.getMachineNum());
			writeToFile("Parçacık Sayısı:"+particle_num);
			writeToFile("-------------------------------------------");
		}
		for (int i = 0; i < particles.length; i++) {
			particles[i] = new Particle();
			particles[i].init(problem,x_min, x_max, v_min, v_max,temp);
			if (dosyaya_yaz){
				writeToFile("X["+i+"] : "+Algorithm.getArrayString(particles[i].getCurrentX()));
				writeToFile("V["+i+"] : "+Algorithm.getArrayString(particles[i].getCurrentV()));
				writeToFile("P["+i+"] : "+Algorithm.getArrayString(particles[i].getBestX()));
				writeToFile("-------------------------------------------");
			}
			if (i == 0){
				System.arraycopy(particles[i].getBestX(), 0, global_best, 0, global_best.length);
				System.arraycopy(particles[i].getBestOrder(), 0, global_best_order, 0, global_best_order.length);
				global_best_tft = particles[i].getBestTFT();
			}
		}
		calculate_global_best(0);
		init = true;
	}
	private void writeToFile(String str){
		try {
			fileOutput.write((str+"\r\n").getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void solve(int maks_itesyon, int maks_dakika){
		double w = 0.9;
		double b = 0.975;
		int percent=0;
		int maks_millis = maks_dakika*60*1000;
		baslangic_zamani = System.currentTimeMillis();
		for (int j = 0;; j++) {
			if (dosyaya_yaz){
				writeToFile("Adım Sayısı: "+j);
			}
			for (int i = 0; i < particles.length; i++) {
				particles[i].nextMove(global_best, w, 2, 2);
				if (dosyaya_yaz){
					writeToFile("X["+i+"] : "+Algorithm.getArrayString(particles[i].getCurrentX()));
					writeToFile("V["+i+"] : "+Algorithm.getArrayString(particles[i].getCurrentV()));
					writeToFile("P["+i+"] : "+Algorithm.getArrayString(particles[i].getBestX()));
					writeToFile("-------------------------------------------");
				}
			}
			w = w * b;
			if (w < 0.4){
				w = 0.4;
			}
			if (dosyaya_yaz){
				writeToFile("Eylemsizlik Katsayısı: "+Algorithm.doubleToString(w));
			}
			calculate_global_best(j);
			
			
			if (maks_itesyon != -1 ){
				percent = (j*100)/maks_itesyon;
				if (j >= maks_itesyon){
					break;
				}
			}
			long ti2 = System.currentTimeMillis();
			PSOScreen.instance().pso_set_current_time(((ti2-baslangic_zamani)));
			if (maks_dakika != -1 ) {
				if ( ti2 - baslangic_zamani > maks_millis){
					break;
				}
				int percent2 = (int)(((ti2-baslangic_zamani)*100)/maks_millis);
				if (percent2 > percent){
					percent = percent2;
				}
			}
			PSOScreen.instance().pso_set_current_percent(percent);
			if (stop){
				break;
			}
			PSOScreen.instance().pso_set_current_iterasyon(j+1);
		}
		if (dosyaya_yaz){
			try {
				fileOutput.close();
			} catch (Exception e) {
			}
			fileOutput = null;
		}
		PSOScreen.instance().pso_set_current_percent(100);
		PSOScreen.instance().pso_stopped();
	}
	private void calculate_global_best(int step){
		for (int i = 0; i < particles.length; i++) {
			double p_x[] = particles[i].getBestX();
			int p_order[] = particles[i].getBestOrder();
			int pbest = particles[i].getBestTFT();
			if (pbest < global_best_tft ){
				global_best_tft = pbest;
				System.arraycopy(p_x, 0, global_best, 0, p_x.length);
				System.arraycopy(p_order, 0, global_best_order, 0, p_x.length);
				screen.invalidate();
				screen.repaint();
				PSOScreen.instance().pso_set_makespan(global_best_tft);
				PSOScreen.instance().pso_set_en_iyi_ulasim_zamani(System.currentTimeMillis() - baslangic_zamani);
				if (dosyaya_yaz){
					writeToFile("Yeni çözüm bulundu. En iyi TFT="+global_best_tft);
					writeToFile("GBEST X: "+Algorithm.getArrayString(global_best));
					writeToFile("GBEST SIRA: "+Algorithm.getArrayString(global_best_order));
					writeToFile("-------------------------------------------");
				}
			}
		}
		
		
		
	}
	public PermutationFlowShopProblem getProblem(){
		return problem;
	}
}

class Particle {
	private double x[];
	private int current_order[];
	private double v[];
	
	private double best_x[];
	private int best_order[];
	private int best_x_tft;
	private int temp[];
	private static Random random = new Random();
	private PermutationFlowShopProblem problem;
	public void init(PermutationFlowShopProblem problem,int x_min,int x_max,int v_min,int v_max,int temp[]){
		this.temp = temp;
		this.problem = problem;
		x = new double[problem.getJobNum()];
		v = new double[problem.getJobNum()];
		best_x = new double[problem.getJobNum()];
		best_order = new int[problem.getJobNum()];
		current_order = new int[problem.getJobNum()];
		for (int i = 0; i < x.length; i++) {
			x[i] = random.nextDouble()%(x_max - x_min) + x_min;			
			v[i] = random.nextDouble()%(v_min - v_max ) + v_min;			
		}
		System.arraycopy(x, 0, best_x, 0, best_x.length);
		Algorithm.apply_SPV("init",best_x, best_order);
		best_x_tft = Algorithm.TFT("particle init",best_order,problem,temp);
	}
	public double[] getBestX(){
		return best_x;
	}
	public int[] getBestOrder(){
		return best_order;
	}
	public int getBestTFT(){
		return best_x_tft;
	}
	public double[] getCurrentX(){
		return x;
	}
	public double[] getCurrentV(){
		return v;
	}
	public void nextMove(double global_best[], double inertia,double c1, double c2){
		for (int i = 0; i < global_best.length; i++) {
			v[i] = v[i] * inertia + c1 * Math.abs(random.nextDouble()) * (best_x[i] - x[i]) + c2 * Math.abs(random.nextDouble())*(global_best[i] - x[i]);
			x[i] = x[i] + v[i];
		}
		Algorithm.apply_SPV("nextMove",x,current_order);
		int current_tft = Algorithm.TFT("particle move",current_order,problem,temp);
		if (current_tft < best_x_tft){
			best_x_tft = current_tft;
			System.arraycopy(x, 0, best_x, 0, x.length);
			System.arraycopy(current_order, 0, best_order, 0, current_order.length);
		}
	}
}

class Algorithm {
	
	public static int[][] TFT_table(int order[],PermutationFlowShopProblem problem){
		int table[][]= new int[problem.getJobNum()][problem.getMachineNum()];
		int temp[] = new int[problem.getMachineNum()];
		for (int i = 0; i < problem.getJobNum(); i++) {
			for (int j = 0; j < problem.getMachineNum(); j++) {
				int job_times[] = problem.getMachineTimes(order[i]);
				if (i == 0){
					if (j == 0){
						temp[j] = job_times[j];
					} else {
						temp[j] = job_times[j] + temp[j-1];
					}
				} else {
					if (j == 0){
						temp[0] = temp[0] + job_times[0];
					} else {
						if (temp[j] > temp[j-1]){
							temp[j] = temp[j]+job_times[j];
						} else {
							temp[j] = temp[j-1]+job_times[j];
						}
					}
				}
			}
			System.arraycopy(temp, 0, table[order[i]], 0, temp.length);
		}
		return table;
	}
	public static int TFT(String from,int order[],PermutationFlowShopProblem problem,int temp[]){
		for (int i = 0; i < problem.getJobNum(); i++) {
			for (int j = 0; j < problem.getMachineNum(); j++) {
				int job_times[] = problem.getMachineTimes(order[i]);
				if (i == 0){
					if (j == 0){
						temp[j] = job_times[j];
					} else {
						temp[j] = job_times[j] + temp[j-1];
					}
				} else {
					if (j == 0){
						temp[0] = temp[0] + job_times[0];
					} else {
						if (temp[j] > temp[j-1]){
							temp[j] = temp[j]+job_times[j];
						} else {
							temp[j] = temp[j-1]+job_times[j];
						}
					}
				}
			}
		}
		int last_start_time = temp[temp.length - 1];
		int last_end_time = last_start_time +  problem.getMachineTimes(problem.getJobNum() - 1)[problem.getMachineNum() - 1];
		return last_end_time;
	}
	public static void apply_SPV(String from,double x[],int order[]){
//		System.out.println("SPV: from > "+from);
//		System.out.println("SPV: order > "+getArrayString(order));
//		System.out.println("SPV: x     > "+getArrayString(x));
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
	
	public static String getArrayString(double array[]){
		StringBuffer buf = new StringBuffer();
		buf.append('[');
		for (int i = 0; i < array.length; i++) {
			buf.append(doubleToString(array[i]));
			if(array.length != i + 1){
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	}
	public static String doubleToString(double d){
		String s = ""+d;
		if (d >= 0){
			s = "+"+s;
		}
		while (s.length() < 5){
			s += "0";
		}
		return s.substring(0,5);
	}
	public static String getArrayString(int array[]){
		StringBuffer buf = new StringBuffer();
		buf.append('[');
		for (int i = 0; i < array.length; i++) {
			buf.append(array[i]);
			if(array.length != i + 1){
				buf.append(',');
			}
		}
		buf.append(']');
		return buf.toString();
	}
}

class Test {
	public static final String names[] = {
			"150_5_09.txt",
			"20_10_06_ta016.txt",
			"300_20_01.txt",
			"300_10_03.txt",
			"100_5_04_ta064.txt",
			"200_15_10.txt",
			"150_20_10.txt",
			"300_20_05.txt",
			"300_10_07.txt",
			"50_10_07_ta047.txt",
			"300_20_09.txt",
			"50_5_03_ta033.txt",
			"200_20_07_ta107.txt",
			"70_10_10.txt",
			"100_20_07_ta087.txt",
			"50_15_02.txt",
			"50_15_06.txt",
			"50_20_10_ta060.txt",
			"300_20_10.txt",
			"20_20_07_ta027.txt",
			"50_20_08_ta058.txt",
			"20_5_03_ta003.txt",
			"500_20_04_ta114.txt",
			"200_10_03_ta093.txt",
			"100_10_05_ta075.txt",
			"20_10_05_ta015.txt",
			"100_5_03_ta063.txt",
			"50_10_06_ta046.txt",
			"150_15_03.txt",
			"150_15_07.txt",
			"50_5_02_ta032.txt",
			"200_20_06_ta106.txt",
			"100_20_06_ta086.txt",
			"30_15_04.txt",
			"70_15_01.txt",
			"30_15_08.txt",
			"70_15_05.txt",
			"20_20_06_ta026.txt",
			"70_15_09.txt",
			"100_15_01.txt",
			"200_15_02.txt",
			"150_20_02.txt",
			"150_10_04.txt",
			"30_5_03.txt",
			"500_15_01.txt",
			"300_15_03.txt",
			"100_15_05.txt",
			"70_5_01.txt",
			"150_20_06.txt",
			"50_20_07_ta057.txt",
			"200_15_06.txt",
			"150_10_08.txt",
			"30_5_07.txt",
			"20_5_02_ta002.txt",
			"500_15_05.txt",
			"300_15_07.txt",
			"100_15_09.txt",
			"70_5_05.txt",
			"500_20_03_ta113.txt",
			"200_10_02_ta092.txt",
			"500_15_09.txt",
			"70_5_09.txt",
			"100_10_04_ta074.txt",
			"30_10_01.txt",
			"30_20_03.txt",
			"30_10_05.txt",
			"70_10_02.txt",
			"20_15_03.txt",
			"30_20_07.txt",
			"30_10_09.txt",
			"70_20_04.txt",
			"150_5_03.txt",
			"70_15_10.txt",
			"70_10_06.txt",
			"20_15_07.txt",
			"20_10_04_ta014.txt",
			"70_20_08.txt",
			"150_5_07.txt",
			"100_5_02_ta062.txt",
			"300_20_02.txt",
			"50_10_05_ta045.txt",
			"100_15_10.txt",
			"300_10_04.txt",
			"300_20_06.txt",
			"300_10_08.txt",
			"500_15_10.txt",
			"70_5_10.txt",
			"50_5_01_ta031.txt",
			"200_20_05_ta105.txt",
			"30_10_10.txt",
			"100_20_05_ta085.txt",
			"50_15_03.txt",
			"50_15_07.txt",
			"50_5_09_ta039.txt",
			"20_20_05_ta025.txt",
			"50_20_06_ta056.txt",
			"20_5_01_ta001.txt",
			"500_20_02_ta112.txt",
			"200_10_01_ta091.txt",
			"100_10_03_ta073.txt",
			"20_5_09_ta009.txt",
			"20_10_03_ta013.txt",
			"100_5_01_ta061.txt",
			"200_10_09_ta099.txt",
			"50_10_04_ta044.txt",
			"150_15_04.txt",
			"200_20_04_ta104.txt",
			"100_5_09_ta069.txt",
			"50_5_10_ta040.txt",
			"150_15_08.txt",
			"100_20_04_ta084.txt",
			"30_15_01.txt",
			"30_15_05.txt",
			"70_15_02.txt",
			"50_5_08_ta038.txt",
			"20_20_04_ta024.txt",
			"30_15_09.txt",
			"70_15_06.txt",
			"150_10_01.txt",
			"100_15_02.txt",
			"50_20_05_ta055.txt",
			"200_15_03.txt",
			"150_20_03.txt",
			"30_5_04.txt",
			"150_10_05.txt",
			"500_15_02.txt",
			"20_5_10_ta010.txt",
			"100_15_06.txt",
			"300_15_04.txt",
			"70_5_02.txt",
			"200_15_07.txt",
			"150_20_07.txt",
			"500_20_01_ta111.txt",
			"30_5_08.txt",
			"150_10_09.txt",
			"300_15_08.txt",
			"500_15_06.txt",
			"100_10_02_ta072.txt",
			"70_5_06.txt",
			"30_10_02.txt",
			"30_20_04.txt",
			"30_15_10.txt",
			"70_20_01.txt",
			"30_10_06.txt",
			"70_10_03.txt",
			"20_5_08_ta008.txt",
			"20_10_02_ta012.txt",
			"20_15_04.txt",
			"30_20_08.txt",
			"70_20_05.txt",
			"150_5_04.txt",
			"500_20_09_ta119.txt",
			"70_10_07.txt",
			"20_15_08.txt",
			"200_10_08_ta098.txt",
			"100_5_10_ta070.txt",
			"70_20_09.txt",
			"150_5_08.txt",
			"50_10_03_ta043.txt",
			"300_10_01.txt",
			"150_10_10.txt",
			"300_20_03.txt",
			"300_10_05.txt",
			"300_20_07.txt",
			"300_10_09.txt",
			"200_20_03_ta103.txt",
			"100_5_08_ta068.txt",
			"100_20_03_ta083.txt",
			"70_20_10.txt",
			"50_15_04.txt",
			"50_5_07_ta037.txt",
			"20_20_03_ta023.txt",
			"50_15_08.txt",
			"300_10_10.txt",
			"50_20_04_ta054.txt",
			"500_20_10_ta120.txt",
			"20_5_07_ta007.txt",
			"20_10_01_ta011.txt",
			"500_20_08_ta118.txt",
			"200_10_07_ta097.txt",
			"100_10_09_ta079.txt",
			"50_10_02_ta042.txt",
			"20_10_09_ta019.txt",
			"150_15_01.txt",
			"200_20_02_ta102.txt",
			"100_5_07_ta067.txt",
			"150_15_05.txt",
			"100_20_02_ta082.txt",
			"150_15_09.txt",
			"30_15_02.txt",
			"50_5_06_ta036.txt",
			"20_20_02_ta022.txt",
			"30_15_06.txt",
			"70_15_03.txt",
			"70_15_07.txt",
			"50_20_03_ta053.txt",
			"200_10_10_ta100.txt",
			"30_5_01.txt",
			"150_10_02.txt",
			"100_15_03.txt",
			"300_15_01.txt",
			"150_20_04.txt",
			"200_15_04.txt",
			"30_5_05.txt",
			"150_15_10.txt",
			"150_10_06.txt",
			"500_15_03.txt",
			"100_15_07.txt",
			"300_15_05.txt",
			"100_10_10_ta080.txt",
			"70_5_03.txt",
			"150_20_08.txt",
			"200_15_08.txt",
			"30_5_09.txt",
			"300_15_09.txt",
			"500_15_07.txt",
			"70_5_07.txt",
			"30_20_01.txt",
			"30_10_03.txt",
			"20_5_06_ta006.txt",
			"20_15_01.txt",
			"30_20_05.txt",
			"20_10_10_ta020.txt",
			"500_20_07_ta117.txt",
			"30_10_07.txt",
			"70_20_02.txt",
			"70_10_04.txt",
			"200_10_06_ta096.txt",
			"150_5_01.txt",
			"20_15_05.txt",
			"100_10_08_ta078.txt",
			"30_20_09.txt",
			"70_20_06.txt",
			"70_10_08.txt",
			"150_5_05.txt",
			"50_10_01_ta041.txt",
			"20_15_09.txt",
			"300_10_02.txt",
			"20_10_08_ta018.txt",
			"30_5_10.txt",
			"300_20_04.txt",
			"300_10_06.txt",
			"300_15_10.txt",
			"200_20_01_ta101.txt",
			"100_5_06_ta066.txt",
			"300_20_08.txt",
			"50_10_09_ta049.txt",
			"100_20_01_ta081.txt",
			"30_20_10.txt",
			"20_15_10.txt",
			"200_20_09_ta109.txt",
			"50_5_05_ta035.txt",
			"20_20_01_ta021.txt",
			"50_15_01.txt",
			"150_5_10.txt",
			"50_15_05.txt",
			"100_20_09_ta089.txt",
			"50_20_02_ta052.txt",
			"50_15_09.txt",
			"20_20_09_ta029.txt",
			"20_5_05_ta005.txt",
			"500_20_06_ta116.txt",
			"200_10_05_ta095.txt",
			"100_10_07_ta077.txt",
			"50_15_10.txt",
			"50_10_10_ta050.txt",
			"20_10_07_ta017.txt",
			"100_5_05_ta065.txt",
			"200_20_10_ta110.txt",
			"150_15_02.txt",
			"50_10_08_ta048.txt",
			"100_20_10_ta090.txt",
			"150_15_06.txt",
			"50_5_04_ta034.txt",
			"200_20_08_ta108.txt",
			"20_20_10_ta030.txt",
			"30_15_03.txt",
			"100_20_08_ta088.txt",
			"30_15_07.txt",
			"70_15_04.txt",
			"50_20_01_ta051.txt",
			"70_15_08.txt",
			"150_20_01.txt",
			"200_15_01.txt",
			"20_20_08_ta028.txt",
			"30_5_02.txt",
			"150_10_03.txt",
			"300_15_02.txt",
			"100_15_04.txt",
			"150_20_05.txt",
			"200_15_05.txt",
			"30_5_06.txt",
			"150_10_07.txt",
			"100_15_08.txt",
			"300_15_06.txt",
			"500_15_04.txt",
			"70_5_04.txt",
			"150_20_09.txt",
			"200_15_09.txt",
			"50_20_09_ta059.txt",
			"20_5_04_ta004.txt",
			"500_15_08.txt",
			"70_5_08.txt",
			"500_20_05_ta115.txt",
			"30_20_02.txt",
			"30_10_04.txt",
			"200_10_04_ta094.txt",
			"100_10_06_ta076.txt",
			"70_10_01.txt",
			"20_15_02.txt",
			"30_20_06.txt",
			"30_10_08.txt",
			"70_20_03.txt",
			"150_5_02.txt",
			"70_10_05.txt",
			"20_15_06.txt",
			"70_20_07.txt",
			"70_10_09.txt",
			"150_5_06.txt",
			"test.txt",
	};
}