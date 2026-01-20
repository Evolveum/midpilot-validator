/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.validator;

import com.evolveum.concepts.SourceLocation;
import com.evolveum.concepts.ValidationLog;
import com.evolveum.validation.common.SupportedLanguage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by Dominik.
 */
public class TestYamlValidation {

    private ValidatorProvider<ValidationParams> validatorProvider;

    @BeforeMethod
    public void setUp() {
        this.validatorProvider = new ValidatorProviderImpl();
    }

    @Test
    public void mappingSnippetTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawYaml = """
                mapping:
                  name: givenName-familyName-to-cn
                  source:
                    - path: "$focus/givenName"
                    - path: "$focus/familyName"
                  expression:
                    script:
                      code: "givenName + ' ' + familyName"
                  condition:
                    script:
                      code: "givenName != null && familyName != null"
                  target:
                    path: "$projection/attributes/cn"
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 2, 3));

        rawYaml = """
                expression:
                  script:
                    code: "givenName + ' ' + familyName"
                """;

        validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 2, 3));
    }

    @Test
    public void snippetWithoutItemDefinitionTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null)
        );

        String rawYaml = """
                attribute:
                  ref: icfs:uid
                  outbound:
                    expression:
                      script:
                        code: "title + ' ' + givenName"
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 2, 3));

        rawYaml = """
            objectType:
              attribute:
                - ref: icfs:uid
                  outbound:
                    expression:
                      script:
                        code: "title + ' ' + givenName"
            """;

        validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertThat(validationLogs)
                .hasSize(1)
                .extracting(ValidationLog::location)
                .containsExactly(SourceLocation.from("unknown", 2, 3));

        rawYaml = """
                schemaHandling:
                  objectType:
                    - attribute:
                        - ref: icfs:uid
                          outbound:
                            expression:
                              script:
                                code: "title + ' ' + givenName"
                """;

        validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }

    @Test
    public void validationContainerValueTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawYaml = """
                user:
                  "@xmlns": "http://midpoint.evolveum.com/xml/ns/public/common/common-3"
                  assignment: "Test"
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);

        assertThat(validationLogs)
                .hasSize(2)
                .extracting(ValidationLog::location)
                .containsExactly(
                        SourceLocation.from("unknown", 2, 73),
                        SourceLocation.from("unknown", 3, 21)
                );
    }

    @Test
    public void correctlyMappingTest() throws Exception {
        CodeValidator codeValidator = validatorProvider.getValidator(
                new ValidationParams(null, null));

        String rawYaml = """
                resource:
                  oid: e1b34373-f61e-4a5a-9fb1-81f931fcfc05
                  version: "0"
                  name: dms
                  schemaHandling:
                    objectType:
                      - kind: account
                        intent: default
                        default: "true"
                        delineation:
                          objectClass: user
                        focus:
                          type: UserType
                        attribute:
                          - ref: name
                            correlator: {}
                            inbound:
                              strength: strong
                              target:
                                path: name
                            outbound:
                              strength: strong
                              source:
                                path: name
                          - ref: fullname
                            inbound:
                              strength: strong
                              target:
                                path: fullName
                            outbound:
                              strength: strong
                              source:
                                path: fullName
                          - ref: email
                            inbound:
                              strength: strong
                              target:
                                path: emailAddress
                            outbound:
                              strength: strong
                              source:
                                path: emailAddress
                        synchronization:
                          reaction:
                            - situation: unmatched
                              actions:
                                addFocus: {}
                            - situation: unlinked
                              actions:
                                link: {}
                            - situation: linked
                              actions:
                                synchronize: {}
                      - kind: generic
                        intent: documentStore
                        delineation:
                          objectClass: documentStore
                        focus:
                          type: ServiceType
                          archetypeRef:
                            oid: 8c0b32f9-fadc-42cb-bc05-878ecfe001e8
                        attribute:
                          - ref: name
                            correlator: {}
                            inbound:
                              strength: strong
                              target:
                                path: name
                          - ref: description
                            inbound:
                              strength: strong
                              target:
                                path: displayName
                        synchronization:
                          reaction:
                            - situation: unmatched
                              actions:
                                addFocus: {}
                            - situation: unlinked
                              actions:
                                link: {}
                            - situation: linked
                              actions:
                                synchronize: {}
                    associationType:
                      name: userDocumentStoreAccess
                      subject:
                        objectType:
                          kind: account
                          intent: default
                        association:
                          ref: access
                          outbound:
                            name: association-outbound
                            expression:
                              associationConstruction:
                                attribute:
                                  ref: level
                                  mapping:
                                    source:
                                      path: targetRef
                                    expression:
                                      script:
                                        code: targetRef?.relation?.localPart
                                objectRef:
                                  ref: documentStore
                                  mapping:
                                    expression:
                                      associationFromLink: {}
                          inbound:
                            name: association-inbound
                            expression:
                              associationSynchronization:
                                objectRef:
                                  ref: documentStore
                                  mapping:
                                    source:
                                      path: $shadow/attributes/level
                                    expression:
                                      shadowOwnerReferenceSearch:
                                        relationExpression:
                                          path: $level
                                    target:
                                      path: targetRef
                                correlation:
                                  correlators:
                                    items:
                                      item:
                                        ref: targetRef
                                synchronization:
                                  reaction:
                                    - situation: unmatched
                                      actions:
                                        addFocusValue: {}
                                    - situation: matched
                                      actions:
                                        synchronize: {}
                """;

        List<ValidationLog> validationLogs  = codeValidator.validate(rawYaml, SupportedLanguage.YAML);
        assertTrue("Expected no validation logs.", validationLogs.isEmpty());
    }
}