package com.evolveum.validation.validator.groovy;

import com.evolveum.validation.validator.CodeValidator;
import com.evolveum.validation.validator.ValidatorProvider;
import org.springframework.stereotype.Component;

@Component
final class GroovyValidatorProviderImpl implements ValidatorProvider<GroovyValidationParams> {

    @Override
    public CodeValidator getValidator(GroovyValidationParams validationParams) {
        return new GroovyValidator(validationParams.scriptBaseClass());
    }

}
