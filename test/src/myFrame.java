import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * Created by Administrator on 2017/10/17.
 */
public class myFrame extends JFrame {


    private Object[][] contain;
    private JTable jTable;

    public myFrame(){
        init();

        setTitle("主界面");
        setVisible(true);
        setSize(1200,700);
        setLocation(100,100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);//结束窗口所在的应用程序

        validate();
    }

    private void init() {
        setLayout(null);
//        TextArea textField = new TextArea();
//        textField.setBounds(10,10,300,200);
//        add(textField);

//        String text = textField.getText();
//        String[] item = text.split("\\s+");
//        for (String str :item) {
//            System.out.println("&&&" + str + "&&&");
//        }



        jTable = new JTable();
        final DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("序号");
        dtm.addColumn("随机数");
        for (int i = 0; i < 10; ++i) {
            dtm.addRow(new Object[] { i, Math.random() * 100 });
        }
        jTable.setModel(dtm);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jScrollPane.setBounds(20,20,200,200);
        add(jScrollPane);

        JButton btn = new JButton("asdf");
        btn.setBounds(10,250,20,20);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < dtm.getRowCount(); i++) {
                    dtm.setValueAt(Math.random() * 100,i,1);
                }
            }
        });
        add(btn);


    }



}
