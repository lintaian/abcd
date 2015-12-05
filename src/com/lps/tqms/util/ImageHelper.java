package com.lps.tqms.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageHelper {

  private String getExamId(String code) {
    String[] examId = { "AE748429-DE51-4DCD-9220-0B96F1473422", "2AD89294-466B-4CBE-ABD0-EC2AE7195FCB", "77E7D9D3-51A0-4BA6-B8ED-1FF5676F3161",
        "5E370952-B55F-4C0F-90E8-5EEA4AFC72C8", "E27A322F-1CAD-4CD8-A136-07BD0AC41DD6", "C93EB125-91CC-4E20-99EA-4F6C86EA5ABD",
        "B42B1DD2-F78F-434A-A7F6-B0C4B44147B5", "2A57A2D5-5CC5-447E-8DA5-E5ECAFB0ECBD", "83F7CC9B-30B4-4360-9F27-8E983C0511E0",
        "EDEDB11A-9715-4773-8B04-C754C53D52FA" };
    String[] examCode = { "5101830015010101", "5101830015010102", "5101830015010103", "5101830015010104", "5101830015010105", "5101830015010106",
        "5101830015010107", "5101830015010108", "5101830015010109", "5101830015010110" };
    for (int i = 0; i < examCode.length; ++i) {
      if (examCode[i].equals(code)) {
        return examId[i];
      }
    }
    return "";
  }

  private String getImageIndex(Connection conn, String examId, String examNo) {
    String imageIndex = "";
    String sql = "select ImageIndex from Inf_Exam_Image where ExamId = ? and ExamNo = ?";

    try {
      PreparedStatement stmt = conn.prepareStatement(sql.toString());
      try {
        stmt.setString(1, examId);
        stmt.setString(2, examNo);
        ResultSet rs = stmt.executeQuery();
        try {
          if (rs.next()) {
            imageIndex = rs.getObject(1).toString();
          }
        } finally {
          rs.close();
        }
      } finally {
        stmt.close();
      }

    } catch (SQLException e) {
    }

    return imageIndex;
  }

  private String getQuestionId(Connection conn, String examId, String questionTitle) {
    String imageId = "";
    String sql = "SELECT QuestionId FROM Inf_Exam_Question where examId = ? and questionName = ?";

    try {
      PreparedStatement stmt = conn.prepareStatement(sql.toString());
      try {
        stmt.setString(1, examId);
        stmt.setString(2, questionTitle);
        ResultSet rs = stmt.executeQuery();
        try {
          if (rs.next()) {
            imageId = rs.getObject(1).toString();
          }
        } finally {
          rs.close();
        }
      } finally {
        stmt.close();
      }

    } catch (SQLException e) {
    }

    return imageId;
  }

  private ByteArrayOutputStream getQuestionImage(Connection conn, String questionId) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    String sql = "select Image from Inf_Question_Source where QuestionId = ?";

    try {
      PreparedStatement stmt = conn.prepareStatement(sql.toString());
      try {
        stmt.setString(1, questionId);
        ResultSet rs = stmt.executeQuery();
        try {
          if (rs.next()) {
            InputStream in = rs.getBinaryStream(1);
            try {
              byte[] buffer = new byte[4096];
              int len = in.read(buffer);
              while (len > 0) {
                stream.write(buffer, 0, len);
                len = in.read(buffer);
              }
            } finally {
              in.close();
            }
          }
        } finally {
          rs.close();
        }
      } finally {
        stmt.close();
      }
    } catch (Exception e) {
    }

    return stream;
  }

  public ByteArrayOutputStream post(String url, String param) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    try {
      URL realUrl = new URL(url);
      URLConnection conn = realUrl.openConnection();
      conn.setRequestProperty("accept", "*/*");
      conn.setRequestProperty("connection", "Keep-Alive");
      conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      conn.setDoOutput(true);
      conn.setDoInput(true);
      OutputStream output = conn.getOutputStream();
      try {
        output.write(param.getBytes("utf-8"));
        output.flush();
        InputStream input = conn.getInputStream();
        try {
          byte[] buffer = new byte[4096];
          int recv = input.read(buffer);
          while (recv > 0) {
            stream.write(buffer, 0, recv);
            recv = input.read(buffer);
          }
        } finally {
          input.close();
        }
      } finally {
        output.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return stream;
  }

  private String connUser = "sa";
  private String connPass = "ansec_888";
  // private String connString =
  // "jdbc:jtds:sqlserver://211.149.146.93:21433/lps_data";
  private String connString = "jdbc:jtds:sqlserver://211.149.147.223:21433/lps_data";

  public String imgLoginUrl = "http://211.149.147.223/ScoreExam/LoginInfo.ashx";
  public String imgQuestionUrl = "http://211.149.147.223/ScoreExam/QuestionImageInfo.ashx";

  public byte[] examQuestionImage(String examCode, String questionTitle) {
    String examId = getExamId(examCode);
    byte[] data = new byte[0];
    if (examId.length() > 0) {
      try {
        Connection conn = DriverManager.getConnection(connString, connUser, connPass);
        String questionId = getQuestionId(conn, examId, questionTitle);
        if (questionId.length() > 0) {
        	data = getQuestionImage(conn, questionId).toByteArray();
        }
      } catch (Exception e) {
      }
    }
    return data;
  }

  public byte[] examStudentImage(String examCode, String examNo, String questionTitle) {
    String examId = getExamId(examCode);
    byte[] data = new byte[0];
    if (examId.length() > 0) {
      try {
        Connection conn = DriverManager.getConnection(connString, connUser, connPass);
        String imageIndex = getImageIndex(conn, examId, examNo);
        if (imageIndex.length() >= 0) {
          String questionId = getQuestionId(conn, examId, questionTitle);
          if (questionId.length() > 0) {
            String param = String.format("method=onLogin&account=%s&password=%s", "j1", "111111");
            post(imgLoginUrl, param);
            param = String.format("method=GetOneExamQuestion&ID=%s&QuestionID=%s&fmt=png", imageIndex, questionId);
            data = post(imgQuestionUrl, param).toByteArray();
          }
        }
      } catch (Exception e) {
      }
    }
    return data;
  }
}

