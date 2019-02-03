import java.util.*;

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

    public static String getDocCommentDestcription(String docComment){
        String description = docComment.split("@")[0];
        return description;
    }

    public static List<String> filterStopWords(Collection<String> collection, Set<String> stopWords){
        List<String> result = new ArrayList<>();
        for (String each : collection){
            if (! stopWords.contains(each)){
                result.add(each);
            }
        }
        return result;
    }



    public static void main(String[] args){

        Set<String> stopwords = new HashSet<>();
        stopwords.add("are");
        stopwords.add("you");
        stopwords.add("don't");
        String sentence = "will you'll split don't. how are you today! {@hdjh dhjskh, return 2;}";
        System.out.println(getDocCommentDestcription(sentence));
        List<String> split = splitSentence(sentence);

        System.out.println(filterStopWords(split,stopwords));

    }

}
