import javax.swing.*;
import java.util.Random;

public class GuessTheNumberGame {
    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_ROUNDS = 3;
    private static final int RANGE = 100;

    private int score = 0;

    public void playGame() {
        JOptionPane.showMessageDialog(null, "Welcome to Guess the Number Game!\nTry to guess a number between 1 and " + RANGE);

        for (int round = 1; round <= MAX_ROUNDS; round++) {
            int numberToGuess = new Random().nextInt(RANGE) + 1;
            int attemptsLeft = MAX_ATTEMPTS;
            boolean guessedCorrect = false;

            while (attemptsLeft > 0) {
                String input = JOptionPane.showInputDialog(null,
                        "Round " + round + "\nAttempts left: " + attemptsLeft + "\nEnter your guess:");

                if (input == null) {
                    JOptionPane.showMessageDialog(null, "Game Cancelled.");
                    return;
                }

                try {
                    int guess = Integer.parseInt(input);
                    if (guess < 1 || guess > RANGE) {
                        JOptionPane.showMessageDialog(null, "Please guess a number between 1 and " + RANGE);
                        continue;
                    }

                    if (guess == numberToGuess) {
                        guessedCorrect = true;
                        int roundScore = attemptsLeft * 20;
                        score += roundScore;
                        JOptionPane.showMessageDialog(null,
                                "Correct! You guessed it!\nYou earned " + roundScore + " points this round.");
                        break;
                    } else if (guess < numberToGuess) {
                        JOptionPane.showMessageDialog(null, "Too low!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Too high!");
                    }

                    attemptsLeft--;
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid input! Please enter a valid number.");
                }
            }

            if (!guessedCorrect) {
                JOptionPane.showMessageDialog(null,
                        "You've used all attempts!\nThe correct number was: " + numberToGuess);
            }
        }

        JOptionPane.showMessageDialog(null, "Game Over!\nYour total score: " + score + " / " + (MAX_ROUNDS * 100));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuessTheNumberGame().playGame());
    }
}
