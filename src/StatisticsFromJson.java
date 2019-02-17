import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsFromJson {


    /*private static HashMap<String, HashMap<CommentCategory, Integer>> fileStatic = new HashMap<>();
    */

    public static void getAllFiles(final File folder, List<File> files){

        for(File fileEntry : folder.listFiles()){
            if (fileEntry.isDirectory()){
                getAllFiles(fileEntry,files);
            }else if (fileEntry.getName().endsWith(".json")){
                files.add(fileEntry);
            }

        }
    }



    public static void main(String[] args){
        final File jsonFileFolder = new File("/home/lifei/Desktop/sourceCode");
        final String extension = ".json";
        List<File> jsonFiles = new ArrayList<>();
        getAllFiles(jsonFileFolder,jsonFiles);


        HashMap<CommentCategory,Integer> totalStatic = new HashMap<>();
        HashMap<CommentCategory,Integer> DocumentComments = new HashMap<>();
        HashMap<CommentCategory,Integer> otherComments = new HashMap<>();
        HashMap<CommentCategory,Integer> innerMethodComments = new HashMap<>();
        int totalJavadoc = 0;

        for (File file : jsonFiles){


            /*System.out.println("good comments count:" + countGoodCommnets(getFileStat(file,true)));
            System.out.println("bad comments count:" + countBadComments(getFileStat(file,true)));
            System.out.println(getFileStat(file,true));

            System.out.println(getFileStat(file,false));
            System.out.println(getFileStatForDocumentCmt(file));*/
            totalJavadoc += countJavaDocComment(parseFile(file));
            combineMap(totalStatic,getFileStat(file,true));
            combineMap(otherComments,getFileStat(file,false));
            combineMap(DocumentComments,getFileStatForDocumentCmt(file));
            combineMap(innerMethodComments,getFileInnerMethodComments(file));
            /*try {
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
            //printMethodCommentsCates(file);

            //System.out.println("\n");

        }

        /*fileStatic.forEach((k,v)-> {
            System.out.print(k +": ");
            System.out.println(v);


        });*/


        System.out.println("Javadoc: " + totalJavadoc);
        int totalCommentsCount = 0;
        Collection<Integer> totalvalues =totalStatic.values();

        for(Integer each : totalvalues){
            totalCommentsCount += each.intValue();
        }
        System.out.println("total comments: " + totalCommentsCount);
        System.out.println("GOOD COMMNETS in all: "+countGoodCommnets(totalStatic));
        System.out.println("Bad COMMNETS in all: "+countBadComments(totalStatic));

        System.out.println("GOOD COMMNETS in javadoc: "+countGoodCommnets(DocumentComments));
        System.out.println("bad COMMNETS in javadoc: "+countBadComments(DocumentComments));

        System.out.println("GOOD COMMNETS in OTHER: "+countGoodCommnets(otherComments));
        System.out.println("bad COMMNETS in OTHER: "+countBadComments(otherComments));


        System.out.println("category statistics of Document comments" + DocumentComments);
        int innerMethodCommentsCount = 0;
        Collection<Integer> values =innerMethodComments.values();

        for(Integer each : values){
            innerMethodCommentsCount += each.intValue();
        }

        System.out.println("inner method comments " + innerMethodCommentsCount);
        System.out.println("GOOD COMMNETS : "+countGoodCommnets(innerMethodComments));
        System.out.println("bad COMMNETS : "+countBadComments(innerMethodComments));
        System.out.println("category statistcs of inner method comments" + innerMethodComments);

    }

    private static int countJavaDocComment(CommentForCat[] comments){
        int counter = 0;
        for (CommentForCat comment : comments){
            if(comment.commentType == CommentType.DocumentComment){
                counter ++ ;
            }
        }
        return counter;
    }
    private static void addNewComment(Map<CommentCategory,Integer> map,CommentCategory category){
        if (!map.containsKey(category)){
            map.put(category,new Integer(1));
        }else{
            int newValue = map.get(category).intValue() + 1;
            map.replace(category, new Integer(newValue));
        }
    }

    private static int countGoodCommnets(Map<CommentCategory,Integer> commentStat){
        int goodCommentsNumber = 0;

        for (CommentCategory k : commentStat.keySet()){
            if (k == CommentCategory.Amplification|| k == CommentCategory.Clarification
                    || k == CommentCategory.Informative || k == CommentCategory.Intent || k == CommentCategory.JavaDoc
                    || k == CommentCategory.Legal || k == CommentCategory.TODO || k == CommentCategory.Warning)
                goodCommentsNumber += commentStat.get(k).intValue();
        }
        return goodCommentsNumber;
    }

    private static int countBadComments(Map<CommentCategory,Integer> commentStat){
        int totalCommentsNumber = 0;
        for (CommentCategory k : commentStat.keySet()){
            totalCommentsNumber += commentStat.get(k).intValue();
        }
        return  totalCommentsNumber - (commentStat.get(CommentCategory.AlgorithmSummary) == null?
                0 : commentStat.get(CommentCategory.AlgorithmSummary).intValue()) -
                (commentStat.get(CommentCategory.OtherUndefined) == null?
                        0 : commentStat.get(CommentCategory.OtherUndefined).intValue())
                - countGoodCommnets(commentStat);

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

    public static Map<CommentCategory,Integer> getFileInnerMethodComments(File jsonFile){
        Map<CommentCategory,Integer> result = new HashMap<>();
        CommentForCat[] comments = parseFile(jsonFile);

        for (CommentForCat comment : comments){
            if (comment.commentLocation == CommentLocation.MethodInnerComment){
                if(!result.containsKey(comment.commentCategory))
                    result.put(comment.commentCategory, 1);
                else {
                    result.replace(comment.commentCategory,result.get(comment.commentCategory)+1);
                }
            }
        }
        return  result;
    }

    public static List<FunctionMap> getCommentsInFunctionMap(List<FunctionMap> functionMaps,CommentForCat[] commentForCats){
       for(FunctionMap functionMap: functionMaps){
           functionMap.mapCommentForCatsToFunction(commentForCats);
       }
       return functionMaps;

    }



}
