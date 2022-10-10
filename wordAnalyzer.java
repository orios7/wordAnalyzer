package com.example.javafxdemo;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
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

import java.io.IOException;

public class wordAnalyzer extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // Establishes connection to website.
        Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();
        // Collects website data by tag.

        Elements title = doc.getElementsByTag("h1");
        Elements body = doc.getElementsByTag("p");

        //StringBuilder to save parsed html code.
        StringBuilder str = new StringBuilder("");

        //for-each loop used to append poem words to StringBuilder.
        for (Element b : body) {

            str.append(b.text());
        }
        //for-each loop to append title to StringBuilder.
        for (Element t : title) {

            str.append(t.text());
        }

        //scanner used for parsing data stored in StringBuilder.
        Scanner fileWord = new Scanner(String.valueOf(str));

        //ArrayList to store each word occurrence.
        ArrayList<String> wordsArray = new ArrayList<String>();

        //ArrayList to store count of word occurrence.
        ArrayList<Integer> freqArray = new ArrayList<Integer>();

        //While loop to itereate to each word provided by scanner.
        while (fileWord.hasNext()) {

            // Delimiter used to parsing purposed and to remove some unneeded alphanumeric characters.
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

        //ArrayList used to store the values of both the wordsArray and freqArray ArrayLists to
        //facilitate sorting.
        ArrayList<sortA> so = new ArrayList<>();
        for (int i = 0; i < wordsArray.size(); i++) {
            so.add(new sortA(freqArray.get(i), wordsArray.get(i)));

        }

        // sort used to sort the so ArrayList in descending order.
        Collections.sort(so, Collections.reverseOrder());

        LinkedHashMap<String, Integer> set = new LinkedHashMap<>();

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        System.out.println("");
        System.out.println("        WORD ANALYZER");
        System.out.println("");
        System.out.println("-------------------------------");
        System.out.println("WORD" + "             " + "WORD FREQUENCY");
        System.out.println("-------------------------------");

        //for loop to display the first 20 values in the so ArrayList and LinkedHashSet.
        Object sum = null;
        for (int i = 0; i < 20; i++) {  // i < so.size()   i < 20

            Object words;

            sum = so.get(i).c;
            words = so.get(i).w;

            //set.add(words, sum);
            set.put((String) words, (Integer) sum);
            //map.put((String) words, (Integer) sum);

            System.out.printf("%-10s %20s\n", words, sum);

        }
        //Prints out LinkedHashMap
        System.out.println("");
        System.out.println("");
        System.out.println("Pairs: " + set);

        //FXMLLoader fxmlLoader = new FXMLLoader(wordAnalyzer.class.getResource("view.fxml"));

        String lhmtoString = set.toString();

        Label label = new Label("The wordAnalyzer program determines the words used and word count in \"The Raven\" by Edgar Allan Poe");
        Label labelTwo = new Label("");
        Button btn = new Button("Click here to display the words and word counts");

        label.setTranslateX(220);
        label.setTranslateY(25);
        labelTwo.setTranslateX(30);
        labelTwo.setTranslateY(150);
        btn.setTranslateX(350);
        btn.setTranslateY(80);

        Group root = new Group();
        root.getChildren().addAll(label, labelTwo, btn);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                labelTwo.setText(lhmtoString);

            }
        };
        btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, eventHandler);

        Scene scene = new Scene(root, 1100, 250);
        stage.setTitle("wordAnalyzer");

        stage.setScene(scene);
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
        //sortarr(int c) {
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