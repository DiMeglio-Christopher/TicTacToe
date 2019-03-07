import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TicTacToe {
    private static Boolean playAgain = false;
    private String turn;
    private String option;
    private int turnNumber;
    private final JFrame window;
    private JButton[] buttonList;
    private final int[][] board;
    private String winner;
    private final CyclicBarrier control;
    private static TicTacToe x = null;

    private TicTacToe() {
        turn = "X";
        option = "";
        turnNumber = 0;
        window = new JFrame();
        buttonList = new JButton[11];
        board = new int[3][3];
        winner = "";
        control = new CyclicBarrier(2);
    }



    private static TicTacToe getInstance() {
        if (x == null) {
            x = new TicTacToe();
        }
        return x;
    }

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        do {
            TicTacToe game = getInstance();
            game.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            optionPrompt();
            drawBoard();
            game.control.reset();
            congratulations();
        } while (playAgain);
    }

    private static void optionPrompt() {
        JPanel panel = new JPanel();
        JButton player = new JButton("vs Player");
        player.addActionListener(new TicTacToe.MyActionListener());
        x.buttonList[9] = player;
        JButton cpu = new JButton("vs CPU");
        cpu.addActionListener(new MyActionListener());
        x.buttonList[10] = cpu;
        panel.add(player);
        panel.add(cpu);
       x.window.setContentPane(panel);
       x.window.pack();
       x.window.setLocationRelativeTo(null);
       x.window.setVisible(true);
    }

    private static void drawBoard() throws InterruptedException, BrokenBarrierException {
        x.control.await();
        JPanel panel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            x.buttonList[i] = new JButton();
            x.buttonList[i].setPreferredSize(new Dimension(200, 200));
            x.buttonList[i].setFocusable(false);
            x.buttonList[i].setFont(new Font("Times Roman", Font.BOLD, 140));
            x.buttonList[i].addActionListener(new MyActionListener());
            panel.add(x.buttonList[i]);
        }
        UIManager.put("Button.disabledText", Color.BLACK);
        x.window.setVisible(false);
        x.window.setContentPane(panel);
        x.window.pack();
        x.window.setLocationRelativeTo(null);
        x.window.setVisible(true);
    }

    private static void changeTurn() throws InterruptedException, BrokenBarrierException {
        x.turnNumber++;
        if (x.turnNumber > 4 && checkWinner()) {
            x.winner = x.turn;
            x.control.await();
        } else if (x.turnNumber > 8) {
            x.winner = "draw";
            x.control.await();
        }
        if ((x.turnNumber % 2) != 0) {
            x.turn = "O";
        } else {
            x.turn = "X";
        }
        if (x.option.equals("cpu") && x.winner.equals("") && x.turn.equals("O")) {
            computer();
        }
    }

    private static boolean checkWinner() {
        boolean won = false;
        int diagSum = 0;
        int antiDiagSum = 0;
        for (int i = 0; i < 3; i++) {
            int rowSum = 0;
            int columnSum = 0;
            diagSum += x.board[i][i];
            antiDiagSum += x.board[i][2 - i];
            for (int j = 0; j < 3; j++) {
                rowSum += x.board[i][j];
                columnSum += x.board[j][i];
                if (j == 2 && (Math.abs(rowSum) == 3 || Math.abs(columnSum) == 3 || Math.abs(diagSum) == 3
                        || Math.abs(antiDiagSum) == 3))
                    won = true;
            }
        }
        return won;
    }

    private static void congratulations() throws InterruptedException, BrokenBarrierException {
        x.control.await();
        for (int i = 0; i < 9; i++) {
            x.buttonList[i].setEnabled(false);
        }
        JFrame message = new JFrame();
        JPanel panel = new JPanel();
        JLabel label;
        if (!x.winner.equals("draw")) {
            label = new JLabel(x.winner + " Wins!");
        } else {
            label = new JLabel("Draw");
        }
        label.setFont(new Font("Times Roman", Font.PLAIN, 80));
        label.setForeground(Color.black);
        panel.setLayout(new BorderLayout());
        JButton button = new JButton("Play Again");
        button.addActionListener(new MyActionListener());
        panel.add(label);
        panel.add(button, BorderLayout.SOUTH);
        message.setContentPane(panel);
        message.pack();
        message.setLocationRelativeTo(null);
        message.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        message.setVisible(true);
        x.control.await();
        message.dispose();
    }

    private static void computer() {
        Random rand = new Random();
        if (x.turnNumber == 1 && !x.buttonList[4].isEnabled()) {
            int[] correctMoves = {0, 2, 6, 8};
            x.buttonList[correctMoves[rand.nextInt(3)]].doClick();
            return;
        }
        if (x.buttonList[4].isEnabled()) {
            x.buttonList[4].doClick();
            return;
        }
        int xRowPos = 0;
        int yRowPos = 0;
        int xColumnPos = 0;
        int yColumnPos = 0;
        int diagPos = 0;
        int antiDiagPos = 0;
        int diagSum = 0;
        int antiDiagSum = 0;
        int buttonIndex;
        for (int i = 0; i < 3; i++) {
            int rowSum = 0;
            int columnSum = 0;
            diagSum += x.board[i][i];
            antiDiagSum += x.board[i][2 - i];
            for (int j = 0; j < 3; j++) {
                rowSum += x.board[i][j];
                columnSum += x.board[j][i];
                if (x.board[i][j] == 0) {
                    xRowPos = i;
                    yRowPos = j;
                }
                if (x.board[j][i] == 0) {
                    xColumnPos = i;
                    yColumnPos = j;
                }
                if (x.board[i][i] == 0) {
                    diagPos = i;
                }
                if (x.board[i][2 - i] == 0) {
                    antiDiagPos = i;
                }
                if (x.turn.equals("O") && j == 2) {
                    if (Math.abs(diagSum) == 2 && i == 2) {
                        buttonIndex = (diagPos * 3) + diagPos;
                        x.buttonList[buttonIndex].doClick();
                        System.out.println("Stop Diag: " + buttonIndex);
                    } else if (Math.abs(antiDiagSum) == 2) {
                        buttonIndex = (antiDiagPos * 3) + (2 - antiDiagPos);
                        x.buttonList[buttonIndex].doClick();
                        System.out.println("Stop AntiDiag: " + buttonIndex);
                    } else if ((Math.abs(rowSum) == 2)) {
                        buttonIndex = (xRowPos * 3) + yRowPos;
                        x.buttonList[buttonIndex].doClick();
                        System.out.println("Stop Row: " + buttonIndex);
                    } else if ((Math.abs(columnSum) == 2)) {
                        buttonIndex = (yColumnPos * 3) + xColumnPos;
                        x.buttonList[buttonIndex].doClick();
                        System.out.println("Stop Column: " + buttonIndex);
                    } else if (i >= 2) {
                        int index;
                        while (x.turn.equals("O")) {
                            int[] correctMoves = {1, 3, 5, 7};
                            index = rand.nextInt(3);
                            x.buttonList[correctMoves[index]].doClick();
                        }
                        System.out.println("Random Clicked");
                    }
                }
            }
        }
    }

    static class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            if (button == x.buttonList[9]) {
                x.option = "player";
                try {
                    x.control.await();
                } catch (InterruptedException | BrokenBarrierException e1) {
                    e1.printStackTrace();
                }
            }
            if (button == x.buttonList[10]) {
                x.option = "cpu";
                try {
                    x.control.await();
                } catch (InterruptedException | BrokenBarrierException e1) {
                    e1.printStackTrace();
                }
            }
            for (int i = 0; i < 9; i++) {
                if (button == x.buttonList[i]) {
                    button.setEnabled(false);
                    button.setText(x.turn);
                    int i1 = i / 3;
                    int y = i % 3;
                    x.board[i1][y] = x.turn.equals("X") ? 1 : -1;
                    try {
                        changeTurn();
                    } catch (InterruptedException | BrokenBarrierException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if (button.getText().equals("Play Again")) {
                playAgain = true;
                x.window.dispose();
                try {
                    x.control.await();
                } catch (InterruptedException | BrokenBarrierException e1) {
                    e1.printStackTrace();
                }
                x.control.reset();
                x = null;
            }
        }
    }
}