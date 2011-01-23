import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Vector;
import javax.crypto.Mac;


public class JobParser {
	public PermutationFlowShopProblem getProblem(String path) {
		InputStream in = null;
		PermutationFlowShopProblem f = null;
		try {
			in = getClass().getResourceAsStream(path);
			f = new PermutationFlowShopProblem();
			if (path.endsWith(".txt")){
				path = path.substring(0,path.length()-4);
			}
			int loc = path.lastIndexOf('/');
			int loc1 = path.lastIndexOf('\\');
			if (loc > loc1){
				loc1 = loc;
			}
			if (loc1 != -1){
				path = path.substring(loc1+1);
			}
			f.setName(path);
			f.parse(in);
		} catch (Exception e) {
			e.printStackTrace();
			f = null;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return f;
	}
	
	
	
	
	
}
