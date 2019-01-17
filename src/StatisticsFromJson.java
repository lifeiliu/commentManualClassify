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
            /*System.out.println(file.getName());
            System.out.println(getFileStat(file,true));
            System.out.println(getFileStat(file,false));
            System.out.println(getFileStatForDocumentCmt(file));
            combineMap(totalStatic,getFileStat(file,true));
            combineMap(DocumentComments,getFileStatForDocumentCmt(file));*/
         /*   try {
                List<FunctionMap> functionMaps = SourceFileUtil.getFunctionMapFormFile(
                        new File(jsonFileNameToJavaName(file.getPath())));
                CommentForCat[] commentForCats = parseFile(file);
                functionMaps = getCommentsInFunctionMap(functionMaps,commentForCats);
                System.out.println(file.getName());
                for(FunctionMap functionMap: functionMaps){
                    if(functionMap.commentForCats.size() > 0){
                        System.out.println(functionMap.functionName);
                        functionMap.commentForCats.forEach(e ->System.out.println(e.commentCategory));
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }*/
            printMethodCommentsCates(file);
            System.out.println("\n");

        }

        /*fileStatic.forEach((k,v)-> {
            System.out.print(k +": ");
            System.out.println(v);


        });*/
        System.out.println("\n\n");
        System.out.println(totalStatic);
        System.out.println(DocumentComments);


    }
    private static void addNewComment(Map<CommentCategory,Integer> map,CommentCategory category){
        if (!map.containsKey(category)){
            map.put(category,new Integer(1));
        }else{
            int newValue = map.get(category).intValue() + 1;
            map.replace(category, new Integer(newValue));
        }
    }

    private static void printMethodCommentsCates(File jsonFile){
        Map<CommentCategory,Integer> functionCommentCateStat = new HashMap<>();
        try {
            List<FunctionMap> functionMaps = SourceFileUtil.getFunctionMapFormFile(
                    new File(jsonFileNameToJavaName(jsonFile.getPath())));
            CommentForCat[] commentForCats = parseFile(jsonFile);
            functionMaps = getCommentsInFunctionMap(functionMaps,commentForCats);
            System.out.println(jsonFile.getName());
            for(FunctionMap functionMap: functionMaps){
                if(functionMap.commentForCats.size() > 0){
                    System.out.println("Method: " + functionMap.functionName);
                    functionMap.commentForCats.forEach(e->addNewComment(functionCommentCateStat,e.commentCategory));
                    System.out.println(functionCommentCateStat);
                    functionCommentCateStat.clear();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String jsonFileNameToJavaName(String jsonFileName){
         return jsonFileName.substring(0,jsonFileName.lastIndexOf('.')).trim();
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

    public static List<FunctionMap> getCommentsInFunctionMap(List<FunctionMap> functionMaps,CommentForCat[] commentForCats){
       for(FunctionMap functionMap: functionMaps){
           functionMap.mapCommentForCatsToFunction(commentForCats);
       }
       return functionMaps;

    }



}
