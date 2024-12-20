package MemoryCardGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryCardGame {
    // Khai báo các thành phần chính của game
    private JFrame frame;          // Cửa sổ chính của game
    private List<Card> cards;      // Danh sách các thẻ bài
    private Card selectedCard;     // Thẻ đang được chọn
    private Card c1;              // Thẻ thứ nhất được lật
    private Card c2;              // Thẻ thứ hai được lật
    private Timer timer;          // Timer để kiểm tra cặp thẻ
    private int moves;            // Số lần lật thẻ
    private int pairs;            // Số cặp đã tìm thấy
    private JLabel scoreLabel;    // Hiển thị điểm số
    private boolean canClick;     // Kiểm soát việc có thể click hay không
    
    // Thêm biến cho tính năng thời gian
    private int timeLeft = 60;    // Thời gian còn lại (giây)
    private Timer gameTimer;      // Timer đếm ngược thời gian
    private JLabel timeLabel;     // Hiển thị thời gian

    public MemoryCardGame() {
        // Thiết lập cửa sổ chính
        frame = new JFrame("Trò Chơi Lật Thẻ Bài");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Khởi tạo các thành phần game
        initializeGame();
        createScorePanel();
        createCardPanel();
        setupGameTimer(); // Thiết lập timer đếm ngược

        frame.setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình
        frame.setVisible(true);
    }

    // Thiết lập timer đếm ngược thời gian
    private void setupGameTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                updateTimeLabel();
                
                // Kiểm tra hết giờ
                if (timeLeft <= 0) {
                    gameTimer.stop();
                    canClick = false;
                    JOptionPane.showMessageDialog(frame, "Hết giờ! Bạn đã tìm thấy " + pairs + " cặp!");
                    resetGame();
                }
            }
        });
    }

    // Cập nhật hiển thị thời gian
    private void updateTimeLabel() {
        timeLabel.setText(String.format("Thời gian: %02d giây", timeLeft));
    }

    // Khởi tạo game
    private void initializeGame() {
        List<String> cardValues = new ArrayList<>();
        // Thêm các cặp thẻ (chữ và số)
        String[] symbols = {"A", "B", "C", "D", "E", "F", "G", "H"};
        for (String symbol : symbols) {
            cardValues.add(symbol);
            cardValues.add(symbol); // Thêm mỗi ký tự 2 lần để tạo cặp
        }
        Collections.shuffle(cardValues); // Xáo trộn các thẻ

        // Tạo các thẻ bài
        cards = new ArrayList<>();
        for (String value : cardValues) {
            cards.add(new Card(value));
        }

        // Khởi tạo các giá trị ban đầu
        moves = 0;
        pairs = 0;
        canClick = true;
        timeLeft = 60; // Reset thời gian

        // Timer kiểm tra cặp thẻ
        timer = new Timer(750, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                checkCards();
            }
        });
        timer.setRepeats(false);
    }
        // Tạo panel hiển thị điểm và thời gian
    private void createScorePanel() {
        JPanel scorePanel = new JPanel();
        
        // Panel cho điểm số
        scoreLabel = new JLabel("Số lần lật: 0 | Cặp đã tìm: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Label hiển thị thời gian
        timeLabel = new JLabel("Thời gian: 60 giây");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Nút chơi lại
        JButton resetButton = new JButton("Chơi lại");
        resetButton.setFont(new Font("Arial", Font.BOLD, 16));
        resetButton.addActionListener(e -> resetGame());

        scorePanel.add(scoreLabel);
        scorePanel.add(timeLabel);
        scorePanel.add(resetButton);
        frame.add(scorePanel, BorderLayout.NORTH);
    }
    // Tạo panel chứa các thẻ bài
    private void createCardPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(232, 232, 232));

        // Thêm các thẻ vào panel và xử lý sự kiện click
        for (Card card : cards) {
            mainPanel.add(card);
            card.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    // Kiểm tra các điều kiện trước khi cho phép lật thẻ
                    if (!canClick) return;
                    if (card.isMatched()) return;
                    if (card == selectedCard) return;

                    if (selectedCard == null) {
                        // Lật thẻ đầu tiên
                        selectedCard = card;
                        card.reveal();
                    } else {
                        // Lật thẻ thứ hai và kiểm tra
                        c1 = selectedCard;
                        c2 = card;
                        card.reveal();
                        canClick = false;
                        timer.start();
                    }
                }
            });
        }

        frame.add(mainPanel, BorderLayout.CENTER);
    }

    // Kiểm tra cặp thẻ đã lật
    private void checkCards() {
        moves++;
        if (c1.getValue().equals(c2.getValue())) {
            // Nếu hai thẻ giống nhau
            c1.setMatched(true);
            c2.setMatched(true);
            pairs++;
            if (pairs == 8) {
                // Thắng game
                gameTimer.stop();
                JOptionPane.showMessageDialog(frame, 
                    "Chúc mừng! Bạn đã hoàn thành trong " + moves + " lượt và " + (60 - timeLeft) + " giây!");
            }
        } else {
            // Nếu hai thẻ khác nhau
            c1.hide();
            c2.hide();
        }
        selectedCard = null;
        canClick = true;
        scoreLabel.setText("Số lần lật: " + moves + " | Cặp đã tìm: " + pairs);
    }

    // Reset lại game
    private void resetGame() {
        // Dừng và reset timer
        gameTimer.stop();
        timeLeft = 60;
        updateTimeLabel();
        
        // Reset các thẻ
        for (Card card : cards) {
            card.reset();
        }
        Collections.shuffle(cards);

        // Reset các giá trị
        moves = 0;
        pairs = 0;
        selectedCard = null;
        canClick = true;
        scoreLabel.setText("Số lần lật: 0 | Cặp đã tìm: 0");
        
        // Bắt đầu timer mới
        gameTimer.start();

        frame.revalidate();
        frame.repaint();
    }

    // Class Card để tạo thẻ bài
    private class Card extends JButton {
        private String value;      // Giá trị của thẻ
        private boolean matched;   // Trạng thái đã ghép đôi

        public Card(String value) {
            this.value = value;
            this.matched = false;
            setFont(new Font("Arial", Font.BOLD, 40));
            setFocusPainted(false);
            hide();
        }

        // Các phương thức của thẻ
        public String getValue() { return value; }

        public void setMatched(boolean matched) {
            this.matched = matched;
            if (matched) {
                setBackground(new Color(179, 255, 179)); // Màu xanh nhạt cho thẻ đã ghép đôi
            }
        }

        public boolean isMatched() { return matched; }

        public void reveal() {
            setText(value);
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        public void hide() {
            setText("?");
            setBackground(new Color(70, 130, 180));
            setForeground(Color.BLACK);
        }

        public void reset() {
            matched = false;
            hide();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MemoryCardGame game = new MemoryCardGame();
            game.gameTimer.start(); // Bắt đầu timer khi game khởi động
        });
    }
}