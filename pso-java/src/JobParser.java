import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import javax.crypto.Mac;


public class JobParser {
	public static void main(String[] args) {
		InputStream in = null;System.out.println("parsing..");
		try {
			in = "".getClass().getResourceAsStream("/flowshop/100_10_01_ta071.txt");
			PermutationFlowShopProblem f = new PermutationFlowShopProblem();
			f.parse(in);
			f.show_in_console();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	
}
