package com.swm.sprint1.payload.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank
    private String name;

    private MultipartFile imageFile;

    @NotEmpty(message = "카테고리가 비어있습니다.")
    private List<String> categories;

    @Builder
    public UpdateUserRequest(@NotBlank String name, @NotBlank MultipartFile imageFile, @NotEmpty(message = "카테고리가 비어있습니다.") List<String> categories) {
        this.name = name;
        this.imageFile = imageFile;
        this.categories = categories;
    }

}
