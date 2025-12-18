package com.evolveum.validation.validator;

import org.springframework.stereotype.Component;


@Component
public class ValidatorProviderImpl implements ValidatorProvider<ValidationParams> {

    @Override
    public CodeValidator getValidator(ValidationParams params) {
        return new CodeValidatorImpl(params);
    }
}
