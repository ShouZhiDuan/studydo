package com.example.provider.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Slf4j
public class SkopeoUtil {

       /**
     * 通用的skopeo命令执行方法
     * @param skopeoCommand skopeo命令数组
     * @return 返回命令的执行结果
     */
    private static String executeSkopeoCommand(String[] skopeoCommand) {
      ProcessBuilder processBuilder = new ProcessBuilder(skopeoCommand);
      processBuilder.redirectErrorStream(true); // 将错误输出流重定向到标准输出流

      StringBuilder output = new StringBuilder();
      try {
          Process process = processBuilder.start(); // 启动进程
          BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); // 获取输出流
          String line;
          while ((line = reader.readLine()) != null) { // 读取输出
              output.append(line).append("\n");
          }
          int exitCode = process.waitFor(); // 等待进程结束
          if (exitCode == 0) {
              output.append("镜像操作成功！\n");
          } else {
              output.append("镜像操作失败，退出码：").append(exitCode).append("\n");
          }
      } catch (IOException e) {
          output.append("启动skopeo进程失败！\n");
          e.printStackTrace();
      } catch (InterruptedException e) {
          output.append("skopeo进程被中断！\n");
          e.printStackTrace();
      }
      return output.toString();
  }


    /**
     * 判断镜像是否存在
     * skopeo inspect --creds flow:Nvx_1024 --tls-verify=false docker://192.168.50.103/flow/cust-cont:20250617191632-x86
     */
  public static String  checkImageExist(String harborUrl, String harborUsername, String harborPassword) {
//      String[] command = {
//              "skopeo",
//              "inspect",
//              "--src-tls-verify=false",
//              "--dest-tls-verify=false",
//              "--dest-creds=" + harborUsername + ":" + harborPassword,
//              "docker://" + harborUrl
//      };
      String[] command = {
              "skopeo",
              "inspect",
              "--tls-verify=false",
              "--creds",
              harborUsername + ":" + harborPassword,
              "docker://" + harborUrl
      };

      String rst = executeSkopeoCommand(command);

      log.info("命令执行判断镜像是否存在结果：" + rst);

      return rst;
  }




  /**
   * 将本地tar包中的镜像推送到Harbor仓库
   * @param harborUrl Harbor仓库地址
   * @param harborUsername Harbor用户名
   * @param harborPassword Harbor密码
   * @param tarFilePath 本地tar包路径
   * @param harborProject Harbor项目名称
   * @param imageName 镜像名称（不包含标签）
   * @param imageTag 镜像标签
   * @return 返回命令的执行结果
   */
  public static String pushTarToHarbor(String harborUrl, String harborUsername, String harborPassword,
                                       String tarFilePath, String harborProject,
                                       String imageName, String imageTag) {
      String harborImage = harborUrl + "/" + harborProject + "/" + imageName + ":" + imageTag;
      log.info("正在将镜像推送到Harbor仓库：" + harborImage);
      String[] command = {
          "skopeo", "copy", "--policy", "/home/app/skopeo/default-policy.json",
          "--src-tls-verify=false", "--dest-tls-verify=false",
          "--dest-creds=" + harborUsername + ":" + harborPassword,
          "docker-archive:" + tarFilePath,
          "docker://" + harborImage
      };

      log.info("执行命令：" + String.join(" ", command));
      String result = executeSkopeoCommand(command);
      log.info("命令执行结果：" + result);
      return result;
  }

  /**
   * 将本地tar包中的镜像推送到Harbor仓库（简化版，使用默认项目和标签）
   * @param harborUrl Harbor仓库地址
   * @param harborUsername Harbor用户名
   * @param harborPassword Harbor密码
   * @param tarFilePath 本地tar包路径
   * @return 返回命令的执行结果
   */
  public static String pushTarToHarbor(String harborUrl, String harborUsername, String harborPassword,
                                       String tarFilePath) {
      String harborProject = "default"; // 默认项目
      String imageName = "default-image"; // 默认镜像名
      String imageTag = "latest"; // 默认标签
      return pushTarToHarbor(harborUrl, harborUsername, harborPassword, tarFilePath,
              harborProject, imageName, imageTag);
  }

  /**
   * 将本地tar包中的镜像推送到Harbor仓库（仅指定基本参数）
   * @param harborUrl Harbor仓库地址
   * @param harborUsername Harbor用户名
   * @param harborPassword Harbor密码
   * @param tarFilePath 本地tar包路径
   * @param harborProject Harbor项目名称
   * @return 返回命令的执行结果
   */
  public static String pushTarToHarbor(String harborUrl, String harborUsername, String harborPassword,
                                       String tarFilePath, String harborProject) {
      String imageName = "default-image"; // 默认镜像名
      String imageTag = "latest"; // 默认标签
      return pushTarToHarbor(harborUrl, harborUsername, harborPassword, tarFilePath,
              harborProject, imageName, imageTag);
  }

  /**
   * 测试方法
   * @param args 命令行参数
   */
  public static void main(String[] args) {
      // 测试完整参数调用
//      String result = SkopeoUtil.pushTarToHarbor(
//              "http://192.168.50.103:80",
//              "flow",
//              "Nvx_1024",
//              "/Users/shouzhi/temp/shanxi/image_tar/cust-cont_20250617105631-x86.tar",
//              "flow",
//              "cust-cont",
//              "20250617105631-x86"
//      );
//      System.out.println(result);

      // 测试简化参数调用
//      String result2 = SkopeoUtil.pushTarToHarbor(
//              "http://192.168.50.103:80",
//              "flow",
//              "Nvx_1024",
//              "/Users/shouzhi/temp/shanxi/image_tar/default-image.tar"
//      );
//      System.out.println(result2);

        String s = SkopeoUtil.checkImageExist(
            "192.168.50.103/flow/cust-cont:20250617191633-x86",
                "flow",
                "Nvx_1024"
        );

        System.out.println(s);

  }

}
