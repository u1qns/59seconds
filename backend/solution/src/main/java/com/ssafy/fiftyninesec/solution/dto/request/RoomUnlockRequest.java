package com.ssafy.fiftyninesec.solution.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomUnlockRequest {
    @NotEmpty(message = "암호는 필수 입력값입니다.")
    private String enterCode;
}
