import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
    SWUM stands for Software Word Usage Model
    This model is used to generate code summary
    I used part of its idea of getting the most important statement in method body
    which can keep the meaning of the method

    idea get from a paper "Towards Automatically Generating
            Summary Comments for Java Methods"

 */
public class SWUM {
    public static Set<String> generateMethodSummary(MethodDeclaration md){
        Set <String> methodSummary = new HashSet<>();

        Set<Statement> sUnits = endingOrVoidSUnit(md.getBody().get());
        for(Statement each : sUnits){
            methodSummary.addAll(WordsUtil.splitSentenceAndCamelWord(each.toString()));
        }
        return methodSummary;
    }


    private static Set<Statement> endingOrVoidSUnit(BlockStmt methodBody){
        Set<Statement> result = new HashSet<>();

        NodeList<Statement> statements = methodBody.getStatements();

        for(Statement each : statements){
            if(each.isReturnStmt() || (!each.isExpressionStmt() && !each.isBreakStmt() &&!each.isContinueStmt()
            &&!each.isAssertStmt() && !each.isThrowStmt() && !each.isUnparsableStmt())){
                each.removeComment();
                result.add(each);
            }
        }

        result.add(statements.get(statements.size() - 1));
        return result;

    }





    private static List<Statement>  dataFacilitatingSUnit(Set<Statement> SUnits){
        List<String> variableNames = new ArrayList<>();


        System.out.println(variableNames);
        return null;

    }


    private static class AssignmentVisitor extends VoidVisitorAdapter<List<Statement>>{
        @Override
        public void visit(AssignExpr n, List<Statement> collector) {
            super.visit(n,collector);

            if(n.getParentNode().isPresent()){
                Node parent = n.getParentNode().get();
                if(parent instanceof Statement){
                    collector.add((Statement) parent);
                    System.out.println(parent);
                }
            }



        }
    }

    private static class ReturnStmtVisitor extends VoidVisitorAdapter<List<String>>{
        @Override
        public void visit(ReturnStmt n, List<String> arg) {
            super.visit(n, arg);
            String statement = n.toString().trim();
            if(!statement.contains("null")){
                arg.add(statement.substring(7));
            }
        }
    }



    public static void main(String[] args) throws FileNotFoundException {
        File sourceFile = new File("/home/ggff/workplace/maven/maven/maven-core/src/main/java/org/apache/maven/ReactorReader.java");
        CompilationUnit cu = JavaParser.parse(sourceFile);
        List<MethodDeclaration> methodDeclarations = cu.findAll(MethodDeclaration.class);
        /*MethodDeclaration aMethod = methodDeclarations.get(methodDeclarations.size()-3);
        System.out.println(aMethod);
        List<String> returnAvariables = new ArrayList<>();
        new ReturnStmtVisitor().visit(aMethod,returnAvariables);
        System.out.println(returnAvariables);
        List<Statement> sUnits = new ArrayList<>();*/
        for(MethodDeclaration md:methodDeclarations){

            System.out.println(md);
            System.out.println();
            System.out.println(generateMethodSummary(md));
            System.out.println("\n");
        }




    }
}


