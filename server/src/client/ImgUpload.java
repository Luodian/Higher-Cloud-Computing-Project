package client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class ImgUpload extends HttpServlet {
  private static final long serialVersionUID = 1L;

  // 上传文件存储目录
  private static final String UPLOAD_DIRECTORY = "portrait";
  private String result = "{\"img_save\":\"0\"}";
  private String user_id = "";
  Boolean imgRecieve = false, userIdReceive = false;
  // 上传配置
  private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
  private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
  private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

  /**
   * 上传数据及保存文件
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // 配置上传参数
    DiskFileItemFactory factory = new DiskFileItemFactory();
    // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
    factory.setSizeThreshold(MEMORY_THRESHOLD);
    // 设置临时存储目录
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

    ServletFileUpload upload = new ServletFileUpload(factory);

    // 设置最大文件上传值
    upload.setFileSizeMax(MAX_FILE_SIZE);

    // 设置最大请求值 (包含文件和表单数据)
    upload.setSizeMax(MAX_REQUEST_SIZE);

    // 中文处理
    upload.setHeaderEncoding("UTF-8");

    // 构造临时路径来存储上传的文件
    // 这个路径相对当前应用的目录
    String uploadPath = request.getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;

    // 如果目录不存在则创建
    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
      uploadDir.mkdir();
    }


      List<FileItem> formItems;
      try {
        formItems = upload.parseRequest(request);


      if (formItems != null && formItems.size() > 0) {
        // 迭代表单数据
        for (FileItem item : formItems) {
          // 处理不在表单中的字段
          if (!item.isFormField()) {
            String name = new File(item.getName()).getName();
            System.out.println("name:"+name);
            if (name.length()!=0) {
              imgRecieve = true;
            }

          } else {
            user_id = item.getString("UTF-8");
            if (!AuxiliaryTools.checkValidParameter(user_id)) {
              break;
            }
            System.out.println(user_id);
            userIdReceive = true;
          }

          if (imgRecieve && userIdReceive) {
            String fileName = user_id + ".jpg";
            String filePath = uploadPath + File.separator + fileName;
            File storeFile = new File(filePath);
            // 在控制台输出文件的上传路径
            System.out.println(filePath);
            // 保存文件到硬盘
            item.write(storeFile);
            result = "{\"img_save\":\"1\"}";
          }
        }
      }
      
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    // 响应
    response.setContentType("text/html;charset=UTF-8");
    response.setStatus(200);
    PrintWriter out;
    try {
      out = response.getWriter();
      out.println(result);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendError(403);
  }
}