import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class TagGenerator extends JFrame implements ActionListener {
    JPanel mainPnl, searchPnl, displayPnlL, displayPnlR, controlPnl;
    JTextArea textAreaL, textAreaC, textAreaR;
    JScrollPane scrollPaneL, scrollPaneC, scrollPaneR;
    JButton searchBtn, quitBtn;

    JFileChooser chooser = new JFileChooser();
    File selectedFile;
    String rec = "";
    Path path = Paths.get(System.getProperty("user.dir"));
    ArrayList<String> searchList = new ArrayList<>();

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchBtn) {
            try (Stream<String> lines = Files.lines(path)) {
                File workingDirectory = new File(System.getProperty("user.dir"));
                chooser.setCurrentDirectory(workingDirectory);
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    int line = 0;
                    while (reader.ready()) {
                        rec = reader.readLine();
                        searchList.add(rec);
                        textAreaL.append(rec + "\n");
                        line++;
                        System.out.printf("\nLine %4d %-60s ", line, rec);
                    }

                    reader.close();
                    System.out.println("\n\nData file read!");

                } else {
                    System.out.println("No file selected!!! ... exiting.\nRun the program again and select a file.");
                }
            } catch (FileNotFoundException ae) {
                System.out.println("File not found!!!");
                JOptionPane.showMessageDialog(null, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ae) {
                JOptionPane.showMessageDialog(null, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            Set<String> filterSet = new HashSet<>();
            try {
                File stopFile = new File("src/stopWords.txt");
                Scanner sca = new Scanner(stopFile);
                while (sca.hasNextLine()) {
                    filterSet.add(sca.nextLine());
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            for(String setLine : filterSet) {
                textAreaC.append(setLine + "\n");
            }

            Map<String, Integer> tagMap = new HashMap<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile.getPath()))) {
                    String tagLine;
                    while ((tagLine = reader.readLine()) != null) {
                        String[] tags = tagLine.split("\\s+");
                        for (String tag : tags) {
                            if (!filterSet.contains(tag)) {
                                tagMap.put(tag, tagMap.getOrDefault(tag, 0) + 1);
                            }
                        }
                    }
                } catch (IOException ae) {
                    JOptionPane.showMessageDialog(null, "Error reading file.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
                    textAreaR.append(entry.getKey() + ": " + entry.getValue() + "\n");
                }
        }


        if (e.getSource() == quitBtn) {
            int closer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (closer == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }


    public TagGenerator() {
        setTitle("Tag Extractor");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        add(mainPnl);
        createSearchPnl();
        createDisplayPnlL();
        createDisplayPanelC();
        createDisplayPnlR();
        createControlPnl();
        setVisible(true);

    }

    public void createSearchPnl() {
        searchPnl = new JPanel();
        searchPnl.setLayout(new BorderLayout());
        searchBtn = new JButton("Search");
        searchBtn.addActionListener(this);
        searchPnl.add(searchBtn, BorderLayout.EAST);
        mainPnl.add(searchPnl, BorderLayout.NORTH);
    }

    public void createDisplayPnlL() {
        displayPnlL = new JPanel();
        textAreaL = new JTextArea(50, 20);
        scrollPaneL = new JScrollPane(textAreaL);
        displayPnlL.add(scrollPaneL);
        mainPnl.add(displayPnlL, BorderLayout.WEST);
    }

    public void createDisplayPanelC() {
        displayPnlR = new JPanel();
        textAreaC = new JTextArea(50, 20);
        scrollPaneC = new JScrollPane(textAreaC);
        displayPnlR.add(scrollPaneC);
        mainPnl.add(displayPnlR, BorderLayout.CENTER);
    }

    public void createDisplayPnlR() {
        displayPnlR = new JPanel();
        textAreaR = new JTextArea(50, 20);
        scrollPaneR = new JScrollPane(textAreaR);
        displayPnlR.add(scrollPaneR);
        mainPnl.add(displayPnlR, BorderLayout.EAST);
    }

    public void createControlPnl() {
        controlPnl = new JPanel();
        quitBtn = new JButton("Quit");
        quitBtn.addActionListener(this);
        controlPnl.add(quitBtn);
        mainPnl.add(controlPnl, BorderLayout.SOUTH);
    }
}
