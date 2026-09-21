package org.aleksvander.petaidemo.petaidemo.exception;

import org.springframework.orm.ObjectOptimisticLockingFailureException;

public final class VersionChecker {

    private VersionChecker() {
    }

    public static void check(Class<?> entityClass, Long id, Long expected, Long actual) {
        if (expected == null) {
            throw new VersionRequiredException("Field 'version' is required to update " + entityClass.getSimpleName());
        }
        if (!expected.equals(actual)) {
            throw new ObjectOptimisticLockingFailureException(entityClass, id);
        }
    }
}
