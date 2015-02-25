package ru.ifmo.ctddev.zenkova.walk;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * Created by daria on 16.02.15.
 */
public class RecursiveWalk {

    public static void main(String[] args) {

        if (args.length < 2 || args[0] == null || args[1] == null) {
            System.err.println("Not enough arguments or they're invalid");
            return;
        }
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);

        try (BufferedReader reader = Files.newBufferedReader(input, Charset.forName("UTF-8"));
             BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"))) {

            String aux;

            while ((aux = reader.readLine()) != null) {
                visit(writer, Paths.get(aux));
            }

        } catch (UnsupportedEncodingException e) {
            System.err.println("Input file's encoding is not UTF-8, check it and try again");
        } catch (IOException e) {
            System.err.println("Invalid input");
        }
    }


    private static int fnvHash(FileChannel channel) throws IOException {
        final int OFFSET_BASIS = 0x811c9dc5;
        final int FNV_PRIME = 0x01000193;
        final int PAGE_SIZE = 1024 * 128;

        int hVal = OFFSET_BASIS;

        ByteBuffer buffer = ByteBuffer.allocate(PAGE_SIZE);

        while (-1 != channel.read(buffer)) {
            buffer.flip();

            int aux;
            while (buffer.hasRemaining()) {
                aux = buffer.get() & 0xff;
                hVal *= FNV_PRIME;
                hVal ^= aux;
            }

            buffer.clear();
        }
        return hVal;
    }

    private static void readFile(Path file, BufferedWriter writer) {
        try {
            int hash = 0;
            try (FileChannel channel = new FileInputStream(file.toFile()).getChannel()) {
                hash = fnvHash(channel);
            } catch (IOException e) {
            }

            writer.write(String.format("%08x", hash) + " " + file.toString() + System.getProperty("line.separator"));
            writer.flush();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void readDirectory(Path directory, BufferedWriter writer) {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
            for (Path file : stream) {
                visit(writer, file);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void visit(BufferedWriter writer, Path file) {
        if (Files.isDirectory(file)) {
            readDirectory(file, writer);
        } else {
            readFile(file, writer);
        }
    }
}
