/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filechecksum;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

/**
 *
 * @author Mundha
 */
public class FXMLDocumentController implements Initializable {

    @FXML private Label label;
    @FXML private BorderPane borderPane;
    @FXML private TextField TextFieldDirectory;
    @FXML private Button ButtonBrowse;
    @FXML private Button ButtonRun;
    @FXML private CheckBox CheckBoxCheckOnly;
    @FXML private TableView TableViewDisplayList;
    @FXML private TableColumn columnMessage;
    @FXML private TableColumn columnPath;

    ObservableList<DisplayListItem> list;
    
    private long TotalBytes = 0;
    private long BytePassed = 0;

    @FXML
    private void handleButtonBrowseAction(ActionEvent event) {
        System.out.println("You clicked me!");
        //label.setText("Hello World!");

        DirectoryChooser dc = new DirectoryChooser();
        //dc.showDialog(null);

        File d = dc.showDialog(borderPane.getScene().getWindow());

        TextFieldDirectory.setText(d.getAbsolutePath());
    }
    
    @FXML
    private void handleButtonRunAction(ActionEvent event) {
        list.clear();
        ScanDirectory(TextFieldDirectory.getText());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ButtonBrowse.requestFocus();
            }
        });
        
        list = FXCollections.observableArrayList();
        
        //list.add(new DisplayListItem("kishan", "test", Color.RED));

        columnMessage.setCellValueFactory(
                new PropertyValueFactory<DisplayListItem,String>("msg")
        );
        
        columnPath.setCellValueFactory(
                new PropertyValueFactory<DisplayListItem,String>("path")
        );        
        
        TableViewDisplayList.setRowFactory(new Callback<TableView, TableRow>() {
            @Override
            public TableRow call(final TableView tableView) {
                return new TableRow <DisplayListItem>() {
                    @Override
                    public void updateItem(DisplayListItem o, boolean empty) {
                        super.updateItem(o, empty);
                        //this.setTextFill(Color.RED);
                        
                        if(o != null) {
                        
                            //setOpacity(0.4);
                            setTextFill(Color.BLUE);
                            //setAlignment(Pos.CENTER_RIGHT);
                            

                            Tooltip tooltip = new Tooltip();
                            tooltip.setText(o.path);
                            setTooltip(tooltip);
                        
                            //System.out.println("RowFactory");
                        }
                    }
                };
            }
        });
        
        /*
        columnMessage.setCellFactory(new Callback<TableColumn, TableCell<DisplayListItem, String>>() {
                public TableCell call(TableColumn param) {
                    return new TableCell<DisplayListItem, String>() {

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            //if (!isEmpty()) {
                                this.setTextFill(Color.RED);
                            //    // Get fancy and change color based on data
                            //    if(item.contains("@")) 
                            //        this.setTextFill(Color.BLUEVIOLET);
                                setText(item);
                            //}
                        }
                    };
                }
            });
        */
        
        TableViewDisplayList.setItems(list);
    }

    /**
     * ****************************
     * Functions
     ****************************
     */
    /**
     * Get total size of directory
     *
     * @param dir Directory Path
     * @return Size in bytes
     */
    private long GetDirectorySize(String dir) {
        try {
            long size = 0;

            // Make sure that dir ended with backslash
            if (!dir.endsWith("\\")) {
                dir += "\\";
            }

            // Make sure that directory exists in system
            if (!Files.exists(Paths.get(dir), LinkOption.NOFOLLOW_LINKS)) {
                throw new FileNotFoundException(String.format("%s not found", dir));
            }

            File file = new File(dir);
//            String[] directories = file.list(new FilenameFilter() {
//              @Override
//              public boolean accept(File current, String name) {
//                return new File(current, name).isDirectory();
//              }
//            });
            
            // Get list of directories and files
            List<String> directories = new ArrayList<String>();
            List<String> files = new ArrayList<String>();

            File[] listOfFiles = file.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    files.add(listOfFiles[i].getAbsolutePath());
                    //System.out.println("File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    directories.add(listOfFiles[i].getAbsolutePath());
                    //System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
            
            for(String fileName : files) {
                File f = new File(fileName);
                size += f.length();
            }

            for(String dirName : directories) {
                size += GetDirectorySize(dirName);
            }

            return size;
        } catch (Exception ex) {
            return 0;
        }
    }

    private void ScanDirectory(String dir) {
        try {
            // show status dir
            showlabelmsg(dir);

            // Make sure that dir ended with backslash
            if (!dir.endsWith("\\")) {
                dir += "\\";
            }

            // Make sure that directory exists in system
            if (!Files.exists(Paths.get(dir), LinkOption.NOFOLLOW_LINKS)) {
                throw new FileNotFoundException(String.format("%s not found", dir));
            }

            // Get list of directories and files
            List<String> directories = new ArrayList<String>();
            List<String> files = new ArrayList<String>();

            File file = new File(dir);
            
            File[] listOfFiles = file.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    files.add(listOfFiles[i].getAbsolutePath());
                    //System.out.println("File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    directories.add(listOfFiles[i].getAbsolutePath());
                    //System.out.println("Directory " + listOfFiles[i].getName());
                }
            }

            // No directory or file found
            if (directories.size() + files.size() == 0) {
                return;
            }
            
            
            //#region File checksum
            // Local variable for old md5 checksum and new md5 checksum
            Map<String, String > md5old = new HashMap<String, String>();
            Map<String, String> md5new = new HashMap<String, String>();

            // Read old md5 checksum
            // and store in local variable
            if (Files.exists(Paths.get(dir + "checksum.md5"), LinkOption.NOFOLLOW_LINKS)) {
                for(String checksum_old : Files.readAllLines(Paths.get(dir + "checksum.md5"), Charset.defaultCharset())){
                    String[] s = checksum_old.split("\t");
                    if (s.length == 2) {
                        md5old.put(s[0], s[1]);
                    }
                }

            }

            for(String fileName : files) {
                File fInfo = new File(fileName);
                
                // dont make checksum of old checksum info file
                String ext = getFileExtension(fInfo);
                if ("md5".equals(ext)) {
                    continue;
                }

                showlabelmsg(String.format("%s (size: %s)", fileName, getFileSizeString(fInfo.length())));

                String md5hash = md5File(fileName);
                md5new.put(fInfo.getName(), md5hash);

                // update progress
                BytePassed += fInfo.length();
            }

            // only update checksum file if need
            boolean IsUpdateChecksumFile = false;

            // old file and new file count not match
            // so files are added or deleted
            // must be update checksum
            if (md5old.size() != md5new.size()) {
                IsUpdateChecksumFile = true;
            }

            for(Map.Entry < String, String > md5Value : md5new.entrySet()){
                // old md5 checksum not found
                // may be new file created
                String key = md5Value.getKey();
                String value = md5Value.getValue();
                
                String oldmd5Value = md5old.get(key);
                
                if(oldmd5Value == null) {
                    addListItem("New file", dir + key);
                    IsUpdateChecksumFile = true;
                    continue;
                }
                
                // old md5 checksum and new md5 checksum not match
                if (!oldmd5Value.equals(value)) {
                    addListItem("Missmatch", dir + key);
                    IsUpdateChecksumFile = true;
                    continue;
                }
            }

            // update checksum file if required
            if (!CheckBoxCheckOnly.isSelected() && IsUpdateChecksumFile) {
                //StreamWriter md5checksumFile = File.CreateText(dir + "checksum.md5");
                
                File f = new File(dir + "checksum.md5");
                
                if (f.exists()) {
                    f.delete();
                }

                f.createNewFile();

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                //bw.write(content);
                        
                for(Map.Entry < String, String > md5Value : md5new.entrySet())
                {
                    //md5checksumFile.WriteLine(String.format("%s\t%s", md5Value.getKey(), md5Value.getValue()));
                    bw.write(String.format("%s\t%s", md5Value.getKey(), md5Value.getValue()));
                    bw.newLine();
                }
                bw.close();
                //md5checksumFile.Close();
            }
            //# endregion

            // Scan Sub Direcotries
            for (String directoryName : directories) {
                ScanDirectory(directoryName);
            }
        } catch (Exception ex) {
            addListItem("Error", ex.getMessage(), Color.RED);
        }
    }

    /**
     * Get MD5 value of file
     *
     * @param fileName File Path
     * @return MD5 hash value
     */
    private String md5File(String fileName) throws FileNotFoundException, IOException, NoSuchAlgorithmException {

        File f = new File(fileName);
        if (!f.exists() || f.isDirectory()) {
            throw new FileNotFoundException(String.format("%s not found", fileName));
        }

        MessageDigest md = MessageDigest.getInstance("MD5");

        InputStream in = Files.newInputStream(Paths.get(fileName));
        //DigestInputStream dis = new DigestInputStream(in, md);
        
        byte[] bytesBuffer = new byte[1024];
        int bytesRead = -1;
        
        while((bytesRead = in.read(bytesBuffer)) != -1) {
            md.update(bytesBuffer, 0, bytesRead);
        }
        
        in.close();
        

        byte[] data = md.digest();
        
        //dis.close();

        // Create a new Stringbuilder to collect the bytes 
        // and create a string.
        StringBuilder sBuilder = new StringBuilder();

        // Loop through each byte of the hashed data  
        // and format each one as a hexadecimal string. 
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(0xFF & data[i]);
            if(hex.length() == 1) {
                sBuilder.append('0');
            }
            sBuilder.append(hex);
        }

        return sBuilder.toString();
    }

    /**
     * Convert long to KB, MB
     *
     * @param size Size in Bytes
     * @return String format of size
     */
    private String getFileSizeString(long size) {
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};

        int pos = 0;

        while (size > 1024 && pos < units.length) {
            pos++;
            size /= 1024;
        }

        return String.format("%d %s", (int) size, units[pos]);
    }

    private void addListItem(String msg, String path) {
        addListItem(msg, path, Color.BLACK);
    }

    private void addListItem(String msg, String path, Color color) {
        list.add(new DisplayListItem(msg, path, color));
        /*
         ListViewItem liv = new ListViewItem(msg);
         liv.SubItems.Add(path);
         liv.ForeColor = color;

         if (!listView1.InvokeRequired)
         {
         listView1.Items.Add(liv);
         }
         else
         {
         listView1.Invoke(new MethodInvoker(delegate()
         {
         listView1.Items.Add(liv);
         }));
         }
         */
    }

    /**
     * Show current message
     * @param msg Message
     */
    private void showlabelmsg(String msg) {
        label.setText(msg);
        /*
        if (!labelStatus.InvokeRequired)
        {
            labelStatus.Text = msg;
        }
        else
        {
            labelStatus.Invoke(new MethodInvoker(delegate()
            {
                labelStatus.Text = msg;
            }));
        }
        */
    }
    
    private String getFileExtension(File file) {
        String ext = "";
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            ext = fileName.substring(fileName.lastIndexOf(".")+1);
        
        return ext;
    }
}
