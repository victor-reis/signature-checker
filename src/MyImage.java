    import javax.imageio.ImageIO;
    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Collections;

    public class MyImage {


        private static int[][] MASCARA_PADRAO = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
        private static int[][] MASCARA_DE_SOBEL = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};
        private static int[][] MASCARA_DE_PREWITT = {{1,0, 1}, {0, 0, 0}, {-1, -1, -1}};
        private static int[][] MASCARA_DE_PASSA_ALTA = {{-1,-1,-1}, {-1,8,-1}, {-1, -1, -1}};
        private static int[][] MASCARA_HORIZONTAL = {{-1,-1,-1},{2,2,2},{-1,-1,-1}};
        private static int[][] MASCARA_VERTICAL =    {{-1,2,-1},{-1,2,-1},{-1,2,-1}};
        private static int[][] MASCARA_45_POSITIVO = {{-1,-1,2},{-1,2,-1},{2,-1,-1}};
        private static int[][] MASCARA_45_NEGATIVO = {{2,-1,-1},{-1,2,-1},{-1,-1,2}};
        private static int[][] MASCARA_LAPLACIANO = {{0,-1,0},{-1,4,-1},{0,-1,0}};
        private static int CONTADOR_SEMENTE_EXPANDIDA = 0;

        private static int VIZINHA = 1;
        private static int RESOLUCAO_DE_CONTRASTE = 255;
        private static double CONSTANTE_DIFERENCA = 1;

        private int largura;
        private int altura;
        private BufferedImage image = null;
        private File diretorio = null;
        private int frequencia[] = new int[256];
        private int frequenciaAcumulada[] = new int[256];

        private String path = "/home/victor-reis/Pictures/assinaturas/doc-trabalho-cg.png";

        private String tipo;

        private void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public void setDimensoes(){
            largura = image.getWidth() ;
            altura = image.getHeight() ;
        }

        private int getLargura() {
            return largura;
        }

        private int getAltura() {
            return altura;
        }

        public Point defineSemente(){
         return new Point(largura/2,altura/2);
        }

        private boolean criaArquivo() {

            try {
                diretorio = new File(this.path);
                System.out.println("Arquivo lido com sucesso!");
            } catch (Exception e) {
                System.out.println("Arquivo nao existe ou diretorio eh invalido!");
            }
            return true;

        }

        private void carregaImagem() throws IOException {

            try {
                image = ImageIO.read(diretorio);
                this.largura = image.getWidth() - 1 ;
                this.altura = image.getHeight() - 1;
            } catch (IllegalArgumentException iae) {
                System.out.println("A largura e Altura devem ser maiores do que 0!!!");
            }
            armazenaFrequencia();
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

        private void armazenaFrequencia() {

            for (int y = 0; y < getAltura(); y++) {
                for (int x = 0; x < getLargura(); x++) {

                    int rgbInteger = image.getRGB(x, y);
                    Color corPixel = new Color(rgbInteger);
                    int vermelho = corPixel.getRed();

                    frequencia[vermelho]++;
                }
            }

            frequenciaAcumulada();

        }

        private void frequenciaAcumulada(){

            frequenciaAcumulada[0] = frequencia[0];
            for(int i = 1; i < RESOLUCAO_DE_CONTRASTE; i++)
                frequenciaAcumulada[i] = frequencia[i] + frequenciaAcumulada[i-1];

        }

        public int quantidadeTotalDePixel(){

           int totalDePixel = 0;
            for(int i = 0; i < RESOLUCAO_DE_CONTRASTE; i++)
                totalDePixel = frequencia[i];

            return totalDePixel;
        }

        public int menorFrequencia(){
         int menorValor = RESOLUCAO_DE_CONTRASTE;
            for (int x = 0; x < RESOLUCAO_DE_CONTRASTE; x++){
                if (frequencia[x] <= menorValor) menorValor = frequencia[x];
            }
          return menorValor;
        }

        public void printaValores() {
            for (int valor : frequencia) System.out.println(valor);
        }

        private int verificaLimites(int pixel) {
            if (pixel > RESOLUCAO_DE_CONTRASTE) {
                pixel = RESOLUCAO_DE_CONTRASTE;
            } else if (pixel < 0) {
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

        private int[][] criaMatriz(BufferedImage image){
            int[][] imgOrigin = new int[getAltura()][getLargura()];

            for (int lin = 0; lin < getAltura(); lin++) {
                for (int col = 0; col < getLargura(); col++) {

                    int tomCinza = image.getRGB(col, lin);
                    Color c = new Color(tomCinza);
                    int tomRed = c.getRed();
                    imgOrigin[lin][col] = tomRed;

                }
            }
            return imgOrigin;
        }

        public void filtraImagem(String tipoDeFiltro, double valorAuxiliar) {
            int[][] imgOrigin = criaMatriz(image);
            int[][] imgDestino = new int[getAltura()][getLargura()];

            int alturaVizinha = getAltura() - VIZINHA - 1;
            int larguraVizinha = getLargura() - VIZINHA - 1;



            for (int lin = VIZINHA; lin < alturaVizinha; lin++)
                for (int col = VIZINHA; col < larguraVizinha; col++)
                    switch (tipoDeFiltro) {
                        case "media":
                            imgDestino[lin][col] = (int)calculaMedia(imgOrigin, col, lin);
                            break;
                        case "mediana":
                            imgDestino[lin][col] = calculaMediana(imgOrigin, col, lin);
                            break;
                        case "quantizacao":
                            imgDestino[lin][col] = quantizacao(imgOrigin[lin][col], (int)valorAuxiliar);
                            break;
                        case "split":
                            imgDestino[lin][col] = splitting(imgOrigin[lin][col], (int) valorAuxiliar);
                            break;
                        case "equalizacao":
                            imgDestino[lin][col] = equalizacao(imgOrigin[lin][col]);
                            break;
                        case "gradiente":
                            imgDestino[lin][col] = gradienteHV(imgOrigin, col, lin);
                            break;
                        case "gradienteHorizontal":
                            imgDestino[lin][col] = gradienteHorizontal(imgOrigin, col, lin);
                            break;
                        case "gradienteVertical":
                            imgDestino[lin][col] = gradienteVertical(imgOrigin, col, lin);
                            break;
                        case "sobel":
                            imgDestino[lin][col] = sobelHV(imgOrigin, col, lin);
                            break;
                        case "sobelHorizontal":
                            imgDestino[lin][col] = sobelHorizontal(imgOrigin, col, lin);
                            break;
                        case "sobelVertical":
                            imgDestino[lin][col] = sobelVertical(imgOrigin, col, lin);
                            break;
                        case "prewitt":
                            imgDestino[lin][col] = prewittHV(imgOrigin, col, lin);
                            break;
                        case "passaAlta":
                            imgDestino[lin][col] = passaAlta(imgOrigin, col, lin);
                            break;
                        case "limiar":
                            imgDestino[lin][col] = limiarizacao(imgOrigin,col,lin,valorAuxiliar);
                            break;
                        case "limiarMedia":
                            imgDestino[lin][col] = limiarizacaoMedia(imgOrigin, col, lin);
                            break;
                        case "limiarDinamica":
                            imgDestino[lin][col] = limiarizacaoDinamica(imgOrigin, col, lin);
                            break;
                        case "limiarPassaAlta":
                            imgDestino[lin][col] = limiarizacaoPassaAlta(imgOrigin,col,lin,(int)valorAuxiliar);
                            break;
                    }
            pintaImagem(imgDestino);
           }

        private double calculaMedia(int[][] imgOrigin, int col, int lin) {
            int soma = 0;
            for (int c = col - VIZINHA; c <= col + VIZINHA; c++)
                for (int l = lin - VIZINHA; l <= lin + VIZINHA; l++)
                    soma += imgOrigin[l][c];

            double media = (VIZINHA*2)+1;
            media = soma /(media*media);

            return media;
        }

        private int calculaMediana(int[][] imgOrigin, int col, int lin) {
            ArrayList<Integer> mediana = new ArrayList<>();
            for (int c = col - 1; c <= col + 1; c++)
                for (int l = lin - 1; l <= lin + 1; l++)
                    mediana.add(imgOrigin[l][c]);

            Collections.sort(mediana);

            return mediana.get(4);
        }

        private int quantizacao(int pixel, int novaResolucao) {
            int intervalo = RESOLUCAO_DE_CONTRASTE / novaResolucao;
            pixel = pixel / intervalo;
            pixel = verificaLimites(pixel * intervalo);
            return pixel;
        }

        private int splitting(int pixel, int jump) {
            return (pixel > RESOLUCAO_DE_CONTRASTE / 2) ? verificaLimites(pixel + jump) : verificaLimites(pixel - jump);
        }

        private int equalizacao(int pixel) {
            int numeroIdeal = (altura * largura) / RESOLUCAO_DE_CONTRASTE;

            int pixelEqualizado = frequenciaAcumulada[pixel] / numeroIdeal;
            if (0 > pixelEqualizado - 1) pixelEqualizado = 0;

            return pixelEqualizado;
        }

        private int passaAlta(int[][] imgOrigin, int col, int lin){
            int pixel = 0;
            for (int linha = 0; linha < 3; linha++)
                for (int coluna = 0; coluna < 3; coluna++)
                    pixel += imgOrigin[lin + linha - 1][col + coluna - 1] * MASCARA_DE_PASSA_ALTA[coluna][linha];

            return verificaLimites(pixel);
        }

        private int gradienteHV(int[][] imgOrigin, int col, int lin) {
            int pixel = Math.abs(imgOrigin[lin][col] - imgOrigin[lin][col + 1])
                    + Math.abs(imgOrigin[lin][col] - imgOrigin[lin + 1][col]);
            return verificaLimites(pixel);
        }

        private int gradienteHorizontal(int[][] imgOrigin, int col, int lin) {
            int pixel = -imgOrigin[lin][col] - imgOrigin[lin][col + 1] + imgOrigin[lin+1][col] + imgOrigin[lin+1][col+1];
            return verificaLimites(pixel);
        }

        private int gradienteVertical(int[][] imgOrigin, int col, int lin) {
            int pixel = Math.abs(- imgOrigin[lin][col] + imgOrigin[lin][col + 1] - imgOrigin[lin + 1][col] + imgOrigin[lin+1][col+1]);
            pixel = verificaLimites(pixel);
            return pixel;
        }

        private int sobelHorizontal(int[][] imgOrigin, int col, int lin){
            int pixelAltura = 0;
            for (int linha = 0; linha < 3; linha++)
                for (int coluna = 0; coluna < 3; coluna++)
                    pixelAltura += imgOrigin[lin + linha - 1][col + coluna - 1] * MASCARA_DE_SOBEL[linha][coluna];


            return verificaLimites(pixelAltura);
        }

        private int sobelVertical(int[][] imgOrigin, int col, int lin){
            int pixelLargura = 0;
                for (int linha = 0; linha < 3; linha++)
                    for (int coluna = 0; coluna < 3; coluna++)
                        pixelLargura += imgOrigin[lin + linha - 1][col + coluna - 1] * MASCARA_DE_SOBEL[coluna][linha];

            return verificaLimites(pixelLargura);
        }

        private int sobelHV(int[][] imgOrigin, int col, int lin) {

            int novoPixel, px,py;
            px = sobelHorizontal(imgOrigin, col, lin)/4;
            py = sobelVertical(imgOrigin, col, lin) /4;
            novoPixel = (px + py)/2;

            return verificaLimites(novoPixel);
        }

        private int prewittHorizontal(int[][] imgOrigin, int col, int lin){
            int pixelAltura = 0;
            for (int linha = 0; linha < 3; linha++)
                for (int coluna = 0; coluna < 3; coluna++)
                    pixelAltura += imgOrigin[lin + linha - 1][col + coluna - 1] * MASCARA_DE_PREWITT[linha][coluna];

            return verificaLimites(pixelAltura);
        }

        private int prewittVertical(int[][] imgOrigin, int col, int lin){
            int pixelLargura = 0;
            for (int linha = 0; linha < 3; linha++)
                for (int coluna = 0; coluna < 3; coluna++)
                    pixelLargura += imgOrigin[lin + linha - 1][col + coluna - 1] * MASCARA_DE_PREWITT[coluna][linha];

            return verificaLimites(pixelLargura);
        }

        private int prewittHV(int[][] imgOrigin, int col, int lin) {

            int novoPixel;

            novoPixel = (prewittHorizontal(imgOrigin, col, lin)/4 + prewittVertical(imgOrigin, col, lin) /4 ) /2;
            return verificaLimites(novoPixel);
        }

        public void printHistograma() {
            armazenaFrequencia();
            BarPlotHistogram hist = new BarPlotHistogram(frequencia, tipo);
        }

        public String verificaInclinacao(){
            String inclinacao = "XXXX";

            int[][] imgOrigin = criaMatriz(image);

            int[] pixel = new int[5];// V,H,+45,-45,empate
            int[] pontuacao = new int[5];

            int alturaVizinha = getAltura() - VIZINHA - 1;
            int larguraVizinha = getLargura() - VIZINHA - 1;

            int maiorValor;
            int indicePixel;

            for (int lin = VIZINHA; lin < alturaVizinha; lin++)
                for (int col = VIZINHA; col < larguraVizinha; col++){
                    pixel[0] = rodaMascaraGeneric(imgOrigin,col,lin,"horizontal");
                    pixel[1] = rodaMascaraGeneric(imgOrigin,col,lin,"vertical");
                    pixel[2] = rodaMascaraGeneric(imgOrigin,col,lin,"45+");
                    pixel[3] = rodaMascaraGeneric(imgOrigin,col,lin,"45-");

                    maiorValor=0;
                    indicePixel=4;
                    for(int i = 0; i < pontuacao.length - 1; i++)
                        if(pixel[i] > maiorValor){
                            maiorValor = pixel[i];
                            indicePixel = i;
                        }
                    pontuacao[indicePixel]++;
                }

            int sum = 0, pos=0;

            for (int counter = 0; counter < pontuacao.length - 1; counter++)
                if (pontuacao[counter] > sum){
                    sum = pontuacao[counter];
                    pos = counter;
                }

                switch (pos){
                    case 0:
                        inclinacao = "horizontal";
                        break;
                    case 1:
                        inclinacao = "vertical";
                        break;
                    case 2:
                        inclinacao = "45-";
                        break;
                    case 3:
                        inclinacao = "45+";
                        break;
                    default:
                        inclinacao = "nada";
                        break;
                }

            System.out.println(inclinacao);
            return inclinacao;
        }

        private int rodaMascaraGeneric(int[][] imgOrigin, int col, int lin, String mask){
        int[][] mascara;
            switch(mask){
                case "vertical":
                mascara = MASCARA_VERTICAL;
                break;
                case "horizontal":
                mascara = MASCARA_HORIZONTAL;
                break;
                case "45+":
                mascara = MASCARA_45_POSITIVO;
                break;
                case "45-":
                mascara = MASCARA_45_NEGATIVO;
                break;
                default:
                    mascara = MASCARA_PADRAO;
            }

            int pixelAltura = 0;
            for (int linha = 0; linha < 3; linha++)
                for (int coluna = 0; coluna < 3; coluna++)
                    pixelAltura += imgOrigin[lin + linha - 1][col + coluna - 1] * mascara[linha][coluna];


            return verificaLimites(pixelAltura);
        }

        private int limiarizacao(int[][] imgOrigin, int col, int lin, double limiar){
               return (imgOrigin[lin][col] > limiar)
                       ?imgOrigin[lin][col]
                       :0;
            }

        private int limiarizacaoMedia(int[][] imgOrigin, int col, int lin){
            return  limiarizacao(imgOrigin,col,lin ,CONSTANTE_DIFERENCA * calculaMedia(imgOrigin,col, lin));
        }

        private int limiarizacaoDinamica (int[][] imgOrigin, int col, int lin){
            int dinamico = (lin >= getAltura()/2)? 2:1;
            return limiarizacao(imgOrigin,col,lin ,dinamico * CONSTANTE_DIFERENCA * calculaMedia(imgOrigin,col, lin));
        }

        private int limiarizacaoPassaAlta (int[][] imgOrigin, int col, int lin, int limiar){
            int pixelPassaAlta = passaAlta(imgOrigin,col,lin);
            return (pixelPassaAlta > limiar) ? pixelPassaAlta : 0;
            }

        private Boolean CrescimentoDeRegiao(int col, int lin, int[][]imgAgregada){
            if (imgAgregada[col][lin] == 255 || imgAgregada[col][lin] == 100) return false;
            CONTADOR_SEMENTE_EXPANDIDA++;
            CrescimentoDeRegiao(col-1,lin-1,imgAgregada);
            CrescimentoDeRegiao(col,lin-1,imgAgregada);
            CrescimentoDeRegiao(col+1,lin-1,imgAgregada);
            CrescimentoDeRegiao(col-1,lin,imgAgregada);
            CrescimentoDeRegiao(col+1,lin,imgAgregada);
            CrescimentoDeRegiao(col-1,lin+1,imgAgregada);
            CrescimentoDeRegiao(col-1,lin+1,imgAgregada);
            CrescimentoDeRegiao(col,lin+1,imgAgregada);
            CrescimentoDeRegiao(col+1,lin+1,imgAgregada);
            return true;
        }

        private void pintaImagem(int[][]imgAgregada){
            for (int lin = 1; lin < getAltura() - 2; lin++) {
                for (int col = 1; col < getLargura() - 2; col++) {
                    Color novoPixel = new Color(imgAgregada[lin][col], imgAgregada[lin][col], imgAgregada[lin][col]);
                    image.setRGB(col, lin, novoPixel.getRGB());
                }
            }
        }

        private void pintaVermelhoImagem(int[][]imgAgregada){
            for (int lin = 1; lin < getAltura() - 2; lin++) {
                for (int col = 1; col < getLargura() - 2; col++) {
                    Color novoPixel ;
                    if(imgAgregada[lin][col] == 100) {
                    novoPixel =  new Color(0, 255, 0);
                    }else {
                        novoPixel = new Color(imgAgregada[lin][col], imgAgregada[lin][col], imgAgregada[lin][col]);
                    }
                    image.setRGB(col, lin, novoPixel.getRGB());
                }
            }
        }

        public String encontraChainCode(int[][]imgOriginal){
            int alturaVizinha = getAltura() - VIZINHA - 1;
            int larguraVizinha = getLargura() - VIZINHA - 1;
            String chainCode = "";

            for(int lin = 1; lin < alturaVizinha - 1; lin ++){
                for (int col = 1; col < larguraVizinha - 1;col ++){
                    if(imgOriginal[lin][col] < 100){
                        chainCode(imgOriginal,col,lin,chainCode,"");
                        return chainCode;
                    }
                }
            }

            return chainCode;
        }

        private boolean chainCode(int[][]imgOriginal, int col, int lin,String chain,String direcao){
            if(col > 98 || col < 1) return false;
            if(lin > 98 || lin < 1) return false;

            if (imgOriginal[lin][col] > 100){
                return false;
            }

            chain += direcao;
            imgOriginal[col][lin] = 255;
            if(chainCode(imgOriginal,col+1,lin,chain,"0")) return true;
            if(chainCode(imgOriginal,col+1,lin-1,chain,"1")) return true;
            if(chainCode(imgOriginal,col,lin-1,chain,"2")) return true;
            if(chainCode(imgOriginal,col-1,lin-1,chain,"3")) return true;
            if(chainCode(imgOriginal,col-1,lin,chain,"4")) return true;
            if(chainCode(imgOriginal,col-1,lin+1,chain,"5")) return true;
            if(chainCode(imgOriginal,col,lin+1,chain,"6")) return true;
            if(chainCode(imgOriginal,col+1,lin+1,chain,"7")) return true;

            return false;
        }

        private Point encontraCentroide(int[][]imagem){
            int menorLinha = getAltura()
                    ,menorColuna = getLargura()
                    ,maiorLinha = -1
                    ,maiorColuna = -1 ;

            for(int lin = 1; lin < getAltura() - 1;lin++){
                for(int col = 1; col < getLargura() - 1; col++){
                    if(imagem[lin][col] < 100){
                        if(lin < menorLinha) menorLinha = lin;
                        if(col < menorColuna) menorColuna = col;
                        if(lin > maiorLinha) maiorLinha = lin;
                        if(col > maiorColuna) maiorColuna= col;
                    }
                }
            }
            Point centroide = new Point();
            centroide.setLocation(maiorColuna-menorColuna,maiorLinha-menorLinha);
            return centroide;
        }

        private int[] criaAssinaturas(int[][]imagem,Point centroide){
          int centroAltura = (int) centroide.getY();
          int centroLargura = (int) centroide.getX();
          int pos = 0;

            int[] distancias = new int[12];
            double novaLin, novaCol;

            for(double rad = 0; rad < 2; rad += 0.125){
                novaLin = centroAltura;
                novaCol = centroLargura;

                while(imagem[(int)novaLin][(int)novaCol]  > 60) {
                  novaLin = Math.round(Math.sin(rad) * (-1) + novaLin);
                  novaCol = Math.round(Math.cos(rad) + novaCol);
                  distancias[pos]++;
                }

              pos++;
           }

            return distancias;
        }

        public static void main(String args[]) throws IOException {
            MyImage imagemOriginal = new MyImage();
            imagemOriginal.setTipo("Original");
            imagemOriginal.criaArquivo();
            imagemOriginal.carregaImagem();
            imagemOriginal.setDimensoes();


            ImageIcon imageIcon = new ImageIcon(imagemOriginal.image);
            JLabel jlabel = new JLabel(imageIcon);

            JPanel painel = new JPanel();
            painel.add(jlabel);


            JFrame janela = new JFrame("ASSINATURAS");

            janela.add(painel);
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.pack();
            janela.setVisible(true);
            janela.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    System.out.println(x);
                    int y = e.getY();
                    System.out.println(y);
                }
            });
        }
    }