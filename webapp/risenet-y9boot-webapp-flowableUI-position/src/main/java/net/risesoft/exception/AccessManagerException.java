package net.risesoft.exception;

public class AccessManagerException extends Exception {
	private static final long serialVersionUID = -5875966731930943997L;

	public AccessManagerException() {
	}

	public AccessManagerException(String s) {
		super(s);
	}

	public AccessManagerException(String s, Throwable arg1) {
		super(s, arg1);
	}

	public AccessManagerException(Throwable arg1) {
		super(arg1);
	}

}
