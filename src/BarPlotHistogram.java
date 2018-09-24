import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class BarPlotHistogram {
//http://stackoverflow.com/a/12520104/714968

    public static void main(String[] args) {
    }

    public BarPlotHistogram(int[] frequencia) {

        Map<Integer, Integer> mapHistory = new TreeMap<Integer, Integer>();

            for (int r = 0; r < frequencia.length; r++) {
                int value = r;
                int amount = frequencia[r];

                mapHistory.put(value, amount);
            }

        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(new Graph(mapHistory)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    protected class Graph extends JPanel {

        protected static final int MIN_BAR_WIDTH = 4;
        private Map<Integer, Integer> mapHistory;

        public Graph(Map<Integer, Integer> mapHistory) {
            this.mapHistory = mapHistory;
            int width = (mapHistory.size() * MIN_BAR_WIDTH) + 11;
            Dimension minSize = new Dimension(width, 128);
            Dimension prefSize = new Dimension(width, 256);
            setMinimumSize(minSize);
            setPreferredSize(prefSize);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mapHistory != null) {
                int xOffset = 5;
                int yOffset = 5;
                int width = getWidth() - 1 - (xOffset * 2);
                int height = getHeight() - 1 - (yOffset * 2);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(xOffset, yOffset, width, height);
                int barWidth = Math.max(MIN_BAR_WIDTH,
                        (int) Math.floor((float) width
                                / (float) mapHistory.size()));
                System.out.println("width = " + width + "; size = "
                        + mapHistory.size() + "; barWidth = " + barWidth);
                int maxValue = 0;
                for (Integer key : mapHistory.keySet()) {
                    int value = mapHistory.get(key);
                    maxValue = Math.max(maxValue, value);
                }
                int xPos = xOffset;
                for (Integer key : mapHistory.keySet()) {
                    int value = mapHistory.get(key);
                    int barHeight = Math.round(((float) value
                            / (float) maxValue) * height);
                    g2d.setColor(new Color(key, key, key));
                    int yPos = height + yOffset - barHeight;
                    Rectangle2D bar = new Rectangle2D.Float(
                            xPos, yPos, barWidth, barHeight);
                    g2d.fill(bar);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.draw(bar);
                    xPos += barWidth;
                }
                g2d.dispose();
            }
        }
    }
}