package com.fasthub.backend;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;
import org.junit.jupiter.api.Test;

public class JasyptEncryptTest {

    @Test
    void encryptNaverApiKeys() {
        // JASYPT_ENCRYPTOR_PASSWORD 환경변수에 설정된 값 입력
        String jasyptPassword = "wjdtjr9401@";

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(jasyptPassword);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setIvGenerator(new NoIvGenerator());

        String clientId     = encryptor.encrypt("AJ1Eh4T1Mcz1vnTHdEZi");
        String clientSecret = encryptor.encrypt("EHCFng0Itr");

        System.out.println("=== 암호화 결과 ===");
        System.out.println("client-id     : ENC(" + clientId + ")");
        System.out.println("client-secret : ENC(" + clientSecret + ")");
    }
}
