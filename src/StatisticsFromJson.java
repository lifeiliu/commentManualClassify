import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;

public class StatisticsFromJson {


    private static HashMap<String, HashMap<CommentCategory, Integer>> fileStatic = new HashMap<>();

    public static void main(String[] args){
        final File jsonFileFolder = new File("/home/ggff/Desktop/sourceCode/crawl4j");
        final String extension = ".json";
        List<File> jsonFiles = Arrays.asList(jsonFileFolder.listFiles((File pathname)
                -> pathname.getName().endsWith(extension)));

        for (File file : jsonFiles){
            parseFile(file);
        }

        fileStatic.forEach((k,v)-> {
            System.out.print(k +": ");
            System.out.println(v);


        });
    }

    private static void parseFile(File jsonFile){
        Gson gson = new Gson();
        HashMap<CommentCategory, Integer> statistics = new HashMap<>();

        try {

            JsonReader jsonReader = new JsonReader(new FileReader(jsonFile));
            CommentForCat[] comments = gson.fromJson(jsonReader,CommentForCat[].class);
            for (CommentForCat comment: comments){
                if(statistics.size() == 0 ||!statistics.containsKey(comment.commentCategory)){
                    statistics.put(comment.commentCategory,new Integer(1));
                }
                else{
                    statistics.replace(comment.commentCategory,
                            new Integer(statistics.get(comment.commentCategory).intValue()+1));

                }


            }

            jsonReader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fileStatic.put(jsonFile.getName(),statistics);

    }


}
