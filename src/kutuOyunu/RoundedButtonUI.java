package kutuOyunu;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButtonUI extends BasicButtonUI {

    private int arcRadius;
    private Color borderColor;
    private static final int BORDER_THICKNESS = 2; // Çerçeve kalınlığını sabit olarak tanımlayalım

    public RoundedButtonUI(int arcRadius, Color borderColor) {
        this.arcRadius = arcRadius;
        this.borderColor = borderColor;
    }

    public RoundedButtonUI(int arcRadius) {
        this(arcRadius, new Color(57, 6, 57)); // Varsayılan çerçeve rengi
    }

    public RoundedButtonUI() {
        this(20); // Varsayılan yarıçap
    }

    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        // Önemli: Butonun varsayılan bir Insets'i olmalı ki metin kenarlara yapışmasın.
        // MainMenuPanel'de EmptyBorder verildiği için burada ek bir border eklemeyeceğiz.
        // Ancak, eğer o border'ı kaldırırsak, burada uygun bir Insets veya EmptyBorder tanımlamamız gerekir.
    }

    @Override
    protected void uninstallDefaults(AbstractButton b) {
        super.uninstallDefaults(b);
        b.setOpaque(true);
        b.setBorderPainted(true);
        b.setFocusPainted(true);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AbstractButton button = (AbstractButton) c;
        ButtonModel model = button.getModel();
        int width = button.getWidth();
        int height = button.getHeight();

        // Butonun iç boşluklarını alalım
        Insets insets = button.getInsets();

        // Arka plan rengini belirle
        Color backgroundColor;
        if (model.isArmed()) {
            backgroundColor = button.getBackground().darker();
        } else if (model.isRollover()) {
            backgroundColor = button.getBackground().brighter();
        } else {
            backgroundColor = button.getBackground();
        }

        g2.setColor(backgroundColor);

        // Dolgu için çizim alanı: Çerçeve kalınlığını ve varsa insets'leri hesaba katalım
        // Dolgu, çerçevenin içinden başlamalı.
        int fillX = BORDER_THICKNESS / 2;
        int fillY = BORDER_THICKNESS / 2;
        int fillWidth = width - BORDER_THICKNESS;
        int fillHeight = height - BORDER_THICKNESS;

        // Yuvarlak arka planı çiz
        g2.fill(new RoundRectangle2D.Float(fillX, fillY, fillWidth, fillHeight, arcRadius, arcRadius));


        // Yuvarlak çerçeveyi çiz
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(BORDER_THICKNESS)); // Çerçeve kalınlığı

        // Çerçeve için çizim alanı: butona tam oturacak şekilde
        // Çerçeve, butona sıfırdan başlasın ve genişlik/yükseklik -1 piksele kadar çizsin.
        // RoundRectangle2D.Float'ın draw metodu, verilen boyutların 1 piksel eksiğine çizer,
        // bu yüzden tam genişlik ve yükseklik verirsek doğru kenara oturacaktır.
        g2.draw(new RoundRectangle2D.Float(0, 0, width -1, height - 1, arcRadius, arcRadius));

        // Metin çizimi
        FontMetrics fm = button.getFontMetrics(button.getFont());
        String text = button.getText();
        if (text != null && !text.isEmpty()) {
            int textWidth = fm.stringWidth(text);
            // Metin koordinatlarını, dolgu alanının ortasına göre ayarlayalım
            // Veya sadece butonun toplam boyutuna göre ortalayalım, bu genellikle daha basit ve işe yarar
            int textX = (width - textWidth) / 2;
            int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(button.getForeground());
            g2.drawString(text, textX, textY);
        }

        // İkon çizimi (varsa)
        Icon icon = button.getIcon();
        if (icon != null) {
            int iconX = (width - icon.getIconWidth()) / 2;
            int iconY = (height - icon.getIconHeight()) / 2;
            icon.paintIcon(button, g2, iconX, iconY);
        }

        g2.dispose();
    }

    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        // Odaklanma çizgisini kapatmak için boş bırakıldı
    }
}