package KettleActionListener;

public class KettleActionListener implements ActionListener{

    private Button addBtn;

    public addBtnListener(Button addBtn){
        this.addBtn = addBtn
    }

    public void addBtnAction(ActionEvent e) {
        System.out.println("KettleLog!");
    }

}