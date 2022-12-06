package com.example.javafxdemo;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;


public class wordAnalyzer extends Application {

    StringBuilder strBu = new StringBuilder("");

    // Stores strBu as a string using String.valueOf
    String str;

    Scanner fileWord;

    //ArrayList to store each word occurrence.
    ArrayList<String> wordsArray = new ArrayList<String>();

    //ArrayList to store count of word occurrence.
    ArrayList<Integer> freqArray = new ArrayList<Integer>();

    //Object Array used to store the wordsArray and freqArray for sorting
    ArrayList<sortA> so = new ArrayList<>();

    //LinkedHashMap used to store sorted values of both wordsArray and freqArray
    LinkedHashMap<String, Integer> set = new LinkedHashMap<>();

    //LinkedHashMap used to store values from the mySQL database
    LinkedHashMap<String, Integer> setTwo = new LinkedHashMap<>();

    //used to store sorted word counts from freqArray
    Object sum = null;

    //used to store sorted words from wordsArray
    Object words;

    // Variables below are used for connecting the wordoccurrances mySQL database



    /***
     * The poemRetrieval() method retrieves the title and body from a free online eBook (#45484) containing a poem by Edgar Allan Poe called the Raven.
     * The eBook was created by the Gutenberg organization and is located at the following address: https://www.gutenberg.org/files/1065/1065-h/1065-h.htm
     *
     * @return This method returns a String containing the poem title and body, which is extracted from the poem using Jsoup.
     * @throws IOException An exception might occur if the website mentioned above is unavailable.
     */

    public String poemRetrieval() throws IOException {

        // Establishes connection to website.
        Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();
        // Collects website data by tag.

        Elements title = doc.getElementsByTag("h1");
        Elements body = doc.getElementsByTag("p");


            //for-each loop used to append poem words to StringBuilder and add words to the wordoccurrences mySQL database.
            for (Element b : body) {

                strBu.append(b.text());
            }
            //for-each loop to append title to StringBuilder  and add words to the wordoccurrences mySQL database.
            for (Element t : title) {

                strBu.append(t.text());
            }

        str = String.valueOf(strBu);

    return str;
    }

    /***
     * The strScanner(String str) accepts a String (str).  The start() method calls this method and passes the poemRetrieval() method as a parameter.
     * This method then parses the string to determine the occurrence of each of the words and the number of times that the words occur in the poem.
     * Each word found is stored in the wordsArray arrayList. The count of each word is stored in the freqArray arrayList.
     * The step above is done once non-alpha numeric characters are removed.
     *
     * @param str  This parameter accepts a string provided by the poemRetrieval() method.
     * @return returns the wordsArray arrayList.
     */
    public ArrayList<String> strScanner(String str) throws SQLException  {

        Connection connect = null;
        //Statement statement = null;

        PreparedStatement preparedStatement = null;
        //ResultSet resultSet = null;


        //scanner used for parsing data stored in StringBuilder.
        fileWord = new Scanner(str);

        //While loop to itereate to each word provided by scanner.
        while (fileWord.hasNext()) {

            // Delimiter used to parsing purposes and to remove some unneeded alphanumeric characters.
            fileWord.useDelimiter("\s|\n|\\—|\\.|\\!");

            //String for each word occurrence
            String word = fileWord.next();

            //String to store words with non-alphameric characters removed in uppercase.
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();

            connect = DriverManager.getConnection("jdbc:mysql://localhost/wordoccurrences?" + "user=root&password=password");
            preparedStatement = connect
                .prepareStatement("INSERT INTO word(words, frequencies) VALUES (?, 1) ON DUPLICATE KEY UPDATE frequencies = frequencies + 1;");

            preparedStatement.setString(1, cleanWord);
            preparedStatement.executeUpdate();

            //If statement to search for word provided by the scanner in the wordsArray ArrayList.
            //If the word is found the corresponding index in the freqArray ArrayList is updated

            if (wordsArray.contains(cleanWord)) {
                int arrayIndex = wordsArray.indexOf(cleanWord);
                freqArray.set(arrayIndex, freqArray.get(arrayIndex) + 1);

                //If the word in not found in the wordsArray, it is added and corresponding freqArray is updated at the same index.
            } else {
                wordsArray.add(cleanWord);
                freqArray.add(1);

            }
        }
        //for loop used to remove empty spaces in both wordsArray and freqArray ArrayLists.
        for (int i = 0; i < wordsArray.size(); i++) {
            if (wordsArray.get(i).isEmpty()) {

                wordsArray.remove(i);
                freqArray.remove(i);
            }
        }
        //mySQL statement to delete row containing empty spaces.
        preparedStatement = connect
                .prepareStatement("DELETE FROM word WHERE words='';");
        preparedStatement.executeUpdate();
        connect.close();

        return wordsArray;
    }

    /***
     * The objArrayCreator() method serves to store the Integer ArrayList and String ArrayList as SortA ArrayList object.
     *
     * @return Returns object ArrayList of type SortA containing the freqArray ArrayList values (word count) and wordsArray ArrayList (poem Words)
     */
    public ArrayList<sortA>  objArrayLCreator() {
        for (int i = 0; i < wordsArray.size(); i++) {
            so.add(new sortA(freqArray.get(i), wordsArray.get(i)));
        }
        return so;
    }

    /***
     *  The consolePrinter() method prints the contents of the first 20 words and word counts sorted by word count in the so Arraylist
     *
     */
    public void consolePrinter() throws SQLException {

        //System.out.println(freqArray);
       // System.out.println(wordsArray);

        System.out.println("");
        System.out.println("        WORD ANALYZER");
        System.out.println("");
        System.out.println("-------------------------------");
        System.out.println("WORD" + "             " + "WORD FREQUENCY");
        System.out.println("-------------------------------");

        //for loop to display the first 20 values in the so ArrayList and LinkedHashSet.
        for (int i = 0; i < 20; i++) {  // i < so.size()   i < 20

            sum = so.get(i).c;
            words = so.get(i).w;

            //set.add(words, sum);
            set.put((String) words, (Integer) sum);


            System.out.printf("%-10s %20s\n", words, sum);
        }

        //Prints out LinkedHashMap
        System.out.println("");
        System.out.println("LinkedHashMap output:");
        System.out.println("Pairs: " + setBuilder());

        Connection connect = null;
        Statement statement = null;

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        //Connect to mySQL database

        connect = DriverManager.getConnection("jdbc:mysql://localhost/wordoccurrences?" + "user=root&password=password");

        statement = connect.createStatement();

        //mySQL statement to sort the word table in descending order by fequency and limit results to 20 rows.

        resultSet = statement.executeQuery("SELECT * FROM word ORDER BY frequencies DESC LIMIT 20;");

        System.out.println ("");
        System.out.println ("Database Output:");
        System.out.println("");

        // While loop to print the contents of the mySQL database to the console.
        while (resultSet.next()) {

            String words = resultSet.getString("words");
            int frequency = resultSet.getInt("frequencies");

            setTwo.put(words, frequency);

            System.out.printf("%-10s %20s\n", words, frequency);

        }
        statement.close();

    }

    /***
     * The setBuilder() method returns the first 20 values of the LinkedHashMap named set.  This LinkedHashMap contains the
     * first 20 most common words sorted by word count in descending order.
     *
     * @return Returns the LinkedHashMap named set.
     */
    public LinkedHashMap<String, Integer> setBuilder() {

        for (int i = 0; i < 20; i++) {  // i < so.size()   i < 20

            sum = so.get(i).c;
            words = so.get(i).w;

            //set.add(words, sum);
            set.put((String) words, (Integer) sum);
            //map.put((String) words, (Integer) sum);
        }
        return set;
    }

    /***
     *  The start() method works to orchestrate the all the methods (poemRetrival(), strScanner(), objArrayCreator(), consolePrinter() and setBuilder()
     *  to produce a list of the 20 most repeated words in the poem.
     *
     * @param stage The stage parameter is used to create a javafx window.
     * @throws IOException  IO exception may be thrown if the poem is inaccessible at the website https://www.gutenberg.org/files/1065/1065-h/1065-h.htm
     */

    @Override
    public void start(Stage stage) throws IOException, SQLException {

        // Retrieves poem data from website using poemRetrieval() method which is passed as argument to the strBldScanner method for parsing.
        strScanner(poemRetrieval());

        // Sorts the so Object ArrayList in descending order.
        Collections.sort(objArrayLCreator(), Collections.reverseOrder());

        //Iterates through the values in the sorted (so) object ArrayList and adds them to the LinkedHashMap (set) and prints sorted LinkedHashMap (set) to the console.
        consolePrinter();

           //The lhmtoString String is used to store the coverted values in the set LinkedHashMap to String.
        String lhmtoString = set.toString();

        //Labels for
        Label label = new Label("The wordAnalyzer program determines the words used and word count in \"The Raven\" by Edgar Allan Poe");
        Label labelTwo = new Label("");
        Label labelThree = new Label("");
        Button btn = new Button("Click here to display the words and word counts");

        //Positions the first label on the scene.
        label.setTranslateX(220);
        label.setTranslateY(25);
        //Positions the labelTwo on the scene.
        labelTwo.setTranslateX(30);
        labelTwo.setTranslateY(150);

        //Positions the labelThree on the scene.
        labelThree.setTranslateX(30);
        labelThree.setTranslateY(200);

        //Positions the btn button on the scene.
        btn.setTranslateX(350);
        btn.setTranslateY(80);

        Group root = new Group();
        //Adds labels and btn to the scene
        root.getChildren().addAll(label, labelTwo, labelThree, btn);
        //Event handler to display the lhmtoString String (LinkedHashMap (set) converted to String) to the blank labelTwo label
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                labelTwo.setText("LinkedHashMap output: \n" + lhmtoString);
                labelThree.setText("Database output: \n" + String.valueOf(setTwo));

            }
        };
        //Event handler to display contents of the LinkedHashMap set which was converted to String.
        btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
        //Sets the size of the scene.
        Scene scene = new Scene(root, 1300, 250);
        //Sets scene title
        stage.setTitle("wordAnalyzer");
        //creates scene
        stage.setScene(scene);
        //Displays scene.
        stage.show();
    }

    /**
     *The main method calls the start method to begin the program.
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {

        launch();
    }
}

/***
 * The sortA class implements the comparable class to sort the object Array containing the poem words and word counts by count.
 *
 *
 */
class sortA implements Comparable<sortA> {
    String w;
    int c;
    sortA(int c, String w) {

        this.c = c;
        this.w = w;
    }
    @Override
    public int compareTo(sortA obj) {
        if (this.c > obj.c)
            return 1;
        else if (this.c < obj.c)
            return -1;
        return 0;
    }
}

