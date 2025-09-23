package com.gumeinteligenciacomercial.orcaja.application.usecase.ia;

import com.gumeinteligenciacomercial.orcaja.application.gateway.PromptGateway;
import com.gumeinteligenciacomercial.orcaja.domain.Prompt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromptUseCaseTest {

    @Mock
    private PromptGateway gateway;

    @InjectMocks
    private PromptUseCase promptUseCase;

}