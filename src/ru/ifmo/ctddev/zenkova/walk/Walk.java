package ru.ifmo.ctddev.zenkova.walk;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by daria on 16.02.15.
 */
public class Walk {
    public static void main(String[] args) {
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);

        try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"));
             BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {
             String aux;

             while ((aux = reader.readLine()) != null) {
                 Path file = Paths.get(aux);
                 int hash = 0;
                 try {
                     hash = fnvHash(new InputStreamReader(Files.newInputStream(file)));
                 } catch (IOException e) {
                 }
                 writer.write(((hash == 0) ? "00000000" : Integer.toHexString(hash)) + " " + aux + System.getProperty("line.separator"));
                 writer.flush();
             }

        } catch (NoSuchFileException e) {
            System.err.println("File " + "\"" + args[0] + "\"" + " not found");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Input file's encoding is not UTF-8, check it and try again");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static int fnvHash(InputStreamReader reader) {
        final int OFFSET_BASIS = 0x811c9dc5;
        final int FNV_PRIME = 0x01000193;

        int hVal = OFFSET_BASIS;

        try {
            int aux;
            while ((aux = reader.read()) >= 0) {
                hVal *= FNV_PRIME;
                hVal ^= aux;
            }
            return hVal;
        } catch (IOException e) {
            return 0;
        }
    }
}
