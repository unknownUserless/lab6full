package client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class Connector implements Closeable {

    private DatagramSocket socket;

    public final Sender sender = new Sender();
    public final Receiver receiver = new Receiver();

    public SocketAddress getSocketAddress(){
        return socket.getLocalSocketAddress();
    }

    public Connector() throws SocketException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(4000);
    }

    public void sendPackToServer(SocketAddress serverAddress, MainPacket packet){
        SocketAddress old = this.sender.address;
        this.sender.address = serverAddress;
        this.sender.sendObj(packet);
        this.sender.address = old;
    }

    public void setUserSenderAddress(SocketAddress address){
        this.sender.address = address;
    }

    public class Sender {

        private SocketAddress address;

        public void sendString(String string) {
            byte[] data = string.getBytes();
            send(data);
        }

        public void sendInt(int i){
            ByteBuffer buf = ByteBuffer.allocate(100);
            buf.clear();
            buf.putInt(i);
            buf.flip();
            byte[] data = buf.array();
            send(data);
        }

        public void sendObj(Object o){
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ObjectOutputStream outObject = new ObjectOutputStream(out);
                outObject.writeObject(o);
                outObject.flush();
                byte[] data = out.toByteArray();
                send(data);
            } catch (IOException e){
                e.printStackTrace();
            }


        }

        private void send(byte[] data){
            DatagramPacket pack = new DatagramPacket(data, data.length, address);
            try {
                socket.send(pack);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public class Receiver {

        public String receiveString(int length) throws SocketTimeoutException {
            DatagramPacket pack = receivePack(length * 8);
            return new String(pack.getData(), 0, pack.getLength());
        }

        public boolean receiveBoolean() throws SocketTimeoutException {
            String value = this.receiveString(5);
            if (value.equals("true")) {
                return true;
            } else if (value.equals("false")) {
                return false;
            } else {
                throw new BooleanException(value);
            }
        }

        public Object receiveObj() throws SocketTimeoutException, ClassNotFoundException{
            DatagramPacket packet = receivePack(4000);
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
                ObjectInputStream ois = new ObjectInputStream(bais);
                return ois.readObject();
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        private DatagramPacket receivePack(int capacity) throws SocketTimeoutException{
            try {
                DatagramPacket packet = new DatagramPacket(new byte[capacity], capacity);
                socket.receive(packet);
                return packet;
            } catch (IOException e){
                if (e instanceof SocketTimeoutException){
                    throw (SocketTimeoutException)e;
                } else {
                    e.printStackTrace();
                    return new DatagramPacket(new byte[10], 10);
                }
            }
        }
    }

    @Override
    public void close(){
        this.socket.close();
    }
}
