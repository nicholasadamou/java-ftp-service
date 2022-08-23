package com.nicholasadamou.ftp.service.util;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class Utils
{
	Logger logger = LoggerFactory.getLogger(Utils.class);

	@Value("${ssh.client.host}")
	private String host;

	public SSHClient getSSHClient() throws IOException {
		SSHClient client = new SSHClient();

		client.addHostKeyVerifier(new PromiscuousVerifier());
		client.connect(host);

		return client;
	}

//	public boolean sendFile(File file, String token) {
//		try {
//			URI uri = URI.create(host).resolve("/sftp");
//
//			RestTemplate restTemplate = new RestTemplate();
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Authorization", token);
//
//			HttpEntity<File> request = new HttpEntity<>(file, headers);
//			restTemplate.postForLocation(uri, request);
//
//			System.out.println("Sent File: " + file);
//		} catch (RestClientException e) {
//			logger.error("sendFile() Exception", e);
//
//			return false;
//		}
//
//		return true;
//	}

//	public boolean sendFile(ParsedFile file, String token) {
//		try {
//			URI uri = URI.create("localhost").resolve("/sftp");
//
//			RestTemplate restTemplate = new RestTemplate();
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Authorization", token);
//
//			MultipartFile[] multipartFiles = new MultipartFile[1];
//			MultipartFile multipartFile = new MockMultipartFile("file", file.getTextFileName(), "text/plain", file.getData());
//			multipartFiles[0] = multipartFile;
//
//			HttpEntity<MultipartFile[]> request = new HttpEntity<>(multipartFiles, headers);
//
//			restTemplate.postForLocation(uri, request);
//
//			System.out.println("Sent File: " + file);
//		} catch (RestClientException e) {
//			logger.error("sendFile() Exception", e);
//
//			return false;
//		}
//
//		return true;
//	}
}
