import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OnlineTest extends JFrame implements ActionListener, ItemListener, Runnable {
    static String studentName;
    int quesNum = 0, mark = 0;
    boolean startTest = false, finished = false;

    JLabel titleLabel, timerLabel, questionLabel, resultLabel;
    JTextArea[] answerBoxes = new JTextArea[4];
    JCheckBox[] options = new JCheckBox[4];
    JButton startBtn, nextBtn, finishBtn;
    JTextPane infoPane;
    Timer timer;
    int seconds = 0, minutes = 0;

    String[] userAnswers = new String[QuestionSeries.tally];

    public OnlineTest() {
        setTitle(QuestionSeries.testtitle);
        setLayout(new BorderLayout());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // North Panel - Title and Timer
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        titleLabel = new JLabel("Welcome to the Online Java Test", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102));
        timerLabel = new JLabel("Time: 0 mins 0 secs", JLabel.CENTER);
        timerLabel.setForeground(Color.RED);
        topPanel.add(titleLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Info & Question
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        infoPane = new JTextPane();
        infoPane.setText(QuestionSeries.info);
        infoPane.setEditable(false);
        infoPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        centerPanel.add(new JScrollPane(infoPane));

        JPanel questionPanel = new JPanel(new BorderLayout());
        questionLabel = new JLabel("Click Start to begin...", JLabel.CENTER);
        questionPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        for (int i = 0; i < 4; i++) {
            options[i] = new JCheckBox();
            options[i].addItemListener(this);
            answerBoxes[i] = new JTextArea();
            answerBoxes[i].setEditable(false);
            answerBoxes[i].setLineWrap(true);
            answerBoxes[i].setWrapStyleWord(true);
            JPanel optBox = new JPanel(new BorderLayout());
            optBox.add(options[i], BorderLayout.WEST);
            optBox.add(answerBoxes[i], BorderLayout.CENTER);
            optionsPanel.add(optBox);
        }
        questionPanel.add(optionsPanel, BorderLayout.CENTER);

        centerPanel.add(questionPanel);
        add(centerPanel, BorderLayout.CENTER);

        // South Panel - Buttons
        JPanel bottomPanel = new JPanel();
        startBtn = new JButton("Start");
        nextBtn = new JButton("Next");
        finishBtn = new JButton("Finish");

        startBtn.addActionListener(this);
        nextBtn.addActionListener(this);
        finishBtn.addActionListener(this);

        bottomPanel.add(startBtn);
        bottomPanel.add(nextBtn);
        bottomPanel.add(finishBtn);

        resultLabel = new JLabel("");
        resultLabel.setForeground(new Color(0, 128, 0));
        bottomPanel.add(resultLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        nextBtn.setEnabled(false);
        finishBtn.setEnabled(false);

        setVisible(true);
    }

    public void run() {
        while (startTest && !finished) {
            try {
                Thread.sleep(1000);
                seconds++;
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                timerLabel.setText("Time: " + minutes + " mins " + seconds + " secs");

                if (minutes >= QuestionSeries.timeLimit) {
                    finished = true;
                    endTest();
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    void displayQuestion() {
        String q = QuestionSeries.question[quesNum];
        String[] opts = QuestionSeries.answers[quesNum];
        questionLabel.setText("<html><b>" + q + "</b></html>");
        for (int i = 0; i < 4; i++) {
            answerBoxes[i].setText(opts[i]);
            answerBoxes[i].setBackground(Color.WHITE);
            options[i].setSelected(false);
        }
    }

    void recordAnswer() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected()) sb.append(i + 1);
        }
        userAnswers[quesNum] = sb.toString();
    }

    void endTest() {
        recordAnswer();
        int total = QuestionSeries.tally;
        int correct = 0;

        for (int i = 0; i < total; i++) {
            if (userAnswers[i] != null && userAnswers[i].equals(QuestionSeries.choice[i])) {
                correct++;
            }
        }

        double percentage = ((double) correct / total) * 100;
        resultLabel.setText("Score: " + correct + "/" + total + " (" + String.format("%.1f", percentage) + "%)");

        if (correct >= QuestionSeries.passMark) {
            JOptionPane.showMessageDialog(this, "Congratulations, you passed!", "Result", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Better luck next time!", "Result", JOptionPane.WARNING_MESSAGE);
        }

        nextBtn.setEnabled(false);
        finishBtn.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == startBtn) {
            studentName = JOptionPane.showInputDialog(this, "Enter your name:");
            if (studentName == null || studentName.trim().isEmpty()) studentName = "Anonymous";
            titleLabel.setText("Welcome: " + studentName);
            startTest = true;
            startBtn.setEnabled(false);
            nextBtn.setEnabled(true);
            finishBtn.setEnabled(true);
            new Thread(this).start(); // Start timer
            displayQuestion();
        } else if (src == nextBtn) {
            recordAnswer();
            quesNum++;
            if (quesNum >= QuestionSeries.tally) {
                quesNum = 0;
            }
            displayQuestion();
        } else if (src == finishBtn) {
            finished = true;
            endTest();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // limit choices to allowed number
        int selectedCount = 0;
        for (JCheckBox box : options) {
            if (box.isSelected()) selectedCount++;
        }

        if (selectedCount > QuestionSeries.n[quesNum]) {
            JOptionPane.showMessageDialog(this, "Only " + QuestionSeries.n[quesNum] + " option(s) allowed!", "Warning", JOptionPane.WARNING_MESSAGE);
            ((JCheckBox) e.getSource()).setSelected(false);
        }
    }

    public static void main(String[] args) {
        new OnlineTest();
    }
}
