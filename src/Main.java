import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
       /* File sourceFile = new File("/home/lifei/Desktop/sourceCode/sparkSample/UnsafeExternalSorter.java");

        try {
            List<CommentForCat> comments = SourceFileUtil.getCommentsFromFile(sourceFile);
            for (CommentForCat each : comments){
                System.out.println(each);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*try {
            CompilationUnit cu = JavaParser.parse(sourceFile);
            List<Comment> comments = SourceFileUtil.combineMultiLineComment(cu.getComments());
            List<RedundantPostprocessing> redundantPostprocessings = new ArrayList<>();
            for (Comment comment : comments){
                System.out.println(comment.getBegin().get().line);
                RedundantPostprocessing newRecord = new RedundantPostprocessing(comment);
                System.out.println(newRecord);
                redundantPostprocessings.add(newRecord);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        File folder = new File("/home/lifei/Desktop/sourceCode");
        List<File> souceCodeFiles = new ArrayList<>();
        List<File> jsonFiles = new ArrayList<>();
        getAllFiles(folder,souceCodeFiles,".java");
        getAllFiles(folder,jsonFiles,".json");
        List<CommentForCat> allComments = new ArrayList<>();
        List<RedundantPostprocessing> allCommertsForProcessing = new ArrayList<>();
        List<RedundantPostprocessing> redundantPostprocessings = new ArrayList<>();
        for(File eachFile: souceCodeFiles){
            try {
                allCommertsForProcessing.addAll(SourceFileUtil.getCommentsForPreprocessing(eachFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<CommentForCat> allFromJson = new ArrayList<>();
        for (File each : jsonFiles){
            allFromJson.addAll(Arrays.asList(StatisticsFromJson.parseFile(each)));
        }

        Map<String, CommentForCat> allRedundantComments = new HashMap<>();
        Set<String> redundants = new HashSet<>();

        for (CommentForCat comment: allFromJson){
            if(comment.commentCategory == CommentCategory.Redundent){
                allRedundantComments.put(comment.fileName + comment.lineStartNumber, comment);
                redundants.add(comment.fileName + comment.lineStartNumber);
            }

        }

        for (RedundantPostprocessing entry : allCommertsForProcessing){
            if (redundants.contains(entry.commentId)){
                entry.isRedudant = true;
            }else {
                entry.isRedudant = false;
            }
        }

        System.out.println(allCommertsForProcessing.size());
        File toSaveJson = new File(folder.getPath()+"/Aredundants.json");
        try {
            saveBeansToJson(allCommertsForProcessing,toSaveJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getAllFiles(final File folder, List<File> files,String suffix){

        for(File fileEntry : folder.listFiles()){
            if (fileEntry.isDirectory()){
                getAllFiles(fileEntry,files,suffix);
            }else if (fileEntry.getName().endsWith(suffix)){
                files.add(fileEntry);
            }

        }
    }

    public static void saveBeansToJson(List<RedundantPostprocessing> beans, File jsonFile) throws IOException {

        BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
        bw.write(new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().create().toJson(beans));
        bw.close();

    }

}
