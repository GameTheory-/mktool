import main_gui.MainGUI;
import unpack_repack_util.UnpackRepackUtil;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            if (UnpackRepackUtil.getOS().contains("linux")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MainGUI();
    }

}
