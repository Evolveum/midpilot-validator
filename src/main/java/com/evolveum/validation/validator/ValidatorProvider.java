package com.evolveum.validation.validator;

public interface ValidatorProvider<P> {

    CodeValidator getValidator(P validationParams);
}
