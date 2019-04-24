package server;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class Connector implements Closeable {

    private DatagramChannel channel;
    private SocketAddress sendAddress;

    public final Receiver receiver = new Receiver();
    public final Sender sender = new Sender();

    public Connector(SelectionKey key){
        this.channel = (DatagramChannel)key.channel();
        if (key.attachment() != null) {
            this.sendAddress = ((User) key.attachment()).getSendAddress();
        }
    }

    public void setSendAddress(SocketAddress address){
        this.sendAddress = address;
    }


    public class Receiver {

        public byte[] receiveByteArray(int capacity) throws IOException {
            ByteBuffer buffer = ByteBuffer.allocate(capacity);
            buffer.clear();
            channel.receive(buffer);
            buffer.flip();
            return getBytes(buffer);
        }

        public String receiveString(int length) throws IOException {
            return new String(this.receiveByteArray(length * 8));
        }

        public Object receiveObj() throws IOException, ClassNotFoundException{
            ByteBuffer buffer = ByteBuffer.allocate(2000);
            buffer.clear();
            channel.receive(buffer);
            buffer.flip();
            byte[] data = getBytes(buffer);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object o = ois.readObject();
            bais.close();
            ois.close();
            return o;
        }

        private byte[] getBytes(ByteBuffer buffer) {
            byte[] result = new byte[buffer.limit()];
            for (int i = 0; i < buffer.limit(); i++) {
                result[i] = buffer.get();
            }
            buffer.clear();
            return result;
        }
    }

    public class Sender {

        public void sendString(String string) throws IOException {
            byte[] data = string.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(data.length);
            buffer.clear();
            buffer.put(data);
            buffer.flip();
            channel.send(buffer, sendAddress);

        }

        public void sendObj(Object o) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            byte[] data = baos.toByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(2000);
            buffer.clear();
            buffer.put(data);
            buffer.flip();
            channel.send(buffer, sendAddress);
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
