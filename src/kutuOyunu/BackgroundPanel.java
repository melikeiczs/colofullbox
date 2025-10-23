package kutuOyunu;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class BackgroundPanel extends JPanel {
    private final int shapeCount = 25; //hareketli şekil
    private final int sparkleCount = 15; //parıltı

    private final int[] xs = new int[shapeCount];  //şekillerin konumu saydamlığı
    private final int[] ys = new int[shapeCount];
    private final int[] sizes = new int[shapeCount];
    private final int[] dxs = new int[shapeCount];
    private final int[] dys = new int[shapeCount];
    private final float[] alphas = new float[shapeCount];
    private final Color[] colors = new Color[shapeCount];

    private final int[] sparkX = new int[sparkleCount];
    private final int[] sparkY = new int[sparkleCount];
    private final float[] sparkAlpha = new float[sparkleCount];
    private final float[] sparkDelta = new float[sparkleCount];

    private final Random rand = new Random();
    private int currentLevel = 1;  //oyunun seviyesi

    public BackgroundPanel() {
        setOpaque(true);

        // Hareketli şekiller için başlangıç değerleri
        for (int i = 0; i < shapeCount; i++) {
            xs[i] = rand.nextInt(600);
            ys[i] = rand.nextInt(700);
            sizes[i] = 15 + rand.nextInt(25);
            dxs[i] = (rand.nextBoolean() ? 1 : -1) * (1 + rand.nextInt(2));
            dys[i] = (rand.nextBoolean() ? 1 : -1) * (1 + rand.nextInt(2));
            alphas[i] = 0.2f + rand.nextFloat() * 0.4f;

            // Daha çeşitli ve pastel renkler
            int baseR = 150 + rand.nextInt(105);
            int baseG = 150 + rand.nextInt(105);
            int baseB = 200 + rand.nextInt(55);
            colors[i] = new Color(baseR, baseG, baseB, (int) (alphas[i] * 180));
        }

        // Parıltılar için başlangıç değerleri
        for (int i = 0; i < sparkleCount; i++) {
            sparkX[i] = rand.nextInt(600);
            sparkY[i] = rand.nextInt(700);
            sparkAlpha[i] = rand.nextFloat();
            sparkDelta[i] = 0.008f + rand.nextFloat() * 0.015f;
        }

        // Animasyon timer'ı
        Timer timer = new Timer(60, e -> {
            // Şekillerin hareketi
            for (int i = 0; i < shapeCount; i++) {
                xs[i] += dxs[i];
                ys[i] += dys[i];

                if (xs[i] < -sizes[i] || xs[i] > getWidth()) {
                    dxs[i] = -dxs[i];
                    xs[i] = Math.max(-sizes[i], Math.min(getWidth(), xs[i]));
                }
                if (ys[i] < -sizes[i] || ys[i] > getHeight()) {
                    dys[i] = -dys[i];
                    ys[i] = Math.max(-sizes[i], Math.min(getHeight(), ys[i]));
                }
            }

            // Parıltıların animasyonu
            for (int i = 0; i < sparkleCount; i++) {
                sparkAlpha[i] += sparkDelta[i]; //sekiller yanıp söner
                if (sparkAlpha[i] > 1f) {
                    sparkAlpha[i] = 1f;
                    sparkDelta[i] = -sparkDelta[i];
                } else if (sparkAlpha[i] < 0f) {
                    sparkAlpha[i] = 0f;
                    sparkDelta[i] = -sparkDelta[i];
                    sparkX[i] = rand.nextInt(getWidth());
                    sparkY[i] = rand.nextInt(getHeight());
                }
            }

            repaint();
        });
        timer.start();
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2d = (Graphics2D) g.create();  //gradyan arka plan çizimi
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Seviyeye göre gradyan renkler
        Color startColor, endColor;
        switch (currentLevel) { //bu metodun görevi arka planı ve renkleri çizmek
            case 1 -> {
                startColor = new Color(154, 104, 198, 255); // Alice Blue
                endColor = new Color(223, 179, 179, 255);   // Lavender
            }

            case 2 -> {
                startColor = new Color(193, 123, 193); // Lavender Blush
                endColor = new Color(255, 228, 225);   // Misty Rose
            }
            case 3 -> {
                startColor = new Color(193, 123, 193); // Honeydew
                endColor = new Color(187, 81, 232);   // Floral White
            }
            case 4 -> {
                startColor = new Color(87, 122, 151); // Ivory
                endColor = new Color(136, 178, 200);   // Cornsilk
            }
            case 5 -> {
                startColor = new Color(248, 228, 109); // Seashell
                endColor = new Color(230, 220, 151);   // Old Lace
            }
            default -> {
                // Yüksek seviyeler için dinamik renkler
                int r1 = 220 + rand.nextInt(35);
                int g1 = 220 + rand.nextInt(35);
                int b1 = 220 + rand.nextInt(35);
                int r2 = 200 + rand.nextInt(55);
                int g2 = 200 + rand.nextInt(55);
                int b2 = 200 + rand.nextInt(55);
                startColor = new Color(r1, g1, b1);
                endColor = new Color(r2, g2, b2);
            }
        }

        // Ana gradyan arka plan
        GradientPaint mainGradient = new GradientPaint(0, 0, startColor, w, h, endColor);
        g2d.setPaint(mainGradient);
        g2d.fillRect(0, 0, w, h);

        // Üst kısımda hafif bir glow(beyaz ısık/parlama) efekti
        GradientPaint topGlow = new GradientPaint(0, 0, new Color(255, 255, 255, 30), 0, h/3, new Color(255, 255, 255, 0));
        g2d.setPaint(topGlow);
        g2d.fillRect(0, 0, w, h/3);

        // Alt kısımda hafif gölge efekti
        GradientPaint bottomShadow = new GradientPaint(0, h*2/3, new Color(0, 0, 0, 0), 0, h, new Color(0, 0, 0, 15));
        g2d.setPaint(bottomShadow);
        g2d.fillRect(0, h*2/3, w, h/3);

        // Hareketli şekilleri çiz
        int xOffset = Math.max(0, (w - 600) / 2);
        for (int i = 0; i < shapeCount; i++) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphas[i]));
            g2d.setColor(colors[i]);

            // Çeşitli şekiller çiz
            int x = xs[i] + xOffset;
            int y = ys[i];
            int size = sizes[i];

            if (i % 3 == 0) {
                // Yuvarlak
                g2d.fillOval(x, y, size, size);
            } else if (i % 3 == 1) {
                // Yuvarlak köşeli kare
                g2d.fillRoundRect(x, y, size, size, size/3, size/3);
            } else {
                // Elips
                g2d.fillOval(x, y, size, size/2);
            }
        }

        // Parıltıları çiz
        for (int i = 0; i < sparkleCount; i++) {
            float alpha = sparkAlpha[i];
            int size = 4 + rand.nextInt(6);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // Çok renkli parıltılar
            Color sparkColor;
            if (i % 4 == 0) sparkColor = new Color(179, 204, 215);
            else if (i % 4 == 1) sparkColor = new Color(255, 255, 200);
            else if (i % 4 == 2) sparkColor = new Color(166, 137, 175);
            else sparkColor = new Color(255, 200, 255);

            g2d.setColor(sparkColor);
            g2d.fillOval(sparkX[i] + xOffset, sparkY[i], size, size);

            // Parıltının etrafında hafif glow
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.3f));
            g2d.fillOval(sparkX[i] + xOffset - 2, sparkY[i] - 2, size + 4, size + 4);
        }

        g2d.dispose();
    }
}