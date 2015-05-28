package ru.ifmo.ctddev.zenkova.uifilecopy;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.List;

/**
 * Created by baba_beda on 5/13/15.
 */

public class UIFileCopy extends JFrame implements ActionListener, PropertyChangeListener, KeyListener {
    private static final long serialVersionUID = 1L;

    private static final int BUFFER_SIZE = 1024;
    private static String strSource;
    private static String strTarget;
    private JTextField txtSource;
    private JTextField txtTarget;
    private JProgressBar progressAll;
    private JProgressBar progressCurrent;
    private JTextField speedAverage;
    private JTextField speedCurrent;
    private JTextField timeElapsed;
    private JTextField timeRemaining;
    private JTextArea txtDetails;
    private JButton btnCopy;
    private CopyTask task;
    private long secondsElapsed;

    public UIFileCopy() {
        buildGUI();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Either source or target is malformed or missing!");
        }
        strSource = args[0];
        strTarget = args[1];
        SwingUtilities.invokeLater(() -> new UIFileCopy().setVisible(true));
    }

    private void buildGUI() {
        setTitle("UIFileCopy");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (task != null) task.cancel(true);
                dispose();
                System.exit(0);
            }
        });

        JLabel lblSource = new JLabel("From: ");
        JLabel lblTarget = new JLabel("To: ");

        txtSource = new JTextField(strSource, 50);
        txtSource.setEditable(false);

        txtTarget = new JTextField(strTarget, 50);
        txtTarget.setEditable(false);

        JLabel lblProgressAll = new JLabel("Overall: ");
        JLabel lblProgressCurrent = new JLabel("Current File: ");

        progressAll = new JProgressBar(0, 100);
        progressAll.setStringPainted(true);

        progressCurrent = new JProgressBar(0, 100);
        progressCurrent.setStringPainted(true);

        JLabel lblSpeedAverage = new JLabel("Average speed: ");
        JLabel lblSpeedCurrent = new JLabel("Current speed: ");
        speedAverage = new JTextField();
        speedAverage.setEditable(false);
        speedCurrent = new JTextField();
        speedCurrent.setEditable(false);

        JLabel lblTimeElapsed = new JLabel("Time elapsed: ");
        JLabel lblTimeRemaining = new JLabel("Time remaining: ");
        timeElapsed = new JTextField();
        timeElapsed.setEditable(false);
        timeRemaining = new JTextField();
        timeRemaining.setEditable(false);

        txtDetails = new JTextArea(5, 50);
        txtDetails.setEditable(false);
        DefaultCaret caret = (DefaultCaret) txtDetails.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(txtDetails, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        btnCopy = new JButton("Copy");
        btnCopy.setFocusPainted(true);
        btnCopy.setFocusable(true);
        btnCopy.setEnabled(true);
        btnCopy.addActionListener(this);
        btnCopy.addKeyListener(this);

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panInputLabels = new JPanel(new BorderLayout(0, 5));
        JPanel panInputFields = new JPanel(new BorderLayout(0, 5));
        JPanel panProgressLabels = new JPanel(new BorderLayout(0, 5));
        JPanel panProgressBars = new JPanel(new BorderLayout(0, 5));
        JPanel panSpeedLabels = new JPanel(new BorderLayout(0, 5));
        JPanel panSpeedDetails = new JPanel(new BorderLayout(0, 5));
        JPanel panTimeLabels = new JPanel(new BorderLayout(0, 5));
        JPanel panTimeDetails = new JPanel(new BorderLayout(0, 5));

        panInputLabels.add(lblSource, BorderLayout.NORTH);
        panInputLabels.add(lblTarget, BorderLayout.CENTER);
        panInputFields.add(txtSource, BorderLayout.NORTH);
        panInputFields.add(txtTarget, BorderLayout.CENTER);
        panProgressLabels.add(lblProgressAll, BorderLayout.NORTH);
        panProgressLabels.add(lblProgressCurrent, BorderLayout.CENTER);
        panProgressBars.add(progressAll, BorderLayout.NORTH);
        panProgressBars.add(progressCurrent, BorderLayout.CENTER);
        panSpeedLabels.add(lblSpeedAverage, BorderLayout.NORTH);
        panSpeedLabels.add(lblSpeedCurrent, BorderLayout.CENTER);
        panSpeedDetails.add(speedAverage, BorderLayout.NORTH);
        panSpeedDetails.add(speedCurrent, BorderLayout.CENTER);
        panTimeLabels.add(lblTimeElapsed, BorderLayout.NORTH);
        panTimeLabels.add(lblTimeRemaining, BorderLayout.CENTER);
        panTimeDetails.add(timeElapsed, BorderLayout.NORTH);
        panTimeDetails.add(timeRemaining, BorderLayout.CENTER);

        JPanel panInput = new JPanel(new BorderLayout(0, 5));
        panInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Copying"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel panProgress = new JPanel(new BorderLayout(0, 5));
        panProgress.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Progress"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel panSpeed = new JPanel(new BorderLayout(0, 5));
        panSpeed.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Speed"), BorderFactory
                .createEmptyBorder(5, 5, 5, 5)));

        JPanel panTime = new JPanel(new BorderLayout(0, 5));
        panTime.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Time"), BorderFactory
                .createEmptyBorder(5, 5, 5, 5)));

        JPanel panDetails = new JPanel(new BorderLayout());
        panDetails.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Details"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel panControls = new JPanel(new BorderLayout());
        panControls.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        panInput.add(panInputLabels, BorderLayout.LINE_START);
        panInput.add(panInputFields, BorderLayout.CENTER);
        panProgress.add(panProgressLabels, BorderLayout.LINE_START);
        panProgress.add(panProgressBars, BorderLayout.CENTER);
        panSpeed.add(panSpeedLabels, BorderLayout.LINE_START);
        panSpeed.add(panSpeedDetails, BorderLayout.CENTER);
        panTime.add(panTimeLabels, BorderLayout.LINE_START);
        panTime.add(panTimeDetails, BorderLayout.CENTER);
        panDetails.add(panTime, BorderLayout.NORTH);
        panDetails.add(scrollPane, BorderLayout.CENTER);
        panControls.add(btnCopy, BorderLayout.CENTER);

        JPanel panUpper = new JPanel(new BorderLayout());
        panUpper.add(panInput, BorderLayout.NORTH);
        panUpper.add(panProgress, BorderLayout.CENTER);
        panUpper.add(panSpeed, BorderLayout.SOUTH);

        contentPane.add(panUpper, BorderLayout.NORTH);
        contentPane.add(panDetails, BorderLayout.CENTER);
        contentPane.add(panControls, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        performAction();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            performAction();
        }
    }


    private void performAction() {
        if ("Copy".equals(btnCopy.getText())) {
            File source = new File(txtSource.getText());
            File target = new File(txtTarget.getText());

            if (source.equals(target)) {
                JOptionPane.showMessageDialog(this, "You can't copy directory in itself", "ERROR", JOptionPane.ERROR_MESSAGE);
                txtDetails.append("Error has occurred");
                btnCopy.setText("Close");
                return;
            }

            if (!source.exists()) {
                JOptionPane.showMessageDialog(this, "The source file/directory does not exist!", "ERROR", JOptionPane.ERROR_MESSAGE);
                txtDetails.append("Error has occurred");
                btnCopy.setText("Close");
                return;
            }

            File result = new File(target.getAbsolutePath() + File.separator + source.getName());

            if (result.exists()) {
                int confirm = JOptionPane.showConfirmDialog(this, "The source file/directory already exists in target directory.\nDo you want to overwrite it?", "Warning", JOptionPane.YES_NO_OPTION);
                txtDetails.append(result.getAbsolutePath() + " already exists\n");
                if (confirm == 0) {
                    txtDetails.append("Overwriting\n");
                } else {
                    txtDetails.append("Copying cancelled");
                    btnCopy.setText("Close");
                    return;
                }
            }

            task = this.new CopyTask(source, result);
            task.addPropertyChangeListener(this);
            task.execute();

            btnCopy.setText("Cancel");
        } else if ("Cancel".equals(btnCopy.getText())) {
            task.cancel(true);
            txtDetails.append("Copying cancelled");
            btnCopy.setText("Close");
        } else if ("Close".equals(btnCopy.getText())) {
            dispose();
            System.exit(0);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressAll.setValue(progress);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            performAction();
        }
    }

    class CopyTask extends SwingWorker<Void, Integer> {
        private File source;
        private File target;
        private Timer timer;
        private long totalBytes = 0L;
        private long copiedBytes = 0L;

        private long fileBytes;
        private long soFar;
        private long lastTime = 0L;
        private long copiedPerSecond = 0L;
        private long summarisedSpeed = 0L;
        private int length;

        public CopyTask(File source, File target) {
            this.source = source;
            this.target = target;

            secondsElapsed = 0;
            timer = new Timer(1000, actionEvent -> {
                timeElapsed.setText((secondsElapsed++) + " s");
                copiedPerSecond = copiedBytes - lastTime;
                lastTime = copiedBytes;
                summarisedSpeed = summarisedSpeed + copiedPerSecond;
                speedCurrent.setText((copiedPerSecond / (1024.0 * 1024)) + " MB/s");
                speedAverage.setText(((summarisedSpeed / (1024.0 * 1024)) / secondsElapsed) + " MB/s");
                if (copiedPerSecond > 0) {
                    timeRemaining.setText((totalBytes - copiedBytes) / copiedPerSecond + " s");
                }
            });

            progressAll.setValue(0);
            progressCurrent.setValue(0);
        }

        @Override
        public Void doInBackground() throws Exception {

            txtDetails.append("Retrieving file info... ");
            retrieveTotalBytes(source);

            txtDetails.append("Done!\n");


            timer.start();
            copyFiles(source, target);

            return null;
        }

        @Override
        public void process(List<Integer> chunks) {
            chunks.forEach(progressCurrent::setValue);
        }

        @Override
        public void done() {
            if (!isCancelled()) {
                setProgress(100);
            }
            speedCurrent.setText(0 + " MB/s");
            timeRemaining.setText(0 + " s");

            timer.stop();
            btnCopy.setText("Close");
        }

        private void retrieveTotalBytes(File sourceFile) {
            if (sourceFile.isDirectory()) {
                File[] files = sourceFile.listFiles();
                for (File file : files != null ? files : new File[0]) {
                    if (file.isDirectory()) retrieveTotalBytes(file);
                    else totalBytes = totalBytes + file.length();
                }
            } else
                totalBytes = totalBytes + sourceFile.length();
        }

        private void copyFiles(File sourceFile, File targetFile) throws IOException {
            if (sourceFile.isDirectory()) {

                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }

                String[] filePaths = sourceFile.list();

                for (String filePath : filePaths) {
                    if (isCancelled()) {
                        return;
                    }
                    File srcFile = new File(sourceFile, filePath);
                    File destFile = new File(targetFile, filePath);

                    copyFiles(srcFile, destFile);
                }
            } else {
                txtDetails.append("Copying " + sourceFile.getAbsolutePath() + " ... ");

                fileBytes = sourceFile.length();
                soFar = 0L;

                try (InputStream is = new FileInputStream(sourceFile);
                     OutputStream os = new FileOutputStream(targetFile)) {
                    byte[] buffer = new byte[BUFFER_SIZE];

                    while (!isCancelled() && (length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                        setProgress((int) ((copiedBytes = copiedBytes + length) * 100 / totalBytes));
                        publish((int) ((soFar = soFar + length) * 100 / fileBytes));
                    }
                }
                if (!isCancelled()) {
                    publish(100);
                    txtDetails.append("Done!\n");
                }
            }
        }
    }
}