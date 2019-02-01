import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.DotPrinter;
import com.github.javaparser.printer.XmlPrinter;
import com.github.javaparser.printer.YamlPrinter;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class test {




    public static void main(String[] args) throws FileNotFoundException {
        String path = "/home/ggff/workplace/maven/maven/maven-core/src/main/java/org/apache/maven/artifact/handler/manager/DefaultArtifactHandlerManager.java";
        CompilationUnit cu = JavaParser.parse(new File(path));
        //CompilationUnit cu = JavaParser.parse("class X{int medthodX() { int x; x = methodB(10); if(x > 10){ x = 10 -1;} return x; }}");
        /*YamlPrinter printer = new YamlPrinter(true);
        System.out.println(printer.output(cu));
        DotPrinter printer = new DotPrinter(true);
        try (FileWriter fileWriter = new FileWriter("ast.dot");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(printer.output(cu));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        Set<Statement> result = new HashSet<>();
        for(MethodDeclaration md : methods){
            new AssignmentVisitor().visit(md,result);
            System.out.println(result +"\n\n");
            result.clear();
        }
    }

    private static class AssignmentVisitor extends VoidVisitorAdapter<Set<Statement>>{
        @Override
        public void visit(AssignExpr n, Set<Statement> collector) {
            super.visit(n,collector);


            if (n.getParentNode().isPresent()){
                Node parenet = n.getParentNode().get();
                if (parenet instanceof ExpressionStmt){
                    collector.add((ExpressionStmt) parenet);
                }
            }
            /*if((n.getAncestorOfType(ExpressionStmt.class).isPresent())){
                System.out.println(n.getAncestorOfType(ExpressionStmt.class));
                collector.add((Statement) n.getAncestorOfType(ExpressionStmt.class).get());
            }*/




        }
    }
}
