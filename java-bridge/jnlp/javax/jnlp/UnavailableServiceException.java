/*
 * Copyright (C) 2026 Mohammed Ammar Hussein (@MohammedALShimmary)
 * Original Java bridge code authored for ASYCUDA Iraq Android.
 * Licensed under GNU GPL-3.0; see the repository LICENSE file and java-bridge/ATTRIBUTION-TERMS.md.
 */
package javax.jnlp;

/**
 * Standard JNLP UnavailableServiceException (`00_jnlp.jar`).
 */
public class UnavailableServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnavailableServiceException() {
        super();
    }

    public UnavailableServiceException(String msg) {
        super(msg);
    }
}
