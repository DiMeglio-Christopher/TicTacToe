import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TicTacToe_V3 {
    private static Boolean playAgain = false;
    private final JFrame window;
    private final JButton[] buttonList;
    private final int[][] board;
    private final CyclicBarrier control;
    private String turn;
    private String option;
    private int turnNumber;
    private String winner;

    private TicTacToe_V3() {
        this.turn = "X";
        this.option = "";
        this.turnNumber = 0;
        this.window = new JFrame();
        this.buttonList = new JButton[11];
        this.board = new int[3][3];
        this.winner = "";
        this.control = new CyclicBarrier(2);
    }

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        do {
            TicTacToe_V3 game = new TicTacToe_V3();
            game.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            game.optionPrompt();
            game.drawBoard();
            game.control.reset();
            game.congratulations();
        } while (playAgain);
    }

    private void optionPrompt() {
        JPanel panel = new JPanel();
        JButton player = new JButton("vs Player");
        player.addActionListener(new MyActionListener());
        buttonList[9] = player;
        JButton cpu = new JButton("vs CPU");
        cpu.addActionListener(new MyActionListener());
        buttonList[10] = cpu;
        panel.add(player);
        panel.add(cpu);
        window.setContentPane(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void drawBoard() throws InterruptedException, BrokenBarrierException {
        control.await();
        JPanel panel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            buttonList[i] = new JButton();
            buttonList[i].setPreferredSize(new Dimension(200, 200));
            buttonList[i].setFocusable(false);
            buttonList[i].setFont(new Font("Times Roman", Font.BOLD, 140));
            buttonList[i].addActionListener(new MyActionListener());
            panel.add(buttonList[i]);
        }
        UIManager.put("Button.disabledText", Color.BLACK);
        window.setVisible(false);
        window.setContentPane(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void changeTurn() throws InterruptedException, BrokenBarrierException {
        turnNumber++;
        if (turnNumber > 4 && checkWinner()) {
            winner = turn;
            control.await();
        } else if (turnNumber > 8) {
            winner = "draw";
            control.await();
        }
        if ((turnNumber % 2) != 0) {
            turn = "O";
        } else {
            turn = "X";
        }
        if (option.equals("cpu") && winner.equals("") && turn.equals("O")) {
            computer();
        }
    }

    private boolean checkWinner() {
        boolean won = false;
        int diagSum = 0;
        int antiDiagSum = 0;
        for (int i = 0; i < 3; i++) {
            int rowSum = 0;
            int columnSum = 0;
            diagSum += board[i][i];
            antiDiagSum += board[i][2 - i];
            for (int j = 0; j < 3; j++) {
                rowSum += board[i][j];
                columnSum += board[j][i];
                if (j == 2 && (Math.abs(rowSum) == 3 || Math.abs(columnSum) == 3 || Math.abs(diagSum) == 3
                        || Math.abs(antiDiagSum) == 3))
                    won = true;
            }
        }
        return won;
    }

    private void congratulations() throws InterruptedException, BrokenBarrierException {
        control.await();
        for (int i = 0; i < 8; i++) {
            buttonList[i].setEnabled(false);
        }
        JFrame message = new JFrame();
        JPanel panel = new JPanel();
        JLabel label;
        if (!winner.equals("draw")) {
            label = new JLabel(winner + " Wins!");
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
        control.await();
        message.dispose();
    }

    private void computer() {
        System.out.println(Thread.currentThread());
        Random rand = new Random();
        if (turnNumber == 1 && !buttonList[4].isEnabled()) {
            int[] correctMoves = {0, 2, 6, 8};
            buttonList[correctMoves[rand.nextInt(3)]].doClick();
        } else {
            buttonList[4].doClick();
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
            diagSum += board[i][i];
            antiDiagSum += board[i][2 - i];
            for (int j = 0; j < 3; j++) {
                rowSum += board[i][j];
                columnSum += board[j][i];
                if (board[i][j] == 0) {
                    xRowPos = i;
                    yRowPos = j;
                }
                if (board[j][i] == 0) {
                    xColumnPos = i;
                    yColumnPos = j;
                }
                if (board[i][i] == 0) {
                    diagPos = i;
                }
                if (board[i][2 - i] == 0) {
                    antiDiagPos = i;
                }
                if (turn.equals("O") && j == 2) {
                    if (Math.abs(diagSum) == 2) {
                        buttonIndex = (diagPos * 3) + diagPos;
                        buttonList[buttonIndex].doClick();
                        System.out.println("Stop Diag: " + buttonIndex);
                    } else if (Math.abs(antiDiagSum) == 2) {
                        buttonIndex = (antiDiagPos * 3) + (2 - antiDiagPos);
                        buttonList[buttonIndex].doClick();
                        System.out.println("Stop AntiDiag: " + buttonIndex);
                    } else if ((Math.abs(rowSum) == 2)) {
                        buttonIndex = (xRowPos * 3) + yRowPos;
                        buttonList[buttonIndex].doClick();
                        System.out.println("Stop Row: " + buttonIndex);
                    } else if ((Math.abs(columnSum) == 2)) {
                        buttonIndex = (yColumnPos * 3) + xColumnPos;
                        buttonList[buttonIndex].doClick();
                        System.out.println("Stop Column: " + buttonIndex);
                    } else if (i == 2) {
                        int index;
                        while (turn.equals("O")) {
                            int[] correctMoves = {1, 3, 5, 7};
                            index = rand.nextInt(3);
                            buttonList[correctMoves[index]].doClick();
                        }
                        System.out.println("Random Clicked");
                    }
                }
            }
        }
    }

    private void uiHandlerThread(ActionEvent e) throws InterruptedException {
        Thread worker = new Thread(() -> {
            JButton button = (JButton) e.getSource();
            try {
                if (button == buttonList[9]) {
                    option = "player";
                    control.await();
                }
                if (button == buttonList[10]) {
                    option = "cpu";
                    control.await();
                }
                for (int i = 0; i < 9; i++) {
                    if (button == buttonList[i]) {
                        button.setEnabled(false);
                        button.setText(turn);
                        int x = i / 3;
                        int y = i % 3;
                        board[x][y] = turn.equals("X") ? 1 : -1;
                        changeTurn();
                    }
                }
                if (button.getText().equals("Play Again")) {
                    playAgain = true;
                    window.dispose();
                    control.await();
                    control.reset();
                }
            } catch (InterruptedException | BrokenBarrierException e1) {
                e1.printStackTrace();
            }
        });
        worker.start();
        Thread.sleep(0, 1);
    }

    class MyActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                uiHandlerThread(e);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}