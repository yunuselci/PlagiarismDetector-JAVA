package com.company;

import java.io.*;
import java.util.*;

public class Main {
    final static File databaseFolder = new File("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\database");//Give main_doc.txt path here
    final static File controlFolder = new File("C:\\Users\\YUNUS\\IdeaProjects\\Cmp3005\\control");//Give controlFolder path here
    static ArrayList<String> databaseSentences = new ArrayList<>();
    static ArrayList<String> controlSentences = new ArrayList<>();
    static LinkedHashMap<Double, String> hash_map = new LinkedHashMap<>();
    static ArrayList<String> controlFolderAbsolutePaths = new ArrayList<>();


    static void readTheDatabaseDirectory(final File folder) throws FileNotFoundException {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readTheDatabaseDirectory(fileEntry);
            } else {
                Scanner sentence = new Scanner(new File(fileEntry.getAbsolutePath()));
                sentence.useDelimiter("(?<=[.!?])\\s*");
                while (sentence.hasNextLine())
                {
                    databaseSentences.add(sentence.next());
                }
                sentence.close();
            }
        }
    }

    static void readTheControlDirectory(final File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                readTheControlDirectory(fileEntry);
            } else {
                controlFolderAbsolutePaths.add(fileEntry.getAbsolutePath());
            }
        }
    }


    static void readTheSentences(final String directory) throws FileNotFoundException {
        Scanner sentence = new Scanner(new File(directory));
        sentence.useDelimiter("(?<=[.!?])\\s*");
        while (sentence.hasNextLine())
        {
            controlSentences.add(sentence.next());
        }
        sentence.close();
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
    /* // If you haves Apache Common Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
        return 100*(longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }
    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public static void putIntoHashMap(String sentence1, String sentence2) {
        hash_map.put(similarity(sentence1,sentence2),sentence2);

    }

    public static void sortTheHashMapByTheKeyAndPrintTheValue(int counter)
    {
        ArrayList<Double> sortedKeys =
                new ArrayList<>(hash_map.keySet());
        Collections.sort(sortedKeys);
        Collections.reverse(sortedKeys);
        double total=0.0;
        double total2=0.0;
        for (Double x : sortedKeys){
            total += x;
        }
        total2 = total/ hash_map.size();
        System.out.println("Similarity Rate For Document:"+counter+" %"+total2);
        int i=0;
        for (Double x : sortedKeys){
            i++;
            if(i<=5){
                System.out.println(
                        "Folder = " + counter +
                        ", Similarity Score = %" + Math.round(x) +
                        ", "+i+"." + "Sentence = "+ hash_map.get(x));
            }
        }



    }


    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        int counter = 0; //Bu counter çıktı da düzgün bir görüntü sağlansın diye bulunuyor, olmasa da olur
        readTheDatabaseDirectory(databaseFolder);
        readTheControlDirectory(controlFolder);
        for (int index = 0; index < controlFolderAbsolutePaths.size(); index++) {
            readTheSentences(controlFolderAbsolutePaths.get(index));
            for (int j = 0; j < databaseSentences.size(); j++) {
                for (int i = 0; i < controlSentences.size(); i++) {
                    putIntoHashMap(databaseSentences.get(j), controlSentences.get(i));

                }
            }
            System.out.println(" ");
            ++counter;
            sortTheHashMapByTheKeyAndPrintTheValue(counter);
            controlSentences.clear();
            hash_map.clear();
            controlSentences.clear();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");


    }
}
