package kutuOyunu;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

public class RoundedComboBoxUI extends BasicComboBoxUI {
//kullanıcının renk secmesine izin veren acılır bileşendir
    @Override
    protected JButton createArrowButton() {
        // Özel ok butonu oluştur
        JButton button = new JButton("\u25BC"); // Unicode aşağı ok karakteri
        button.setBorder(BorderFactory.createEmptyBorder()); // Kenarlığı kaldır
        button.setContentAreaFilled(false); // İç alanı doldurma
        button.setFocusPainted(false); // Odaklanma çizgisini kapat
        button.setForeground(new Color(57, 6, 57)); // Ok rengi
        return button;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c); // Varsayılan UI kurulumunu yap
        c.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // İç boşluk ekle
        c.setOpaque(false); // Şeffaf yap
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        // Seçili değerin arka planını boya
        g.setColor(new Color(230, 230, 255)); // Açık mavi arka plan rengi
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 15, 15); // Yuvarlak köşeli dikdörtgen çiz
    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        // Seçili değeri boya (metin vb.)
        super.paintCurrentValue(g, bounds, hasFocus);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        // ComboBox'ın genel arka planını boya
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Kenar yumuşatma
        g2.setColor(new Color(219, 234, 234)); // Açık mor arka plan rengi
        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15); // Yuvarlak köşeli dikdörtgen çiz
        g2.dispose(); // Graphics nesnesini serbest bırak
        super.paint(g, c); // Varsayılan boyama işlemini yap
    }
}
