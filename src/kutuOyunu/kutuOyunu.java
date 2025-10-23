package kutuOyunu;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List; // java.util.List'i açıkça belirt

public class kutuOyunu extends JFrame {
    private static final int INITIAL_COMBINATION_LENGTH = 4;
    private SoundPlayer soundPlayer; // SoundPlayer'ı sınıf değişkeni olarak tanımla

    // Daha canlı ve belirgin renkler
    public void KutuOyunu() {
        soundPlayer = new SoundPlayer();
    }

    private static final Color[] COLORS = {
            new Color(255, 105, 180, 240), // Vibrant pembe
            new Color(30, 144, 255, 240),  // Vibrant mavi
            new Color(50, 205, 50, 240),   // Vibrant yeşil
            new Color(255, 215, 0, 240),   // Vibrant sarı
            new Color(186, 85, 211, 240),  // Vibrant mor
            new Color(255, 140, 0, 240)    // Vibrant turuncu
    };

    private static final Map<String, Color> COLOR_MAP = new HashMap<>();
    private static final Map<Color, String> NAME_MAP = new HashMap<>();

    static {
        COLOR_MAP.put("Pembe", new Color(255, 105, 180, 240));
        COLOR_MAP.put("Mavi", new Color(30, 144, 255, 240));
        COLOR_MAP.put("Yeşil", new Color(50, 205, 50, 240));
        COLOR_MAP.put("Sarı", new Color(255, 215, 0, 240));
        COLOR_MAP.put("Mor", new Color(186, 85, 211, 240));
        COLOR_MAP.put("Turuncu", new Color(255, 140, 0, 240));

        for (Map.Entry<String, Color> entry : COLOR_MAP.entrySet()) {
            NAME_MAP.put(entry.getValue(), entry.getKey());
        }
    }

    private List<Color> secretCode = new ArrayList<>();
    private final JPanel guessesPanel = new JPanel();
    private final JPanel inputPanel = new JPanel();
    private final JButton guessButton = new JButton("Tahmin Et");
    // Ana Ekrana Dön butonunu sınıf değişkeni olarak tanımla ve bir kez başlat
    private final JButton backToMenuButton = new JButton("Ana Ekrana Dön");
    private JComboBox<String>[] colorSelectors;
    private final JLabel attemptsLabel = new JLabel("Deneme hakkı: 10");
    private JScrollPane scrollPane;
    private int attemptsLeft = 10;
    private int currentLevel = 1;
    private int currentCombinationLength = INITIAL_COMBINATION_LENGTH;

    private int score = 0; // Puan değişkeni
    private final JLabel scoreLabel = new JLabel("Puan: 0"); // Puanı göstermek için etiket
    private JPanel gamePanel; // Ana oyun içeriğini tutacak panel
    private MainMenuPanel mainMenuPanel; // Giriş ekranı

    public kutuOyunu() {
        // SoundPlayer'ı başlat
        soundPlayer = new SoundPlayer();

        setTitle("Renk Kodları Oyunu"); // Başlangıçta genel başlık
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);

        // Ana panel (CardLayout ile panelleri değiştireceğiz)
        JPanel cardPanel = new JPanel(new CardLayout());
        setContentPane(cardPanel);

        // --- Ana Menü Paneli Oluşturma ---
        // "Oyuna Başla" butonuna basıldığında showGamePanel metodunu çağır
        mainMenuPanel = new MainMenuPanel(e -> showGamePanel());
        cardPanel.add(mainMenuPanel, "MainMenu");

        // --- Oyun Paneli Oluşturma ---
        gamePanel = new JPanel(new BorderLayout()); // Oyun içeriğini tutacak panel

        // BackgroundPanel'i gamePanel'in ana içeriği yap
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout()); // BackgroundPanel'in kendi layout'u olacak
        gamePanel.add(backgroundPanel, BorderLayout.CENTER); // gamePanel'e ekle

        // Deneme hakkı ve puan etiketleri için ayrı bir panel oluştur ve BackgroundPanel'in en üstüne ekle
        JPanel topInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Biraz boşluk ekle
        topInfoPanel.setOpaque(false); // Şeffaf yapıldığından emin olundu
        attemptsLabel.setForeground(Color.DARK_GRAY);
        scoreLabel.setForeground(Color.DARK_GRAY);
        topInfoPanel.add(attemptsLabel);
        topInfoPanel.add(scoreLabel);
        backgroundPanel.add(topInfoPanel, BorderLayout.NORTH); // BackgroundPanel'in en üstüne ekle

        guessesPanel.setLayout(new BoxLayout(guessesPanel, BoxLayout.Y_AXIS));
        guessesPanel.setOpaque(false); // Şeffaf yap
        guessesPanel.setPreferredSize(new Dimension(600, 400));
        scrollPane = new JScrollPane(guessesPanel);
        // Kaydırma çubuğunu kaldır
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false); // Şeffaf yap
        scrollPane.getViewport().setOpaque(false); // Viewport'u şeffaf yap
        backgroundPanel.add(scrollPane, BorderLayout.CENTER); // guessesPanel'i BackgroundPanel'in ortasına ekle

        // inputPanel'in layout'u setupColorSelectors() içinde ayarlanacak
        inputPanel.setOpaque(false); // Şeffaf yap

        // Tahmin Et butonu ayarları
        // Renk güncellendi: new Color(75, 166, 241) - Steel Blue
        guessButton.setBackground(new Color(164, 187, 188));
        guessButton.setForeground(Color.DARK_GRAY);

        // Ana Ekrana Dön butonu ayarları (Sadece bir kez başlatıldığı için burada sadece ayarları yapılır)
        backToMenuButton.setBackground(new Color(192, 207, 207));
        backToMenuButton.setForeground(Color.DARK_GRAY);
        backToMenuButton.addActionListener(e -> {
            // Buton ses efekti

            showMainMenu(); // Ana menüye dönme aksiyonu
        });

        // setupColorSelectors metodunu burada çağırarak tüm input bileşenlerini ekle
        // Bu metot, JComboBox'ları, guessButton'ı, backToMenuButton'ı ekler.
        setupColorSelectors();

        // inputPanel şimdi BackgroundPanel'in güneyine eklenecek
        backgroundPanel.add(inputPanel, BorderLayout.SOUTH);
        cardPanel.add(gamePanel, "GamePanel"); // gamePanel'i ana cardPanel'e ekle

        guessButton.addActionListener(e -> {
            // Tahmin butonu ses efekti

            processGuess(); // Tahmin et butonu aksiyonu
        });

        // Başlangıçta ana menüyü göster
        showMainMenu();

        setVisible(true);
    }

    // Oyun panelini gösteren metot
    private void showGamePanel() {
        // Oyun başlama ses efekti


        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "GamePanel");
        // Yeni bir oyun başlat
        currentLevel = 1; // Seviyeyi sıfırla
        currentCombinationLength = INITIAL_COMBINATION_LENGTH; // Kombinasyon uzunluğunu sıfırla
        setTitle("Renk Kodları - Seviye " + currentLevel); // Başlığı güncelle
        guessesPanel.removeAll(); // Eski tahminleri temizle
        guessesPanel.revalidate();
        guessesPanel.repaint();
        generateSecretCode(); // Yeni gizli kod oluştur
        setupColorSelectors(); // Renk seçicileri yeniden kur (bu da butonları yeniden ekler)
        score = 0; // Puanı sıfırla
        updateScoreLabel(); // Puan etiketini güncelle
        // Arka plan panelinin seviyesini güncelle
        ((BackgroundPanel) ((BorderLayout) gamePanel.getLayout()).getLayoutComponent(BorderLayout.CENTER)).setCurrentLevel(currentLevel);
    }

    // Ana menü panelini gösteren metot
    private void showMainMenu() {
        CardLayout cl = (CardLayout)(getContentPane().getLayout());
        cl.show(getContentPane(), "MainMenu");
        setTitle("Renk Kodları Oyunu"); // Başlığı ana menü başlığına geri döndür
    }

    // Renk seçicileri (JComboBox) oluşturma ve ayarlama
    // Bu metot aynı zamanda guessButton, attemptsLabel, scoreLabel ve backToMenuButton'ı da ekler.
    private void setupColorSelectors() {
        inputPanel.removeAll(); // Mevcut seçicileri ve butonları kaldır
        inputPanel.setLayout(new BorderLayout()); // inputPanel'in layout'unu BorderLayout yap

        // Deneme hakkı ve puan etiketleri artık topInfoPanel'de yönetiliyor, buradan kaldırıldı.
        // JComboBox'lar için ayrı bir panel oluştur
        JPanel comboBoxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        comboBoxPanel.setOpaque(false); // Şeffaf yap

        colorSelectors = new JComboBox[currentCombinationLength];
        for (int i = 0; i < currentCombinationLength; i++) {
            JComboBox<String> comboBox = new JComboBox<>();
            for (String colorName : COLOR_MAP.keySet()) {
                comboBox.addItem(colorName); // Renk isimlerini ekle
            }
            comboBox.setRenderer(new ColorComboBoxRenderer()); // Özel renderer kullan
            comboBox.setSelectedIndex(0); // İlk öğeyi seçili yap
            // UI'yi yuvarlak yapmak için:
            comboBox.setUI(new RoundedComboBoxUI()); // Özel UI delegate kullan

            // ComboBox değişiklik ses efekti ekle
            comboBox.addActionListener(e -> {

            });

            colorSelectors[i] = comboBox;
            comboBoxPanel.add(comboBox); // comboBoxPanel'e ekle
        }
        // JComboBox panelini inputPanel'in ortasına ekle
        inputPanel.add(comboBoxPanel, BorderLayout.CENTER);

        // Butonlar için ayrı bir panel oluştur
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        controlPanel.setOpaque(false); // Şeffaf yap

        // Butonların eklenme sırasını değiştir: Önce "Ana Ekrana Dön", sonra "Tahmin Et"
        controlPanel.add(backToMenuButton); // Ana Ekrana Dön butonu
        controlPanel.add(guessButton);      // Tahmin Et butonu

        inputPanel.add(controlPanel, BorderLayout.SOUTH); // Kontrol panelini inputPanel'in altına ekle

        inputPanel.revalidate(); // Paneli yeniden doğrula
        inputPanel.repaint(); // Paneli yeniden çiz
    }

    // Gizli renk kodunu oluşturma
    private void generateSecretCode() {
        secretCode.clear(); // Önceki kodu temizle
        Random rand = new Random();
        for (int i = 0; i < currentCombinationLength; i++) {
            secretCode.add(COLORS[rand.nextInt(COLORS.length)]); // Rastgele renk ekle
        }
        attemptsLeft = 15; // Deneme hakkını sıfırla
        attemptsLabel.setText("Deneme hakkı: " + attemptsLeft); // Etiketi güncelle
        guessButton.setEnabled(true); // Butonu aktif et
        for (JComboBox<String> selector : colorSelectors) {
            selector.setEnabled(true); // Seçicileri aktif et
        }
    }

    // Tahmini işleme metodu
    private void processGuess() {
        if (attemptsLeft <= 0) {
            // Deneme hakkı kalmadıysa uyarı ver

            JOptionPane.showMessageDialog(this, "Deneme hakkınız kalmadı! Oyunu kaybettiniz.", "Oyun Bitti", JOptionPane.WARNING_MESSAGE);
            guessButton.setEnabled(false); // Butonu devre dışı bırak
            for (JComboBox<String> selector : colorSelectors) {
                selector.setEnabled(false); // Seçicileri devre dışı bırak
            }
            return;
        }

        List<Color> guess = new ArrayList<>();
        for (JComboBox<String> selector : colorSelectors) {
            guess.add(getColorFromName((String) selector.getSelectedItem())); // Seçilen renkleri al
        }

        JPanel guessRow = new JPanel();
        guessRow.setLayout(new FlowLayout(FlowLayout.CENTER));
        guessRow.setOpaque(false); // Şeffaf yap
        guessRow.setPreferredSize(new Dimension(580, 50));

        for (Color c : guess) {
            JPanel colorPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Gradient efekti için daha canlı renkler
                    Color baseColor = getBackground();
                    Color lighterColor = new Color(
                            Math.min(255, baseColor.getRed() + 40),
                            Math.min(255, baseColor.getGreen() + 40),
                            Math.min(255, baseColor.getBlue() + 40),
                            Math.min(255, baseColor.getAlpha() + 15)
                    );

                    GradientPaint gradient = new GradientPaint(
                            0, 0, lighterColor,
                            getWidth(), getHeight(), baseColor
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                    // Çerçeveyi de yuvarlatmak için
                    g2.setColor(new Color(69, 63, 63, 200)); // Daha belirgin çerçeve
                    g2.setStroke(new BasicStroke(2f)); // Biraz daha kalın çerçeve
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                    g2.dispose();
                }
            };
            colorPanel.setOpaque(false); // Gradient için şeffaf yap
            colorPanel.setBackground(c);
            colorPanel.setPreferredSize(new Dimension(40, 40));
            guessRow.add(colorPanel);
        }

        int correctPosition = 0; // Doğru konumdaki doğru renk sayısı
        int correctColor = 0;    // Doğru renkteki yanlış konum sayısı
        boolean[] usedInSecret = new boolean[currentCombinationLength]; // Gizli kodda kullanılan renkleri takip et
        boolean[] usedInGuess = new boolean[currentCombinationLength];   // Tahminde kullanılan renkleri takip et

        // Doğru konumdaki renkleri bul
        for (int i = 0; i < currentCombinationLength; i++) {
            if (guess.get(i).equals(secretCode.get(i))) {
                correctPosition++;
                usedInSecret[i] = true;
                usedInGuess[i] = true;
            }
        }

        // Doğru renkteki yanlış konumdaki renkleri bul
        for (int i = 0; i < currentCombinationLength; i++) {
            if (usedInGuess[i]) continue; // Zaten doğru konumda kullanıldıysa atla
            for (int j = 0; j < currentCombinationLength; j++) {
                if (!usedInSecret[j] && guess.get(i).equals(secretCode.get(j))) {
                    correctColor++;
                    usedInSecret[j] = true; // Bu rengi gizli kodda kullanıldı olarak işaretle
                    break;
                }
            }
        }

        JLabel resultLabel = new JLabel("  ✔: " + correctPosition + " | ⚠: " + correctColor); // Sonuç etiketi
        resultLabel.setForeground(Color.DARK_GRAY);
        guessRow.add(resultLabel);

        guessesPanel.add(guessRow); // Tahmin satırını panele ekle
        guessesPanel.revalidate();
        guessesPanel.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();

        attemptsLeft--; // Deneme hakkını azalt
        attemptsLabel.setText("Deneme hakkı: " + attemptsLeft); // Etiketi güncelle

        if (correctPosition == currentCombinationLength) {
            // Başarılı tahmin: Başarı ses efekti
            // Puan hesapla
            int baseScore = 200 ; // İlk seviye için başlangıç puanı
            // Her deneme için 5 puan düş. (10 - attemptsLeft - 1) ifadesi, yapılan deneme sayısını verir.
            // Örneğin, 1. denemede bulursa (10 - 9 - 1) = 0 deneme düşüşü, 100 puan.
            // 2. denemede bulursa (10 - 8 - 1) = 1 deneme düşüşü, 100 - 5 = 95 puan.
            int earnedScore = baseScore - ((10 - attemptsLeft - 1) * 5);

            if (earnedScore < 0) earnedScore = 0; // Puanın negatif olmamasını sağla
            score += earnedScore; // Toplam puana ekle
            updateScoreLabel(); // Puan etiketini güncelle

            JOptionPane.showMessageDialog(this, "Seviye " + currentLevel + " tamamlandı! Kazanılan Puan: " + earnedScore + "\nToplam Puan: " + score, "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            nextLevel(); // Sonraki seviyeye geç
        } else if (attemptsLeft == 0) {
            updateScoreLabel(); // Puanı son kez güncelle
            JOptionPane.showMessageDialog(this, "Deneme hakkınız bitti! Doğru cevap: " + getSecretCodeString() + "\nToplam Puanınız: " + score, "Oyun Bitti", JOptionPane.WARNING_MESSAGE);
            guessButton.setEnabled(false); // Butonu devre dışı bırak
            for (JComboBox<String> selector : colorSelectors) {
                selector.setEnabled(false); // Seçicileri devre dışı bırak
            }
        }
    }

    // Sonraki seviyeye geçiş metodu
    private void nextLevel() {
        // Seviye atlama ses efekti


        currentLevel++; // Seviyeyi artır

        setTitle("Renk Kodları - Seviye " + currentLevel); // Başlığı güncelle
        guessesPanel.removeAll(); // Eski tahminleri temizle
        guessesPanel.revalidate();
        guessesPanel.repaint();
        scrollPane.revalidate();
        scrollPane.repaint();
        generateSecretCode(); // Yeni gizli kod oluştur
        setupColorSelectors(); // Renk seçicileri yeniden kur (bu da butonları yeniden ekler)
        attemptsLeft = 15-currentLevel; // Yeni seviye için deneme hakkını sıfırla
        attemptsLabel.setText("Deneme hakkı: " + attemptsLeft); // Etiketi güncelle
        guessButton.setEnabled(true); // Butonu tekrar aktif et
        for (JComboBox<String> selector : colorSelectors) {
            selector.setEnabled(true); // Seçicileri tekrar aktif et
        }
        // BackgroundPanel'e seviye bilgisini gönder
        ((BackgroundPanel) ((BorderLayout) gamePanel.getLayout()).getLayoutComponent(BorderLayout.CENTER)).setCurrentLevel(currentLevel);
    }

    // Puan etiketini güncelleme metodu
    private void updateScoreLabel() {
        scoreLabel.setText("Puan: " + score);
    }

    // Gizli kodun string temsilini döndürür
    private String getSecretCodeString() {
        StringBuilder sb = new StringBuilder();
        for (Color c : secretCode) {
            sb.append(getColorName(c)).append(" ");
        }
        return sb.toString().trim();
    }

    // Renk nesnesinden renk adını döndürür
    private String getColorName(Color c) {
        return NAME_MAP.getOrDefault(c, "Bilinmeyen");
    }

    // Renk adından renk nesnesini döndürür
    private Color getColorFromName(String name) {
        return COLOR_MAP.getOrDefault(name, Color.GRAY);
    }

    // JComboBox için özel renderer (renkleri gösterir)
    private static class ColorComboBoxRenderer extends JLabel implements ListCellRenderer<String> {
        public ColorComboBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value);
            Color baseColor = COLOR_MAP.getOrDefault(value, Color.GRAY);

            // ComboBox için daha canlı, açık renkler
            Color lightColor = new Color(
                    Math.min(255, baseColor.getRed() + 60),
                    Math.min(255, baseColor.getGreen() + 60),
                    Math.min(255, baseColor.getBlue() + 60),
                    160 // Daha az şeffaf, daha belirgin
            );

            if (isSelected) {
                setBackground(new Color(200, 230, 255, 220)); // Daha belirgin selection
                setForeground(new Color(
                        Math.max(0, baseColor.getRed() - 60),
                        Math.max(0, baseColor.getGreen() - 60),
                        Math.max(0, baseColor.getBlue() - 60)
                ));
            } else {
                setBackground(lightColor);
                setForeground(new Color(
                        Math.max(0, baseColor.getRed() - 80),
                        Math.max(0, baseColor.getGreen() - 80),
                        Math.max(0, baseColor.getBlue() - 80)
                ));
            }

            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
        }
    }

    // Ana metot, Swing uygulamasını başlatır
    public static void main(String[] args) {
        // Müzik çalma kodu
        SoundPlayer player = new SoundPlayer();


        // Oyunu başlat
        SwingUtilities.invokeLater(kutuOyunu::new);
    }
}