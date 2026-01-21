package com.example.demo.quartz;

import com.example.demo.controller.FileController;
import com.example.demo.controller.UserController;
import com.example.demo.model.UserDTO;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileUploaderJob implements Job {
    @Autowired
    UserController userController;
    @Autowired
    FileController fileController;

    private static final String ADMIN_NAME = "ADMIN";
    private static final String ADMIN_PASSWORD = "PASSWORD";
    private static final String ADMIN_ROLE = "ADMIN";
    private static boolean isAdminRegistered = false;
    private final UserDTO userDTO = new UserDTO(ADMIN_NAME, ADMIN_PASSWORD, ADMIN_ROLE);

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        if (!isAdminRegistered) {
            userController.register(userDTO);
            isAdminRegistered = true;
        }


        System.out.println("This is a quartz jobbbbbbbbbbbbb!");
    }
}