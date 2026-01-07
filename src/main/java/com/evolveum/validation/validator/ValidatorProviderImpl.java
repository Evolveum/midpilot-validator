package com.evolveum.validation.validator;

import com.evolveum.validation.validator.CodeValidator;
import com.evolveum.validation.validator.CodeValidatorImpl;
import com.evolveum.validation.validator.ValidationParams;
import com.evolveum.validation.validator.ValidatorProvider;
import org.springframework.stereotype.Component;


@Component
public class ValidatorProviderImpl implements ValidatorProvider<com.evolveum.validation.validator.ValidationParams> {

    @Override
    public CodeValidator getValidator(ValidationParams params) {
        return new CodeValidatorImpl(params);
    }
}
