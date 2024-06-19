package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData)
            throws Exception {
        UUID uuid = UUID.randomUUID(); // 랜덤으로 UUID를 생성
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); //.기준으로 제일 작은거를 뺌
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName; //실제 저장경로와 실제 파일내임
        System.out.println(fileUploadFullUrl);
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl); //ouputstream으로 객체 만듦
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);

        if (deleteFile.exists()) { //deleteFile 객체여부를 확인
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
