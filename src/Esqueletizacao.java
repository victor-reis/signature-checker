import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Esqueletizacao {

    final static int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1},
            {-1, 1}, {-1, 0}, {-1, -1}};

    private static int VIZINHA = 1;
    private static int RESOLUCAO_DE_CONTRASTE = 255;

    private int largura;
    private int altura;
    private BufferedImage image = null;
    private File diretorio = null;
    private String path = "/home/victor-reis/Pictures/CG/21x21.png";

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

    private boolean criaArquivo() {

        try {
            diretorio = new File(this.path);
            System.out.println("Arquivo lido com sucesso!");
        } catch (Exception e) {
            System.out.println("Arquivo nao existe ou diretorio eh invalido!");
        }
        return true;

    }

    private void carregaImagem(){

        try {
            image = ImageIO.read(diretorio);
            this.largura = image.getWidth() - 1 ;
            this.altura = image.getHeight() - 1;
        } catch (Exception e) {
            System.out.println("A largura e Altura devem ser maiores do que 0!!!");
        }
    }

    public void escreveImagem() {
        try {
            // Output file path
            File output_file = new File("/home/victor-reis/Pictures/CG/H-esqueleto");

            // Writing to file taking type and path as
            ImageIO.write(image, "png", output_file);

            System.out.println("Writing complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private int[][] criaMatriz(BufferedImage image){
        int[][] imgOrigin = new int[getLargura()][getAltura()];

        for (int col = 0; col < getLargura(); col++) {
            for (int lin = 0; lin < getAltura(); lin++) {

                int tomCinza = image.getRGB(col, lin);
                Color c = new Color(tomCinza);
                int tomRed = c.getRed();
                if(tomRed == 0)
                imgOrigin[col][lin] = 0;
                else
                    imgOrigin[col][lin] = 255;

            }
        }
        return imgOrigin;
    }

    private void pintaImagem(int[][]imgAgregada){
        for (int col = 1; col < getLargura() - 2; col++)
            for (int lin = 1; lin < getAltura() - 2; lin++) {
                Color novoPixel = new Color(imgAgregada[col][lin], imgAgregada[col][lin], imgAgregada[col][lin]);
                image.setRGB(col, lin, novoPixel.getRGB());
            }
    }

    public void percorreImagem(){
        int[][] imgOrigin = criaMatriz(image);
        int[][] imgDestino = imgOrigin.clone();

        boolean marcadoPrimeiroPasso, marcadoSegundoPasso;
        do {
            marcadoPrimeiroPasso = primeiroPasso(imgOrigin,imgDestino);
            imgOrigin = imgDestino.clone();

            marcadoSegundoPasso = segundoPasso(imgOrigin,imgDestino);
            imgOrigin = imgDestino.clone();

        }while(marcadoPrimeiroPasso || marcadoSegundoPasso);

        pintaImagem(imgOrigin);
    }

    public boolean primeiroPasso(int[][] imgOrigin, int[][] imgDestino){
        int alturaVizinha = getAltura() - VIZINHA;
        int larguraVizinha = getLargura() - VIZINHA;

        boolean foiMarcado = false;

        int NP, SP, setaDireita, setaBaixo;

        int[] vetorSentidoHorario;
        for (int col = VIZINHA; col < larguraVizinha; col++)
            for (int lin = VIZINHA; lin < alturaVizinha; lin++) {
                if(imgOrigin[col][lin] == 0)
                    continue;

                vetorSentidoHorario = criaVetorSentidoHorario(imgOrigin, col, lin);

                NP = numPixelsImage(vetorSentidoHorario);
                SP = numTransitions(vetorSentidoHorario);
                setaDireita = verificaP2P4P6(vetorSentidoHorario);
                setaBaixo = verificaP4P6P8(vetorSentidoHorario);

                if (NP >= 2
                    && NP <= 6
                    && SP == 1
                    && setaDireita == 0
                    && setaBaixo == 0) {

                    foiMarcado = true;
                    imgDestino[col][lin] = 0;
                }
            }

        return foiMarcado;
    }

    public boolean segundoPasso(int[][] imgOrigin, int[][] imgDestino){
        int alturaVizinha = getAltura() - VIZINHA - 1;
        int larguraVizinha = getLargura() - VIZINHA - 1;

        boolean foiMarcado = false;

        int NP, SP, setaCima,setaEsquerda;

        int[] vetorSentidoHorario;
        for (int col = VIZINHA; col < larguraVizinha; col++)
            for (int lin = VIZINHA; lin < alturaVizinha; lin++) {
                if(imgOrigin[col][lin] == 0)
                continue;

                    vetorSentidoHorario = criaVetorSentidoHorario(imgOrigin, col, lin);

                    NP = numPixelsImage(vetorSentidoHorario);
                    SP = numTransitions(vetorSentidoHorario);
                    setaCima = verificaP2P4P8(vetorSentidoHorario);
                    setaEsquerda = verificaP2P6P8(vetorSentidoHorario);

                    if (NP >= 2
                    && NP <= 6
                    && SP == 1
                    && setaEsquerda == 0
                    && setaCima == 0) {

                        foiMarcado = true;
                        imgDestino[col][lin] = 0;
                    }
            }

        return foiMarcado;
    }

    public int[] criaVetorSentidoHorario(int[][] matrizOriginal, int col, int lin){
        int[] vetorSentidoHorario = new int[8];

        vetorSentidoHorario[0] = aux(matrizOriginal[col][lin-1]);
        vetorSentidoHorario[1] = aux(matrizOriginal[col+1][lin-1]);
        vetorSentidoHorario[2] = aux(matrizOriginal[col+1][lin]);
        vetorSentidoHorario[3] = aux(matrizOriginal[col+1][lin+1]);
        vetorSentidoHorario[4] = aux(matrizOriginal[col][lin+1]);
        vetorSentidoHorario[5] = aux(matrizOriginal[col-1][lin+1]);
        vetorSentidoHorario[6] = aux(matrizOriginal[col-1][lin]);
        vetorSentidoHorario[7] = aux(matrizOriginal[col-1][lin-1]);


    return vetorSentidoHorario;
    }

    public int aux(int i){
        return (i==0)?0:1;
    }

    public int numPixelsImage(int[] vetorSentidoHorario){
      int totalDePixelObjeto = 0;
      for(int pixel : vetorSentidoHorario)
          if(pixel == 1)
          totalDePixelObjeto ++;

      return totalDePixelObjeto;
    }

    static int numTransitions(int[]vetorSentidoHorario) {
        int count = 0;

        for(int i = 0; i < vetorSentidoHorario.length - 1;i++){
            if(vetorSentidoHorario[i] == 0)
                if(vetorSentidoHorario[i+1] == 1)
                    count++;
        }

        if(vetorSentidoHorario[7] == 0)
            if(vetorSentidoHorario[0] == 1)
                count++;


        return count;
    }

    public int verificaP2P4P6(int[]vetorSentidoHorario){
        return vetorSentidoHorario[0] * vetorSentidoHorario[2] * vetorSentidoHorario [4];
    }

    public int verificaP4P6P8(int[]vetorSentidoHorario){
        return vetorSentidoHorario[2] * vetorSentidoHorario[4] * vetorSentidoHorario [6];
    }

    public int verificaP2P6P8(int[]vetorSentidoHorario){
        return vetorSentidoHorario[0] * vetorSentidoHorario[6] * vetorSentidoHorario [6];
    }

    public int verificaP2P4P8(int[]vetorSentidoHorario){
        return vetorSentidoHorario[0] * vetorSentidoHorario[4] * vetorSentidoHorario [6];
    }


   public static void main (String[] args){
        Esqueletizacao my = new Esqueletizacao();
        my.criaArquivo();
        my.carregaImagem();
        my.setDimensoes();

        my.percorreImagem();


       ImageIcon imageIcon = new ImageIcon(my.image);
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

