package unpack_repack_util;

import org.apache.commons.io.FilenameUtils;
import shell_tools.Shell;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


@SuppressWarnings("Convert2Lambda")
public class UnpackRepackUtil {
  private static final String fs = File.separator;
  private static final String tool = getDir() + fs + "tools" + fs + "unpackbootimg -i ";
  private static final String input = getDir() + fs + "input";
  private static final String aboot = input + fs + "aboot";
  private static final String output = getDir() + fs + "output";
  private static final String extracted = getDir() + fs + "extracted";
  private static final String configs = extracted + fs + "configs";
  private static final String img_info = getDir() + fs + "img_info";

  public static String imgInfo(String listItem) {
    Path path = Paths.get(img_info);
    if (Files.exists(path)) {
      Shell.exec1("rm -rf " + img_info + fs + "*");
    } else {
      try {
        Files.createDirectory(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    String console = Shell.exec0(tool + input + fs + listItem + " -o " + img_info);
    Shell.exec1("rm -rf " + img_info);
    return console;
  }

  public static String startUnpack(String listItem) {
    manageDIR();
    String console = Shell.exec0(tool + input + fs + listItem + " -o " + configs);
    String ramDisks;
    String ramdisk = getFile(configs, "img-ramdisk");
    String vramdisk = getFile(configs, "img-vendor_ramdisk");
    if (vramdisk.contains("img-vendor_ramdisk")) {
      ramDisks = vramdisk;
    } else {
      ramDisks = ramdisk;
    }
    String[] cmds = {"mv -f " + configs + fs + "*img-kernel " + extracted,
            "mv -f " + configs + fs + ramDisks + " " + extracted};
    Shell.exec1(cmds);
    return console;
  }

  public static String unpackLok(String listItem) {
    String noExt = FilenameUtils.removeExtension(listItem);
    String console = Shell.exec0(getDir() + fs + "tools" + fs + "loki unlok " + input +
      fs + listItem + " " + input + fs + noExt + "-unloki.img");
    String imgUpack = "";
    File unlok = new File(input + fs + noExt + "-unloki.img");
    if (unlok.length() >= 1048576) {
      imgUpack = startUnpack(noExt + "-unloki.img") + System.lineSeparator();
    }
    return console + imgUpack;
  }

  public static String startRepack() {
    String console = "Failed to create new image!";
    String zImage = getFile(extracted, "img-kernel");

    String ramdisk = getFile(extracted, "img-ramdisk");
    String vramdisk = getFile(configs, "img-vendor_ramdisk");
    String second = getFile(configs, "-second");
    String dt = getFile(configs, "-dt");
    String dtb = getFile(configs, "-dtb");
    String dtbo = getFile(configs, "-recovery_dtbo");
    String recovery_acpio = getFile(configs, "-recovery_acpio");
    String header_version = getFileContent(configs + fs + getFile(configs, "-header_version"), "-header_version");
    String cmdline = getFileContent(configs + fs + getFile(configs, "-cmdline"), "-cmdline");
    String vendor_cmdline = getFileContent(configs + fs + getFile(configs, "-vendor_cmdline"), "-vendor_cmdline");
    String base = getFileContent(configs + fs + getFile(configs, "-base"), "-base");
    String pagesize = getFileContent(configs + fs + getFile(configs, "-pagesize"), "-pagesize");
    String ramdiskoff = getFileContent(configs + fs + getFile(configs, "-ramdisk_offset"), "-ramdisk_offset");
    String tagsoff = getFileContent(configs + fs + getFile(configs, "-tags_offset"), "-tags_offset");
    String dtboff = getFileContent(configs + fs + getFile(configs, "-dtb_offset"), "-dtb_offset");
    String board = getFileContent(configs + fs + getFile(configs, "-board"), "-board");
    String hash = getFileContent(configs + fs + getFile(configs, "-hashtype"), "-hashtype");
    String secondoff = getFileContent(configs + fs + getFile(configs, "-second_offset"), "-second_offset");
    String kerneloff = getFileContent(configs + fs + getFile(configs, "-kernel_offset"), "-kernel_offset");
    String osversion = getFileContent(configs + fs + getFile(configs, "-os_version"), "-os_version");
    String oslevel = getFileContent(configs + fs + getFile(configs, "-os_patch_level"), "-os_patch_level");
    String id = getFileContent(configs + fs + getFile(configs, "-id"), "-id");

    ArrayList<String> params = new ArrayList<>();
    if (zImage.contains("img-kernel") && ramdisk.contains("img-ramdisk") || vramdisk.contains("img-vendor_ramdisk")) {
      params.add("--kernel " + extracted + fs + zImage);
      if (vramdisk.contains("img-vendor_ramdisk")) {
        params.add(" --vendor_ramdisk " + extracted + fs + ramdisk);
      } else {
        params.add(" --ramdisk " + extracted + fs + ramdisk);
      }
      if (second.contains("-second")) {
        params.add(" --second " + configs + fs + second);
      }
      if (dt.contains("-dt")) {
        params.add(" --dt " + configs + fs + dt);
      }
      if (dtb.contains("-dtb")) {
        params.add(" --dtb " + configs + fs + dtb);
      }
      if (dtbo.contains("-recovery_dtbo")) {
        params.add(" --recovery_dtbo " + configs + fs + dtbo);
      }
      if (recovery_acpio.contains("-recovery_acpio")) {
        params.add(" --recovery_acpio " + configs + fs + recovery_acpio);
      }
      if (header_version.length() > 0) {
        params.add(" --header_version " + '"' + header_version + '"');
      }
      if (cmdline.length() > 0) {
        params.add(" --cmdline " + '"' + cmdline + '"');
      }
      if (vendor_cmdline.length() > 0) {
        params.add(" --vendor_cmdline " + '"' + vendor_cmdline + '"');
      }
      if (base.length() > 0) {
        params.add(" --base " + '"' + base + '"');
      }
      if (pagesize.length() > 0) {
        params.add(" --pagesize " + '"' + pagesize + '"');
      }
      if (ramdiskoff.length() > 0) {
        params.add(" --ramdisk_offset " + '"' + ramdiskoff + '"');
      }
      if (tagsoff.length() > 0) {
        params.add(" --tags_offset " + '"' + tagsoff + '"');
      }
      if (dtboff.length() > 0) {
        params.add(" --dtb_offset " + '"' + dtboff + '"');
      }
      if (board.length() > 0) {
        params.add(" --board " + '"' + board + '"');
      }
      if (hash.length() > 0) {
        params.add(" --hashtype " + '"' + hash + '"');
      }
      if (secondoff.length() > 0) {
        params.add(" --second_offset " + '"' + secondoff + '"');
      }
      if (kerneloff.length() > 0) {
        params.add(" --kernel_offset " + '"' + kerneloff + '"');
      }
      if (osversion.length() > 0) {
        params.add(" --os_version " + '"' + osversion + '"');
      }
      if (oslevel.length() > 0) {
        params.add(" --os_patch_level " + '"' + oslevel + '"');
      }
      if (id.length() > 0) {
        params.add(" --id " + '"' + id + '"');
      }

      createOutputDir();
      Shell.exec1(getDir() + fs + "tools" + fs + "mkbootimg " + buildArray(params) + " -o " + output + fs + "new-image.img");
      File outImage = new File(output + fs + "new-image.img");
      if (outImage.length() >= 2000000) {
        console = "Your image was created in:" + System.lineSeparator() + output + fs + "new-image.img";
      }
      params.clear();
    }
    return console;
  }

  public static String lokiPatch(String listItem, String param, String aboots) {
    createOutputDir();
    String noExt = FilenameUtils.removeExtension(listItem);
    return Shell.exec0(getDir() + fs + "tools" + fs + "loki patch " + param + " " +
      aboot + fs + aboots + " " + input + fs + listItem + " " + output + fs + noExt + ".lok");
  }

  private static void createOutputDir() {
    Path path = Paths.get(output);
    if (Files.exists(path)) {
      Shell.exec1("rm -rf " + output + fs + "*");
    } else {
      try {
        Files.createDirectory(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // remove newlines from params array in startRepack()
  private static String buildArray(ArrayList<String> array) {
    StringBuilder builder = new StringBuilder();
    for (String value : array) {
      builder.append(value);
    }
    return builder.toString().replace(System.lineSeparator(), "");
  }

  private static void manageDIR() {
    Path confs = Paths.get(configs);
    Path extract = Paths.get(extracted);
    if (Files.exists(extract)) {
      Shell.exec1("rm -rf " + extracted + fs + "*");
    } else {
      try {
        Files.createDirectory(extract);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      Files.createDirectory(confs);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // remove square brackets of Arrays.toString()
  private static String getFile(String dir, String pattern) {
    return Arrays.toString(getFiles(dir, pattern, "?"))
      .replace("[", "").replace("]", "");
  }

  // finds extension pattern in directory and returns files with those extensions
  public static String[] getFiles(String dir, String pattern, String pattern2) {
    String[] files = {};
    File inputDir = new File(dir);
    if (inputDir.length() > 0) {
      files = inputDir.list(new FilenameFilter() {
        @Override
        public boolean accept(File file, String s) {
          return s.endsWith(pattern) || s.endsWith(pattern2);
        }
      });
    }
    if (files == null || files.length == 0) {
      files = new String[]{"No file available!"};
    }
    return files;
  }

  // return contents of web page
  public static String getAppVersion() {
    String content = "";
    try {
      URL url = new URL("https://raw.githubusercontent.com/GameTheory-/mktool/master/README.md");
      BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
        if (line != null) {
          sb.append(System.lineSeparator());
        }
      }
      content = sb.toString();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return content;
  }

  // check internet connection
  public static boolean internet() {
    boolean result = false;
    try {
      URL url = new URL("https://raw.githubusercontent.com/GameTheory-/mktool/master/README.md");
      URLConnection connection = url.openConnection();
      connection.connect();
      result = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  // return contents of file
  private static String getFileContent(String fileWithPath, String pattern) {
    String content = "";
    if (fileWithPath.contains(pattern)) {
      try (BufferedReader br = new BufferedReader(new FileReader(fileWithPath))) {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
          sb.append(line);
          line = br.readLine();
          if (line != null) {
            sb.append(System.lineSeparator());
          }
        }
        content = sb.toString();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return content;
  }

  // return mktool.jar directory path
  public static String getDir() {
    // production path:
    String path = UnpackRepackUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    // testing path:
    // String path = System.getProperty("user.dir") + fs;
    path = path.substring(0, path.lastIndexOf(fs));
    // for java >= 10
    // path = URLDecoder.decode(path, StandardCharsets.UTF_8);
    // for java <= 9
    try {
      path = URLDecoder.decode(path, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return path;
  }

  public static void launcherShortcut(String param) {
    String icon = getDir() + fs + "tools" + fs + "icon.png";
    String appName = new File(UnpackRepackUtil.class.getProtectionDomain()
      .getCodeSource().getLocation().getPath()).getName();
    String launcher = System.getProperty("user.home") +
      "/.local/share/applications/mktool.desktop";
    String which_java = Shell.exec0("echo -n $(command -v java)");
    if (param.contains("create")) {
      String[] cl = {
        "echo '[Desktop Entry]' > " + launcher,
        "echo 'Type=Application' >> " + launcher,
        "echo 'Name=mktool' >> " + launcher,
        "echo 'Comment=Boot & Recovery image tool' >> " + launcher,
        "echo 'Exec=" + which_java + " -jar \"" + getDir() + fs + appName + "\"' >> " + launcher,
        "echo 'Icon=" + icon + "' >> " + launcher,
        "echo 'Categories=Development;' >> " + launcher,
        "echo 'Terminal=false' >> " + launcher
      };
      Shell.exec1(cl);
    } else if (param.contains("remove")) {
      Shell.exec1("rm -f " + launcher);
    }
  }

  public static String getOS() {
    return System.getProperty("os.name").toLowerCase();
  }

}
