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

    //used to store sorted word counts from freqArray
    Object sum = null;

    //used to store sorted words from wordsArray
    Object words;

//10/10/2022 ADDED method below for testing

    public ArrayList<String> returnWordArray() {

        return wordsArray;

    }

    public String poemRetrieval() throws IOException {

        // Establishes connection to website.
        Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();
        // Collects website data by tag.

        Elements title = doc.getElementsByTag("h1");
        Elements body = doc.getElementsByTag("p");

        //for-each loop used to append poem words to StringBuilder.
        for (Element b : body) {

            strBu.append(b.text());
        }
        //for-each loop to append title to StringBuilder.
        for (Element t : title) {

            strBu.append(t.text());
        }

        str = String.valueOf(strBu);


    return str;
    }

    public ArrayList<String> strScanner(String str) throws IOException {

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

        return wordsArray;
    }

    public ArrayList<sortA>  objArrayLCreator() {
        for (int i = 0; i < wordsArray.size(); i++) {
            so.add(new sortA(freqArray.get(i), wordsArray.get(i)));
        }
        return so;
    }

    public void consolePrinter() {

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
        System.out.println("");
        System.out.println("Pairs: " + setBuilder());
    }

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

    @Override
    public void start(Stage stage) throws IOException {

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
        Button btn = new Button("Click here to display the words and word counts");

        //Positions the first label on the scene.
        label.setTranslateX(220);
        label.setTranslateY(25);
        //Positions the labelTwo on the scene.
        labelTwo.setTranslateX(30);
        labelTwo.setTranslateY(150);
        //Positions the btn button on the scene.
        btn.setTranslateX(350);
        btn.setTranslateY(80);

        Group root = new Group();
        //Adds labels and btn to the scene
        root.getChildren().addAll(label, labelTwo, btn);
        //Event handler to display the lhmtoString String (LinkedHashMap (set) converted to String) to the blank labelTwo label
        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                labelTwo.setText(lhmtoString);

            }
        };
        //Event handler to display contents of the LinkedHashMap set which was converted to String.
        btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);
        //Sets the size of the scene.
        Scene scene = new Scene(root, 1100, 250);
        //Sets scene title
        stage.setTitle("wordAnalyzer");
        //creates scene
        stage.setScene(scene);
        //Displays scene.
        stage.show();
    }

    public static void main(String[] args) throws IOException {

        launch();
    }
}

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