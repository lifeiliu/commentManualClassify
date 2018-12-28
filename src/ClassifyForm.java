
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class ClassifyForm extends JFrame {

    static JTextArea sourceCode,comment,commentedCode;

    private JLabel label1,label2,label3;
    private JPanel panel1,panel4;
    private JButton saveButton,nextButton,openButton;
    private JComboBox comboBox;
    private JScrollPane panel2;
    private JSplitPane panel3;

    private File sourceCodeFile;
    private LinkedList<Comment> comments;
    private int index = 0;




    public ClassifyForm(){
        panel1 = new JPanel();

        panel3 = new JSplitPane();
        panel4 = new JPanel();
        label1 = new JLabel("comment");
        label2 = new JLabel("commentedCode");
        label3 = new JLabel("choose source file");
        sourceCode = new JTextArea();
        panel2 = new JScrollPane(sourceCode,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        comment = new JTextArea();
        commentedCode = new JTextArea();

        saveButton = new JButton("Save");
        nextButton = new JButton("ask for next comment");
        openButton = new JButton("open java source file");
        comboBox = new JComboBox();

        panel1.add(label3);
        panel1.add(openButton);


        panel3.setLeftComponent(comment);
        panel3.setRightComponent(commentedCode);

        panel4.add(saveButton);
        panel4.add(comboBox);
        panel4.add(nextButton);

        this.setLayout(new GridLayout(5,5));
        this.add(panel1,0,3);
        this.add(panel2,1,3);
        this.add(panel3,2,3);
        this.add(panel4,5,5);

        this.setSize(1000,800);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    sourceCodeFile = openFileDialog(panel1, sourceCode);
                    CompilationUnit cu = JavaParser.parse(sourceCodeFile);
                    comments = (LinkedList) cu.getComments();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        nextButton.addActionListener( e-> {

                    comment.setText(null);
                    commentedCode.setText(null);
                    comment.append(comments.get(index).getContent());
                    commentedCode.append(comments.get(index).getCommentedNode().toString());
                    index++;


                });

    }



    public static void main(String[] args){
        new ClassifyForm();

    }

    private File openFileDialog(Component parent, JTextArea textArea) throws IOException {
        textArea.setText(null);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("java file only","java"));
        String s ;

        int result = fileChooser.showOpenDialog(parent);


        if (result ==  JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((s = bufferedReader.readLine()) != null){

                textArea.append(s + "\n");

            }

            bufferedReader.close();
            return file;


        }
        return null;


    }





}
