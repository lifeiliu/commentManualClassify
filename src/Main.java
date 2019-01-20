import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //System.out.println("Hello World!");
        File sourceFile = new File("/home/ggff/Desktop/sourceCode/crawl4j/AuthInfo.java");

        try {
            List<CommentForCat> comments = SourceFileUtil.getCommentsFromFile(sourceFile);
            for (CommentForCat each : comments){
                System.out.println(each);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
