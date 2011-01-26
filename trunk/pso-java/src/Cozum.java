
public class Cozum {
	int yayilma_zaman;
	int iterasyon_sayisi;
	long toplam_zaman;
	long en_iyi_cozum_zaman;
	int cozum[];
	int table[][];
	String label;
	public String toString() {
		return label;
	}
	public Cozum getClone()  {
		Cozum c = new Cozum();
		c.yayilma_zaman = yayilma_zaman;
		c.iterasyon_sayisi = iterasyon_sayisi;
		c.toplam_zaman = toplam_zaman;
		c.en_iyi_cozum_zaman = c.en_iyi_cozum_zaman ;
		c.cozum = new int[cozum.length];
		System.arraycopy(cozum, 0, c.cozum, 0, cozum.length);
		c.table = new int[table.length][table[0].length];
		for (int i = 0; i < c.table.length; i++) {
			System.arraycopy(table[i], 0, c.table[i], 0, table[i].length);
		}
		return c;
	}
}
