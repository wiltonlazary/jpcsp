package jpcsp.util;

import org.bouncycastle.util.Strings;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.Sys;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

// References:
// https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/utils/SharedLibraryLoader.java
// https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/main/java/org/lwjgl/system/SharedLibraryLoader.java
// This is not required on lwjgl3 since SharedLibraryLoader takes care of it and allows fatjars and launch4j executables
@SuppressWarnings("ResultOfMethodCallIgnored")
public class LWJGLFixer {
    static public void fix() {
        try {
            fixInternal();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static private String[] getLibrariesToLoad() {
        if (OS.isWindows) {
            return new String[]{
                    "lwjgl.dll", "lwjgl64.dll",
                    "OpenAL32.dll", "OpenAL64.dll",
                    "jinput-dx8.dll", "jinput-dx8_64.dll",
                    "jinput-raw.dll", "jinput-raw_64.dll",
                    "jinput-wintab.dll"
            };
        } else if (OS.isLinux) {
            return new String[]{
                    "liblwjgl.so", "liblwjgl64.so",
                    "libopenal.so", "libopenal64.so",
                    "libjinput-linux.so", "libjinput-linux64.so"
            };
        } else if (OS.isMac) {
            return new String[]{
                    "liblwjgl.dylib", "openal.dylib",
                    "libjinput-osx.jnilib",
            };
        } else {
            return new String[0];
        }
    }

    static public void fixInternal() throws Throwable {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        String[] libraries = getLibrariesToLoad();

        File folder = new File(System.getProperty("java.io.tmpdir") + "/lwgl-2.9.3/" + System.getProperty("user.name"));
        folder.mkdirs();

        for (String library : libraries) {
            URL libUrl = systemClassLoader.getResource(library);
            String basename = FileUtil.getURLBaseName(libUrl);
            File outFile = new File(folder, basename);

            if (!outFile.exists()) {
                FileUtil.writeBytes(outFile, FileUtil.readURL(libUrl));
            }
        }

        System.setProperty("org.lwjgl.librarypath", folder.getAbsolutePath());
        System.setProperty("net.java.games.input.librarypath", folder.getAbsolutePath());
    }
}