package dab.fast;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastReader {
  final private int BUFFER_SIZE = 1 << 16;
  private DataInputStream din;
  private byte[] buffer;
  private int bufferPointer, bytesRead;

  public FastReader() {
    this(System.in);
  }

  public FastReader(InputStream in) {
    din = new DataInputStream(System.in);
    buffer = new byte[BUFFER_SIZE];
    bufferPointer = bytesRead = 0;
  }

  public FastReader(byte inputs[]) {
    buffer = new byte[inputs.length];
    System.arraycopy(inputs, 0, buffer, 0, inputs.length);
    bufferPointer = 0;
    bytesRead = inputs.length;
  }

  public FastReader(String filename) throws IOException {
    din = new DataInputStream(new FileInputStream(filename));
    buffer = new byte[BUFFER_SIZE];
    bufferPointer = bytesRead = 0;
  }

  public String readLine()  {
    byte[] buf = new byte[64]; // line length
    int cnt = 0, c;
    while ((c = read()) != -1) {
      if (c == '\n')
        break;
      buf[cnt++] = (byte) c;
    }
    return new String(buf, 0, cnt);
  }

  public int nextInt() {
    int ret = 0;
    byte c = read();
    while (c <= ' ')
      c = read();
    boolean neg = (c == '-');
    if (neg)
      c = read();
    do {
      ret = ret * 10 + c - '0';
    } while ((c = read()) >= '0' && c <= '9');

    if (neg)
      return -ret;
    return ret;
  }

  public long nextLong() {
    long ret = 0;
    byte c = read();
    while (c <= ' ')
      c = read();
    boolean neg = (c == '-');
    if (neg)
      c = read();
    do {
      ret = ret * 10 + c - '0';
    } while ((c = read()) >= '0' && c <= '9');
    if (neg)
      return -ret;
    return ret;
  }

  public double nextDouble()  {
    double ret = 0, div = 1;
    byte c = read();
    while (c <= ' ')
      c = read();
    boolean neg = (c == '-');
    if (neg)
      c = read();

    do {
      ret = ret * 10 + c - '0';
    } while ((c = read()) >= '0' && c <= '9');

    if (c == '.') {
      while ((c = read()) >= '0' && c <= '9') {
        ret += (c - '0') / (div *= 10);
      }
    }

    if (neg)
      return -ret;
    return ret;
  }

  private void fillBuffer()  {
    try {
      bytesRead = din.read(buffer, bufferPointer = 0, BUFFER_SIZE);
      if (bytesRead == -1)
        buffer[0] = -1;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private byte read()  {
    if (bufferPointer == bytesRead)
      fillBuffer();
    return buffer[bufferPointer++];
  }

  public void close()  {
    if (din == null)
      return;
    try {
      din.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static byte[] buf = new byte[64]; // line length
  public byte next()  {
    int cnt = 0, c;
    while ((c = read()) != -1) {
      if (c == '\n' || c == ' ')
        break;
      buf[cnt++] = (byte) c;
    }
    return buf[0];
  }

  public static char[] stringAsChar = new char[256];
  public static int stringAsCharFE = 0;
  public void nextStringAsChars() {

    stringAsCharFE = 0;
    byte c = read();
    while (c == ' ') {
      c = read();
    }
    while (c != ' ' && c != '\n' && c != '\t' && c!= '\r') {
      stringAsChar[stringAsCharFE++] = (char)c;
      c = read();
    }
    
    // void some char after the end
    stringAsChar[stringAsCharFE] = 0;
    stringAsChar[stringAsCharFE+1] = 0;
    stringAsChar[stringAsCharFE+2] = 0;
    stringAsChar[stringAsCharFE+3] = 0;
    stringAsChar[stringAsCharFE+4] = 0;
  }
  
  public String nextString() {
    StringBuffer str = new StringBuffer();
    
    byte c = read();
    while (c == ' ') {
      c = read();
    }
    while (c != ' ' && c != '\n' && c != '\t' && c!= '\r') {
      str.append((char)c);
      c = read();
    }
    
    return str.toString();
  }
}
