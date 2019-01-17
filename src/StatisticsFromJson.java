import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsFromJson {


    /*private static HashMap<String, HashMap<CommentCategory, Integer>> fileStatic = new HashMap<>();
    */

    public static void main(String[] args){
        final File jsonFileFolder = new File("/home/ggff/Desktop/sourceCode/android");
        final String extension = ".json";
        List<File> jsonFiles = Arrays.asList(jsonFileFolder.listFiles((File pathname)
                -> pathname.getName().endsWith(extension)));
        HashMap<CommentCategory,Integer> totalStatic = new HashMap<>();
        HashMap<CommentCategory,Integer> DocumentComments = new HashMap<>();
        for (File file : jsonFiles){
            System.out.println(file.getName());
            System.out.println(getFileStat(file,true));
            System.out.println(getFileStat(file,false));
            System.out.println(getFileStatForDocumentCmt(file));
            combineMap(totalStatic,getFileStat(file,true));
            combineMap(DocumentComments,getFileStatForDocumentCmt(file));

        }

        /*fileStatic.forEach((k,v)-> {
            System.out.print(k +": ");
            System.out.println(v);


        });*/
        System.out.println("\n\n");
        System.out.println(totalStatic);
        System.out.println(DocumentComments);

    }

    private  static Map<CommentCategory,Integer> combineMap(Map<CommentCategory,Integer> map1,
                                                            Map<CommentCategory,Integer> map2){
        for(Map.Entry<CommentCategory,Integer> entry : map2.entrySet()){
            int value = entry.getValue().intValue();
            CommentCategory key = entry.getKey();
            if(map1.containsKey(key)){
                int newValue = map1.get(key).intValue() + value;
                map1.replace(key,new Integer(newValue));
            }else {
                map1.put(key, value);
            }
        }
        return map1;
    }

    public static CommentForCat[] parseFile(File jsonFile){
        Gson gson = new Gson();
        try {

            JsonReader jsonReader = new JsonReader(new FileReader(jsonFile));
            CommentForCat[] comments = gson.fromJson(jsonReader,CommentForCat[].class);

            jsonReader.close();
            return comments;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<CommentCategory,Integer> getFileStat(File jsonFile,boolean withDocumentComment){
        Map<CommentCategory,Integer> result = new HashMap<>();
        CommentForCat[] comments = parseFile(jsonFile);
        if(withDocumentComment){
            for(CommentForCat comment : comments){
                if (!result.containsKey(comment.commentCategory))
                    result.put(comment.commentCategory,new Integer(1));
                else {
                    int newValue = result.get(comment.commentCategory).intValue() + 1 ;
                    result.replace(comment.commentCategory,new Integer(newValue));
                }
            }
        }else {
            for(CommentForCat comment : comments){
                if(comment.commentType!=CommentType.DocumentComment){
                    if (!result.containsKey(comment.commentCategory))
                        result.put(comment.commentCategory,new Integer(1));
                    else {
                        int newValue = result.get(comment.commentCategory).intValue() + 1 ;
                        result.replace(comment.commentCategory,new Integer(newValue));
                    }
                }
            }
        }

        return result;
    }

    public static Map<CommentCategory,Integer> getFileStatForDocumentCmt(File jsonFile){
        Map<CommentCategory,Integer> result = new HashMap<>();
        CommentForCat[] comments = parseFile(jsonFile);
        for(CommentForCat comment : comments){
            if(comment.commentType==CommentType.DocumentComment){
                if (!result.containsKey(comment.commentCategory))
                    result.put(comment.commentCategory,new Integer(1));
                else {
                    int newValue = result.get(comment.commentCategory).intValue() + 1 ;
                    result.replace(comment.commentCategory,new Integer(newValue));
                }
            }
        }
       return result;

    }

}
