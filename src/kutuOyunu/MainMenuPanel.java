package kutuOyunu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class MainMenuPanel extends JPanel {

    private final Color START_BUTTON_COLOR = new Color(175, 136, 164, 255);
    private final Color BUTTON_TEXT_COLOR = new Color(57, 6, 57);
    private final Color TITLE_COLOR = Color.WHITE;

    // Animasyon için değişkenler
    private Timer animationTimer;
    private float animationPhase = 0f;
    private Random random = new Random();

    public MainMenuPanel(ActionListener startButtonListener) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        // Başlık güncellendi
        JLabel titleLabel = new JLabel("COLORFUL BOX");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Alt başlık eklendi
        JLabel subtitleLabel = new JLabel("Renklerin Büyülü Dünyasına Hoş Geldin!");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(248, 249, 255));

        JButton startButton = new JButton("OYUNA BAŞLA");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setBackground(START_BUTTON_COLOR);
        startButton.setForeground(BUTTON_TEXT_COLOR);
        startButton.setPreferredSize(new Dimension(200, 60));
        startButton.addActionListener(startButtonListener);
        startButton.setUI(new RoundedButtonUI(40, BUTTON_TEXT_COLOR));

        GridBagConstraints gbc = new GridBagConstraints();

        // Başlık
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(titleLabel, gbc);

        // Alt başlık
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 50, 0);
        add(subtitleLabel, gbc);

        // Başlat butonu
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        add(startButton, gbc);

        // Animasyon timer'ı başlat
        startAnimation();
    }

    // Alpha değerini güvenli aralıkta tutan yardımcı fonksiyon
    private float clampAlpha(float alpha) {
        if (alpha < 0.0f) return 0.0f;
        if (alpha > 1.0f) return 1.0f;
        return alpha;
    }

    private void startAnimation() {
        animationTimer = new Timer(50, e -> {
            animationPhase += 0.05f;
            if (animationPhase > Math.PI * 2) {
                animationPhase = 0f;
            }
            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Ana gradient arka plan
        drawAnimatedGradientBackground(g2d);

        // Yüzen renkli kutular
        drawFloatingBoxes(g2d);

        // Renk orb'ları (yumuşak ışık efektleri)
        drawColorOrbs(g2d);

        // Yıldızlar
        drawStars(g2d);

        g2d.dispose();
    }

    private void drawAnimatedGradientBackground(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // Animasyonlu gradient renkleri
        float phase1 = (float) Math.sin(animationPhase) * 0.3f + 0.7f;
        float phase2 = (float) Math.sin(animationPhase + Math.PI/3) * 0.3f + 0.7f;
        float phase3 = (float) Math.sin(animationPhase + Math.PI*2/3) * 0.3f + 0.7f;

        Color color1 = new Color(
                (int)(102 * phase1),
                (int)(126 * phase1),
                (int)(234 * phase1)
        );
        Color color2 = new Color(
                (int)(118 * phase2),
                (int)(75 * phase2),
                (int)(162 * phase2)
        );
        Color color3 = new Color(
                (int)(240 * phase3),
                (int)(147 * phase3),
                (int)(251 * phase3)
        );

        // Çoklu gradient efekti
        GradientPaint gp1 = new GradientPaint(0, 0, color1, width/2, height/2, color2);
        g2d.setPaint(gp1);
        g2d.fillRect(0, 0, width, height);

        // İkinci katman - şeffaflık ile
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        GradientPaint gp2 = new GradientPaint(width, 0, color3, 0, height, color1);
        g2d.setPaint(gp2);
        g2d.fillRect(0, 0, width, height);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawFloatingBoxes(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // Yüzen kutular
        Color[] boxColors = {
                new Color(255, 107, 107),
                new Color(78, 205, 196),
                new Color(254, 202, 87),
                new Color(72, 219, 251),
                new Color(255, 159, 243),
                new Color(84, 160, 255)
        };

        int[][] boxPositions = {
                {width/8, height/6, 80, 80},
                {width*7/8-60, height/5, 60, 60},
                {width/10, height*3/4, 100, 100},
                {width*9/10-70, height*4/5, 70, 70},
                {width/20, height/2, 90, 90},
                {width*19/20-50, height*2/5, 50, 50}
        };

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));

        for (int i = 0; i < boxColors.length && i < boxPositions.length; i++) {
            float offsetY = (float) Math.sin(animationPhase + i * Math.PI/3) * 20;
            float scale = 1.0f + (float) Math.sin(animationPhase + i * Math.PI/4) * 0.1f;

            int x = boxPositions[i][0];
            int y = (int) (boxPositions[i][1] + offsetY);
            int w = (int) (boxPositions[i][2] * scale);
            int h = (int) (boxPositions[i][3] * scale);

            g2d.setColor(boxColors[i]);
            g2d.fill(new RoundRectangle2D.Float(x, y, w, h, 12, 12));
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawColorOrbs(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // Büyük yumuşak renk orb'ları
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));

        // Orb 1 - Kırmızımsı
        float orb1Y = height/8 + (float) Math.sin(animationPhase) * 30;
        RadialGradientPaint rgp1 = new RadialGradientPaint(
                width/8, orb1Y, 100,
                new float[]{0f, 1f},
                new Color[]{new Color(255, 107, 107, 100), new Color(255, 107, 107, 0)}
        );
        g2d.setPaint(rgp1);
        g2d.fill(new Ellipse2D.Float(width/8-100, orb1Y-100, 200, 200));

        // Orb 2 - Turkuaz
        float orb2Y = height*3/5 + (float) Math.sin(animationPhase + Math.PI) * 30;
        RadialGradientPaint rgp2 = new RadialGradientPaint(
                width*7/8, orb2Y, 75,
                new float[]{0f, 1f},
                new Color[]{new Color(78, 205, 196, 100), new Color(78, 205, 196, 0)}
        );
        g2d.setPaint(rgp2);
        g2d.fill(new Ellipse2D.Float(width*7/8-75, orb2Y-75, 150, 150));

        // Orb 3 - Sarı
        float orb3Y = height*4/5 + (float) Math.sin(animationPhase + Math.PI/2) * 20;
        RadialGradientPaint rgp3 = new RadialGradientPaint(
                width/2, orb3Y, 90,
                new float[]{0f, 1f},
                new Color[]{new Color(254, 202, 87, 100), new Color(254, 202, 87, 0)}
        );
        g2d.setPaint(rgp3);
        g2d.fill(new Ellipse2D.Float(width/2-90, orb3Y-90, 180, 180));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private void drawStars(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        String[] stars = {"★", "✦", "★", "✧"};
        int[][] starPositions = {
                {width/4, height/6},
                {width*3/4, height/4},
                {width/5, height*3/4},
                {width*4/5, height/5}
        };

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));

        for (int i = 0; i < stars.length && i < starPositions.length; i++) {
            // Alpha değerini güvenli aralıkta tut
            float alpha = 0.3f + (float) Math.sin(animationPhase + i * Math.PI/2) * 0.4f;
            alpha = clampAlpha(alpha); // Alpha değerini güvenli aralığa zorla

            float scale = 1.0f + (float) Math.sin(animationPhase + i * Math.PI/3) * 0.2f;

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(Color.WHITE);

            FontMetrics fm = g2d.getFontMetrics();
            int x = starPositions[i][0] - fm.stringWidth(stars[i])/2;
            int y = starPositions[i][1] + fm.getHeight()/2;

            // Ölçekleme efekti için transform kullan
            g2d.translate(starPositions[i][0], starPositions[i][1]);
            g2d.scale(scale, scale);
            g2d.drawString(stars[i], -fm.stringWidth(stars[i])/2, fm.getHeight()/4);
            g2d.scale(1/scale, 1/scale);
            g2d.translate(-starPositions[i][0], -starPositions[i][1]);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}