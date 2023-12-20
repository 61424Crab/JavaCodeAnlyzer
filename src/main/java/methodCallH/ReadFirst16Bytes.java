番号
300NTK-661

import java.io.FileInputStream;
import java.io.IOException;

public class ReadFirst16Bytes {
    public static void main(String[] args) {
        try (FileInputStream fileInputStream = new FileInputStream("path/to/your/file")) {
            byte[] bytes = new byte[16];
            int bytesRead = fileInputStream.read(bytes);

            // 处理读取到的字节
            for (int i = 0; i < bytesRead; i++) {
                System.out.printf("%02X ", bytes[i]); // 以十六进制格式打印
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
