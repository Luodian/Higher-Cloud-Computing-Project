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

  // �ϴ��ļ��洢Ŀ¼
  private static final String UPLOAD_DIRECTORY = "portrait";
  private String result = "{\"img_save\":\"0\"}";
  private String user_id = "";
  Boolean imgRecieve = false, userIdReceive = false;
  // �ϴ�����
  private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
  private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
  private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB

  /**
   * �ϴ����ݼ������ļ�
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    // �����ϴ�����
    DiskFileItemFactory factory = new DiskFileItemFactory();
    // �����ڴ��ٽ�ֵ - �����󽫲�����ʱ�ļ����洢����ʱĿ¼��
    factory.setSizeThreshold(MEMORY_THRESHOLD);
    // ������ʱ�洢Ŀ¼
    factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

    ServletFileUpload upload = new ServletFileUpload(factory);

    // ��������ļ��ϴ�ֵ
    upload.setFileSizeMax(MAX_FILE_SIZE);

    // �����������ֵ (�����ļ��ͱ�����)
    upload.setSizeMax(MAX_REQUEST_SIZE);

    // ���Ĵ���
    upload.setHeaderEncoding("UTF-8");

    // ������ʱ·�����洢�ϴ����ļ�
    // ���·����Ե�ǰӦ�õ�Ŀ¼
    String uploadPath = request.getServletContext().getRealPath("./") + File.separator + UPLOAD_DIRECTORY;

    // ���Ŀ¼�������򴴽�
    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
      uploadDir.mkdir();
    }


      List<FileItem> formItems;
      try {
        formItems = upload.parseRequest(request);


      if (formItems != null && formItems.size() > 0) {
        // ����������
        for (FileItem item : formItems) {
          // �����ڱ��е��ֶ�
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
            // �ڿ���̨����ļ����ϴ�·��
            System.out.println(filePath);
            // �����ļ���Ӳ��
            item.write(storeFile);
            result = "{\"img_save\":\"1\"}";
          }
        }
      }
      
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    // ��Ӧ
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