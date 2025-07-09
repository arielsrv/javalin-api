package com.iskaypet.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.iskaypet.clients.UserClient;
import com.iskaypet.dto.UserDTO;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class UserService {

	@Inject
	UserClient userClient;

	public Observable<List<UserDTO>> getUsers() {
		return this.userClient.getUsers().map(userResponses -> {
			List<UserDTO> users = new ArrayList<>();
			userResponses.forEach(userResponse -> {
				UserDTO userDTO = new UserDTO();
				userDTO.userId = userResponse.id;
				userDTO.email = userResponse.email;
				userDTO.name = userResponse.name;
				users.add(userDTO);
			});
			return users;
		});
	}
}
