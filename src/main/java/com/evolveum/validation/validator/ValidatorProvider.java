package com.evolveum.validation.validator;

import com.evolveum.validation.validator.CodeValidator;

public interface ValidatorProvider<P> {

    CodeValidator getValidator(P validationParams);
}
