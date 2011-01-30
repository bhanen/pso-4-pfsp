import java.awt.Color; 
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PSOScreen extends JFrame {
	
	
	public static void main(String[] args) {
		for (int i = 0; i < Test.names.length-1; i++) {
			for (int j = 0; j < Test.names.length-1-i; j++) {
				if (Test.names[j].compareToIgnoreCase(Test.names[j + 1]) > 0){
					String tmp = Test.names[j];
					Test.names[j] = Test.names[j+1];
					Test.names[j+1] = tmp;
				}
			}
		}
		new PSOScreen();
	}
	private JComboBox genel_problemler;
	private JComboBox makine_sayisi;
	private JComboBox is_sayisi;
	private JComboBox parcacik_sayisi;
	private JComboBox test_turu; 
	private JComboBox zaman_kisiti ;
	private JComboBox iterasyon_sayisi ;
	private JComboBox deneme_sayisi;
	private JButton coz;
	private JLabel makespan;
	private JLabel current_iterasyon_sayisi;
	private JLabel current_time;
	private JLabel en_iyi_cozume_ulasma_zamani;
	private JLabel problem_ismi;
	private JButton durdur;
	private JComboBox cozum_kumesi;
	private JCheckBox dosyaya_yaz;
	private Gauge bar;
	private static PSOScreen instance;
	public static PSOScreen instance(){
		return instance;
	}
	public PSOScreen() {
		
		instance = this;
		Container con1 = getContentPane();
		con1.setLayout(new GridBagLayout());
		GridBagConstraints g;
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.gridheight = 2;
		g.fill = GridBagConstraints.BOTH;
		con1.add(ayarlar(),g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 0;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		con1.add(yonetim(),g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 1;
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		con1.add(PSO.instance().getCozumEkrani(),g);
		
		setVisible(true);
		setSize(800,600);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void pso_started(){
		coz.setEnabled(false);
		durdur.setEnabled(true);
	}
	public void pso_stopped(){
		coz.setEnabled(true);
		durdur.setEnabled(false);
	}
	private JPanel yonetim(){
		GridBagConstraints g;
		JPanel yonetim = new JPanel();
		yonetim.setLayout(new GridBagLayout());
		coz = new JButton("Çözümü Başlat");
		durdur = new JButton("Çözümü Durdur");
		bar = new Gauge();
		durdur.setEnabled(false);
		coz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				set_pso();
				PSO.instance().baslat();
				coz.setEnabled(false);
				durdur.setEnabled(true);
			}
		});
		durdur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PSO.instance().durdur();
				coz.setEnabled(true);
				durdur.setEnabled(false);
			}
		});
		cozum_kumesi = new JComboBox();
		cozum_kumesi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cozum_goster();
			}
		});
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.weightx = 1;
		yonetim.add(coz,g);
		
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 0;
		g.weightx = 1;
		yonetim.add(durdur,g);
		
		g = new GridBagConstraints();
		g.gridx = 2;
		g.gridy = 0;
		g.weightx = 1;
		yonetim.add(cozum_kumesi,g);
		
		
		
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 1;
		g.weightx = 1;
		g.gridwidth = 3;
		g.fill = GridBagConstraints.HORIZONTAL;
		problem_ismi = new JLabel("Problem: ");
		yonetim.add(problem_ismi,g);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 2;
		g.weightx = 1;
		g.gridwidth = 3;
		g.fill = GridBagConstraints.HORIZONTAL;
		makespan = new JLabel("Yayılma Zamanı: ");
		yonetim.add(makespan,g);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 3;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.gridwidth = 3;
		current_iterasyon_sayisi = new JLabel("İterasyon Sayısı: ");
		yonetim.add(current_iterasyon_sayisi,g);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 4;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.gridwidth = 3;
		current_time = new JLabel("Zaman: ");
		yonetim.add(current_time,g);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 5;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.gridwidth = 3;
		en_iyi_cozume_ulasma_zamani = new JLabel("En iyi çözüme ulaşma zamanı: ");
		yonetim.add(en_iyi_cozume_ulasma_zamani,g);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 6;
		g.gridwidth = 2;
		g.weightx = 1;
		g.gridwidth = 3;
		g.fill = GridBagConstraints.HORIZONTAL;
		
		yonetim.add(bar,g);
		return yonetim;
	}
	private void cozum_goster(){
		if (cozum_kumesi.getItemCount() > 0){
			Cozum i = (Cozum)cozum_kumesi.getSelectedItem();
			pso_set_current_iterasyon(i.iterasyon_sayisi);
			pso_set_current_time(i.toplam_zaman);
			pso_set_en_iyi_ulasim_zamani(i.en_iyi_cozum_zaman);
			pso_set_makespan(i.yayilma_zaman);
			PSO.instance().setCozum(i);
		}
		
	}
	private JPanel ayarlar(){
		
		JPanel ayarlar= new JPanel();
		ayarlar.setLayout(new GridBagLayout());
		GridBagConstraints g;
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("Problem İsmi"),g);
		
		genel_problemler = new JComboBox();
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(genel_problemler,g);
		
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 2;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("Makine Sayısı"),g);
		
		makine_sayisi = new JComboBox();
		makine_sayisi.addItem(new ComboItem("Hepsi",-1));
		makine_sayisi.addItem(new ComboItem("5 Makine",5));
		makine_sayisi.addItem(new ComboItem("10 Makine",10));
		makine_sayisi.addItem(new ComboItem("15 Makine",15));
		makine_sayisi.addItem(new ComboItem("20 Makine",20));
		makine_sayisi.setSelectedIndex(1);
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 3;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(makine_sayisi,g);
		makine_sayisi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					problem_kumesini_tekrar_belirle();
				}
			}
		});
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 4;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("İş Sayısı"),g);
		
		is_sayisi= new JComboBox();
		is_sayisi.addItem(new ComboItem("Hepsi",-1));
		is_sayisi.addItem(new ComboItem("10 İş",10));
		is_sayisi.addItem(new ComboItem("20 İş",20));
		is_sayisi.addItem(new ComboItem("15 İş",15));
		is_sayisi.addItem(new ComboItem("20 İş",20));
		is_sayisi.addItem(new ComboItem("30 İş",30));
		is_sayisi.addItem(new ComboItem("50 İş",50));
		is_sayisi.addItem(new ComboItem("70 İş",70));
		is_sayisi.addItem(new ComboItem("100 İş",100));
		is_sayisi.addItem(new ComboItem("150 İş",150));
		is_sayisi.addItem(new ComboItem("200 İş",200));
		is_sayisi.addItem(new ComboItem("300 İş",300));
		is_sayisi.addItem(new ComboItem("500 İş",500));
		is_sayisi.setSelectedIndex(5);
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 5;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(is_sayisi,g);
		is_sayisi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					problem_kumesini_tekrar_belirle();
				}
			}
		});
		
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 6;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("Parçacık Sayısı"),g);
		
		parcacik_sayisi = new JComboBox();
		parcacik_sayisi.addItem(new ComboItem("10 Parçacık",10));
		parcacik_sayisi.addItem(new ComboItem("20 Parçacık",20));
		parcacik_sayisi.addItem(new ComboItem("30 Parçacık",30));
		parcacik_sayisi.addItem(new ComboItem("50 Parçacık",50));

		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 7;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(parcacik_sayisi,g);
	
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 8;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("Test Türü"),g);
		
		test_turu = new JComboBox();
		test_turu.addItem(new ComboItem("Hepsi",-1));
		test_turu.addItem(new ComboItem("Taillard",0));
		test_turu.addItem(new ComboItem("Diğer",1));

		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 9;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(test_turu,g);
		test_turu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED){
					problem_kumesini_tekrar_belirle();
				}
			}
		});
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 10;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("Zaman Kısıtı"),g);
		
		zaman_kisiti = new JComboBox();
		zaman_kisiti.addItem(new ComboItem("Kısıt Yok",-1));
		zaman_kisiti.addItem(new ComboItem("1 Dakika",1));
		zaman_kisiti.addItem(new ComboItem("2 Dakika",2));
		zaman_kisiti.addItem(new ComboItem("5 Dakika",5));
		zaman_kisiti.addItem(new ComboItem("10 Dakika",10));
		zaman_kisiti.setSelectedIndex(1);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 11;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(zaman_kisiti,g);
		
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 12;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("İterasyon Kısıtı"),g);
		
		iterasyon_sayisi = new JComboBox();
		iterasyon_sayisi.addItem(new ComboItem("Kısıt Yok",-1));
		iterasyon_sayisi.addItem(new ComboItem("10 İterasyon",10));
		iterasyon_sayisi.addItem(new ComboItem("50 İterasyon",50));
		iterasyon_sayisi.addItem(new ComboItem("100 İterasyon",100));
		iterasyon_sayisi.addItem(new ComboItem("1000 İterasyon",1000));
		iterasyon_sayisi.addItem(new ComboItem("10000 İterasyon",10000));
		iterasyon_sayisi.addItem(new ComboItem("50000 İterasyon",50000));
		iterasyon_sayisi.setSelectedIndex(4);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 13;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(iterasyon_sayisi,g);
		
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 14;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(new JLabel("Deneme Sayısı"),g);
		
		deneme_sayisi = new JComboBox();
		deneme_sayisi.addItem(new ComboItem("1 Deneme",1));
		deneme_sayisi.addItem(new ComboItem("5 Deneme",5));
		deneme_sayisi.addItem(new ComboItem("10 Deneme",10));
		deneme_sayisi.addItem(new ComboItem("20 Deneme",20));
		deneme_sayisi.setSelectedIndex(1);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 15;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(deneme_sayisi,g);
		
		
		dosyaya_yaz = new JCheckBox("Dosyaya Yaz");
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 16;
		g.fill = GridBagConstraints.HORIZONTAL;
		ayarlar.add(dosyaya_yaz,g);
		problem_kumesini_tekrar_belirle();
		return ayarlar;
	}
	private void problem_kumesini_tekrar_belirle(){
		ComboItem is = (ComboItem)is_sayisi.getSelectedItem();
		ComboItem makine = (ComboItem)makine_sayisi.getSelectedItem();
		ComboItem problem= (ComboItem)test_turu.getSelectedItem();
		genel_problemler.removeAllItems();
		for (int i = 0; i < Test.names.length; i++) {
			if (problem.option == 0){
				if (Test.names[i].indexOf("_ta") == -1){
					continue;
				}
			} else if (problem.option == 1){
				if (Test.names[i].indexOf("_ta") != -1){
					continue;
				}
			}
			if (is.option != -1){
				if (!Test.names[i].startsWith(""+is.option+"_")){
					continue;
				}
			}
			if (makine.option != -1){
				if (Test.names[i].indexOf("_"+makine.option+"_") == -1){
					continue;
				}
			}
			String s[] = parse(Test.names[i].substring(0,Test.names[i].length()-4));
			if (s.length == 3){
				genel_problemler.addItem(new ComboItem(s[0]+" İş, "+s[1]+" Makine ("+s[2]+")", i));
			} else {
				genel_problemler.addItem(new ComboItem(s[0]+" İş, "+s[1]+" Makine ("+s[3]+")", i));
			}
		}
		
		
		
		
		
	}
	private String[] parse(String txt){
		Vector v = new Vector();
		int loc0 = 0;
		int loc1 = txt.indexOf('_');
		while (loc1 != -1){
			v.add(txt.substring(loc0, loc1));
			loc0 = loc1+1;
			loc1 = txt.indexOf('_',loc0);
		}
		if (loc0 < txt.length() - 1){
			v.add(txt.substring(loc0, txt.length()));
		}
		String s[] = new String[v.size()];
		v.copyInto(s);
		return s;
	}
	public JLabel getLabel(Color c,String str){
		JLabel l = new JLabel(str);
		l.setOpaque(true);
		l.setBackground(c);
		return l;
	}
	public void init() {
	
	}
	public void pso_set_current_iterasyon(int m){
		current_iterasyon_sayisi.setText("İterasyon Sayısı: "+m);
	}
	public void pso_set_name(String name){
		problem_ismi.setText("Problem: "+name);
	}
	public void pso_set_en_iyi_ulasim_zamani(long m){
		en_iyi_cozume_ulasma_zamani.setText("En iyi çözüme ulaşma zamanı: "+m+" ms");
	}
	public void pso_cozum_ekle(Cozum cozum){
		int count = cozum_kumesi.getItemCount();
		cozum.label = ""+(count+1)+". Çözüm";
		cozum_kumesi.addItem(cozum);
		int min;
		int min_index;
		Cozum c = (Cozum)cozum_kumesi.getItemAt(0);
		min = c.yayilma_zaman;
		min_index = 0;
		
		for (int i = 1; i < cozum_kumesi.getItemCount(); i++) {
			c = (Cozum)cozum_kumesi.getItemAt(i);
			if (c.yayilma_zaman < min){
				min = c.yayilma_zaman ;
				min_index = i;
			}
		}
		
		for (int i = 1; i < cozum_kumesi.getItemCount(); i++) {
			c = (Cozum)cozum_kumesi.getItemAt(i);
			if (i == min_index){
				c.label = ""+(i+1)+". Çözüm(En İyi)";
			} else  {
				c.label = ""+(i+1)+". Çözüm";
			}
		}
	}
	public void pso_set_current_time(long m){
		current_time.setText("Zaman: "+m+" ms");
	}
	public void pso_set_makespan(int m){
		makespan.setText("Yayılma Zamanı: "+m);
	}
	public void pso_set_current_percent(int percent){
		bar.setCurrentAmount(percent);
	}
	private void set_pso(){
		bar.setCurrentAmount(0);
		makespan.setText("Üretim Zamanı: ");
		current_iterasyon_sayisi.setText("İterasyon Sayısı: ");
		problem_ismi.setText("Problem: ");
		current_time.setText("Zaman: ");
		cozum_kumesi.removeAllItems();
		PSO.instance().setDosyayaYaz(dosyaya_yaz.isSelected());
		PSO.instance().setProblemIndex(((ComboItem)genel_problemler.getSelectedItem()).option);
		PSO.instance().setMaksimum_iterasyon(((ComboItem)iterasyon_sayisi.getSelectedItem()).option);
		PSO.instance().setParcacik_sayisi(((ComboItem)parcacik_sayisi.getSelectedItem()).option);
		PSO.instance().setZaman_kisiti(((ComboItem)zaman_kisiti.getSelectedItem()).option);
		PSO.instance().setDenemeSayisi(((ComboItem)deneme_sayisi.getSelectedItem()).option);
	}
	class ComboItem {
		String lbl;
		int option;
		public ComboItem(String lbl,int option) {
			this.lbl = lbl;
			this.option = option;
		}
		public String toString() {
			return lbl;
		}
	}
}

class Gauge extends JComponent {
    
	  // the current and total amounts that the gauge reperesents
	  int current = 0;
	  int total = 100;

	  // The preferred size of the gauge
	  int Height = 18;   // looks good
	  int Width  = 250;  // arbitrary 

	  /**
	   * Constructs a Gauge
	   */
	  public Gauge() {
	      this(Color.lightGray);
	  }

	  /**
	   * Constructs a that will be drawn uses the
	   * specified color.
	   *
	   * @gaugeColor the color of this Gauge
	   */
	  public Gauge(Color gaugeColor) {
	      setBackground(gaugeColor);
	  }

	  public void paint(Graphics g) {
	      int barWidth = (int) (((float)current/(float)total) * getSize().width);
	      g.setColor(getBackground());
	      g.fill3DRect(0, 0, barWidth, getSize().height-2, true);
	  }

	  public void setCurrentAmount(int Amount) {
	      current = Amount; 

	      // make sure we don't go over total
	      if(current > 100)
	       current = 100;

	      repaint();
	  }

	  public int getCurrentAmount() {
	      return current;
	  }

	  public int getTotalAmount() {
	      return total;
	  }

	  public Dimension getPreferredSize() {
	      return new Dimension(Width, Height);
	  }

	  public Dimension getMinimumSize() {
	      return new Dimension(Width, Height);
	  }
	}
