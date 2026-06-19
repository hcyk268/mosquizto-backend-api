package com.mosquizto.api.controller;

import com.mosquizto.api.dto.response.MediaSignResponse;
import com.mosquizto.api.dto.response.ResponseData;
import com.mosquizto.api.model.User;
import com.mosquizto.api.service.CurrentUserProvider;
import com.mosquizto.api.service.MediaSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media/cloudinary")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Signed media upload APIs")
public class MediaController {

    private final MediaSignService mediaSignService;
    private final CurrentUserProvider currentUserProvider;

    @Operation(
            summary = "Get signed upload params",
            description = "Returns Cloudinary signed upload parameters for the current user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Signed upload params returned")
    @GetMapping("/sign")
    public ResponseData<MediaSignResponse> sign(
            @Parameter(description = "Upload folder", example = "mosquizto/avatars")
            @RequestParam(required = false) String folder) {
        User user = this.currentUserProvider.getCurrentUser();
        MediaSignResponse response = this.mediaSignService.signForUser(user.getId(), folder);
        return new ResponseData<>(HttpStatus.OK.value(), "Get upload signature success", response);
    }
}
