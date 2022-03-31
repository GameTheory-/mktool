package main_gui;

import unpack_repack_util.UnpackRepackUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;

import static java.nio.file.StandardWatchEventKinds.*;


@SuppressWarnings("Convert2Lambda")
public class MainGUI {
  private final String appVersion = "mktool v5.4";
  private final String fs = File.separator;
  private final String input = UnpackRepackUtil.getDir() + fs + "input";
  private final String aboot = input + fs + "aboot";
  private JPanel jPanel;
  private JList<String> imgUnpackList;
  private JList<String> infoList;
  private JList<String> lokUnpackList;
  private JList<String> imgPatchList;
  private JList<String> paramPatchList;
  private JList<String> abootList;
  private JTextArea imgUnpackTextArea;
  private JTextArea repackTextArea;
  private JTextArea infoTextArea;
  private JTextArea lokUnpackTextArea;
  private JTextArea patchTextArea;
  private JButton repackButton;
  private JButton getImageInfoButton;
  private JButton unpackImageButton;
  private JButton unpackLokButton;
  private JButton lokiPathButton;
  private final JMenuItem menuItem1 = new JMenuItem("Add Launcher Shortcut");
  private final JMenuItem menuItem2 = new JMenuItem("Remove Launcher Shortcut");
  private final JMenuItem menuItem3 = new JMenuItem("Check for Updates");
  private final JProgressBar progressBar = new JProgressBar();
  private final JDialog dialog = new JDialog();

  public MainGUI() {
    myJFrame();

    imgUnpackList.setListData(UnpackRepackUtil.getFiles(input, ".img", "?"));
    lokUnpackList.setListData(UnpackRepackUtil.getFiles(input, ".lok", "?"));
    infoList.setListData(UnpackRepackUtil.getFiles(input, ".img", ".lok"));
    imgPatchList.setListData(UnpackRepackUtil.getFiles(input, ".img", "?"));
    abootList.setListData(UnpackRepackUtil.getFiles(aboot, ".img", "?"));

    // unpack image
    imgUnpackList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        imgUnpackList.getSelectedValue();
      }
    });
    imgUnpackTextArea.setText("Select an image above to unpack," + System.lineSeparator() +
      "then click the button below.");
    unpackImageButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        showProgress(true);
        new Thread(new Runnable() {
          @Override
          public void run() {
            imgUnpackTextArea.setText(UnpackRepackUtil.startUnpack(imgUnpackList.getSelectedValue()));
            showProgress(false);
          }
        }).start();
      }
    });

    // repack image
    repackTextArea.setText("Click the button below to create" + System.lineSeparator() +
      "your image from the extracted folder.");
    repackButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        showProgress(true);
        new Thread(new Runnable() {
          @Override
          public void run() {
            repackTextArea.setText(UnpackRepackUtil.startRepack());
            showProgress(false);
          }
        }).start();
      }
    });

    // image info
    infoList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        infoList.getSelectedValue();
      }
    });
    infoTextArea.setText("Select a .img or .lok image above," + System.lineSeparator() +
      "then click the button below.");
    getImageInfoButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        showProgress(true);
        new Thread(new Runnable() {
          @Override
          public void run() {
            infoTextArea.setText(UnpackRepackUtil.imgInfo(infoList.getSelectedValue()));
            showProgress(false);
          }
        }).start();
      }
    });

    // unpack lok
    lokUnpackList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        lokUnpackList.getSelectedValue();
      }
    });
    lokUnpackTextArea.setText("Select a lok image above to unpack," + System.lineSeparator() +
      "then click the button below.");
    unpackLokButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        showProgress(true);
        new Thread(new Runnable() {
          @Override
          public void run() {
            lokUnpackTextArea.setText(UnpackRepackUtil.unpackLok(lokUnpackList.getSelectedValue()));
            showProgress(false);
          }
        }).start();
      }
    });

    // loki patch
    imgPatchList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        imgPatchList.getSelectedValue();
      }
    });
    paramPatchList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        paramPatchList.getSelectedValue();
      }
    });
    abootList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        abootList.getSelectedValue();
      }
    });
    patchTextArea.setText("Instructions:" + System.lineSeparator() +
      "1. First box = Select the image to patch" + System.lineSeparator() +
      "2. Second box = Select the image type" + System.lineSeparator() +
      "3. Third box = Select the aboot image" + System.lineSeparator() +
      "4. Finally, click the button below");
    lokiPathButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        showProgress(true);
        new Thread(new Runnable() {
          @Override
          public void run() {
            patchTextArea.setText(UnpackRepackUtil.lokiPatch(imgPatchList.getSelectedValue(),
              paramPatchList.getSelectedValue(), abootList.getSelectedValue()));
            showProgress(false);
          }
        }).start();
      }
    });

    // File menu
    menuItem1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        UnpackRepackUtil.launcherShortcut("create");
        String launcher = System.getProperty("user.home") +
          "/.local/share/applications/mktool.desktop";
        Path path = Paths.get(launcher);
        if (Files.exists(path)) {
          JOptionPane.showMessageDialog(jPanel, "Launcher shortcut created!" +
            System.lineSeparator() + "Check your apps menu for the shortcut.");
        } else {
          JOptionPane.showMessageDialog(jPanel, "Could not create launcher shortcut!");
        }
      }
    });
    menuItem2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        UnpackRepackUtil.launcherShortcut("remove");
        String launcher = System.getProperty("user.home") +
          "/.local/share/applications/mktool.desktop";
        Path path = Paths.get(launcher);
        if (!Files.exists(path)) {
          JOptionPane.showMessageDialog(jPanel, "Launcher shortcut removed!");
        } else {
          JOptionPane.showMessageDialog(jPanel, "Could not remove launcher!" +
            System.lineSeparator() + "Please remove manually " + launcher);
        }
      }
    });
    menuItem3.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            JOptionPane.showMessageDialog(jPanel, getUpdateInfo());
          }
        }).start();
      }
    });

    // watch input and aboot directories for changes
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          File input_dir = new File(input);
          File aboot_dir = new File(aboot);
          WatchService watcher = FileSystems.getDefault().newWatchService();
          if (input_dir.exists()) {
            Path inputDir = Paths.get(input);
            inputDir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
          }
          if (aboot_dir.exists()) {
            Path abootDir = Paths.get(aboot);
            abootDir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
          }
          while (true) {
            WatchKey key = watcher.take();
            Watchable watchable = key.watchable();
            Path directory = (Path) watchable;
            for (WatchEvent<?> event : key.pollEvents()) {
              WatchEvent.Kind<?> kind = event.kind();
              if (ENTRY_CREATE.equals(kind) || ENTRY_MODIFY.equals(kind) || ENTRY_DELETE.equals(kind)) {
                if (directory.toString().equals(input)) {
                  imgUnpackList.setListData(UnpackRepackUtil.getFiles(input, ".img", "?"));
                  lokUnpackList.setListData(UnpackRepackUtil.getFiles(input, ".lok", "?"));
                  infoList.setListData(UnpackRepackUtil.getFiles(input, ".img", ".lok"));
                  imgPatchList.setListData(UnpackRepackUtil.getFiles(input, ".img", "?"));
                } else if (directory.toString().equals(aboot)) {
                  abootList.setListData(UnpackRepackUtil.getFiles(aboot, ".img", "?"));
                }
              }
            }
            if (!key.reset()) {
              break;
            }
          }
        } catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();

  }

  private Object getUpdateInfo() {
    showProgress(true);
    String str;
    if (UnpackRepackUtil.internet()) {
      if (!UnpackRepackUtil.getAppVersion().contains(appVersion)) {
        str = "New Update Available!<br>" +
          "For the latest update visit:<br>" +
          "<a href=\"https://techstop.github.io/mktool/\" style=\"color: #0099cc\">https://techstop.github.io/mktool/</a>";
      } else {
        str = "No updates available!";
      }
    } else {
      str = "No internet connection detected!";
    }
    JEditorPane editor = new JEditorPane();
    editor.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
    editor.setEditable(false);
    editor.setText(str);
    editor.addHyperlinkListener(new HyperlinkListener() {
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          if (Desktop.isDesktopSupported()) {
            try {
              Desktop.getDesktop().browse(e.getURL().toURI());
            } catch (IOException | URISyntaxException ex) {
              ex.printStackTrace();
            }
          }
        }
      }
    });
    showProgress(false);
    return editor;
  }

  private void showProgress(boolean show) {
    if (show) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          progressBar.setIndeterminate(true);
          dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
          dialog.setUndecorated(true);
          dialog.setAlwaysOnTop(true);
          dialog.add(progressBar);
          dialog.pack();
          dialog.setLocationRelativeTo(jPanel);
          dialog.setVisible(true);
        }
      });
    } else {
      dialog.dispose();
    }
  }

  private void myJFrame() {
    URL i16 = ClassLoader.getSystemResource("icons" + fs + "icon-16x16.png");
    URL i24 = ClassLoader.getSystemResource("icons" + fs + "icon-24x24.png");
    URL i32 = ClassLoader.getSystemResource("icons" + fs + "icon-32x32.png");
    URL i48 = ClassLoader.getSystemResource("icons" + fs + "icon-48x48.png");
    URL i64 = ClassLoader.getSystemResource("icons" + fs + "icon-64x64.png");
    URL i96 = ClassLoader.getSystemResource("icons" + fs + "icon-96x96.png");
    ArrayList<Image> icons = new ArrayList<>();
    try {
      icons.add(ImageIO.read(i16));
      icons.add(ImageIO.read(i24));
      icons.add(ImageIO.read(i32));
      icons.add(ImageIO.read(i48));
      icons.add(ImageIO.read(i64));
      icons.add(ImageIO.read(i96));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Toolkit kit = Toolkit.getDefaultToolkit();
    // Image img = kit.createImage(url);
    // You should work with the UI inside the Event Dispatch Thread (EDT)
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JFrame frame = new JFrame();
        JMenuBar menuBar = new JMenuBar();
        menuBar.setMargin(new Insets(2, 3, 5, 0));
        JMenu menu = new JMenu("File");
        menu.setDisplayedMnemonicIndex(0);
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);
        menuBar.add(menu);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        frame.setIconImages(icons);
        frame.setTitle(appVersion);
        frame.add(jPanel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
    });
  }
}
