package shell_tools;

import java.io.*;

public class Shell {

    private static String shell() {
        String result;
        File bash = new File("/bin/bash");
        File csh = new File("/bin/csh");
        File ksh = new File("/bin/ksh");
        File tcsh = new File("/bin/tcsh");
        File zsh = new File("/bin/zsh");
        if (bash.exists()) {
            result = "bash";
        } else if (ksh.exists()) {
            result = "ksh";
        } else if (zsh.exists()) {
            result = "zsh";
        } else if (tcsh.exists()) {
            result = "tcsh";
        } else if (csh.exists()) {
            result = "csh";
        } else {
            result = "sh";
        }
        return result;
    }

    public static String exec0(String cmds) {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{shell(), "-c", cmds});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();
            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exec1(String[] cmds) {
        try {
            Process process = Runtime.getRuntime().exec(shell());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd + System.lineSeparator());
            }
            os.writeBytes("exit" + System.lineSeparator());
            os.flush();
            os.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void exec1(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(shell());
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + System.lineSeparator());
            os.writeBytes("exit" + System.lineSeparator());
            os.flush();
            os.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
