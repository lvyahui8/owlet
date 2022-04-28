package io.github.lvyahui8.owlet.graph;

import org.apache.commons.codec.digest.DigestUtils;
import org.nustaq.serialization.FSTConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class GraphSerializer {
    public void serialize(Graph graph, File targetFile) throws IOException {
        // https://github.com/RuedigerMoeller/fast-serialization/wiki/Serialization
        FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
        byte[] bytes = conf.asByteArray(graph);
        if (! targetFile.exists()) {
            targetFile.createNewFile();
        }
        RandomAccessFile raf = new RandomAccessFile(targetFile,"rw");
        FileChannel channel = raf.getChannel();
        try {
            MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            mbb.put(bytes);
        } finally {
            channel.close();
        }
    }

    public Graph deserialize(File sourceFile) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(sourceFile, "r");
        FileChannel channel = raf.getChannel();
        byte[] results = new byte[(int) sourceFile.length()];
        try {
            MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, sourceFile.length());
            mbb.get(results);
        } finally {
            channel.close();
        }
        FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
        Object object = conf.asObject(results);
        return (Graph) object;
    }

    public static File GetGraphFile(String classpath) {
        String path = System.getProperty("java.io.tmpdir");
        return new File(path + File.separator + DigestUtils.md5Hex(classpath).toLowerCase() + ".gf");
    }
}
