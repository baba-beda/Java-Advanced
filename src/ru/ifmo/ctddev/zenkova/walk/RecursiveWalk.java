package ru.ifmo.ctddev.zenkova.walk;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * Created by daria on 16.02.15.
 */
public class RecursiveWalk {

    public static void main(String[] args) {
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);

        try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"));
             BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {

            String aux;

            while ((aux = reader.readLine()) != null) {
                Path path = Paths.get(aux);
                if (Files.isDirectory(path)) {
                    readDirectory(path, writer);
                }
                else {
                    readFile(path, writer);
                }
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

    private static void readFile(Path file, BufferedWriter writer) {
        try {
            int hash = 0;

            try (FileChannel channel = new FileInputStream(file.toFile()).getChannel()) {
                MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                hash = fnvHash(byteBuffer);

                System.gc();
            } catch (Exception e) {
            }

            writer.write(((hash == 0) ? "00000000" : Integer.toHexString(hash)) + " " + file.toString() + System.getProperty("line.separator"));
            writer.flush();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void readDirectory(Path directory, BufferedWriter writer) {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
            for (Path file : stream) {
                if (Files.isDirectory(file)) {
                    readDirectory(file, writer);
                }
                else {
                    readFile(file, writer);
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
