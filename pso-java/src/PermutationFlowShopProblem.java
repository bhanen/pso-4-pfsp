import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;


public class PermutationFlowShopProblem {
	
	private int job_num;
	private int machine_num;
	private boolean isValid;
	private long initial_seed;
	private String name;
	/**
	 * birinci boyut is sayisi. 
	 * ikinci boyut bu islerin sirasiyla hangi makinelerdeki sureleri
	 * 
	 */
	private int job_times[][]; 
	/**
	 * her işin bitiş süresi
	 */
	private int due_times[];
	
	public void setName(String name){
		this.name = name;
	}
	public void parse(InputStream input){
		try {
			isValid = false;
			Scanner sc = new Scanner(input);
			int state = 0;
			int current_job=0;
			while (sc.hasNextLine()){
				String line = sc.nextLine();
				switch (state) {
				case 0: // iş sayısını al
					job_num = Integer.parseInt(line);
					state ++;
					break;
				case 1: // makine sayısını al. ve değişkenleri başlat
					machine_num = Integer.parseInt(line);
					state ++;
					job_times = new int[job_num][machine_num];
					due_times = new int[job_num];
					break;
				case 2: // başlangıç seed'ini oku (rastgele sayı üreteci için)
					initial_seed = Long.parseLong(line);
					state ++;
					break;
				case 3: // işlenecek iş numarasını oku
					current_job = Integer.parseInt(line);
					state ++;
					break;
				case 4: // işlenecek iş numarasını oku
					due_times[current_job]= Integer.parseInt(line);
					state ++;
					break;
				case 5: // işlenecek iş numarasını oku
					int loc0 = 0;
					int loc1 = line.indexOf(' ');
					int counter = 0;
					while (loc1 != -1){
						String job_time = line.substring(loc0,loc1);
						job_times[current_job][counter] = Integer.parseInt(job_time);
						counter++;
						loc0 = loc1+1;
						loc1 = line.indexOf(' ',loc0);
					}
					state = 3;
					break;
				default:
					break;
				}
			}
			isValid = true;
		} catch (Exception e) {
			e.printStackTrace();
			isValid = false;
		}
	}
	public void show_in_console(){
		if (!isValid){
			System.out.println("Geçerli bir problem değil");
			return;
		}
		System.out.println("Permutation Flow Shop Problem");
		System.out.println("Problemin İsmi: "+name);
		System.out.println("İş Sayısı: "+job_num);
		System.out.println("Makine Sayısı: "+machine_num);
		System.out.println("-------------------------------------");
		for (int i = 0; i < job_times.length; i++) {
			System.out.print("İş["+i+"] = [");
			for (int j = 0; j < job_times[i].length; j++) {
				System.out.print(job_times[i][j]);
				if (j != job_times[i].length -1){
					System.out.print(",");
				}
			}
			System.out.println("] işlem süres["+due_times[i]+"]");
		}
		System.out.println("-------------------------------------");
	}
	
}
