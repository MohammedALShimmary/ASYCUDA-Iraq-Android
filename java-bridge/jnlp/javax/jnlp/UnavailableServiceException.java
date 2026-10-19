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
