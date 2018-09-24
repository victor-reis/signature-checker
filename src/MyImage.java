import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MyImage {

	int largura = 3000;
	int altura = 1687;
	BufferedImage image = null;
	File diretorio = null;
	int frequencia[] = new int[256];


	public boolean criaArquivo() {

		try {

			diretorio = new File("/home/victor/paisagem");
			System.out.println("Arquivo lido com sucesso!");
		} catch (Exception e) {
			System.out.println("Arquivo nao existe ou diretorio eh invalido!");
		}

		return true;

	}

	public void carregaImagem() throws IOException {

		try {
			image = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
			image = ImageIO.read(diretorio);
		} catch (IllegalArgumentException iae) {
			System.out.println("A largura e Altura devem ser maiores do que 0!!!");
		}
	}

	public void armazenaFrequencia(BufferedImage bimg) {

		for (int y = 0; y < getAltura(); y++) {
			for (int x = 0; x < getLargura(); x++) {

				int rgbInteger = bimg.getRGB(x, y);

				Color corPixel = new Color(rgbInteger);

				int vermelho = corPixel.getRed();

				frequencia[vermelho]++;
			}
		}

	}

	public void printaValores() {

		for(int i = 0; i < frequencia.length; i++) {
			System.out.println(frequencia[i]);
		}

	}

	public int getLargura() {
		return largura;
	}

	public void setLargura(int largura) {
		this.largura = largura;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public static void main(String args[]) throws IOException {

		MyImage img1 = new MyImage();

		img1.setLargura(960);
		img1.setAltura(640);
		img1.criaArquivo();
		img1.carregaImagem();
		img1.armazenaFrequencia(img1.image);
		img1.printaValores();


		BarPlotHistogram H = new BarPlotHistogram(img1.frequencia);
	}
}



