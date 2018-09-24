import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MyImage {

	public static int RESOLUCAO_DE_CONTRASTE = 255;
	int largura = 640;
	int altura = 378;
	BufferedImage image = null;
	File diretorio = null;
	int frequencia[] = new int[256];

	String tipo;

	public void setTipo(String tipo) {
		this.tipo = tipo;
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

	public boolean criaArquivo() {

		try {

			diretorio = new File("/home/victor-reis/Pictures/bwimg.jpg");
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

	public void escreveImagem() {
		try {
			// Output file path
			File output_file = new File("/home/victor-reis/Pictures/");

			// Writing to file taking type and path as
			ImageIO.write(image, "jpg", output_file);

			System.out.println("Writing complete.");
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}

	public void armazenaFrequencia() {

		for (int y = 0; y < getAltura(); y++) {
			for (int x = 0; x < getLargura(); x++) {

				int rgbInteger = image.getRGB(x, y);
				Color corPixel = new Color(rgbInteger);
				int vermelho = corPixel.getRed();

				frequencia[vermelho]++;
			}
		}

	}

	public void printaValores() {
for (int valor:	 frequencia) System.out.println(valor);
	}

	public int verificaLimites(int pixel){
		if(pixel > RESOLUCAO_DE_CONTRASTE){
			pixel = RESOLUCAO_DE_CONTRASTE;
		}else if (pixel < 0){
			pixel = 0;
		}
		return pixel;
	}

	public void alteraBrilho(int valor) {
		for (int y = 0; y < getAltura(); y++) {
			for (int x = 0; x < getLargura(); x++) {

				int rgbInteger = image.getRGB(x, y);
				Color corPixel = new Color(rgbInteger);

				int vermelho = corPixel.getRed() + valor;
				int azul = corPixel.getBlue() + valor;
				int verde = corPixel.getGreen() + valor;
				vermelho = verificaLimites(vermelho);
				azul = verificaLimites(azul);
				verde = verificaLimites(verde);

				Color novoPixel = new Color(verde, vermelho, azul);
				image.setRGB(x, y, novoPixel.getRGB());
			}
		}

	}

	public BufferedImage filtraImagem(String tipoDeFiltro, int valorAuxiliar) {
		int[][] imgOrigin = new int[getAltura()][getLargura()];
		int[][] imgDestino = new int[getAltura()][getLargura()];

		for (int lin = 0; lin < getAltura(); lin++) {
			for (int col = 0; col < getLargura(); col++) {

				int tomCinza = image.getRGB(col, lin);
				Color c = new Color(tomCinza);
				int tomRed = c.getRed();
				imgOrigin[lin][col] = tomRed;

			}
		}
		for (int lin = 1; lin < getAltura() - 2; lin++)
			for (int col = 1; col < getLargura() - 2; col++)
				switch (tipoDeFiltro) {
					case "media":
						imgDestino[lin][col] = calculaMedia(imgOrigin, col, lin);
						break;
					case "mediana":
						imgDestino[lin][col] = calculaMediana(imgOrigin, col, lin);
						break;
					case "quantizacao":
						imgDestino[lin][col] = quantizacao(imgOrigin[lin][col], valorAuxiliar);
						break;
					case "split":
						imgDestino[lin][col] = splitting(imgOrigin[lin][col], valorAuxiliar);
						break;
					case "gradiente":
						imgDestino[lin][col] = gradiente(imgOrigin, col, lin);
						break;
				}
		for (int lin = 1; lin < getAltura() - 2; lin++) {
			for (int col = 1; col < getLargura() - 2; col++) {
				Color novoPixel = new Color(imgDestino[lin][col], imgDestino[lin][col], imgDestino[lin][col]);
				image.setRGB(col, lin, novoPixel.getRGB());
			}
		}
		return image;
	}

	public int calculaMedia(int[][] imgOrigin, int col, int lin) {
		int soma = 0;
		for (int c = col - 1; c <= col + 1; c++)
			for (int l = lin - 1; l <= lin + 1; l++)
				soma += imgOrigin[l][c];

		int media = soma / 9;

		return media;
	}

	public int calculaMediana(int[][] imgOrigin, int col, int lin) {
		ArrayList<Integer> mediana = new ArrayList<>();
		for (int c = col - 1; c <= col + 1; c++)
			for (int l = lin - 1; l <= lin + 1; l++)
				mediana.add(imgOrigin[l][c]);

		Collections.sort(mediana);

		return mediana.get(4);
	}

	public int quantizacao(int pixel, int novaResolucao){
		int intervalo = RESOLUCAO_DE_CONTRASTE / novaResolucao;
		pixel = pixel / intervalo;
		pixel = verificaLimites(pixel  * intervalo);
		return pixel;
	}

	public int splitting(int pixel, int jump){
		return (pixel > RESOLUCAO_DE_CONTRASTE/2)? verificaLimites(pixel + jump) :verificaLimites(pixel - jump);
	}

	public int equalizacao(){return 0;}

	public int gradiente(int[][] imgOrigin, int col, int lin){
		int pixel = Math.abs(imgOrigin[lin][col] - imgOrigin[lin][col + 1])
				+ Math.abs(imgOrigin[lin][col] - imgOrigin[lin + 1][col]);
		pixel = verificaLimites(pixel);
		return pixel;
	}

	public void printHistograma() {
		armazenaFrequencia();
		BarPlotHistogram hist = new BarPlotHistogram(frequencia, tipo);

	}

	public static void main(String args[]) throws IOException {

		MyImage imagemOriginal = new MyImage();
		imagemOriginal.setTipo("Original");
		imagemOriginal.criaArquivo();
		imagemOriginal.carregaImagem();

		MyImage imagemFiltrada = new MyImage();
		imagemFiltrada.setTipo("Tratada");
		imagemFiltrada.criaArquivo();
		imagemFiltrada.carregaImagem();
		imagemFiltrada.filtraImagem("gradiente",0);

		imagemFiltrada.printHistograma();
		imagemOriginal.printHistograma();

		ImageIcon imageIcon = new ImageIcon(imagemOriginal.image);
		JLabel jlabel = new JLabel(imageIcon);

		ImageIcon imageIcon1 = new ImageIcon(imagemFiltrada.image);
		JLabel jlabel1 = new JLabel(imageIcon1);

		JButton botaoCarregar = new JButton("Tratar imagem");
		botaoCarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("aa");
				imagemOriginal.filtraImagem("mediana",0);
				imagemOriginal.filtraImagem("media",0);
				imagemOriginal.escreveImagem();
			}
		});

		JButton botaoSair = new JButton("Sair");
		botaoSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JPanel painel = new JPanel();
		painel.add(jlabel);
		painel.add(jlabel1);
		painel.add(botaoCarregar);
		painel.add(botaoSair);


		JFrame janela = new JFrame("Computação gráfica");

		janela.add(painel);
		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setSize(1150, 500);
		janela.pack();
		janela.setVisible(true);
	}
}