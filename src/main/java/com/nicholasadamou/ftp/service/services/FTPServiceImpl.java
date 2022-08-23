package com.nicholasadamou.ftp.service.services;

import com.nicholasadamou.ftp.service.controller.FTPController;
import com.nicholasadamou.ftp.service.model.User;
import com.nicholasadamou.ftp.service.util.ServiceResponseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import java.util.List;

public class FTPServiceImpl implements FTPService {
	Logger logger = LoggerFactory.getLogger(FTPServiceImpl.class);

	@Autowired
	private FTPController ftpController;

	@Override
	public Response sftp(MultipartFile[] files, String token) {
		User user = User.getUserFrom(token);

		if (user.isNotValidUser()) {
			logger.error("Unable to retrieve user from Authorization token.");

			return ServiceResponseConstants.DEFAULT_AUTH_ERROR_RESPONSE;
		}

		boolean success = ftpController.sftp(List.of(files));

		if (success) {
			return Response.status(Response.Status.OK).build();
		} else {
			return ServiceResponseConstants.DEFAULT_ERROR_RESPONSE;
		}
	}
}
