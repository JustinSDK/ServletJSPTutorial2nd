package cc.openhome;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/upload")
public class Upload extends HttpServlet {
    private final Pattern fileNameRegex =
                Pattern.compile("filename=\"(.*)\"");
    private final Pattern fileRangeRegex =
                Pattern.compile("filename=\".*\"\\r\\n.*\\r\\n\\r\\n(.*+)");

    @Override
    protected void doPost(
        HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        byte[] content = request.getInputStream().readAllBytes();
        String contentAsTxt = new String(content, "ISO-8859-1");

        String filename = filename(contentAsTxt);
        Range fileRange = fileRange(contentAsTxt, request.getContentType());

        write(
            content, 
            contentAsTxt.substring(0, fileRange.start)
                        .getBytes("ISO-8859-1")
                        .length, 
            contentAsTxt.substring(0, fileRange.end)
                        .getBytes("ISO-8859-1")
                        .length, 
            String.format("c:/workspace/%s", filename)
        );
    }

    // 取得檔案名稱
    private String filename(String contentTxt)
              throws UnsupportedEncodingException {
        Matcher matcher = fileNameRegex.matcher(contentTxt);
        matcher.find();
        
        String filename =  matcher.group(1);
        // 如果名稱上包含資料夾符號「\」，就只取得最後的檔名
        if(filename.contains("\\")) {  
            return filename.substring(filename.lastIndexOf("\\") + 1); 
        }
        return filename;
    }

    // 封裝範圍起始與結束
    private static class Range {
        final int start;
        final int end;
        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    // 取得檔案邊界範圍
    private Range fileRange(String content, String contentType) {
        Matcher matcher = fileRangeRegex.matcher(content);
        matcher.find();
        int start = matcher.start(1);

        String boundary = contentType.substring(
                contentType.lastIndexOf("=") + 1, contentType.length());
        int end = content.indexOf(boundary, start) - 4;   

        return new Range(start, end);
    }

    // 儲存檔案內容
    private void write(byte[] content, int start, int end, String file)
                     throws IOException {
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(content, start, (end - start));
        }   
    }
}
