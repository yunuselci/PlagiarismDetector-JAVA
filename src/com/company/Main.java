package com.company;

import java.io.*;
import java.util.*;

public class Main {
    final static File databaseFolder = new File("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\database");//Give main_doc.txt path here
    final static File controlFolder = new File("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\control");//Give controlFolder path here
    static ArrayList<String> databaseSentences = new ArrayList<>(); //Hold the main file sentences
    static ArrayList<String> controlSentences = new ArrayList<>();  //Hold the sentences to check
    static HashMap<String, Double> hash_map = new HashMap<>(); //Similarity rate with sentences
    static ArrayList<String> controlFolderAbsolutePaths = new ArrayList<>(); //Absolute path of control folders


    static void readTheDatabaseDirectory(final File folder) throws FileNotFoundException {
        //This method assumes that there will be only one txt file in the database folder.
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readTheDatabaseDirectory(fileEntry);
            } else {
                Scanner sentence = new Scanner(new File(fileEntry.getAbsolutePath())); //standart scanner operation
                sentence.useDelimiter("(?<=[.!?])\\s*"); //to read sentences
                while (sentence.hasNextLine()) {
                    databaseSentences.add(sentence.next());
                }
                sentence.close();
            }
        }
    }

    static void readTheControlDirectory(final File folder) {
        //This method does not read any sentences. It just puts the exact paths of the files to be compared in the arraylist
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readTheControlDirectory(fileEntry);
            } else {
                controlFolderAbsolutePaths.add(fileEntry.getAbsolutePath());
            }
        }
    }


    static void readTheSentences(final String directory) throws FileNotFoundException {
        Scanner sentence = new Scanner(new File(directory)); //standart scanner operation
        sentence.useDelimiter("(?<=[.!?])\\s*"); //to read sentences
        while (sentence.hasNextLine()) {
            controlSentences.add(sentence.next());
        }
        sentence.close();
    }

    public static double similarity(String string1, String string2) {
        String longer = string1, shorter = string2;
        if (string1.length() < string2.length()) { // longer should always have greater length
            longer = string2;
            shorter = string1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return (100 * (longerLength - editDistance(longer, shorter)) / (double) longerLength);
    }

    public static int editDistance(String string1, String string2) { //Levenshtein distance algorithm implementation
        string1 = string1.toLowerCase();
        string2 = string2.toLowerCase();
        //mean of costs "cost to convert one string to another"
        int[] costs = new int[string2.length() + 1];
        for (int i = 0; i <= string1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= string2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (string1.charAt(i - 1) != string2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[string2.length()] = lastValue;
            }
        }
        //We get costs from this implementation and use to calculate similarity between two string
        return costs[string2.length()];
    }

    public static void putIntoHashMap(String sentence1, String sentence2) {
        //Create <key,value> structure
        //you can run this put method to see which sentences are compared
        hash_map.put((sentence2 + "\tBETWEEN:" + sentence1), similarity(sentence1, sentence2));


    }

    public static void sortByValue() {
    //convert HashMap into List
        List<Map.Entry<String, Double>> list = new LinkedList<>(hash_map.entrySet());
    //sorting the list elements
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
    //prints the sorted HashMap
        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        printMap(sortedMap);
    }
    //method for printing the elements
    public static void printMap(Map<String, Double> map) {
        double sumOfValues=0.0, similarityRateOfWholeDocument;
        int i = 0; //to control the foreach loop
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if(i<controlSentences.size()){
                i++;
                sumOfValues += entry.getValue();
            }
        }
        similarityRateOfWholeDocument = sumOfValues / controlSentences.size(); //measure similarity rate of document
        System.out.println("Similarity Rate For Document:"+" %"+similarityRateOfWholeDocument);
        int j = 0; //to control the foreach loop
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            j++;
            if(j<=5){
                System.out.println(
                        "Similarity Score = %" + Math.round(entry.getValue()) +
                                ", "+j+"." + "Sentence = "+ entry.getKey());;
            }
        }
    }


    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis(); //to measure run time
        readTheDatabaseDirectory(databaseFolder);
        readTheControlDirectory(controlFolder);
        System.out.println("If the similarity rate more than 60 percent we can be regarded as a plagiarism.");
        for (int index = 0; index < controlFolderAbsolutePaths.size(); index++) { //loop1
            //The 1st loop will iterate the number of files to be checked
            readTheSentences(controlFolderAbsolutePaths.get(index));
            for (int j = 0; j < databaseSentences.size(); j++) { //loop2
                for (int i = 0; i < controlSentences.size(); i++) { //loop3
                    /*
                    Main algorithm work in here
                    Through the 2nd loop, we can compare the first sentence of the database and all the sentences of the document to be controlled in the 3rd loop.
                     */
                    putIntoHashMap(databaseSentences.get(j), controlSentences.get(i));
                }
            }
            System.out.println(" ");
            System.out.println("-------------------");
            System.out.println(controlFolderAbsolutePaths.get(index));
            System.out.println("-------------------");
            sortByValue();
            /*
            after compare 2 files and print them we have to release the hash_map and arraylist to fill with new values
             */
            hash_map.clear();
            controlSentences.clear();
        }
        //to measure run time
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }
}
