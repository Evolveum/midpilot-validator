/*
 *
 * Copyright (c) 2025 Evolveum and contributors
 *
 * Licensed under the EUPL-1.2 or later.
 *
 */

package com.evolveum.validation.command;

import com.evolveum.validation.converter.ConverterCommand;
import com.evolveum.validation.validator.ValidatorCommand;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * Created by Dominik.
 */
@Component
@CommandLine.Command(
        name = "app",
        subcommands = {
                ValidatorCommand.class,
                ConverterCommand.class
        }
)
public class AppCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("Use one of the subcommands.");
    }
}
