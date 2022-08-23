package com.nicholasadamou.ftp.service.services;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RestController
public interface FTPService {
	@CrossOrigin(methods = RequestMethod.POST)
	@RequestMapping(path = "/sftp", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA)
	Response sftp(@RequestPart MultipartFile[] files, @RequestHeader(value = "Authorization") String token);
}
