package fast.read;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FastReader {
  final private static int BUFFER_SIZE = 1 << 16;
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
    copyFromString(inputs);
  }

  public void copyFromString(byte[] inputs) {
    System.arraycopy(inputs, 0, buffer, 0, inputs.length);
    bufferPointer = 0;
    bytesRead = inputs.length;
  }
  
  public static FastReader fromString(String input) {
    return new FastReader(input.getBytes());
  }

  
  public static FastReader fromFile(String filename) throws IOException {
    FastReader reader = new FastReader();
    reader.din = new DataInputStream(new FileInputStream(filename));
    reader.buffer = new byte[BUFFER_SIZE];
    reader.bufferPointer = reader.bytesRead = 0;
    return reader;
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
    if (bufferPointer == bytesRead) {
      try {
        fillBuffer();
      } catch(Exception e) {
        return -1;
      }
    }
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

  public String nextString() {
    return next();
  }
  
  public String next()  {
    byte c;
    StringBuilder sBuf = new StringBuilder(64);

    do {
      c = read();
    } while (c <= ' ');
    
    do {
      if (c == '\n' || c == ' ')
        break;
      sBuf.append((char)c);
    } while ((c = read()) != -1);
    
    return sBuf.toString();
  }

  public String nextLine()  {
    byte c;
    StringBuilder sBuf = new StringBuilder(64);

    do {
      c = read();
    } while (c <= ' ');
    
    do {
      if (c == '\n')
        break;
      sBuf.append((char)c);
    } while ((c = read()) != -1);
    
    return sBuf.toString();
  }
  
  public void nextLinePass()  {
    byte c;

    do {
      c = read();
    } while (c <= ' ');
    
    do {
      if (c == '\n')
        break;
    } while ((c = read()) != -1);
  }

  public byte nextByte()  {
    return nextBytes()[0];
  }

  public byte[] nextBytes() {
    byte[] buf = new byte[64]; // max line length
    int cnt = 0, c;
    while ((c = read()) != -1) {
      if (c == '\n' || c == ' ')
        break;
      buf[cnt++] = (byte) c;
    }
    return buf;
  }

  public char[] nextChars() {
    char[] buf = new char[64]; // max line length
    int cnt = 0, c;
    while ((c = read()) != -1) {
      if (c == '\n' || c == ' ') {
        buf[cnt++] = '\n';
        break;
      } else {
        buf[cnt++] = (char)c;
      }
    }
    buf[cnt++] = '\n';
    return buf;
  }
}
