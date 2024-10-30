package project.management.exception;

import lombok.Getter;
import lombok.Setter;
import project.management.project_enum.ExceptionEnum;

@Setter
@Getter
public class ApplicationException extends RuntimeException{
    public ApplicationException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getMessage());
        this.exceptionEnum = exceptionEnum;
    }

    private ExceptionEnum exceptionEnum;
}
