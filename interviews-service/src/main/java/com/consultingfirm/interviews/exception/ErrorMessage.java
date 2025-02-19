package com.consultingfirm.interviews.exception;

import java.time.LocalDateTime;

public record ErrorMessage (LocalDateTime timestamp, String message, String details){

}