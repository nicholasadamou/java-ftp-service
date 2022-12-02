package com.nicholasadamou.ftp.service.controller;

import com.nicholasadamou.ftp.service.util.Utils;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class FTPController {
	Logger logger = LoggerFactory.getLogger(FTPController.class);

	@Autowired
	private Utils utils;

	@Value("${ssh.client.username}")
	private String username;

	@Value("${ssh.client.password}")
	private String password;

	@Value("${ssh.client.destination}")
	private String destination;

	public boolean sftp(List<MultipartFile> files) {
		try {
			final SSHClient sshClient = utils.getSSHClient();

			File sshDir = OpenSSHKnownHosts.detectSSHDir();

			if (sshDir == null) {
				sshDir = new File("/", ".ssh");
			}

			File knownHosts = new File(sshDir, "known_hosts");

			sshClient.loadKnownHosts(knownHosts);

			File privateKey = new File(sshDir, "id_rsa");

			KeyProvider keyProvider = sshClient.loadKeys(privateKey.getPath(), password);

			sshClient.authPublickey(username, keyProvider);

			final SFTPClient sftpClient = sshClient.newSFTPClient();

			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();

				if (file.isEmpty()) {
					logger.error(String.format("File %s is empty.", fileName));

					continue;
				}

				String fileExtension = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf(".") + 1);

				boolean isTextFile = fileExtension.equalsIgnoreCase("txt");
				boolean isZipFile = fileExtension.equalsIgnoreCase("zip");

				if (!isTextFile && !isZipFile) {
					continue;
				}

				File shm = new File("/dev/shm/");
				File tmp;

				if (shm.exists() && shm.canWrite() && shm.canRead()) {
					tmp = new File(shm, fileName);
				} else {
					tmp = new File("/tmp/", fileName);
				}

				file.transferTo(tmp);

				try {
					sftpClient.put(new FileSystemFile(tmp), destination + fileName);

					logger.info(String.format("The file %s was uploaded.", fileName));
				} catch (IOException e) {
					logger.error(String.format("The file %s failed to be uploaded.", fileName), e);

					return false;
				}

				if (tmp.delete()) {
					logger.info(String.format("The file %s was successfully deleted.", fileName));
				} else {
					logger.error(String.format("The file %s failed to be deleted.", fileName));
				}
			}

			sftpClient.close();
			sshClient.disconnect();
		} catch (IOException e) {
			logger.error("sftp Exception ", e);

			return false;
		}

		return true;
	}
}
