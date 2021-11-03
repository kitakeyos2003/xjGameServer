// 
// Decompiled by Procyon v0.5.36
// 
package hero.share.service;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;

public class AllPictureDataDict {

    private HashMap<String, byte[]> imageMap;
    private HashMap<String, byte[]> animationMap;
    private HashMap<String, byte[]> fileDataMap;
    private static AllPictureDataDict instance;

    private AllPictureDataDict() {
        this.imageMap = new HashMap<String, byte[]>();
        this.animationMap = new HashMap<String, byte[]>();
        this.fileDataMap = new HashMap<String, byte[]>();
        this.load();
    }

    public static AllPictureDataDict getInstance() {
        if (AllPictureDataDict.instance == null) {
            AllPictureDataDict.instance = new AllPictureDataDict();
        }
        return AllPictureDataDict.instance;
    }

    private void load() {
        ArrayList<File> files = this.readDirFiles(ShareServiceImpl.getInstance().getConfig().getPicture());
        for (int i = 0; i < files.size(); ++i) {
            String imageURL = "";
            File file = files.get(i);
            imageURL = file.getPath().toLowerCase().replace("\\", "/");
            this.fileDataMap.put(imageURL, this.getImageBytes(file));
        }
        files = null;
    }

    public ArrayList<File> readDirFiles(final String dirName) {
        ArrayList<File> fileList = new ArrayList<File>();
        try {
            File file = new File(dirName);
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    File tempFile = files[i];
                    if (tempFile.getName().indexOf(".svn") == -1) {
                        if (tempFile.isDirectory()) {
                            String subDirName = tempFile.getPath();
                            ArrayList<File> list = this.readDirFiles(subDirName);
                            for (int j = 0; j < list.size(); ++j) {
                                fileList.add(list.get(j));
                            }
                        } else {
                            if (!tempFile.isFile()) {
                                continue;
                            }
                            fileList.add(tempFile);
                        }
                        if (i == files.length - 1) {
                            return fileList;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return fileList;
    }

    private byte[] getImageBytes(final File _imageFile) {
        byte[] rtnValue = null;
        try {
            DataInputStream dis = null;
            dis = new DataInputStream(new FileInputStream(_imageFile));
            int imgFileByteSize = dis.available();
            rtnValue = new byte[imgFileByteSize];
            int pos = 0;
            while ((pos = dis.read(rtnValue, pos, imgFileByteSize - pos)) != -1) {
            }
            dis.close();
        } catch (Exception e) {
            LogWriter.error(null, e);
        }
        return rtnValue;
    }

    public byte[] getFileBytes(final String _fileURL) {
        return this.fileDataMap.get(_fileURL);
    }

    public byte[] getImageBytes(final String _imageURL) {
        return this.imageMap.get(_imageURL);
    }

    public byte[] getAnimationBytes(final String _anuURL) {
        return this.animationMap.get(_anuURL);
    }
}
