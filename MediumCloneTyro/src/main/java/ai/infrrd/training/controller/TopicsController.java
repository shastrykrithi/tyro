package ai.infrrd.training.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.infrrd.training.exception.BusinessException;
import ai.infrrd.training.filter.AuthTokenFilter;
import ai.infrrd.training.payload.request.TopicFollowRequest;
import ai.infrrd.training.payload.response.MessageResponse;
import ai.infrrd.training.repository.TopicRepository;
import ai.infrrd.training.repository.UserRepository;
import ai.infrrd.training.service.ResponseModel;
import ai.infrrd.training.service.TopicsService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@PreAuthorize("isAuthenticated()")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1")
public class TopicsController {

	private static final Logger logger = LoggerFactory.getLogger(FollowersController.class);

	@Autowired
	ResponseModel responseModel;

	@Autowired
	TopicsService topicService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	TopicRepository topicRepository;

	@PostMapping("/topics/follow")
	@ApiOperation(value = "User request to follow a topic", notes = "Provide username and topic id to follow", authorizations = {
			@Authorization(value = "jwtToken") }, response = MessageResponse.class)
	public ResponseModel followTopic(@RequestBody TopicFollowRequest topicFollowRequest) throws BusinessException {
		if (!userRepository.existsByUsername(AuthTokenFilter.currentUser)) {
			logger.error("User not found");
			throw new BusinessException(HttpStatus.BAD_REQUEST, "User not found");
		}
		if (!topicRepository.existsById(topicFollowRequest.getTopicID())) {
			logger.error("User to follow not found");
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Topic Not found!!");
		}

		try {
			topicService.followTopic(topicFollowRequest, AuthTokenFilter.currentUser);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			responseModel.setData("error", e.getMessage());
			return responseModel;

		}
		responseModel.setData("result", "Topic followed");
		return responseModel;
	}

	@PostMapping("/topics/unfollow")
	@ApiOperation(value = "User request to unfollow a topic", notes = "Provide username and topic id to unfollow", authorizations = {
			@Authorization(value = "jwtToken") }, response = MessageResponse.class)
	public ResponseModel unfollowTopic(@RequestBody TopicFollowRequest topicFollowRequest) throws BusinessException {
		if (!userRepository.existsByUsername(AuthTokenFilter.currentUser)) {
			logger.error("User not found");
			throw new BusinessException(HttpStatus.BAD_REQUEST, "User not found");
		}
		if (!topicRepository.existsById(topicFollowRequest.getTopicID())) {
			logger.error("User to follow not found");
			throw new BusinessException(HttpStatus.BAD_REQUEST, "Topic Not found!!");
		}
		try {
			topicService.unfollowTopic(topicFollowRequest, AuthTokenFilter.currentUser);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			responseModel.setData("error", e.getMessage());
			return responseModel;

		}
		responseModel.setData("result", "Topic un-followed");
		return responseModel;
	}

	@GetMapping("/topics")
	@ApiOperation(value = "List of topics following", notes = "Username based following topics list", authorizations = {
			@Authorization(value = "jwtToken") }, response = MessageResponse.class)
	public ResponseModel followTopicsList() throws BusinessException {
		if (!userRepository.existsByUsername(AuthTokenFilter.currentUser)) {
			logger.error("User not found");
			throw new BusinessException(HttpStatus.BAD_REQUEST, "User not found");
		}

		try {
			responseModel.setData("result", topicService.getUserTopics(AuthTokenFilter.currentUser));
			return responseModel;
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			responseModel.setData("error", e.getMessage());
			return responseModel;
		}
	}

	@GetMapping("/topics/auto-fill/{stringMatch}")
	@ApiOperation(value = "List of starts-with matching topics", notes = "Get the list of topics starts-with match", authorizations = {
			@Authorization(value = "jwtToken") }, response = MessageResponse.class)
	public ResponseModel startsWith(@PathVariable String stringMatch) throws BusinessException {
		try {
			responseModel.setData("result", topicService.getStringMatchTopics(stringMatch));
			return responseModel;
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			responseModel.setData("error", e.getMessage());
			return responseModel;
		}
	}

}
