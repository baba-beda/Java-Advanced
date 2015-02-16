package ru.ifmo.ctddev.zenkova.walk;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
                 int hash = 0;

                 try (FileChannel channel = new FileInputStream(aux).getChannel()) {
                     MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                     hash = fnvHash(byteBuffer);

                     System.gc();
                 } catch (Exception e) {
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

    private static int fnvHash(MappedByteBuffer buffer) {
        final int OFFSET_BASIS = 0x811c9dc5;
        final int FNV_PRIME = 0x01000193;

        int hVal = OFFSET_BASIS;

        try {
            int aux;
            while (buffer.hasRemaining()) {
                aux = buffer.get();
                hVal *= FNV_PRIME;
                hVal ^= aux;
            }
            return hVal;
        } catch (Exception e) {
            return 0;
        }
    }
}
