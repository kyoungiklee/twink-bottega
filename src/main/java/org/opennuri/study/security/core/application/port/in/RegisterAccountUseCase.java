package org.opennuri.study.security.core.application.port.in;

import org.opennuri.study.security.core.domain.Account;
import org.springframework.dao.DuplicateKeyException;

public interface RegisterAccountUseCase {
    Account register(RegisterAccountCommand command) throws DuplicateKeyException;
}
