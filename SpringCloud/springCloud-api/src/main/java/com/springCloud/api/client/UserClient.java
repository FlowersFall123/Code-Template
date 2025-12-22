package com.springCloud.api.client;


import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", path = "/api/user")
public interface UserClient {

}