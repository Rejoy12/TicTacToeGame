import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToe extends JFrame {
    private final JButton[] buttons = new JButton[9];
    private final JLabel statusLabel = new JLabel("Player X's Turn");
    private final JLabel themeLabel = new JLabel("Current Theme: Light");
    private String currentPlayer = "X";
    private boolean gameActive = true;
    private boolean isLightTheme = true;

    public TicTacToe() {
        setTitle("Tic-Tac-Toe");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

       
        JPanel gridPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 40));
            buttons[i].setFocusPainted(false);
            buttons[i].addActionListener(new ButtonClickListener(i));
            gridPanel.add(buttons[i]);
        }
        add(gridPanel, BorderLayout.CENTER);

        
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        themeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusPanel.add(statusLabel);
        statusPanel.add(themeLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Create control panel
        JPanel controlPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetGame());
        JButton themeSwitchButton = new JButton("Switch Theme");
        themeSwitchButton.addActionListener(e -> toggleTheme());

        controlPanel.add(resetButton);
        controlPanel.add(themeSwitchButton);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void resetGame() {
        for (JButton button : buttons) {
            button.setText("");
            button.setBackground(isLightTheme ? Color.WHITE : Color.DARK_GRAY);
        }
        currentPlayer = "X";
        statusLabel.setText("Player X's Turn");
        gameActive = true;
    }

    private void checkWinner() {
        int[][] winningCombinations = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combo : winningCombinations) {
            if (buttons[combo[0]].getText().equals(currentPlayer) &&
                buttons[combo[1]].getText().equals(currentPlayer) &&
                buttons[combo[2]].getText().equals(currentPlayer)) {
                for (int i : combo) {
                    buttons[i].setBackground(Color.GREEN);
                }
                statusLabel.setText("Player " + currentPlayer + " Wins!");
                gameActive = false;
                return;
            }
        }

        boolean draw = true;
        for (JButton button : buttons) {
            if (button.getText().equals("")) {
                draw = false;
                break;
            }
        }
        if (draw) {
            statusLabel.setText("It's a Draw!");
            gameActive = false;
        }
    }

    private void aiMove() {
        if (!gameActive) return;

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getText().equals("")) {
                buttons[i].setText("O");
                int score = minimax(0, false);
                buttons[i].setText("");
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        if (bestMove != -1) {
            buttons[bestMove].setText("O");
            buttons[bestMove].setBackground(isLightTheme ? Color.PINK : Color.MAGENTA);
            checkWinner();

            if (gameActive) {
                currentPlayer = "X";
                statusLabel.setText("Player X's Turn");
            }
        }
    }

    private int minimax(int depth, boolean isMaximizing) {
        if (checkWinningCondition("O")) return 10 - depth;
        if (checkWinningCondition("X")) return depth - 10;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("O");
                    int score = minimax(depth + 1, false);
                    buttons[i].setText("");
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].getText().equals("")) {
                    buttons[i].setText("X");
                    int score = minimax(depth + 1, true);
                    buttons[i].setText("");
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private boolean checkWinningCondition(String player) {
        int[][] winningCombinations = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combo : winningCombinations) {
            if (buttons[combo[0]].getText().equals(player) &&
                buttons[combo[1]].getText().equals(player) &&
                buttons[combo[2]].getText().equals(player)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (JButton button : buttons) {
            if (button.getText().equals("")) return false;
        }
        return true;
    }

    private void toggleTheme() {
        isLightTheme = !isLightTheme;
        Color bgColor = isLightTheme ? Color.WHITE : Color.DARK_GRAY;
        Color textColor = isLightTheme ? Color.BLACK : Color.WHITE;

        getContentPane().setBackground(bgColor);
        statusLabel.setForeground(textColor);
        themeLabel.setForeground(textColor);
        themeLabel.setText("Current Theme: " + (isLightTheme ? "Light" : "Dark"));

        for (JButton button : buttons) {
            button.setBackground(bgColor);
            button.setForeground(textColor);
        }
    }

    private class ButtonClickListener implements ActionListener {
        private final int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        
        public void actionPerformed(ActionEvent e) {
            if (gameActive && buttons[index].getText().equals("")) {
                buttons[index].setText(currentPlayer);
                buttons[index].setBackground(isLightTheme ? Color.CYAN : Color.ORANGE);
                checkWinner();

                if (gameActive) {
                    currentPlayer = "O";
                    statusLabel.setText("Player O's Turn");
                    aiMove();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TicTacToe game = new TicTacToe();
            game.setVisible(true);
        });
    }
}
