package unpack_repack_util;

import org.apache.commons.io.FilenameUtils;
import shell_tools.Shell;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;


@SuppressWarnings({"Convert2Lambda", "ResultOfMethodCallIgnored"})
public class UnpackRepackUtil {
  private static final String fs = File.separator;
  private static final String tool = getDir() + fs + "tools" + fs + "unpackbootimg -i ";
  private static final String input = getDir() + fs + "input";
  private static final String aboot = input + fs + "aboot";
  private static final String output = getDir() + fs + "output";
  private static final String extracted = getDir() + fs + "extracted";
  private static final String configs = extracted + fs + "configs";
  private static final String ramdisk = extracted + fs + "ramdisk";
  private static final String img_info = getDir() + fs + "img_info";

  public static String imgInfo(String listItem) {
    File img_info_dir = new File(img_info);
    if (img_info_dir.exists()) {
      Shell.exec1("rm -rf " + img_info + fs + "*");
    } else {
      img_info_dir.mkdir();
    }
    String console = Shell.exec0(tool + input + fs + listItem + " -o " + img_info);
    Shell.exec1("rm -rf " + img_info);
    return console;
  }

  public static String startUnpack(String listItem) {
    manageDIR();
    String console = Shell.exec0(tool + input + fs + listItem + " -o " + configs);
    String inputGZ = configs + fs + arrayToString(getFiles(configs, ".gz", "?"));
    if (Shell.exec0("file " + inputGZ).toLowerCase().contains("lzma")) {
      String gz_file = inputGZ;
      Shell.exec1("mv -f " + gz_file + " " + gz_file + ".xz");
      gz_file = gz_file + "*.xz";
      Shell.exec1("lzma -d " + gz_file);
      gz_file = configs + fs + arrayToString(getFiles(configs, ".gz", "?"));
      Shell.exec1("mv -f " + gz_file + " " + gz_file + ".cpio");
      gz_file = gz_file + ".cpio";
      String[] cmds = {
        "( cd " + ramdisk + " && cpio -i 2>/dev/null < " + gz_file + " )",
        "rm -f " + gz_file,
        "mv -f " + configs + fs + "*zImage " + extracted,
        "echo 'true' > " + configs + fs + "lzma"
      };
      Shell.exec1(cmds);
    } else {
      String[] cmds = {
        "gunzip -c " + inputGZ + " | ( cd " + ramdisk + "; cpio -i 2>/dev/null )",
        "rm -f " + inputGZ,
        "mv -f " + configs + fs + "*zImage " + extracted
      };
      Shell.exec1(cmds);
    }
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
    String zImage = getFile(extracted, "zImage");
    File ramdisk_dir = new File(ramdisk);
    File lzma_fl = new File(configs + fs + "lzma");
    String compress;
    if (zImage.contains("zImage") && ramdisk_dir.exists()) {
      if (lzma_fl.exists()) {
        compress = "lzma";
      } else {
        compress = "gzip";
      }
      Shell.exec1("find " + ramdisk + fs + " 2>/dev/null | cpio -o -H newc 2>/dev/null | " +
        compress + " -f > " + extracted + fs + "ramdisk.gz");
    }

    String ramdisk_archive = getFile(extracted, ".gz");
    String second = getFile(configs, "-secondz");
    String dt = getFile(configs, "-dt");
    String dtb = getFile(configs, "-dtb");
    String dtbo = getFile(configs, "-recoverydtbo");
    String recovery_acpio = getFile(configs, "-recoveryacpio");
    String header_version = getFileContent(configs + fs + getFile(configs, "-headerversion"), "-headerversion");
    String cmdline = getFileContent(configs + fs + getFile(configs, "-cmdline"), "-cmdline");
    String base = getFileContent(configs + fs + getFile(configs, "-base"), "-base");
    String pagesize = getFileContent(configs + fs + getFile(configs, "-pagesize"), "-pagesize");
    String ramdiskoff = getFileContent(configs + fs + getFile(configs, "-ramdiskoff"), "-ramdiskoff");
    String tagsoff = getFileContent(configs + fs + getFile(configs, "-tagsoff"), "-tagsoff");
    String dtboff = getFileContent(configs + fs + getFile(configs, "-dtboff"), "-dtboff");
    String board = getFileContent(configs + fs + getFile(configs, "-board"), "-board");
    String hash = getFileContent(configs + fs + getFile(configs, "-hash"), "-hash");
    String secondoff = getFileContent(configs + fs + getFile(configs, "-secondoff"), "-secondoff");
    String kerneloff = getFileContent(configs + fs + getFile(configs, "-kerneloff"), "-kerneloff");
    String osversion = getFileContent(configs + fs + getFile(configs, "-osversion"), "-osversion");
    String oslevel = getFileContent(configs + fs + getFile(configs, "-oslevel"), "-oslevel");

    ArrayList<String> params = new ArrayList<>();
    if (zImage.contains("zImage") && ramdisk_archive.contains(".gz")) {
      params.add("--kernel " + extracted + fs + zImage);
      params.add(" --ramdisk " + extracted + fs + ramdisk_archive);
      if (second.contains("-second")) {
        params.add(" --second " + configs + fs + second);
      }
      if (dt.contains("-dt")) {
        params.add(" --dt " + configs + fs + dt);
      }
      if (dtb.contains("-dtb")) {
        params.add(" --dtb " + configs + fs + dtb);
      }
      if (dtbo.contains("-recoverydtbo")) {
        params.add(" --recovery_dtbo " + configs + fs + dtbo);
      }
      if (recovery_acpio.contains("-recoveryacpio")) {
        params.add(" --recovery_acpio " + configs + fs + recovery_acpio);
      }
      if (header_version.length() > 0) {
        params.add(" --header_version " + '"' + header_version + '"');
      }
      if (cmdline.length() > 0) {
        params.add(" --cmdline " + '"' + cmdline + '"');
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
        params.add(" --hash " + '"' + hash + '"');
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

      createOutputDir();
      if (!params.isEmpty()) {
        Shell.exec1(getDir() + fs + "tools" + fs + "mkbootimg " + buildArray(params) + " -o " + output + fs + "new-image.img");
      }
      File outImage = new File(output + fs + "new-image.img");
      if (outImage.length() >= 1048576) {
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
    File output_dir = new File(output);
    if (output_dir.exists()) {
      Shell.exec1("rm -rf " + output + fs + "*");
    } else {
      output_dir.mkdir();
    }
  }

  // remove newlines from params array in startRepack()
  private static String buildArray(ArrayList array) {
    StringBuilder builder = new StringBuilder();
    for (Object value : array) {
      builder.append(value);
    }
    return builder.toString().replace(System.lineSeparator(), "");
  }

  private static void manageDIR() {
    File extracted_dir = new File(extracted);
    File configs_dir = new File(configs);
    File ramdisk_dir = new File(ramdisk);
    if (extracted_dir.exists()) {
      Shell.exec1("rm -rf " + extracted + fs + "*");
      configs_dir.mkdir();
      ramdisk_dir.mkdir();
    } else {
      extracted_dir.mkdir();
      configs_dir.mkdir();
      ramdisk_dir.mkdir();
    }
  }

  private static String arrayToString(String[] strArray) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String s : strArray) {
      stringBuilder.append(s);
    }
    return stringBuilder.toString();
  }

    /*private static void decompressGzip(File input, File output) {
        try (GZIPInputStream in = new GZIPInputStream(new FileInputStream(input))) {
            try (FileOutputStream out = new FileOutputStream(output)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            System.out.println("GameTheory - Gzip file doesn't exist!!!");
            e.printStackTrace();
        }
    }*/

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

    /*private static int javaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }*/

  public static void launcherShortcut(String param) {
    String icon = getDir() + fs + "tools" + fs + "icon.png";
    String appName = new File(UnpackRepackUtil.class.getProtectionDomain()
      .getCodeSource().getLocation().getPath()).getName();
    String launcher = System.getProperty("user.home") +
      "/.local/share/applications/mktool.desktop";
    if (param.contains("create")) {
      String[] cl = {
        "echo '[Desktop Entry]' > " + launcher,
        "echo 'Type=Application' >> " + launcher,
        "echo 'Name=mktool' >> " + launcher,
        "echo 'Comment=Boot & Recovery image tool' >> " + launcher,
        "echo 'Exec=java -jar \"" + getDir() + fs + appName + "\"' >> " + launcher,
        "echo 'Icon=" + icon + "' >> " + launcher,
        "echo 'Categories=Development;' >> " + launcher,
        "echo 'Terminal=false' >> " + launcher,
        "gtk-update-icon-cache /usr/share/icons/*"
      };
      Shell.exec1(cl);
    } else if (param.contains("remove")) {
      File file = new File(launcher);
      if (file.exists()) {
        String[] rml = {"rm -f " + launcher, "gtk-update-icon-cache /usr/share/icons/*"};
        Shell.exec1(rml);
      }
    }
  }

  public static String getOS() {
    return System.getProperty("os.name").toLowerCase();
  }

}
