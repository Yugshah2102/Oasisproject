public class QuestionSeries {
    static String info = "Java Online Test\nThere are 5 questions in this test and 5 minutes to complete them.\n\nINSTRUCTIONS:\n" +
            "- Choose the correct option(s) for each question.\n" +
            "- Click 'Finish' to submit.\n" +
            "- Results will be shown with correct answers marked in red.\n\nBest of luck!";

    static String testtitle = "Java Programming Online Test";
    static int tally = 5;
    static int passMark = 3;
    static int timeLimit = 5;

    static String[] question = {
            "Question 1: What does JVM stand for?",
            "Question 2: Which method is the entry point of Java programs?",
            "Question 3: What is the size of int in Java?",
            "Question 4: Which of these is not a Java keyword?",
            "Question 5: Which keyword is used to inherit a class?"
    };

    static String[][] answers = {
            {"Java Virtual Machine", "Java Variable Method", "Java Volatile Machine", "None"},
            {"start()", "main()", "run()", "execute()"},
            {"4 bytes", "2 bytes", "8 bytes", "Depends on OS"},
            {"static", "try", "unsigned", "extends"},
            {"inherit", "extends", "implements", "super"}
    };

    static int[] n = {1, 1, 1, 1, 1}; // number of correct options per question
    static String[] choice = {"1", "2", "1", "3", "2"}; // correct answers (1-indexed)
}
