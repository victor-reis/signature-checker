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

	int largura = 960;
	int altura = 640;
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

			diretorio = new File("/home/victor/praiaCinza");
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
			File output_file = new File("/home/victor/saidaComButton");

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

		for (int i = 0; i < frequencia.length; i++) {
			System.out.println(frequencia[i]);
		}

	}

	public void alteraBrilho(int valor) {
		for (int y = 0; y < getAltura(); y++) {
			for (int x = 0; x < getLargura(); x++) {

				int rgbInteger = image.getRGB(x, y);
				Color corPixel = new Color(rgbInteger);

				int vermelho = corPixel.getRed() + valor;
				int azul = corPixel.getBlue() + valor;
				int verde = corPixel.getGreen() + valor;
				if (vermelho < 0) vermelho = 0;
				if (verde < 0) verde = 0;
				if (azul < 0) azul = 0;
				if (vermelho > 254) vermelho = 254;
				if (verde > 254) verde = 254;
				if (azul > 254) azul = 254;

				Color novoPixel = new Color(verde, vermelho, azul);
				image.setRGB(x, y, novoPixel.getRGB());
			}
		}

	}

	public BufferedImage filtraImagem(String tipoDeFiltro) {
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

	public void printHistograma() {
		armazenaFrequencia();
		BarPlotHistogram hist = new BarPlotHistogram(frequencia, tipo);

	}

	public static void main(String args[]) throws IOException {

		MyImage imagemOriginal = new MyImage();
		imagemOriginal.setTipo("Original");
		imagemOriginal.setLargura(512);
		imagemOriginal.setAltura(384);
		imagemOriginal.criaArquivo();
		imagemOriginal.carregaImagem();

		MyImage imagemFiltrada = new MyImage();
		imagemFiltrada.setTipo("Tratada");
		imagemFiltrada.setLargura(512);
		imagemFiltrada.setAltura(384);
		imagemFiltrada.criaArquivo();
		imagemFiltrada.carregaImagem();
		imagemFiltrada.filtraImagem("media");

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
				imagemOriginal.filtraImagem("mediana");
				imagemOriginal.filtraImagem("media");
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
		//janela.pack();
		janela.setVisible(true);
	}
}