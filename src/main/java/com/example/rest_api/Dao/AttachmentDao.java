package com.example.rest_api.Dao;


import com.example.rest_api.Entities.Attachments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentDao extends JpaRepository<Attachments,String> { }
