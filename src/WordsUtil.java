import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordsUtil {

    public static List<String> splitCamelWord(String wordToSplit){
        List<String> results = new ArrayList<>();
        for (String w : wordToSplit.split(
                "(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Z][a-z])|(?<=[a-zA-Z])(?=[0-9])")){
            w = w.toLowerCase();
            results.add(w);
        }
        return results;
    }

    public static List<String> splitSentence(String sentence){

        String[] words = sentence.split("[\\W+&&[^\"']]");
        return Arrays.asList(words);
    }

    public static List<String> splitSentenceAndCamelWord(String sentence){
        List<String> result = new ArrayList<>();
        List<String> sentenceWords = splitSentence(sentence);
        for(String eachWord: sentenceWords){
            result.addAll(splitCamelWord(eachWord));
        }
        return result;
    }

    public static void main(String[] args){
        String sentence = "will you'll split don't. how are you today! {hdjh dhjskh, return 2;}";
        System.out.println(splitSentence(sentence));
    }
}
