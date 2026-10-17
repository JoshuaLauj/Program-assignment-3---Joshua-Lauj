import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Random;
//NAME: Joshua Lor
//ASSIGNMENT: Assignment 3
//LAB SECTION:
//LECTURE SECTION:

public class Main{

    public static void main(String[] args) throws FileNotFoundException {
        ArrayList<SentList> sentList = new ArrayList<SentList>();
        ArrayList<SentList> posList = new ArrayList<SentList>();
        ArrayList<SentList> negList = new ArrayList<SentList>();
        ArrayList<Words> wordList = new ArrayList<Words>();

        //load sentiment, positive words and negative words arraylists
        readSentimentList(sentList, posList, negList);

        //read review
        //load ArrayList wordList that will contain original review & pos & neg
        String inFileName;
        String outFileName = "reviews.txt";
        PrintWriter outFile = new PrintWriter(outFileName);

        // open input file adding review + number + ".txt" to review
        // if not able to open, print a message and continue
        // else process the file
        // if the file can be read properly, print the results
        for (int i = 1; i <= 8; i++){
            inFileName = "review" + i + ".txt";

        wordList.clear();

        if (readReview(sentList, posList, negList, wordList, inFileName)) {
            printReview(wordList, inFileName, outFile);
            }
        }
    

        outFile.close();
    }

    //PRE:  accept the empty ArrayLists created in main
    //POST: the arrays are loaded with the proper words and information
    public static void readSentimentList(ArrayList<SentList> sentList,
                                         ArrayList<SentList> posList,
                                         ArrayList<SentList> negList){
        String csvFilePath = "sentiment.txt";
        String line;
        double tempValue;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            while ((line = br.readLine()) != null) {
                 // Split the line by commas into an array of strings
                String[] values = line.split(",");
                 //Create object SentList & add to sentList arraylist
                tempValue = Double.parseDouble(values[1]);
                SentList tempSent = new SentList(values[0], tempValue);
                sentList.add(tempSent);
                 //if word values are pos add to posList
                if (tempValue > 1.25) {
                    posList.add(tempSent);
                }
                 //if word values are neg, add to neglist
                 else if (tempValue < -1.25) {
                    negList.add(tempSent);
                    }
            }
        } 
        catch (IOException e) {
            System.out.println("Error reading the file.");
            e.printStackTrace();
        }
    }

    //PRE: accept the word lists and file name to open
    //POST: read the file while the line is not null
    //      each word is edited (to lower case without punctuation)
    //      the sentiment value is accessed
    //      if the word is positive - update to a random word in the negative list and update the word value
    //      if the word is negative - update to a random positive word in the positive list & update the word value

    public static boolean readReview(ArrayList<SentList> sentList,
                                  ArrayList<SentList> posList,
                                  ArrayList<SentList> negList,
                                  ArrayList<Words> wordList,
                                  String fileName){

        String line;
        Random rand = new Random();

        Words.setPosFlag(false);
        Words.setNegFlag(false);
        
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                 // Split the line by commas into an array of strings
                 String[] values = line.split(" ");
                 
                 //for each word
                for (int i = 0; i < values.length; i++) {
                     


                 //  strip off punctuation at the end of word set charWord to null or value of punctuation
                    String cleanWord = "";
                 //  save origWord, posWord, negWord
                    String origWord = values[i];
                    String editWord = origWord;
                 //  change to lower case  (set editWord)
                
                    for (int j = 0; j < editWord.length(); j++) {

                    char ch = editWord.charAt(j);
                        if (!isPunctuation(ch)) {
                            cleanWord = cleanWord + ch;
                        }
                    }
                    editWord = cleanWord.toLowerCase();
                 //  look up word in sentList (set origValue, posVlue, negValue)
                    double origValue = getSentiment(sentList, editWord);
                    String posWord = origWord;
                    String negWord = origWord;
                    double posValue = origValue;
                    double negValue = origValue;
                 //  if positive enough, find new negword in negList & reset negWord & NegValue, reset negFlag
                    if (origValue < -1) {

                        int randomIndex = rand.nextInt(posList.size());

                        posWord = posList.get(randomIndex).word;
                        posValue = posList.get(randomIndex).value;

                        Words.setPosFlag(true);
                    }
                 //  if negative enough, find new posword in posList & reset posWord & posValue, reset posFlag
                    if (origValue > 1) {

                        int randomIndex = rand.nextInt(negList.size());

                        negWord = negList.get(randomIndex).word;
                        negValue = negList.get(randomIndex).value;

                    Words.setNegFlag(true);
                    }
                    Words tempWord = new Words(origWord, editWord, posWord, negWord,
                           origValue, posValue, negValue);

                    wordList.add(tempWord);
                }

            }
            return true;
        }
            
        catch (IOException e) {
            System.out.println("Error reading the file: " + fileName);
            return false;
        }
    }

    //PRE:  accept the updated wordlist
    //POST: loop through word list, create a string that will be the original, positive & negative reviews
    //      print each review
    public static void printReview(ArrayList<Words> wordList, String inFile, 
                                  PrintWriter outFile){

        double origTotal = 0.0;
        double posTotal = 0.0;
        double negTotal = 0.0;

        String origLine = "";
        String posLine = "";
        String negLine = "";

         // this print original review heading
        outFile.println("PROCESSING FILE: " + inFile);
        outFile.println();
        outFile.println("FORMATTED REVIEW:");

         // ORIGINAL REVIEW
    for (int i = 0; i < wordList.size(); i++) {

        Words temp = wordList.get(i);

        origTotal = origTotal + temp.sentOrigValue;

        // check if adding next word would go over 80 characters
        if (origLine.length() + temp.origWord.length() + 1 > 80) {
            outFile.println(origLine);
            origLine = "";
        }

        if (origLine.length() > 0) {
            origLine = origLine + " ";
        }

        origLine = origLine + temp.origWord;
    }

    // print any leftover words
    if (origLine.length() > 0) {
        outFile.println(origLine);
    }

    outFile.println();
    outFile.printf("ORIGINAL SENTIMENT: %.2f%n", origTotal);
    outFile.println();


    // POSITIVE REVIEW
    outFile.println("POSITIVE REVIEW FOR FILE: " + inFile);

    if (!Words.getPosFlag()) {
        outFile.println("Review cannot be made more positive.");
        outFile.printf("POSITIVE SENTIMENT: %.2f%n", origTotal);
    }
    else {

        for (int i = 0; i < wordList.size(); i++) {

            Words temp = wordList.get(i);

            posTotal = posTotal + temp.sentPosValue;

            if (posLine.length() + temp.posWord.length() + 1 > 80) {
                outFile.println(posLine);
                posLine = "";
            }

            if (posLine.length() > 0) {
                posLine = posLine + " ";
            }

            posLine = posLine + temp.posWord;
        }

        if (posLine.length() > 0) {
            outFile.println(posLine);
        }

        outFile.println();
        outFile.println("WORDS UPDATED TO BE MORE POSITIVE:");

        for (int i = 0; i < wordList.size(); i++) {

            Words temp = wordList.get(i);

            if (!temp.origWord.equals(temp.posWord)) {
                outFile.printf("%-15s %6.2f   %-15s %6.2f%n",
                        temp.origWord,
                        temp.sentOrigValue,
                        temp.posWord,
                        temp.sentPosValue);
            }
        }

        outFile.println();
        outFile.printf("POSITIVE SENTIMENT: %.2f%n", posTotal);
    }

    outFile.println();


    // NEGATIVE REVIEW
    outFile.println("NEGATIVE REVIEW FOR FILE: " + inFile);

    if (!Words.getNegFlag()) {
        outFile.println("Review cannot be made more negative.");
        outFile.printf("NEGATIVE SENTIMENT: %.2f%n", origTotal);
    }
    else {

        for (int i = 0; i < wordList.size(); i++) {

            Words temp = wordList.get(i);

            negTotal = negTotal + temp.sentNegValue;

            if (negLine.length() + temp.negWord.length() + 1 > 80) {
                outFile.println(negLine);
                negLine = "";
            }

            if (negLine.length() > 0) {
                negLine = negLine + " ";
            }

            negLine = negLine + temp.negWord;
        }

        if (negLine.length() > 0) {
            outFile.println(negLine);
        }

        outFile.println();
        outFile.println("WORDS UPDATED TO BE NEGATIVE:");

        for (int i = 0; i < wordList.size(); i++) {

            Words temp = wordList.get(i);

            if (!temp.origWord.equals(temp.negWord)) {
                outFile.printf("%-15s %6.2f   %-15s %6.2f%n",
                    temp.origWord,
                    temp.sentOrigValue,
                    temp.negWord,
                    temp.sentNegValue);
            }
        }

        outFile.println();
        outFile.printf("NEGATIVE SENTIMENT: %.2f%n", negTotal);
    }

    // three blank lines between reviews
    outFile.println();
    outFile.println();
    outFile.println();
}
    

    //PRE:  accept a character
    //POST: return true if this character is punctuation; false otherwise
    //      There may be a better way to write this!
    static boolean isPunctuation(char ch) {
        if (ch == '!' || ch == '\"' || ch == '#' || ch == '$' || ch == '%' || ch == '&' || ch == '\'' || ch == '(' || ch == ')' || ch == '*' || ch == '+' || ch == ',' || ch == '-' || ch == '.' || ch == '/' || ch == ':' || ch == ';' || ch == '<' || ch == '=' || ch == '>' || ch == '?' || ch == '@' || ch == '[' || ch == '\\' || ch == ']' || ch == '^' || ch == '`' || ch == '{' || ch == '|' || ch == '}')
          return true;
        return false;
    }

    //PRE:  accept the sentiment words list and a word to find
    //POST: return the value of the sentiment if found, 0 otherwise
    static double getSentiment (ArrayList<SentList> sentList, String eWord) {
        for (int i = 0; i < sentList.size(); i++) {

            if (sentList.get(i).word.equals(eWord)) {
                return sentList.get(i).value;
                }
        }

        
        return 0.0;
    }
}