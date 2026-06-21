package com.mosquizto.api.service.impl;

import com.mosquizto.api.dto.response.FollowNotificationResponse;
import com.mosquizto.api.dto.response.PageResponse;
import com.mosquizto.api.dto.response.UserSummaryResponse;
import com.mosquizto.api.event.dto.UserFollowedEvent;
import com.mosquizto.api.exception.ResourceNotFoundException;
import com.mosquizto.api.mapper.UserMapper;
import com.mosquizto.api.mapper.UserReportMapper;
import com.mosquizto.api.model.Follow;
import com.mosquizto.api.model.User;
import com.mosquizto.api.repository.FollowRepository;
import com.mosquizto.api.repository.UserRepository;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final CurrentUserProvider currentUserProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserReportMapper userReportMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void follow(String username) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        User targetUser = this.userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        boolean wasAlreadyActive = this.followRepository
                .findActiveByFollowerAndFollowing(currentUser.getId(), targetUser.getId())
                .isPresent();

        Follow follow = this.followRepository.findByFollowerAndFollowing(currentUser.getId(), targetUser.getId())
                .map(existingFollow -> {
                    if (!existingFollow.isActive()) {
                        existingFollow.restore();
                    }
                    return existingFollow;
                })
                .orElseGet(() -> Follow.create(currentUser, targetUser));

        follow = this.followRepository.save(follow);

        if (!wasAlreadyActive) {
            this.eventPublisher.publishEvent(new UserFollowedEvent(
                    follow.getId(),
                    targetUser.getUsername(),
                    UserReportMapper.displayName(currentUser)
            ));
        }
    }

    @Override
    @Transactional
    public void unfollow(String username) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        User targetUser = this.userRepository.findActiveByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        this.followRepository.findActiveByFollowerAndFollowing(currentUser.getId(), targetUser.getId())
                .ifPresent(follow -> {
                    follow.unfollow();
                    this.followRepository.save(follow);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserSummaryResponse> getFollowers(int page, int size) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        Page<User> followers = this.followRepository.findActiveFollowers(
                currentUser.getId(),
                PageRequest.of(page - 1, size)
        );

        List<Long> followerIds = followers.getContent().stream()
                .map(User::getId)
                .toList();
        Set<Long> followedUserIds = followerIds.isEmpty()
                ? Set.of()
                : new HashSet<>(this.followRepository.findActiveFollowingIds(currentUser.getId(), followerIds));

        List<UserSummaryResponse> items = followers.getContent().stream()
                .map(user -> this.userMapper.toSummaryResponse(user, followedUserIds.contains(user.getId())))
                .toList();

        return this.toPageResponse(followers, page, size, items);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserSummaryResponse> getFollowing(int page, int size) {
        User currentUser = this.currentUserProvider.getCurrentUser();
        Page<User> following = this.followRepository.findActiveFollowing(
                currentUser.getId(),
                PageRequest.of(page - 1, size)
        );

        List<UserSummaryResponse> items = following.getContent().stream()
                .map(user -> this.userMapper.toSummaryResponse(user, true))
                .toList();

        return this.toPageResponse(following, page, size, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FollowNotificationResponse> getFollowNotifications() {
        User currentUser = this.currentUserProvider.getCurrentUser();

        return this.followRepository.findActiveFollowsReceivedBy(currentUser.getId()).stream()
                .map(this.userReportMapper::toFollowNotificationResponse)
                .toList();
    }

    private PageResponse<UserSummaryResponse> toPageResponse(Page<User> userPage,
                                                             int page,
                                                             int size,
                                                             List<UserSummaryResponse> items) {
        return PageResponse.<UserSummaryResponse>builder()
                .page(page)
                .size(size)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .items(items)
                .build();
    }
}
